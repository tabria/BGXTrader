package trader.controller;

import trader.exception.NotImplementedException;
import trader.responder.Response;

import java.util.HashMap;
import java.util.Map;

public interface TraderController<T> {

    default Response<T> execute(HashMap<String, String> settings){throw new NotImplementedException();}

    default Response<T> execute(Map<String, Object> settings) { throw new NotImplementedException(); }
}
