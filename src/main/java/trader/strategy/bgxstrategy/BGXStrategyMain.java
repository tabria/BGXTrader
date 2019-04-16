package trader.strategy.bgxstrategy;

import trader.broker.BrokerGateway;
import trader.controller.TraderController;
import trader.controller.*;
import trader.entity.indicator.Indicator;
import trader.entry.EntryStrategy;
import trader.observer.Observer;
import trader.observer.PositionObserver;
import trader.observer.UpdateIndicatorObserver;
import trader.requestor.*;
import trader.strategy.Observable;
import trader.order.OrderStrategy;
import trader.configuration.TradingStrategyConfiguration;
import trader.strategy.bgxstrategy.service.ConfigurationService;
import trader.strategy.observable.PriceObservable;
import trader.strategy.Strategy;
import trader.exit.ExitStrategy;
import trader.strategy.observable.PricePull;
import trader.validation.Validator;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public final class BGXStrategyMain implements Strategy {

    private static final String LOCATION = "location";
    private static final String BROKER_NAME = "brokerName";
    private static final String ENTRY_STRATEGY_KEY_NAME = "entryStrategy";
    private static final String ORDER_STRATEGY_KEY_NAME = "orderStrategy";
    private static final String EXIT_STRATEGY_KEY_NAME = "exitStrategy";

    // to be removed
    private RequestOLDBuilder requestOLDBuilder;
    // to be removed
    private final RequestBuilderCreator requestBuilderCreator;
    private UseCaseFactory useCaseFactory;
    private List<Indicator> indicatorList;
    private TradingStrategyConfiguration configuration;
    private BrokerGateway brokerGateway;
    private Observable priceObservable;
    private EntryStrategy entryStrategy;
    private OrderStrategy orderStrategy;

    private ExitStrategy exitStrategy;

    private Observer positionObserver;



    public BGXStrategyMain( String brokerName, String configurationFileName, String brokerConfigurationFileName ) {
        // to be removed
        requestOLDBuilder = new RequestBuilderImpl();
        // to be removed

        Validator.validateStrings(brokerName, configurationFileName, brokerConfigurationFileName);
        requestBuilderCreator = new RequestBuilderCreator();
        useCaseFactory = new UseCaseFactoryImpl();

        configuration = setConfiguration(configurationFileName);

        brokerGateway = setBrokerGateway(brokerName, brokerConfigurationFileName);
        priceObservable = PriceObservable.create(brokerGateway, configuration);
        indicatorList = createIndicatorsFromConfiguration(configuration.getIndicators());
        entryStrategy = setEntryStrategy();
        orderStrategy = setOrderStrategy();
   //     exitStrategy = setExitStrategy();


       // exitStrategy = ServiceExitStrategy.createInstance();
    }


    @Override
    public void execute() {
        brokerGateway.validateConnector();
        addIndicatorsToObservable(priceObservable, indicatorList);
        new PricePull("PricePull", priceObservable);

    }

    @Override
    public String toString() {
        return "bgxstrategy";
    }

    TradingStrategyConfiguration getConfiguration() {
        return configuration;
    }

    private TradingStrategyConfiguration setConfiguration(String configurationFileName) {
        ConfigurationService configService = new ConfigurationService(useCaseFactory);
        return configService.createConfiguration(configurationFileName);
    }

    BrokerGateway getBrokerGateway() {
        return brokerGateway;
    }

    List<Indicator> createIndicatorsFromConfiguration(List<HashMap<String, String>> indicators){
        TraderController<Indicator> addIndicatorController = new CreateIndicatorController<>(requestOLDBuilder, useCaseFactory);
        List<Indicator> indicatorList = new ArrayList<>();
        for (HashMap<String, String> indicator :indicators) {
//            Response<Indicator> indicatorResponse = addIndicatorController.execute(indicator);
//            indicatorList.add(indicatorResponse.getResponseDataStructure());
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

       return new PositionObserver(brokerGateway, entryStrategy, orderStrategy, configuration);
    }


///// not tested//////////////





    private ExitStrategy setExitStrategy() {
//        HashMap<String, String> settings = new HashMap<>();
//        settings.put(EXIT_STRATEGY_KEY_NAME, configuration.getExitStrategy());
//        TraderController<ExitStrategy> controller = new AddExitStrategyController<>(requestOLDBuilder, useCaseFactory);
//        Response<ExitStrategy> exitResponse = controller.execute(settings);
//        ExitStrategy exitStrategy = exitResponse.getResponseDataStructure();
        return null; // exitStrategy;
    }

    private OrderStrategy setOrderStrategy() {
//        HashMap<String, String> settings = new HashMap<>();
//        settings.put(ORDER_STRATEGY_KEY_NAME, configuration.getOrderStrategy());
//        TraderController<OrderStrategy> controller = new AddOrderStrategyController<>(requestOLDBuilder, useCaseFactory);
//        Response<OrderStrategy> orderResponse = controller.execute(settings);
        return null; // orderResponse.getResponseDataStructure();
    }

    private EntryStrategy setEntryStrategy() {
//        HashMap<String, String> settings = new HashMap<>();
//        settings.put(ENTRY_STRATEGY_KEY_NAME, configuration.getEntryStrategy());
//        TraderController<EntryStrategy> controller = new AddEntryStrategyController<>(requestOLDBuilder, useCaseFactory);
//        Response<EntryStrategy> entryResponse = controller.execute(settings);
//        EntryStrategy entryStrategy = entryResponse.getResponseDataStructure();
//        entryStrategy.setIndicators(indicatorList);
//        entryStrategy.setCreateTradeController(new CreateTradeController<>(requestOLDBuilder, useCaseFactory));
        return null; //entryStrategy;
    }

    private BrokerGateway setBrokerGateway(String brokerName, String brokerConfigurationFileName) {
//        HashMap<String, String> settings = new HashMap<>();
//        settings.put(BROKER_NAME, brokerName);
//        settings.put(LOCATION, brokerConfigurationFileName);
//        TraderController<BrokerGateway> controller = new AddBrokerConnectorController<>(requestOLDBuilder, useCaseFactory);
//        Response<BrokerGateway> brokerResponse = controller.execute(settings);
//        BrokerConnector connector = (BrokerConnector) brokerResponse.getResponseDataStructure();
//        BrokerGateway brokerGateway = BaseGateway.create(brokerName, connector);
//      //  brokerGateway.validateConnector();
        return null; //brokerGateway;
    }

}
