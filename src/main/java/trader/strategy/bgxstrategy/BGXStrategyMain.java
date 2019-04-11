package trader.strategy.bgxstrategy;

import trader.broker.BrokerGateway;
import trader.broker.connector.BaseGateway;
import trader.broker.connector.BrokerConnector;
import trader.controller.TraderController;
import trader.broker.connector.ApiConnector;
import trader.controller.*;
import trader.entity.indicator.Indicator;
import trader.entry.EntryStrategy;
import trader.exception.EmptyArgumentException;
import trader.exception.NullArgumentException;
import trader.observer.Observer;
import trader.observer.PositionObserver;
import trader.observer.UpdateIndicatorObserver;
import trader.requestor.RequestBuilderImpl;
import trader.requestor.UseCaseFactoryImpl;
import trader.requestor.RequestBuilder;
import trader.requestor.UseCaseFactory;
import trader.responder.Response;
import trader.strategy.Observable;
import trader.order.OrderStrategy;
import trader.configuration.TradingStrategyConfiguration;
import trader.strategy.observable.PriceObservable;
import trader.strategy.Strategy;
import trader.order.OrderService;
import trader.exit.exit_strategie.BaseExitStrategy;
import trader.exit.ExitStrategy;
import trader.strategy.observable.PricePull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public final class BGXStrategyMain implements Strategy {

    private static final String LOCATION = "location";
    private static final String BROKER_NAME = "brokerName";
    private static final String ENTRY_STRATEGY_KEY_NAME = "entryStrategy";
    private static final String ORDER_STRATEGY_KEY_NAME = "orderStrategy";

    private RequestBuilder requestBuilder;
    private UseCaseFactory useCaseFactory;
    private List<Indicator> indicatorList;
    private TradingStrategyConfiguration configuration;
    private BrokerGateway brokerGateway;
    private Observable priceObservable;
    private EntryStrategy entryStrategy;
    private OrderStrategy orderStrategy;


    private Observer positionObserver;

    private ApiConnector apiConnector;


    private ExitStrategy exitStrategy;



    public BGXStrategyMain(String brokerName, String configurationFileName, String brokerConfigurationFileName, String entryStrategyName, String orderStrategyName) {
        validateInput(brokerName, configurationFileName, brokerConfigurationFileName, entryStrategyName);
        requestBuilder = new RequestBuilderImpl();
        useCaseFactory = new UseCaseFactoryImpl();
        configuration = setConfiguration(configurationFileName);
        brokerGateway = setBrokerGateway(brokerName, brokerConfigurationFileName);
        priceObservable = PriceObservable.create(brokerGateway, configuration);
        indicatorList = createIndicatorsFromConfiguration(configuration.getIndicators());
        entryStrategy = setEntryStrategy(entryStrategyName);
        orderStrategy = setOrderStrategy(orderStrategyName);

//        orderStrategy = new OrderService(apiConnector);
        exitStrategy = BaseExitStrategy.createInstance();
    }


    @Override
    public void execute() {
        addIndicatorsToObservable(priceObservable, indicatorList);
        new PricePull("PricePull", priceObservable);



        if(haveOpenTrades())
            exitStrategy.execute();
    //    if (haveOpenOrders())
        //    orderStrategy.closeUnfilledOrder();
      //  orderStrategy.submitNewOrder();
    }

    @Override
    public String toString() {
        return "bgxstrategy";
    }

    TradingStrategyConfiguration getConfiguration() {
        return configuration;
    }

    BrokerGateway getBrokerGateway() {
        return brokerGateway;
    }

    List<Indicator> createIndicatorsFromConfiguration(List<HashMap<String, String>> indicators){
        TraderController<Indicator> addIndicatorController = new CreateIndicatorController<>(requestBuilder, useCaseFactory);
        List<Indicator> indicatorList = new ArrayList<>();
        for (HashMap<String, String> indicator :indicators) {
            Response<Indicator> indicatorResponse = addIndicatorController.execute(indicator);
            indicatorList.add(indicatorResponse.getResponseDataStructure());
        }
        return indicatorList;
    }

    void addIndicatorsToObservable(Observable observable, List<Indicator> indicators){
        for (Indicator indicator:indicators) {
            observable.registerObserver(
                    new UpdateIndicatorObserver(indicator, configuration, brokerGateway));
        }
    }


    //////////////////////////////////////////////////// not tested/////////
    Observer setPositionObserver(){

       return new PositionObserver(brokerGateway, entryStrategy, orderStrategy);
    }


    private void addIndicatorsToBGXGenerator(){

    }

///// not tested//////////////

    private void validateInput(String brokerName, String configurationFileName, String brokerConfigurationFileName, String entryStrategyName) {
        if(brokerName == null || configurationFileName == null || brokerConfigurationFileName == null || entryStrategyName == null)
            throw new NullArgumentException();
        if(brokerName.trim().isEmpty() || configurationFileName.trim().isEmpty() || brokerConfigurationFileName.trim().isEmpty() || entryStrategyName.trim().isEmpty())
            throw new EmptyArgumentException();
    }

    private OrderStrategy setOrderStrategy(String entryStrategyName) {
        HashMap<String, String> settings = new HashMap<>();
        settings.put(ORDER_STRATEGY_KEY_NAME, entryStrategyName);
        TraderController<OrderStrategy> controller = new AddOrderStrategyController<>(requestBuilder, useCaseFactory);
        Response<OrderStrategy> entryResponse = controller.execute(settings);
        OrderStrategy orderStrategy = entryResponse.getResponseDataStructure();
        return orderStrategy;
    }



    private EntryStrategy setEntryStrategy(String entryStrategyName) {
        HashMap<String, String> settings = new HashMap<>();
        settings.put(ENTRY_STRATEGY_KEY_NAME, entryStrategyName);
        TraderController<EntryStrategy> controller = new AddEntryStrategyController<>(requestBuilder, useCaseFactory);
        Response<EntryStrategy> entryResponse = controller.execute(settings);
        EntryStrategy entryStrategy = entryResponse.getResponseDataStructure();
        entryStrategy.setIndicators(indicatorList);
        entryStrategy.setCreateTradeController(new CreateTradeController<>(requestBuilder, useCaseFactory));
        return entryStrategy;
    }


    private TradingStrategyConfiguration setConfiguration(String configurationFileName) {
        HashMap<String, String> settings = new HashMap<>();
        settings.put(LOCATION, configurationFileName);
        TraderController<TradingStrategyConfiguration> controller = new AddBGXConfigurationController<>(requestBuilder, useCaseFactory);
        Response<TradingStrategyConfiguration> configurationResponse = controller.execute(settings);
        return configurationResponse.getResponseDataStructure();
    }

    private BrokerGateway setBrokerGateway(String brokerName, String brokerConfigurationFileName) {
        HashMap<String, String> settings = new HashMap<>();
        settings.put(BROKER_NAME, brokerName);
        settings.put(LOCATION, brokerConfigurationFileName);
        TraderController<BrokerGateway> controller = new AddBrokerConnectorController<>(requestBuilder, useCaseFactory);
        Response<BrokerGateway> brokerResponse = controller.execute(settings);
        BrokerConnector connector = (BrokerConnector) brokerResponse.getResponseDataStructure();
        BrokerGateway brokerGateway = BaseGateway.create(brokerName, connector);
        brokerGateway.validateConnector();
        return brokerGateway;
    }



    private boolean haveOpenOrders() {
        return apiConnector.getOpenOrders().size() > 0;
    }

    private boolean haveOpenTrades() {
        return apiConnector.getOpenTrades().size()>0;
    }

}
