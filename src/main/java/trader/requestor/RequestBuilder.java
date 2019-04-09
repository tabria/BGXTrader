package trader.requestor;

import trader.exception.NotImplementedException;

import java.util.HashMap;
import java.util.Map;

public interface RequestBuilder {

  default Request<?> build(String controllerName, HashMap<String, String> settings){throw new NotImplementedException();};

//    default Request<?> build(String controllerName, Map<String, Object> settings){throw new NotImplementedException();};

}
