package trader.interactor.createentrystrategy;

import trader.entry.EntryStrategy;
import trader.exception.NoSuchStrategyException;
import trader.exception.NullArgumentException;
import trader.interactor.ResponseImpl;
import trader.presenter.Presenter;
import trader.requestor.Request;
import trader.requestor.UseCase;
import trader.responder.Response;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class CreateEntryStrategyUseCase implements UseCase {

    private Presenter presenter;

    public CreateEntryStrategyUseCase(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public <T, E> Response<E> execute(Request<T> request) {
        Map<String, Object> settings = (Map<String, Object>) request.getBody();
        EntryStrategy strategy = setEntryStrategy(settings);
        Response<E> response = setResponse((E) strategy);
        presenter.execute(response);
        return response;
    }

    private EntryStrategy setEntryStrategy(Map<String, Object> inputSettings) {
        Map<String, String> settings = (Map<String, String>) inputSettings.get("settings");
        try {
            Class<?> entryStrategyClass = Class.forName(composeName(settings));
            Constructor<?> entryStrategyConstructor = entryStrategyClass.getDeclaredConstructor();
            return (EntryStrategy) entryStrategyConstructor.newInstance();
        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException | StringIndexOutOfBoundsException e) {
            throw new NoSuchStrategyException();
        } catch (Exception e) {
            throw new NullArgumentException();
        }
    }

    private <E> Response<E> setResponse(E entryStrategy) {
        Response<E> response = new ResponseImpl<>();
        response.setBody(entryStrategy);
        return response;
    }

    private String composeName(Map<String, String> settings){
        String className = settings.get("entryStrategy").trim();
        className = Character.toUpperCase(className.charAt(0)) + className.substring(1);
        return String.format("%s.%s.%s.%s%s", "trader", "entry", className.toLowerCase(), className, "EntryStrategy");
    }
}
