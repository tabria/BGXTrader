package trader.strategy.bgxstrategy;

import trader.broker.BrokerGateway;
import trader.broker.connector.BaseGateway;
import trader.broker.connector.BrokerConnector;
import trader.controller.TraderController;
import trader.broker.connector.ApiConnector;
import trader.controller.*;
import trader.entity.indicator.Indicator;
import trader.entity.trade.Trade;
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

 //   private static final String BGX_STRATEGY_CONFIG_FILE_NAME = "bgxStrategyConfig.yaml";
 //   private static final String BROKER_CONFIG_FILE_NAME = "oandaBrokerConfig.yaml";
    private static final String LOCATION = "location";
    private static final String BROKER_NAME = "brokerName";

    private RequestBuilder requestBuilder;
    private UseCaseFactory useCaseFactory;
    private List<Indicator> indicatorList;
    private TradingStrategyConfiguration configuration;
    private BrokerGateway brokerGateway;
    private Observable priceObservable;

    private Observer positionObserver;

    private ApiConnector apiConnector;


    private ExitStrategy exitStrategy;
    private OrderStrategy orderStrategy;


    public BGXStrategyMain(String brokerName, String configurationFileName, String brokerConfigurationFileName) {
        requestBuilder = new RequestBuilderImpl();
        useCaseFactory = new UseCaseFactoryImpl();
        configuration = setConfiguration(configurationFileName);
        brokerGateway = setBrokerGateway(brokerName, brokerConfigurationFileName);
        priceObservable = PriceObservable.create(brokerGateway, configuration);
        indicatorList = createIndicatorsFromConfiguration(configuration.getIndicators());


        orderStrategy = new OrderService(apiConnector);
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


    Observer setPositionObserver(){

        TraderController<Trade> addTradeController = new CreateTradeController<>(requestBuilder, useCaseFactory ,configuration);
       return new PositionObserver(brokerGateway, addTradeController);
    }

//////////////////////////////////////////////////// not tested/////////
    private void addIndicatorsToBGXGenerator(){

    }
///// not tested//////////////

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
