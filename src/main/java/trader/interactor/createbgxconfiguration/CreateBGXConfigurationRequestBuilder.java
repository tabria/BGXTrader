package trader.interactor.createbgxconfiguration;

import trader.interactor.BaseRequestBuilder;
import trader.interactor.RequestImpl;
import trader.requestor.Request;
import trader.validation.Validator;

import java.util.Map;

public class CreateBGXConfigurationRequestBuilder extends BaseRequestBuilder {

    @Override
    public  Request<?> build(Map<String, Object> inputSettings) {
        Validator.validateForNull(inputSettings);
        Map<String, Map<String, String>> outputSettings = (Map<String, Map<String, String>>) inputSettings.get("settings");
        Request<Map<String, Map<String, String>>> request = new RequestImpl<>();
        request.setBody(outputSettings);
        return request;
    }
}
