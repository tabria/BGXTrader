package trader.interactor.createexitstrategy;

import trader.exception.NoSuchStrategyException;
import trader.exception.NullArgumentException;
import trader.exit.ExitStrategy;
import trader.interactor.ResponseImpl;
import trader.presenter.Presenter;
import trader.requestor.Request;
import trader.requestor.UseCase;
import trader.responder.Response;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class CreateExitStrategyUseCase implements UseCase {

    private Presenter presenter;

    public CreateExitStrategyUseCase(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public <T, E> Response<E> execute(Request<T> request) {
        Map<String, Object> settings = (Map<String, Object>) request.getBody();
        ExitStrategy strategy = setExitStrategy(settings);
        Response<E> response = setResponse((E) strategy);
        presenter.execute(response);
        return response;
    }

    private ExitStrategy setExitStrategy(Map<String,Object> inputSettings) {
        Map<String, String> settings = (Map<String, String>) inputSettings.get("settings");
        try {
            Class<?> exitStrategyClass = Class.forName(composeName(settings));
            Constructor<?> orderStrategyConstructor = exitStrategyClass.getDeclaredConstructor();
            return (ExitStrategy) orderStrategyConstructor.newInstance();
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
        String className = settings.get("exitStrategy").trim();
        className = Character.toUpperCase(className.charAt(0)) + className.substring(1);
        return String.format("%s.%s.%s.%s%s", "trader", "exit", className.toLowerCase(), className, "ExitStrategy");
    }

}
