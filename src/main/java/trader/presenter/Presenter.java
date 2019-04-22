package trader.presenter;

import trader.responder.Response;

import java.util.Map;

public interface Presenter {
    void execute(Response response);
    void execute(String... args);
}
