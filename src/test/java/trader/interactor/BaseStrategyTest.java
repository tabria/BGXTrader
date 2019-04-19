package trader.interactor;

import org.junit.Before;
import trader.requestor.Request;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class BaseStrategyTest {
    protected Request requestMock;

    @Before
    public void setUp() throws Exception {
        requestMock = mock(Request.class);
    }

    protected void setFakeSettings(String keyName, String strategyName) {
        Map<String, Object> inputSettings = new HashMap<>();
        Map<String, String> settings = new HashMap<>();
        settings.put(keyName, strategyName);
        inputSettings.put("settings", settings);
        when(requestMock.getBody()).thenReturn(inputSettings);
    }
}
