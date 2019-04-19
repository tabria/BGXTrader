package trader.interactor.createbrokerconnector;

import org.junit.Before;
import org.junit.Test;
import trader.exception.NullArgumentException;
import trader.interactor.BaseRequestBuilder;
import trader.interactor.BaseRequestBuilderTest;
import trader.requestor.Request;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class CreateBrokerConnectorRequestBuilderTest extends BaseRequestBuilderTest {


    private CreateBrokerConnectorRequestBuilder requestBuilder;

    @Before
    public void setUp(){
        requestBuilder = new CreateBrokerConnectorRequestBuilder();
        super.setRequestBuilder(requestBuilder);
    }

    @Test
    public void givenValidSettings_WhenCallBuild_ThenReturnCorrectRequest(){
        Map<String, Object> inputSettings = new HashMap<>();
        inputSettings.put("brokerName", "Oanda");
        inputSettings.put("settings", new HashMap<>());
        Request<?> request = requestBuilder.build(inputSettings);

        assertEquals(inputSettings, request.getBody());
    }

}
