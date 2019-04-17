package trader.interactor.addbgxconfiguration;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import trader.requestor.Request;
import trader.responder.Response;
import trader.configuration.TradingStrategyConfiguration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class CreateBGXConfigurationUseCaseTest {

    private Request requestMock;

    private TradingStrategyConfiguration configurationMock;
    private CreateBGXConfigurationUseCase createBgxConfigurationUseCase;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() {
        requestMock = mock(Request.class);
        configurationMock = mock(TradingStrategyConfiguration.class);
        createBgxConfigurationUseCase = new CreateBGXConfigurationUseCase();
    }
    

    @Test
    public void givenNotExistingCandlesQuantity_WhenCallSetCandlesQuantity_ThenNoUpdate(){
        createBgxConfigurationUseCase.setCandlesQuantities(configurationMock, setSettings("", "", ""));

        doNothing().when(configurationMock).setInitialCandlesQuantity(anyString());

        verify(configurationMock, times(0)).setInitialCandlesQuantity(anyString());
    }

    @Test
    public void givenNonExistingInitialOrUpdateCandlesQuantityEntry_WhenCallSetCandlesQuantity_ThenNoUpdate(){
        createBgxConfigurationUseCase.setCandlesQuantities(configurationMock, setSettings("candlesQuantity", "", ""));

        doNothing().when(configurationMock).setInitialCandlesQuantity(anyString());
        doNothing().when(configurationMock).setUpdateCandlesQuantity(anyString());

        verify(configurationMock, times(0)).setInitialCandlesQuantity(anyString());
        verify(configurationMock, times(0)).setUpdateCandlesQuantity(anyString());
    }

    @Test
    public void givenCorrectInitialCandlesQuantityValue_WhenCallSetCandlesQuantity_ThenUpdate(){
        createBgxConfigurationUseCase.setCandlesQuantities(configurationMock, setSettings("candlesQuantity", "initial", "12"));

        doNothing().when(configurationMock).setInitialCandlesQuantity(anyString());

        verify(configurationMock, times(1)).setInitialCandlesQuantity(anyString());
    }

    @Test
    public void givenCorrectUpdateCandlesQuantityValue_WhenCallSetCandlesQuantity_ThenUpdate(){
        createBgxConfigurationUseCase.setCandlesQuantities(configurationMock, setSettings("candlesQuantity", "update", "17"));

        doNothing().when(configurationMock).setUpdateCandlesQuantity(anyString());

        verify(configurationMock, times(1)).setUpdateCandlesQuantity(anyString());
    }

    @Test
    public void givenNotExistingRiskEntry_WhenCallSetRiskPerTrade_ThenNoUpdate(){
        createBgxConfigurationUseCase.setRiskPerTrade(configurationMock, setSettings("", "", ""));

        doNothing().when(configurationMock).setRiskPerTrade(anyString());

        verify(configurationMock, times(0)).setRiskPerTrade(anyString());
    }

    @Test
    public void givenNonExistingRiskPerTradeEntry_WhenCallSetRiskPerTrade_ThenNoUpdate(){
        createBgxConfigurationUseCase.setCandlesQuantities(configurationMock, setSettings("risk", "", ""));

        doNothing().when(configurationMock).setRiskPerTrade(anyString());

        verify(configurationMock, times(0)).setRiskPerTrade(anyString());
    }

    @Test
    public void givenCorrectRiskPerTradeValue_WhenCallSetRiskPerTrade_ThenUpdate(){
        createBgxConfigurationUseCase.setRiskPerTrade(configurationMock, setSettings("risk", "riskPerTrade", "12"));

        doNothing().when(configurationMock).setRiskPerTrade(anyString());

        verify(configurationMock, times(1)).setRiskPerTrade(anyString());
    }

    @Test
    public void givenNotExistingEntry_WhenCallSetEntryStrategy_ThenNoUpdate(){
        createBgxConfigurationUseCase.setEntryStrategy(configurationMock, setSettings("", "", ""));

        doNothing().when(configurationMock).setEntryStrategy(anyString());

        verify(configurationMock, times(0)).setEntryStrategy(anyString());
    }

    @Test
    public void givenNonExistingSettings_WhenCallSetEntryStrategy_ThenNoUpdate(){
        createBgxConfigurationUseCase.setEntryStrategy(configurationMock, setSettings("entry", "", ""));

        doNothing().when(configurationMock).setEntryStrategy(anyString());
        doNothing().when(configurationMock).setEntryFilter(anyString());
        doNothing().when(configurationMock).setStopLossFilter(anyString());
        doNothing().when(configurationMock).setRsiFilter(anyString());
        doNothing().when(configurationMock).setTarget(anyString());

        verify(configurationMock, times(0)).setEntryStrategy(anyString());
        verify(configurationMock, times(0)).setEntryFilter(anyString());
        verify(configurationMock, times(0)).setStopLossFilter(anyString());
        verify(configurationMock, times(0)).setRsiFilter(anyString());
        verify(configurationMock, times(0)).setTarget(anyString());
    }

    @Test
    public void givenCorrectEntryStrategyValues_WhenCallSetEntryStrategy_ThenUpdate(){
        createBgxConfigurationUseCase.setEntryStrategy(configurationMock, setSettings("entry", "entryStrategy", "standard"));

        doNothing().when(configurationMock).setEntryStrategy(anyString());

        verify(configurationMock, times(1)).setEntryStrategy(anyString());
    }

    @Test
    public void givenCorrectEntryFilterValues_WhenCallSetEntryStrategy_ThenUpdate(){
        createBgxConfigurationUseCase.setEntryStrategy(configurationMock, setSettings("entry", "entryFilter", "0.002"));

        doNothing().when(configurationMock).setEntryFilter(anyString());

        verify(configurationMock, times(1)).setEntryFilter(anyString());
    }

    @Test
    public void givenCorrectStopLossValues_WhenCallSetEntryStrategy_ThenUpdate(){
        createBgxConfigurationUseCase.setEntryStrategy(configurationMock, setSettings("entry", "stopLossFilter", "0.008"));

        doNothing().when(configurationMock).setStopLossFilter(anyString());

        verify(configurationMock, times(1)).setStopLossFilter(anyString());
    }

    @Test
    public void givenCorrectEntryRsiFilter_WhenCallSetEntryStrategy_ThenUpdate(){
        createBgxConfigurationUseCase.setEntryStrategy(configurationMock, setSettings("entry", "rsiFilter", "0.001"));

        doNothing().when(configurationMock).setRsiFilter(anyString());

        verify(configurationMock, times(1)).setRsiFilter(anyString());
    }

    @Test
    public void givenCorrectTargetValue_WhenCallSetEntryStrategy_ThenUpdate(){
        createBgxConfigurationUseCase.setEntryStrategy(configurationMock, setSettings("entry", "target", "0.12"));

        doNothing().when(configurationMock).setTarget(anyString());

        verify(configurationMock, times(1)).setTarget(anyString());
    }

    @Test
    public void givenNotExistingOrder_WhenCallSetOrderStrategy_ThenNoUpdate(){
        createBgxConfigurationUseCase.setOrderStrategy(configurationMock, setSettings("", "", ""));

        doNothing().when(configurationMock).setOrderStrategy(anyString());

        verify(configurationMock, times(0)).setOrderStrategy(anyString());
    }

    @Test
    public void givenNonExistingSettings_WhenCallSetOrderStrategy_ThenNoUpdate(){
        createBgxConfigurationUseCase.setOrderStrategy(configurationMock, setSettings("order", "", ""));

        doNothing().when(configurationMock).setOrderStrategy(anyString());

        verify(configurationMock, times(0)).setOrderStrategy(anyString());
    }

    @Test
    public void givenCorrectOrderStrategyValues_WhenCallSetOrderStrategy_ThenUpdate(){
        createBgxConfigurationUseCase.setOrderStrategy(configurationMock, setSettings("order", "orderStrategy", "standard"));

        doNothing().when(configurationMock).setOrderStrategy(anyString());

        verify(configurationMock, times(1)).setOrderStrategy(anyString());
    }



    @Test
    public void givenNotExistingExit_WhenCallSetExitStrategy_ThenNoUpdate(){
        createBgxConfigurationUseCase.setExitStrategy(configurationMock, setSettings("", "", ""));

        doNothing().when(configurationMock).setExitStrategy(anyString());

        verify(configurationMock, times(0)).setExitStrategy(anyString());
    }

    @Test
    public void givenNonExistingSettings_WhenCallSetExitStrategy_ThenNoUpdate(){
        createBgxConfigurationUseCase.setExitStrategy(configurationMock, setSettings("exit", "", ""));

        doNothing().when(configurationMock).setExitStrategy(anyString());
        doNothing().when(configurationMock).setExitGranularity(anyString());

        verify(configurationMock, times(0)).setExitStrategy(anyString());
        verify(configurationMock, times(0)).setExitGranularity(anyString());
    }

    @Test
    public void givenCorrectExitStrategyValue_WhenCallSetExitStrategy_ThenUpdate(){
        createBgxConfigurationUseCase.setExitStrategy(configurationMock, setSettings("exit", "exitStrategy", "fullClose"));

        doNothing().when(configurationMock).setExitStrategy(anyString());

        verify(configurationMock, times(1)).setExitStrategy(anyString());
    }

    @Test
    public void givenCorrectExitGranularityValue_WhenCallSetExitStrategy_ThenUpdate(){
        createBgxConfigurationUseCase.setExitStrategy(configurationMock, setSettings("exit", "exitGranularity", "M30"));

        doNothing().when(configurationMock).setExitGranularity(anyString());

        verify(configurationMock, times(1)).setExitGranularity(anyString());
    }

    @Test
    public void givenNotExistingIndicator_WhenCallSetIndicators_ThenNoUpdate(){
        createBgxConfigurationUseCase.setIndicators(configurationMock, setSettings("", "", ""));

        doNothing().when(configurationMock).addIndicator(any(Map.class));

        verify(configurationMock, times(0)).addIndicator(any(Map.class));
    }

    @Test
    public void givenNonExistingSettings_WhenCallSetIndicators_ThenNoUpdate(){
        createBgxConfigurationUseCase.setExitStrategy(configurationMock, setSettings("indicator1", "", ""));

        doNothing().when(configurationMock).addIndicator(any(Map.class));

        verify(configurationMock, times(0)).addIndicator(any(Map.class));
    }

    @Test
    public void givenCorrectIndicatorValue_WhenCallSetIndicators_ThenUpdate(){
        createBgxConfigurationUseCase.setIndicators(configurationMock, setSettings("indicator1", "type", "rsi"));

        doNothing().when(configurationMock).addIndicator(any(Map.class));

        verify(configurationMock, times(1)).addIndicator(any(Map.class));
    }

    @Test
    public void givenCorrectRequest_WhenCallExecute_ThenCorrectResponse(){
        when(requestMock.getBody()).thenReturn(setSettings("indicator1", "type", "rsi"));
        Response<TradingStrategyConfiguration> bgxConfigurationResponse = createBgxConfigurationUseCase.execute(requestMock);

        TradingStrategyConfiguration configuration = bgxConfigurationResponse.getBody();

        List<Map<String, String>> indicators = configuration.getIndicators();
        assertEquals(1, indicators.size());
        assertEquals("rsi", indicators.get(0).get("type"));
    }

    private Map<String, Map<String, String>> setSettings(String entryName, String keyName, String value) {
        Map<String, Map<String, String>> settings = new HashMap<>();
        Map<String, String> values = new HashMap<>();
        values.put(keyName, value);
        settings.put(entryName, values);
        return settings;
    }
}
