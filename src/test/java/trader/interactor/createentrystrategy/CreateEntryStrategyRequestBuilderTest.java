package trader.interactor.createentrystrategy;

import org.junit.Before;
import org.junit.Test;
import trader.exception.NullArgumentException;
import trader.requestor.Request;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;


public class CreateEntryStrategyRequestBuilderTest {

    private CreateEntryStrategyRequestBuilder requestBuilder;

    @Before
    public void setUp() throws Exception {
        requestBuilder = new CreateEntryStrategyRequestBuilder();
    }

    @Test(expected = NullArgumentException.class)
    public void givenNullSettings_WhenCallBuild_ThenException(){
        requestBuilder.build(null);
    }


    @Test
    public void givenValidSettings_WhenCallBuild_ThenReturnCorrectRequest(){
        Map<String, Object> inputSettings = new HashMap<>();
        Map<String, String> settings = new HashMap<>();
        settings.put("entryStrategy", "standard");
        inputSettings.put("settings", settings);
        Request<Map<String, Object>> request = requestBuilder.build(inputSettings);

        assertEquals(inputSettings, request.getBody());
    }

}
