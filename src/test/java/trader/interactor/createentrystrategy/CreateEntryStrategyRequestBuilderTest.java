package trader.interactor.createentrystrategy;

import org.junit.Before;
import org.junit.Test;
import trader.interactor.BaseRequestBuilderTest;
import trader.requestor.Request;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;


public class CreateEntryStrategyRequestBuilderTest extends BaseRequestBuilderTest {

    private CreateEntryStrategyRequestBuilder requestBuilder;

    @Before
    public void setUp() throws Exception {
        requestBuilder = new CreateEntryStrategyRequestBuilder();
        super.setRequestBuilder(requestBuilder);
    }

    @Test
    public void givenValidSettings_WhenCallBuild_ThenReturnCorrectRequest(){
        Map<String, Object> inputSettings = new HashMap<>();
        Map<String, String> settings = new HashMap<>();
        settings.put("entryStrategy", "standard");
        inputSettings.put("settings", settings);
        Request<?> request = requestBuilder.build(inputSettings);

        assertEquals(inputSettings, request.getBody());
    }

}
