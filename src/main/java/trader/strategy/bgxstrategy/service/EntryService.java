package trader.strategy.bgxstrategy.service;

import trader.controller.CreateEntryStrategyController;
import trader.controller.CreateTradeController;
import trader.controller.TraderController;
import trader.entity.indicator.Indicator;
import trader.entry.EntryStrategy;
import trader.exception.NullArgumentException;
import trader.presenter.Presenter;
import trader.requestor.UseCaseFactory;
import trader.responder.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntryService {

    private Presenter presenter;
    private UseCaseFactory useCaseFactory;

    public EntryService(UseCaseFactory useCaseFactory, Presenter presenter) {
        this.useCaseFactory = useCaseFactory;
        this.presenter = presenter;
    }

    public EntryStrategy createEntryStrategy(String entryStrategyName, List<Indicator> indicators) {
        if(entryStrategyName == null || indicators == null)
            throw new NullArgumentException();
        Map<String, Object> inputSettings = new HashMap<>();
        Map<String, String> settings = new HashMap<>();
        settings.put("entryStrategy", entryStrategyName);
        inputSettings.put("settings", settings);
        TraderController<EntryStrategy> controller = new CreateEntryStrategyController<>(useCaseFactory, presenter);
        Response<EntryStrategy> entryResponse = controller.execute(inputSettings);
        EntryStrategy strategy = entryResponse.getBody();
        strategy.setIndicators(indicators);
        strategy.setCreateTradeController(new CreateTradeController<>(useCaseFactory, presenter));
        return strategy;
    }
}
