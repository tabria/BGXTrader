package trader.controller;

import trader.responder.Response;
import java.util.Map;

public interface TraderController<T> {


    Response<T> execute(Map<String, Object> settings);
}
