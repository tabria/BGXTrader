package trader.requestor;

import java.util.HashMap;

public interface RequestBuilder {

    Request<?> build(String indicatorType, HashMap<String, String> settings);

}