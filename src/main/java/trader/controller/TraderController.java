package trader.controller;

import trader.responder.Response;

import java.util.HashMap;

public interface TraderController<T> {
    Response<T> execute(HashMap<String, String> settings);
}
