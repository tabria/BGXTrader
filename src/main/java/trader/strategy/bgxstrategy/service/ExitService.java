package trader.strategy.bgxstrategy.service;

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

    public ExitService(UseCaseFactory useCaseFactory, Presenter presenter) {
        this.useCaseFactory = useCaseFactory;
        this.presenter = presenter;
    }

    public ExitStrategy createExitStrategy(String exitStrategyName) {
        if(exitStrategyName == null)
            throw new NullArgumentException();
        TraderController<ExitStrategy> controller = new CreateExitStrategyController<>(useCaseFactory, presenter);
        Map<String, Object> inputSettings = new HashMap<>();
        Map<String, String> settings = new HashMap<>();
        settings.put("exitStrategy", exitStrategyName);
        inputSettings.put("settings", settings);
        Response<ExitStrategy> orderResponse = controller.execute(inputSettings);
        return orderResponse.getBody();
    }
}
