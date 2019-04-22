package trader.strategy.bgxstrategy.service;

import trader.broker.BrokerGateway;
import trader.configuration.TradingStrategyConfiguration;
import trader.controller.CreateExitStrategyController;
import trader.controller.TraderController;
import trader.exception.NullArgumentException;
import trader.exit.ExitStrategy;
import trader.presenter.Presenter;
import trader.requestor.UseCaseFactory;
import trader.responder.Response;

import java.util.HashMap;
import java.util.Map;

public class ExitService {

    private UseCaseFactory useCaseFactory;
    private Presenter presenter;
    private BrokerGateway brokerGateway;
    private TradingStrategyConfiguration configuration;

    public ExitService(UseCaseFactory useCaseFactory, Presenter presenter, BrokerGateway brokerGateway, TradingStrategyConfiguration configuration) {
        this.useCaseFactory = useCaseFactory;
        this.presenter = presenter;
        this.brokerGateway = brokerGateway;
        this.configuration = configuration;
    }

    public ExitStrategy createExitStrategy(String exitStrategyName) {
        if(exitStrategyName == null)
            throw new NullArgumentException();
        TraderController<ExitStrategy> controller = new CreateExitStrategyController<>(useCaseFactory, presenter);
        Map<String, Object> inputSettings = new HashMap<>();
        Map<String, String> settings = new HashMap<>();
        settings.put("exitStrategy", exitStrategyName);
        inputSettings.put("settings", settings);
        Response<ExitStrategy> exitStrategyResponse = controller.execute(inputSettings);
        ExitStrategy strategy = exitStrategyResponse.getBody();
        strategy.setPresenter(presenter);
        strategy.setBrokerGateway(brokerGateway);
        strategy.setConfiguration(configuration);
        return strategy;
    }
}
