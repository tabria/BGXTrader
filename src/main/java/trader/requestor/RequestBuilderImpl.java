package trader.requestor;

import trader.broker.connector.BrokerConnector;
import trader.entity.indicator.Indicator;
import trader.entity.indicator.ma.MovingAverageBuilder;
import trader.entity.indicator.ma.enums.MAType;
import trader.entity.indicator.rsi.RSIBuilder;
import trader.entity.trade.Trade;
import trader.entity.trade.TradeImpl;
import trader.entry.EntryStrategy;
import trader.exception.*;
import trader.exit.ExitStrategy;
import trader.interactor.enums.DataStructureType;
import trader.interactor.RequestImpl;
import trader.configuration.TradingStrategyConfiguration;
import trader.configuration.BGXConfigurationImpl;
import trader.order.OrderStrategy;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class RequestBuilderImpl implements RequestBuilder {


    private static final String LOCATION = "location";
    private static final String BROKER_NAME = "brokerName";
    private static final String TYPE = "type";
    private static final String RSI = "rsi";
    private static final String TRADABLE = "tradable";
    private static final String DIRECTION = "direction";
    private static final String ENTRY_PRICE = "entryPrice";
    private static final String STOP_LOSS_PRICE = "stopLossPrice";

    @Override
    public Request<?> build(String controllerName, HashMap<String, String> settings) {
        verifyInput(controllerName, settings);
        controllerName = controllerName.trim().toLowerCase();
        if(controllerName.contains(type(DataStructureType.INDICATOR)))
            return buildIndicatorRequest(settings);
        if(controllerName.contains(type(DataStructureType.BGX_CONFIGURATION)))
            return buildBGXConfigurationRequest(settings);
        if(controllerName.contains(type(DataStructureType.BROKER_CONNECTOR)))
            return buildBrokerConnector(settings);
        if(controllerName.contains(type(DataStructureType.CREATE_TRADE)))
            return buildTrade(settings);
        if(controllerName.contains(type(DataStructureType.ENTRY_STRATEGY)))
            return buildEntryStrategy(settings);
        if(controllerName.contains(type(DataStructureType.ORDER_STRATEGY)))
            return buildOrderStrategy(settings);
        if(controllerName.contains(type(DataStructureType.EXIT_STRATEGY)))
            return buildExitStrategy(settings);
        throw new NoSuchDataStructureException();
    }

    private Request<?> buildExitStrategy(HashMap<String,String> settings) {
        Request<ExitStrategy> request = new RequestImpl<>();
        ExitStrategy exitStrategy = createExitStrategyInstance(settings);
        request.setRequestDataStructure(exitStrategy);
        return request;
    }

    private Request<?> buildOrderStrategy(HashMap<String,String> settings) {
        Request<OrderStrategy> request = new RequestImpl<>();
        OrderStrategy orderStrategy = createOrderStrategyInstance(settings);
        request.setRequestDataStructure(orderStrategy);
        return request;
    }

    private Request<?> buildEntryStrategy(HashMap<String,String> settings) {
        Request<EntryStrategy> request = new RequestImpl<>();
        EntryStrategy entryStrategy = createEntryStrategyInstance(settings);
        request.setRequestDataStructure(entryStrategy);
        return request;
    }

    private Request<?> buildTrade(HashMap<String,String> settings) {
        Request<Trade> request = new RequestImpl<>();
        Trade trade = new TradeImpl();
        setTradeValues(settings, trade);
        request.setRequestDataStructure(trade);
        return request;
    }

    private Request<?> buildBrokerConnector(HashMap<String, String> settings) {
        Request<BrokerConnector> request = new RequestImpl<>();
        BrokerConnector brokerConnector = BrokerConnector.create(settings.get(BROKER_NAME));
        brokerConnector.setFileLocation(settings.get(LOCATION));
        request.setRequestDataStructure(brokerConnector);
        return request;
    }

    private Request<?> buildBGXConfigurationRequest(HashMap<String, String> settings) {
        Request<TradingStrategyConfiguration> request = new RequestImpl<>();
        BGXConfigurationImpl bgxConfiguration = new BGXConfigurationImpl();
        bgxConfiguration.setFileLocation(settings.get(LOCATION));
        request.setRequestDataStructure(bgxConfiguration);
        return request;
    }

    private Request<?> buildIndicatorRequest(HashMap<String, String> settings) {
        Request<Indicator> request = new RequestImpl<>();
        String dataStructureType = getDataStructureType(settings).toLowerCase();
        if (dataStructureType.contains(RSI)) {
            request.setRequestDataStructure(new RSIBuilder().build(settings));
            return request;
        } else if(isMovingAverage(dataStructureType)) {
            request.setRequestDataStructure(new MovingAverageBuilder().build(settings));
            return request;
        }
        throw new NoSuchDataStructureException();
    }

    private ExitStrategy createExitStrategyInstance(HashMap<String,String> settings) {
        try {
            String className = settings.get("exitStrategy").trim();
            className = Character.toUpperCase(className.charAt(0)) + className.substring(1);
            Class<?> exitStrategyClass = Class.forName("trader.exit." + className.toLowerCase() + "." + className +"ExitStrategy");
            Constructor<?> exitStrategyConstructor = exitStrategyClass.getDeclaredConstructor();
            return (ExitStrategy) exitStrategyConstructor.newInstance();
        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException | StringIndexOutOfBoundsException e) {
            throw new NoSuchStrategyException();
        } catch (NullPointerException e) {
            throw new NullArgumentException();
        }
    }

    private EntryStrategy createEntryStrategyInstance(HashMap<String, String> settings) {
        try {
            String className = settings.get("entryStrategy").trim();
            className = Character.toUpperCase(className.charAt(0)) + className.substring(1);
            Class<?> entryStrategyClass = Class.forName("trader.entry." + className.toLowerCase() + "." + className +"EntryStrategy");
            Constructor<?> entryStrategyConstructor = entryStrategyClass.getDeclaredConstructor();
            return (EntryStrategy) entryStrategyConstructor.newInstance();
        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException | StringIndexOutOfBoundsException e) {
            throw new NoSuchStrategyException();
        } catch (NullPointerException e) {
            throw new NullArgumentException();
        }
    }

    private OrderStrategy createOrderStrategyInstance(HashMap<String, String> settings) {
        try {
            String className = settings.get("orderStrategy").trim();
            className = Character.toUpperCase(className.charAt(0)) + className.substring(1);
            Class<?> orderStrategyClass = Class.forName("trader.order." +className.toLowerCase() + "." + className +"OrderStrategy");
            Constructor<?> orderStrategyConstructor = orderStrategyClass.getDeclaredConstructor();
            return (OrderStrategy) orderStrategyConstructor.newInstance();
        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException | StringIndexOutOfBoundsException e) {
            throw new NoSuchStrategyException();
        } catch (NullPointerException e) {
            throw new NullArgumentException();
        }
    }

    private void setTradeValues(HashMap<String, String> settings, Trade trade) {
        if(settings.size()>0){
            trade.setTradable(settings.get(TRADABLE));
            trade.setDirection(settings.get(DIRECTION));
            trade.setEntryPrice(settings.get(ENTRY_PRICE));
            trade.setStopLossPrice(settings.get(STOP_LOSS_PRICE));
        }
    }

    private String getDataStructureType(HashMap<String, String> settings) {
        if(settings.size() == 0)
            throw new EmptyArgumentException();
        if(!settings.containsKey(TYPE))
            throw new WrongIndicatorSettingsException();
        String dataStructureType = settings.get(TYPE);
        if(dataStructureType == null || dataStructureType.isEmpty())
            throw new WrongIndicatorSettingsException();
        return dataStructureType;
    }

    private boolean isMovingAverage(String dataStructureName) {
        return dataStructureName.contains(indicator(MAType.SIMPLE)) ||
                dataStructureName.contains(indicator(MAType.WEIGHTED)) ||
                dataStructureName.contains(indicator(MAType.EXPONENTIAL));
    }

    private void verifyInput(String controllerName, HashMap<String, String> settings) {
        if(controllerName == null || settings == null)
            throw new NullArgumentException();
        if(controllerName.trim().isEmpty())
            throw new EmptyArgumentException();
    }

    private String indicator(MAType name){
        return name.toString().trim().toLowerCase();
    }

    private String type(DataStructureType type){
        return type.toString().trim().toLowerCase();
    }
}
