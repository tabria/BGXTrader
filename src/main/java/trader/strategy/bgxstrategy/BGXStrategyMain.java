package trader.strategy.bgxstrategy;

import trader.connector.ApiConnector;
import trader.controller.AddIndicatorController;
import trader.controller.BGXConfigurationController;
import trader.interactor.RequestBuilderImpl;
import trader.interactor.UseCaseFactoryImpl;
import trader.requestor.RequestBuilder;
import trader.requestor.UseCaseFactory;
import trader.strategy.Observable;
import trader.exception.NullArgumentException;
import trader.order.OrderStrategy;
import trader.strategy.bgxstrategy.configuration.BGXConfiguration;
import trader.strategy.observable.PriceObservable;
import trader.strategy.Strategy;
import trader.order.OrderService;
import trader.exit.exit_strategie.BaseExitStrategy;
import trader.exit.ExitStrategy;

import java.util.HashMap;

import static trader.Main.BGX_STRATEGY_CONFIG_FILE_NAME;

public final class BGXStrategyMain implements Strategy {


    private BGXConfiguration configuration;//not tested
    private ApiConnector apiConnector;
    private Observable priceObservable;
    private RequestBuilder requestBuilder;
    private UseCaseFactory useCaseFactory;

    private ExitStrategy exitStrategy;
    private OrderStrategy orderStrategy;


    public BGXStrategyMain(ApiConnector connector) {
        this(connector, BGX_STRATEGY_CONFIG_FILE_NAME);
    }

    public BGXStrategyMain(ApiConnector connector, String configurationFileName) {
        requestBuilder = new RequestBuilderImpl();
        useCaseFactory = new UseCaseFactoryImpl();
        configuration = setConfiguration(configurationFileName);
        setApiConnector(connector);
        priceObservable = PriceObservable.create(apiConnector);


        orderStrategy = new OrderService(apiConnector);
        exitStrategy = BaseExitStrategy.createInstance();
     //   init();
    }

    private BGXConfiguration setConfiguration(String configurationFileName) {
        BGXConfigurationController configurationController = new BGXConfigurationController(requestBuilder, useCaseFactory);
        String controllerName = configurationController.getClass().getSimpleName();
        HashMap<String, String> settings = new HashMap<>();
        settings.put("location", configurationFileName);
        configurationController.execute(controllerName, settings);
        return null; //new BGXConfigurationImpl();
    }


    @Override
    public void execute() {
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

    void addIndicators(String indicatorType, HashMap<String, String> settings){
        AddIndicatorController addIndicatorController = new AddIndicatorController(requestBuilder, useCaseFactory);
        addIndicatorController.execute(indicatorType, settings, priceObservable);
    }

    BGXConfiguration getConfiguration() {
        return configuration;
    }

    private void setApiConnector(ApiConnector connector) {
        if(connector == null){
            throw new NullArgumentException();
        }
        this.apiConnector = connector;
    }


    private boolean haveOpenOrders() {
        return apiConnector.getOpenOrders().size() > 0;
    }

    private boolean haveOpenTrades() {
        return apiConnector.getOpenTrades().size()>0;
    }

}
