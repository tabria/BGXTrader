package trader.strategy.bgxstrategy.service;

import trader.controller.CreateOrderStrategyController;
import trader.controller.TraderController;
import trader.exception.NullArgumentException;
import trader.order.OrderStrategy;
import trader.presenter.Presenter;
import trader.requestor.UseCaseFactory;
import trader.responder.Response;

import java.util.HashMap;
import java.util.Map;

public class OrderService {


    private UseCaseFactory useCaseFactory;
    private Presenter presenter;

    public OrderService(UseCaseFactory useCaseFactory, Presenter presenter){
        this.useCaseFactory = useCaseFactory;
        this.presenter = presenter;
    }


    public OrderStrategy createOrderStrategy(String orderStrategyName) {
        if(orderStrategyName == null)
            throw new NullArgumentException();
        TraderController<OrderStrategy> controller = new CreateOrderStrategyController<>(useCaseFactory, presenter);
        Map<String, Object> inputSettings = new HashMap<>();
        Map<String, String> settings = new HashMap<>();
        settings.put("orderStrategy", orderStrategyName);
        inputSettings.put("settings", settings);
        Response<OrderStrategy> orderResponse = controller.execute(inputSettings);
        return orderResponse.getBody();
    }
}
