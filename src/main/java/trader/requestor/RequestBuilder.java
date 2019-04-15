package trader.requestor;

import java.util.HashMap;


public interface RequestBuilder {

  Request<?> build(String controllerName, HashMap<String, String> settings);


}
