package trader.interactor.addbgxconfiguration;

import org.junit.Before;
import org.junit.Test;
import trader.exception.NullArgumentException;
import trader.requestor.Request;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class AddBGXConfigurationRequestTest {

    private AddBGXConfigurationRequestBuilder requestBuilder;

    @Before
    public void setUp(){
        requestBuilder = new AddBGXConfigurationRequestBuilder();
    }

    @Test(expected = NullArgumentException.class)
    public void givenNullSettings_WhenCallBuild_ThenException(){
        requestBuilder.build(null);
    }

    @Test
    public void givenValidSettings_WhenCallBuild_ThenReturnCorrectRequest(){
        Map<String, Object> inputSettings = new HashMap<>();
        Map<String, Map<String, String>> outputSettings = new HashMap<>();
        inputSettings.put("settings", outputSettings);
        Request<Map<String, Map<String, String>>> request = requestBuilder.build(inputSettings);

        assertEquals(outputSettings, request.getBody());
    }

}
