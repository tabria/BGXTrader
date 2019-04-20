package trader.interactor.createorderstrategy;

import trader.exception.NoSuchStrategyException;
import trader.exception.NullArgumentException;
import trader.interactor.ResponseImpl;
import trader.order.OrderStrategy;
import trader.presenter.Presenter;
import trader.requestor.Request;
import trader.requestor.UseCase;
import trader.responder.Response;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class CreateOrderStrategyUseCase implements UseCase {

    private Presenter presenter;

    public CreateOrderStrategyUseCase(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public <T, E> Response<E> execute(Request<T> request) {
        Map<String, Object> settings = (Map<String, Object>) request.getBody();
        OrderStrategy strategy = setOrderStrategy(settings);
        Response<E> response = setResponse((E) strategy);
        presenter.execute(response);
        return response;
    }

    private OrderStrategy setOrderStrategy(Map<String,Object> inputSettings) {
        Map<String, String> settings = (Map<String, String>) inputSettings.get("settings");
        try {
            Class<?> orderStrategyClass = Class.forName(composeName(settings));
            Constructor<?> orderStrategyConstructor = orderStrategyClass.getDeclaredConstructor();
            return (OrderStrategy) orderStrategyConstructor.newInstance();
        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException | StringIndexOutOfBoundsException e) {
            throw new NoSuchStrategyException();
        } catch (Exception e) {
            throw new NullArgumentException();
        }
    }

    private <E> Response<E> setResponse(E orderStrategy) {
        Response<E> response = new ResponseImpl<>();
        response.setBody(orderStrategy);
        return response;
    }

    private String composeName(Map<String, String> settings){
        String className = settings.get("orderStrategy").trim();
        className = Character.toUpperCase(className.charAt(0)) + className.substring(1);
        return String.format("%s.%s.%s.%s%s", "trader", "order", className.toLowerCase(), className, "OrderStrategy");
    }
}
