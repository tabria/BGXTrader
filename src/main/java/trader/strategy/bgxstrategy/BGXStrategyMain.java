package trader.strategy.bgxstrategy;

import trader.broker.BrokerGateway;
import trader.broker.connector.BaseGateway;
import trader.broker.connector.BrokerConnector;
import trader.controller.TraderController;
import trader.broker.connector.ApiConnector;
import trader.controller.*;
import trader.entity.indicator.Indicator;
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
    private BrokerGateway brokerGateway;
    private TraderController<TradingStrategyConfiguration> configurationController;
    private Observable priceObservable;

    private ApiConnector apiConnector;



    private ExitStrategy exitStrategy;
    private OrderStrategy orderStrategy;


//    public BGXStrategyMain(String brokerName) {
//        this(brokerName, BGX_STRATEGY_CONFIG_FILE_NAME, BROKER_CONFIG_FILE_NAME);
//    }

    public BGXStrategyMain(String brokerName, String configurationFileName, String brokerConfigurationFileName) {
        requestBuilder = new RequestBuilderImpl();
        useCaseFactory = new UseCaseFactoryImpl();
        configurationController = new AddBGXConfigurationController<>(requestBuilder, useCaseFactory);

        configuration = setConfiguration(configurationFileName);
        brokerGateway = setBrokerGateway(brokerName, brokerConfigurationFileName);
        priceObservable = PriceObservable.create(brokerGateway, configuration);


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

    BrokerGateway getBrokerGateway() {
        return brokerGateway;
    }

    void addIndicatorsFromConfiguration(List<HashMap<String, String>> indicators){
        TraderController<Indicator> addIndicatorController = new AddIndicatorController<>(requestBuilder, useCaseFactory, priceObservable, configuration);
        for (HashMap<String, String> indicator :indicators)
            addIndicatorController.execute(indicator);
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
        TraderController<BrokerGateway> controller =new AddBrokerConnectorController<>(requestBuilder, useCaseFactory);
        Response<BrokerGateway> brokerResponse = controller.execute(settings);
        BrokerConnector connector = (BrokerConnector) brokerResponse.getResponseDataStructure();
        return BaseGateway.create(brokerName, connector);
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
