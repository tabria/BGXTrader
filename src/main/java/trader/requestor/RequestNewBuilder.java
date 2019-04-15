package trader.requestor;

import java.util.Map;

public interface RequestNewBuilder {

    Request<?> build(String controllerName, Map<String, Object> settings);

}
