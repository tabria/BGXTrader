package trader.interactor.createbgxconfiguration;

import org.junit.Before;
import org.junit.Test;
import trader.interactor.BaseRequestBuilderTest;
import trader.requestor.Request;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class CreateBGXConfigurationRequestBuilderTest extends BaseRequestBuilderTest {

    private CreateBGXConfigurationRequestBuilder requestBuilder;

    @Before
    public void setUp(){
        requestBuilder = new CreateBGXConfigurationRequestBuilder();
        super.setRequestBuilder(requestBuilder);
    }

    @Test
    public void givenValidSettings_WhenCallBuild_ThenReturnCorrectRequest(){
        Map<String, Object> inputSettings = new HashMap<>();
        Map<String, Map<String, String>> outputSettings = new HashMap<>();
        inputSettings.put("settings", outputSettings);
        Request<?> request = requestBuilder.build(inputSettings);

        assertEquals(outputSettings, request.getBody());
    }

}
