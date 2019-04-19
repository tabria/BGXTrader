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
import trader.strategy.bgxstrategy.service.*;
import trader.strategy.observable.PriceObservable;
import trader.strategy.Strategy;
import trader.exit.ExitStrategy;
import trader.strategy.observable.PricePull;
import trader.validation.Validator;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public final class BGXStrategyMain implements Strategy {

    private static final String LOCATION = "location";
    private static final String BROKER_NAME = "brokerName";
    private static final String ENTRY_STRATEGY_KEY_NAME = "entryStrategy";
    private static final String ORDER_STRATEGY_KEY_NAME = "orderStrategy";
    private static final String EXIT_STRATEGY_KEY_NAME = "exitStrategy";

//    // to be removed
//    private RequestOLDBuilder requestOLDBuilder;
//    // to be removed
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
//        // to be removed
//        requestOLDBuilder = new RequestBuilderImpl();
//        // to be removed

        Validator.validateStrings(brokerName, configurationFileName, brokerConfigurationFileName);
        requestBuilderCreator = new RequestBuilderCreator();
        useCaseFactory = new UseCaseFactoryImpl();
        configuration = setConfiguration(configurationFileName);
        brokerGateway = setBrokerGateway(brokerName, brokerConfigurationFileName);
        indicatorList = setIndicators(configuration.getIndicators());
        priceObservable = PriceObservable.create(brokerGateway, configuration);
        entryStrategy = setEntryStrategy();
        orderStrategy = setOrderStrategy();
        exitStrategy = setExitStrategy();
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

    private BrokerGateway setBrokerGateway(String brokerName, String brokerConfigurationFileName) {
        BrokerService brokerService = new BrokerService(useCaseFactory);
        return brokerService.createBrokerGateway(brokerName, brokerConfigurationFileName);
    }

    private List<Indicator> setIndicators(List<Map<String, String>> indicators){
        IndicatorService indicatorService = new IndicatorService(useCaseFactory);
        return indicatorService.createIndicators(indicators);
    }

    private EntryStrategy setEntryStrategy() {
        EntryService entryService = new EntryService(useCaseFactory);
        return entryService.createEntryStrategy(configuration.getEntryStrategy(), indicatorList);
    }

    private OrderStrategy setOrderStrategy() {
        OrderService orderService = new OrderService(useCaseFactory);
        return orderService.createOrderStrategy(configuration.getOrderStrategy());
    }

    private ExitStrategy setExitStrategy() {
        ExitService exitService = new ExitService(useCaseFactory);
        return exitService.createOrderStrategy(configuration.getExitStrategy());
    }

    void addIndicatorsToObservable(Observable observable, List<Indicator> indicators){
        for (Indicator indicator:indicators) {
            observable.registerObserver(
                    new UpdateIndicatorObserver(indicator, configuration, brokerGateway));
        }
    }

    BrokerGateway getBrokerGateway() {
        return brokerGateway;
    }

    //////////////////////////////////////////////////// not tested/////////
    Observer setPositionObserver(){

       return new PositionObserver(brokerGateway, entryStrategy, orderStrategy, configuration);
    }


///// not tested//////////////





}
