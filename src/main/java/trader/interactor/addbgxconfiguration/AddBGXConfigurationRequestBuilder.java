package trader.interactor.addbgxconfiguration;

import trader.interactor.RequestImpl;
import trader.requestor.Request;
import trader.requestor.RequestBuilder;
import trader.validation.Validator;

import java.util.Map;

public class AddBGXConfigurationRequestBuilder implements RequestBuilder {

    @SuppressWarnings("unchecked")
    @Override
    public Request<Map<String, Map<String, String>>> build(Map<String, Object> inputSettings) {
        Validator.validateForNull(inputSettings);
        Map<String, Map<String, String>> outputSettings = (Map<String, Map<String, String>>) inputSettings.get("settings");
        Request<Map<String, Map<String, String>>> request = new RequestImpl<>();
        request.setBody(outputSettings);
        return null;
    }
}
