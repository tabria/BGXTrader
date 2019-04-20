package trader.interactor;

import org.junit.Before;
import trader.presenter.Presenter;
import trader.requestor.Request;
import trader.responder.Response;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class BaseStrategyTest {
    protected Request requestMock;
    protected Presenter presenterMock;

    @Before
    public void setUp() throws Exception {
        presenterMock = mock(Presenter.class);
        requestMock = mock(Request.class);
    }

    protected void setFakeSettings(String keyName, String strategyName) {
        Map<String, Object> inputSettings = new HashMap<>();
        Map<String, String> settings = new HashMap<>();
        settings.put(keyName, strategyName);
        inputSettings.put("settings", settings);
        when(requestMock.getBody()).thenReturn(inputSettings);
        doNothing().when(presenterMock).execute(any(Response.class));
    }
}
