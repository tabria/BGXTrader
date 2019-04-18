package trader.strategy.bgxstrategy.service;

import trader.controller.CreateIndicatorController;
import trader.controller.TraderController;
import trader.entity.indicator.Indicator;
import trader.exception.EmptyArgumentException;
import trader.requestor.UseCaseFactory;
import trader.responder.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IndicatorService {

    private static final int MINIMUM_INDICATORS_QUANTITY = 6;

    private UseCaseFactory useCaseFactory;

    public IndicatorService(UseCaseFactory useCaseFactory) {
        this.useCaseFactory = useCaseFactory;
    }

    public List<Indicator> createIndicators(List<Map<String,String>> indicators) {
        if(indicators.size()< MINIMUM_INDICATORS_QUANTITY)
            throw new EmptyArgumentException();
        List<Indicator> indicatorList = new ArrayList<>();
        TraderController<Indicator> createIndicatorController = new CreateIndicatorController<>(useCaseFactory);
        Map<String, Object> transportMap = new HashMap<>();
        for (Map<String, String> indicator :indicators) {
            transportMap.clear();
            transportMap.put("settings", indicator);
            Response<Indicator> indicatorResponse = createIndicatorController.execute(transportMap);
            indicatorList.add(indicatorResponse.getBody());
        }

        return indicatorList;
    }
}
