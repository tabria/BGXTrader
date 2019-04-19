package trader.strategy.bgxstrategy.service;

import trader.controller.CreateExitStrategyController;
import trader.controller.TraderController;
import trader.exception.NullArgumentException;
import trader.exit.ExitStrategy;
import trader.requestor.UseCaseFactory;
import trader.responder.Response;

import java.util.HashMap;
import java.util.Map;

public class ExitService {

    private UseCaseFactory useCaseFactory;

    public ExitService(UseCaseFactory useCaseFactory) {
        this.useCaseFactory = useCaseFactory;
    }

    public ExitStrategy createOrderStrategy(String exitStrategyName) {
        if(exitStrategyName == null)
            throw new NullArgumentException();
        TraderController<ExitStrategy> controller = new CreateExitStrategyController<>(useCaseFactory);
        Map<String, Object> inputSettings = new HashMap<>();
        Map<String, String> settings = new HashMap<>();
        settings.put("exitStrategy", exitStrategyName);
        inputSettings.put("settings", settings);
        Response<ExitStrategy> orderResponse = controller.execute(inputSettings);
        return orderResponse.getBody();
    }
}
