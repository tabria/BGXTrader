package trader.strategy.bgxstrategy;

import trader.broker.connector.ApiConnector;
import trader.broker.BrokerConnector;
import trader.controller.AddBGXConfigurationController;
import trader.controller.AddBrokerConnectorController;
import trader.controller.AddIndicatorController;
import trader.controller.UpdateIndicatorController;
import trader.interactor.RequestBuilderImpl;
import trader.interactor.UseCaseFactoryImpl;
import trader.requestor.RequestBuilder;
import trader.requestor.UseCaseFactory;
import trader.responder.Response;
import trader.strategy.Observable;
import trader.order.OrderStrategy;
import trader.strategy.bgxstrategy.configuration.TradingStrategyConfiguration;
import trader.strategy.observable.PriceObservable;
import trader.strategy.Strategy;
import trader.order.OrderService;
import trader.exit.exit_strategie.BaseExitStrategy;
import trader.exit.ExitStrategy;
import trader.strategy.observable.PricePull;

import java.util.HashMap;
import java.util.List;


public final class BGXStrategyMain implements Strategy {

 //   private static final String BGX_STRATEGY_CONFIG_FILE_NAME = "bgxStrategyConfig.yaml";
 //   private static final String BROKER_CONFIG_FILE_NAME = "oandaBrokerConfig.yaml";
    private static final String LOCATION = "location";
    private static final String BROKER_NAME = "brokerName";

    private RequestBuilder requestBuilder;
    private UseCaseFactory useCaseFactory;
    private TradingStrategyConfiguration configuration;
    private BrokerConnector brokerConnector;

    private AddBGXConfigurationController configurationController;
    private AddBrokerConnectorController addBrokerConnectorController;
    private UpdateIndicatorController updateIndicatorController;
    private AddIndicatorController addIndicatorController;

    private ApiConnector apiConnector;
    private Observable priceObservable;


    private ExitStrategy exitStrategy;
    private OrderStrategy orderStrategy;


//    public BGXStrategyMain(String brokerName) {
//        this(brokerName, BGX_STRATEGY_CONFIG_FILE_NAME, BROKER_CONFIG_FILE_NAME);
//    }

    public BGXStrategyMain(String brokerName, String configurationFileName, String brokerConfigurationFileName) {
        requestBuilder = new RequestBuilderImpl();
        useCaseFactory = new UseCaseFactoryImpl();
        configurationController = new AddBGXConfigurationController(requestBuilder, useCaseFactory);
        addBrokerConnectorController = new AddBrokerConnectorController(requestBuilder, useCaseFactory);
        configuration = setConfiguration(configurationFileName);
        brokerConnector = setBrokerConnector(brokerName, brokerConfigurationFileName);
        updateIndicatorController = new UpdateIndicatorController(requestBuilder, useCaseFactory, configuration, brokerConnector);
        priceObservable = PriceObservable.create(apiConnector);
        addIndicatorController = new AddIndicatorController(requestBuilder, useCaseFactory, updateIndicatorController, priceObservable);

        // setApiConnector(connector);

        addIndicatorsFromConfiguration(configuration.getIndicators());


        orderStrategy = new OrderService(apiConnector);
        exitStrategy = BaseExitStrategy.createInstance();
     //   init();
    }

    @Override
    public void execute() {
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

    BrokerConnector getBrokerConnector() {
        return brokerConnector;
    }

    void addIndicatorsFromConfiguration(List<HashMap<String, String>> indicators){

        for (HashMap<String, String> indicator :indicators) {
            addIndicatorController.execute(indicator);
        }
    }

    private TradingStrategyConfiguration setConfiguration(String configurationFileName) {
        HashMap<String, String> settings = new HashMap<>();
        settings.put(LOCATION, configurationFileName);
        Response<TradingStrategyConfiguration> configurationResponse = configurationController.execute(settings);
        return configurationResponse.getResponseDataStructure();
    }

    private BrokerConnector setBrokerConnector(String brokerName, String brokerConfigurationFileName) {
        HashMap<String, String> settings = new HashMap<>();
        settings.put(BROKER_NAME, brokerName);
        settings.put(LOCATION, brokerConfigurationFileName);
        Response<BrokerConnector> brokerConfigurationResponse = addBrokerConnectorController.execute(settings);
        return brokerConfigurationResponse.getResponseDataStructure();
    }


//    private void setApiConnector(ApiConnector connector) {
//        if(connector == null){
//            throw new NullArgumentException();
//        }
//        this.apiConnector = connector;
//    }


    private boolean haveOpenOrders() {
        return apiConnector.getOpenOrders().size() > 0;
    }

    private boolean haveOpenTrades() {
        return apiConnector.getOpenTrades().size()>0;
    }

}
