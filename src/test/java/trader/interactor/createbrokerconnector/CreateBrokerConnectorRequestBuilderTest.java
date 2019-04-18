package trader.interactor.createbrokerconnector;

import org.junit.Before;
import org.junit.Test;
import trader.exception.NullArgumentException;
import trader.requestor.Request;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class CreateBrokerConnectorRequestBuilderTest {


    private CreateBrokerConnectorRequestBuilder requestBuilder;

    @Before
    public void setUp(){
        requestBuilder = new CreateBrokerConnectorRequestBuilder();
    }

    @Test(expected = NullArgumentException.class)
    public void givenNullSettings_WhenCallBuild_ThenException(){
        requestBuilder.build(null);
    }

    @Test
    public void givenValidSettings_WhenCallBuild_ThenReturnCorrectRequest(){
        Map<String, Object> inputSettings = new HashMap<>();
        inputSettings.put("brokerName", "Oanda");
        inputSettings.put("settings", new HashMap<>());
        Request<Map<String, Object>> request = requestBuilder.build(inputSettings);

        assertEquals(inputSettings, request.getBody());
    }

}
