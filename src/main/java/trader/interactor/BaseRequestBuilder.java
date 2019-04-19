package trader.interactor;

import trader.requestor.Request;
import trader.requestor.RequestBuilder;
import trader.validation.Validator;

import java.util.Map;

public abstract class BaseRequestBuilder implements RequestBuilder {

    @Override
    public Request<?> build(Map<String, Object> inputSettings) {
        Validator.validateForNull(inputSettings);
        Request<Map<String, Object>> request = new RequestImpl<>();
        request.setBody(inputSettings);
        return request;
    }
}