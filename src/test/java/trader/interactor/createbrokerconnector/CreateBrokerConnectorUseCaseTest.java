package trader.interactor.createbrokerconnector;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import trader.broker.connector.oanda.OandaConnector;
import trader.exception.NoSuchConnectorException;
import trader.requestor.Request;
import trader.responder.Response;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.when;

public class CreateBrokerConnectorUseCaseTest {

    private Request requestMock;
    private CreateBrokerConnectorUseCase createBrokerConnectorUseCase;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        requestMock = mock(Request.class);
        createBrokerConnectorUseCase = new CreateBrokerConnectorUseCase();
    }


    @Test(expected = NoSuchConnectorException.class)
    public void givenNotExistingBrokerName_WhenCallExecute_ThenException(){
        Map<String, Object> wrapper = changeSettings("brokerName", "brokerName", "xddd");
        when(requestMock.getBody()).thenReturn(wrapper);
        createBrokerConnectorUseCase.execute(requestMock);
    }

    @Test(expected = NoSuchConnectorException.class)
    public void givenNonExistingURLKeyName_WhenCallExecute_ThenException(){
        Map<String, Object> wrapper = changeSettings("url", "urr", "ddd");
        when(requestMock.getBody()).thenReturn(wrapper);
        createBrokerConnectorUseCase.execute(requestMock);
    }

    @Test(expected = NoSuchConnectorException.class)
    public void givenNonExistingTokenKeyName_WhenCallExecute_ThenException(){
        Map<String, Object> wrapper = changeSettings("token", "tok", "fff");
        when(requestMock.getBody()).thenReturn(wrapper);
        createBrokerConnectorUseCase.execute(requestMock);
    }

    @Test(expected = NoSuchConnectorException.class)
    public void givenNonExistingIDKeyName_WhenCallExecute_ThenException(){
        Map<String, Object> wrapper = changeSettings("id", "itr", "dsddf");
        when(requestMock.getBody()).thenReturn(wrapper);
        createBrokerConnectorUseCase.execute(requestMock);
    }

    @Test(expected = NoSuchConnectorException.class)
    public void givenNonExistingLeverageKeyName_WhenCallExecute_ThenException(){
        Map<String, Object> wrapper = changeSettings("leverage", "leve", "xxd");
        when(requestMock.getBody()).thenReturn(wrapper);
        createBrokerConnectorUseCase.execute(requestMock);
    }

    @Test
    public void givenCorrectSettings_WhenCallExecute_ThenCreateCorrectConnector(){
        Map<String, Object> wrapper = changeSettings(null, null, null);
        when(requestMock.getBody()).thenReturn(wrapper);
        Response response = createBrokerConnectorUseCase.execute(requestMock);
        String a = "";

        OandaConnector connector = (OandaConnector) response.getBody();
        assertEquals("http://sss.com", connector.getUrl());
        assertEquals("ssae1234redsad", connector.getToken());
        assertEquals("12", connector.getAccountID());
        assertEquals("1", connector.getLeverage());

    }

    private Map<String, Object> changeSettings(String existingKey, String targetKey, String newValue) {
        Map<String, Object> wrapper = new HashMap<>();
        Map<String, String> settings = new HashMap<>();
        settings.put("brokerName", "Oanda");
        settings.put("url", "http://sss.com");
        settings.put("token", "ssae1234redsad");
        settings.put("id", "12");
        settings.put("leverage", "1");
        if (settings.containsKey(existingKey)) {
            settings.remove(existingKey);
            settings.put(targetKey, newValue);
        }
        wrapper.put("settings", settings);
        return wrapper;
    }
}