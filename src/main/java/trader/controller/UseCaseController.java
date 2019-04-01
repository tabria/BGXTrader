package trader.controller;

import trader.responder.Response;

import java.util.HashMap;

public interface UseCaseController {
    <T> Response<T> execute(String indicatorType, HashMap<String, String> settings);
}
