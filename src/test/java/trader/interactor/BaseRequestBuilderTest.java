package trader.interactor;

import org.junit.Test;
import trader.exception.NullArgumentException;
import trader.requestor.Request;
import trader.requestor.RequestBuilder;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;


public abstract class BaseRequestBuilderTest {


    private RequestBuilder requestBuilder;

    public void setRequestBuilder(RequestBuilder builder){
        requestBuilder = builder;
    }

    @Test(expected = NullArgumentException.class)
    public void givenNullSettings_WhenCallBuild_ThenException(){
        requestBuilder.build(null);
    }

    @Test
    public void givenValidSettings_WhenCallBuild_ThenReturnCorrectRequest(){
        Map<String, String> indicatorSettings = new HashMap<>();
        Map<String, Object> inputSettings = new HashMap<>();
        inputSettings.put("settings", indicatorSettings);
        Request<?> request = requestBuilder.build(inputSettings);

        assertEquals(inputSettings, request.getBody());
    }
}
