package trader.strategy.bgxstrategy;

import trader.broker.BrokerGateway;
import trader.connection.Connection;
import trader.entity.indicator.Indicator;
import trader.entry.EntryStrategy;
import trader.observer.Observer;
import trader.observer.PositionObserver;
import trader.observer.UpdateIndicatorObserver;
import trader.presenter.ConsolePresenter;
import trader.presenter.Presenter;
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

import java.util.List;
import java.util.Map;


public final class BGXStrategyMain implements Strategy {

    private Presenter presenter;
    private UseCaseFactory useCaseFactory;
    private List<Indicator> indicatorList;
    private TradingStrategyConfiguration configuration;
    private BrokerGateway brokerGateway;
    private Observable priceObservable;
    private EntryStrategy entryStrategy;
    private OrderStrategy orderStrategy;
    private Observer positionObserver;
    private ExitStrategy exitStrategy;



    public BGXStrategyMain( String brokerName, String configurationFileName, String brokerConfigurationFileName ) {

        Validator.validateStrings(brokerName, configurationFileName, brokerConfigurationFileName);
        useCaseFactory = new UseCaseFactoryImpl();
        presenter = new ConsolePresenter();
        configuration = setConfiguration(configurationFileName);
        brokerGateway = setBrokerGateway(brokerName, brokerConfigurationFileName);
        indicatorList = setIndicators(configuration.getIndicators());
        priceObservable = PriceObservable.create(brokerGateway, configuration);
        entryStrategy = setEntryStrategy();
        orderStrategy = setOrderStrategy();
        exitStrategy = setExitStrategy();
        positionObserver = setPositionObserver(brokerGateway, entryStrategy, orderStrategy, configuration, exitStrategy);
    }


    @Override
    public void execute() {
        Connection.waitToConnect(brokerGateway.getConnector().getUrl(), presenter);
        brokerGateway.validateConnector();
        addIndicatorsToObservable(priceObservable, indicatorList);
        priceObservable.registerObserver(positionObserver);

        new PricePull("PricePull", priceObservable);


    }

    @Override
    public String toString() {
        return "bgxstrategy";
    }

    private TradingStrategyConfiguration setConfiguration(String configurationFileName) {
        ConfigurationService configService = new ConfigurationService(useCaseFactory, presenter);
        return configService.createConfiguration(configurationFileName);
    }

    private BrokerGateway setBrokerGateway(String brokerName, String brokerConfigurationFileName) {
        BrokerService brokerService = new BrokerService(useCaseFactory, presenter);
        return brokerService.createBrokerGateway(brokerName, brokerConfigurationFileName);
    }

    private List<Indicator> setIndicators(List<Map<String, String>> indicators){
        IndicatorService indicatorService = new IndicatorService(useCaseFactory, presenter);
        return indicatorService.createIndicators(indicators);
    }

    private EntryStrategy setEntryStrategy() {
        EntryService entryService = new EntryService(useCaseFactory, presenter);
        return entryService.createEntryStrategy(configuration.getEntryStrategy(), indicatorList);
    }

    private OrderStrategy setOrderStrategy() {
        OrderService orderService = new OrderService(useCaseFactory, presenter);
        return orderService.createOrderStrategy(configuration.getOrderStrategy());
    }

    private ExitStrategy setExitStrategy() {
        ExitService exitService = new ExitService(useCaseFactory, presenter, brokerGateway, configuration);
        return exitService.createExitStrategy(configuration.getExitStrategy());
    }

    void addIndicatorsToObservable(Observable observable, List<Indicator> indicators){
        for (Indicator indicator:indicators) {
            observable.registerObserver(
                    new UpdateIndicatorObserver(indicator, configuration, brokerGateway));
        }
    }


    //////////////////////////////////////////////////// not tested/////////
    Observer setPositionObserver(BrokerGateway brokerGateway,
                                 EntryStrategy entryStrategy,
                                 OrderStrategy orderStrategy,
                                 TradingStrategyConfiguration configuration,
                                 ExitStrategy exitStrategy){

       return new PositionObserver(brokerGateway, entryStrategy, orderStrategy, configuration, exitStrategy);
    }


///// not tested//////////////





}
