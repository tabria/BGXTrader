package trader.requestor;

import java.util.Map;

public interface RequestBuilder {

    Request build(Map<String, Object> settings);

}
