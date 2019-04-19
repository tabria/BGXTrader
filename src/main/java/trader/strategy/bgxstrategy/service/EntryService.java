package trader.strategy.bgxstrategy.service;

import trader.controller.CreateEntryStrategyController;
import trader.controller.CreateTradeController;
import trader.controller.TraderController;
import trader.entity.indicator.Indicator;
import trader.entry.EntryStrategy;
import trader.exception.NullArgumentException;
import trader.requestor.UseCaseFactory;
import trader.responder.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntryService {


    private UseCaseFactory useCaseFactory;

    public EntryService(UseCaseFactory useCaseFactory) {
        this.useCaseFactory = useCaseFactory;
    }

    public EntryStrategy createEntryStrategy(String entryStrategyName, List<Indicator> indicators) {
        if(entryStrategyName == null || indicators == null)
            throw new NullArgumentException();
        Map<String, Object> settings = new HashMap<>();
        settings.put("entryStrategy", entryStrategyName);
        TraderController<EntryStrategy> controller = new CreateEntryStrategyController<>(useCaseFactory);
        Response<EntryStrategy> entryResponse = controller.execute(settings);
        EntryStrategy strategy = entryResponse.getBody();
        strategy.setIndicators(indicators);
        strategy.setCreateTradeController(new CreateTradeController<>(useCaseFactory));
        return strategy;
    }
}
