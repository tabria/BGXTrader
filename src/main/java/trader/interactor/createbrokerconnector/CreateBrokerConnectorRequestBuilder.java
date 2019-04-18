package trader.interactor.createbrokerconnector;

import trader.interactor.RequestImpl;
import trader.requestor.Request;
import trader.requestor.RequestBuilder;
import trader.validation.Validator;

import java.util.Map;

public class CreateBrokerConnectorRequestBuilder implements RequestBuilder {

    @SuppressWarnings("unchecked")
    @Override
    public Request<Map<String, Object>> build(Map<String, Object> inputSettings) {
        Validator.validateForNull(inputSettings);
        Request<Map<String, Object>> request = new RequestImpl<>();
        request.setBody(inputSettings);
        return request;
    }

}
