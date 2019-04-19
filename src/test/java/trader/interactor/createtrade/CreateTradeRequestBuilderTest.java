package trader.interactor.createtrade;

import org.junit.Before;
import org.junit.Test;
import trader.exception.NullArgumentException;
import trader.requestor.Request;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class CreateTradeRequestBuilderTest {

    private CreateTradeRequestBuilder requestBuilder;

    @Before
    public void setUp() throws Exception {
        requestBuilder = new CreateTradeRequestBuilder();
    }

    @Test(expected = NullArgumentException.class)
    public void givenNullSettings_WhenCallBuild_ThenException(){
        requestBuilder.build(null);
    }


    @Test
    public void givenValidSettings_WhenCallBuild_ThenReturnCorrectRequest(){
        Map<String, String> tradeSettings = new HashMap<>();
        Map<String, Object> inputSettings = new HashMap<>();
        inputSettings.put("settings", tradeSettings);
        Request<Map<String, Object>> request = requestBuilder.build(inputSettings);

        assertEquals(inputSettings, request.getBody());
    }

}
