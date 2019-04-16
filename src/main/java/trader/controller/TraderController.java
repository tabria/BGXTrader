package trader.controller;

import trader.responder.Response;
import java.util.HashMap;
import java.util.Map;

public interface TraderController<T> {

    ////////////// to be removed//////////////////
  //  default Response<T> execute(HashMap<String, String> settings){return null;};
    ////////////to be removed///////////////////////////////////////////

    default Response<T> execute(Map<String, Object> settings) {return null;}
}
