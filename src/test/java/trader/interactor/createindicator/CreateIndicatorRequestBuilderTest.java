package trader.interactor.createindicator;

import org.junit.Before;
import org.junit.Test;
import trader.exception.NullArgumentException;
import trader.requestor.Request;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class CreateIndicatorRequestBuilderTest {


    private CreateIndicatorRequestBuilder requestBuilder;

    @Before
    public void setUp() throws Exception {
        requestBuilder = new CreateIndicatorRequestBuilder();
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
        Request<Map<String, Object>> request = requestBuilder.build(inputSettings);

        assertEquals(inputSettings, request.getBody());
    }

}
