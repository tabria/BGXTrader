package trader.interactor.addbgxconfiguration;

import org.junit.Before;
import org.junit.Test;
import trader.exception.NullArgumentException;
import trader.requestor.Request;

import java.util.HashMap;
import java.util.Map;

public class AddBGXConfigurationRequestTest {

    private AddBGXConfigurationRequest builder;

    @Before
    public void setUp(){

        builder = new AddBGXConfigurationRequest();

    }

    @Test(expected = NullArgumentException.class)
    public void givenNullSettings_WhenCallBuild_ThenException(){
        builder.make(null);
    }

    @Test
    public void givenSettings_WhenCallBuild_ThenReturnCorrectObject(){
        Map<String, Object> settingsDto = new HashMap<>();
        Map<String, Map<String, String>> fileSettings = new HashMap<>();
        settingsDto.put("settings", fileSettings);
        Request request = builder.make(settingsDto);
    }

}
