package trader.controller;

import trader.responder.Response;
import java.util.HashMap;

public interface TraderController<T> {

    Response<T> execute(HashMap<String, String> settings);

//    default Response<T> execute(Map<String, Object> settings) { throw new NotImplementedException(); }
}
