package trader.interactor.addbgxconfiguration;

import trader.requestor.Request;
import trader.validation.Validator;

import java.util.Map;

public class AddBGXConfigurationRequest implements Request {

    @SuppressWarnings("unchecked")
    public Request make(Map<String, Object> settingsDto) {
        Validator.validateForNull(settingsDto);
        Map<String, Map<String, String>> settings = (Map<String, Map<String, String>>) settingsDto.get("settings");

        return null;
    }

    @Override
    public Object getbody() {
        return null;
    }

    @Override
    public void setRequestDataStructure(Object dataStructure) {

    }
}
