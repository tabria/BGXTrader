package trader.interactor.addbgxconfiguration;

import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.yaml.snakeyaml.error.YAMLException;
import trader.exception.BadRequestException;
import trader.exception.EmptyArgumentException;
import trader.exception.NullArgumentException;
import trader.interactor.addbgxconfiguration.AddBGXConfigurationUseCase;
import trader.requestor.Request;
import trader.responder.Response;
import trader.configuration.TradingStrategyConfiguration;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AddBGXConfigurationUseCaseTest {

    private static final String DEFAULT_BGX_CONFIG_FILE_LOCATION = "bgxStrategyConfig.yaml";

    private AddBGXConfigurationUseCase addBgxConfigurationUseCase;
    private Request requestMock;
    private TradingStrategyConfiguration configurationMock;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() {
        requestMock = mock(Request.class);
        configurationMock = mock(TradingStrategyConfiguration.class);
        addBgxConfigurationUseCase = new AddBGXConfigurationUseCase();
    }
    

    @Test(expected = NullPointerException.class)
    public void WhenCallSetCandlesQuantityWithNullValue_Exception(){
        Map<String, Map<String, String>> settings = setSettings("candlesQuantity", "initial", null);

        addBgxConfigurationUseCase.setCandlesQuantities(configurationMock, settings);
    }

    @Test(expected = NumberFormatException.class)
    public void WhenCallSetCandlesQuantityWithEmptyValue_Exception(){
        Map<String, Map<String, String>> settings = setSettings("candlesQuantity", "update", "");

        addBgxConfigurationUseCase.setCandlesQuantities(configurationMock, settings);
    }

    @Test(expected = NullPointerException.class)
    public void WhenCallSetCandlesQuantityWithCorrectInitialValues_CorrectResult(){
        long expectedInitial = 200L;
        Map<String, Map<String, String>> settings = setSettings("candlesQuantity", "initial", "200");
      //  doThrow(NullPointerException.class).when(configurationMock).setInitialCandlesQuantity(expectedInitial);

        addBgxConfigurationUseCase.setCandlesQuantities(configurationMock, settings);

    }

    @Test(expected = NullPointerException.class)
    public void WhenCallSetCandlesQuantityWithCorrectUpdateValues_CorrectResult(){
        long expectedUpdate = 100L;
        Map<String, Map<String, String>> settings = setSettings("candlesQuantity", "update", "100");
//        doThrow(NullPointerException.class).when(configurationMock).setUpdateCandlesQuantity(expectedUpdate);

        addBgxConfigurationUseCase.setCandlesQuantities(configurationMock, settings);

    }

    @Test(expected = NullPointerException.class)
    public void WhenCallSetCandlesQuantityWithCorrectInitialValuesContainingSpaces_CorrectResult(){
        long expectedInitial = 200L;
        Map<String, Map<String, String>> settings = setSettings("candlesQuantity", "initial", " 200 ");
 //       doThrow(NullPointerException.class).when(configurationMock).setInitialCandlesQuantity(expectedInitial);

        addBgxConfigurationUseCase.setCandlesQuantities(configurationMock, settings);

    }

    @Test(expected = NullPointerException.class)
    public void WhenCallSetCandlesQuantityWithCorrectUpdateValuesContainingSpaces_CorrectResult(){
        long expectedUpdate = 100L;
        Map<String, Map<String, String>> settings = setSettings("candlesQuantity", "update", " 100 ");
 //       doThrow(NullPointerException.class).when(configurationMock).setUpdateCandlesQuantity(expectedUpdate);

        addBgxConfigurationUseCase.setCandlesQuantities(configurationMock, settings);

    }

    @Test(expected = NumberFormatException.class)
    public void WhenCallSetSetRiskPerTradeWithNotANumberValue_Exception(){
        Map<String, Map<String, String>> settings = setSettings("risk", "riskPerTrade", "  ");

        addBgxConfigurationUseCase.setRiskPerTrade(configurationMock, settings);
    }

    @Test(expected = NullPointerException.class)
    public void WhenCallSetSetRiskPerTradeWithNullValue_Exception(){
        Map<String, Map<String, String>> settings = setSettings("risk", "riskPerTrade", null);

        addBgxConfigurationUseCase.setRiskPerTrade(configurationMock, settings);
    }

    @Test(expected = RuntimeException.class)
    public void WhenCallSetSetRiskPerTradeWithCorrectValueContainingSpaces_CorrectResult(){
        Map<String, Map<String, String>> settings = setSettings("risk", "riskPerTrade", " 0.03 ");

        doThrow(new RuntimeException()).when(configurationMock).setRiskPerTrade("0.03");
        addBgxConfigurationUseCase.setRiskPerTrade(configurationMock, settings);
    }

   

    @Test
    public void WhenCallExecuteWithCorrectRequest_CorrectResult(){
//        when(configurationMock.getFileLocation()).thenReturn(DEFAULT_BGX_CONFIG_FILE_LOCATION);
        when(requestMock.getBody()).thenReturn(configurationMock);
        Response<TradingStrategyConfiguration> bgxConfigurationResponse = addBgxConfigurationUseCase.execute(requestMock);

        TradingStrategyConfiguration configuration = bgxConfigurationResponse.getBody();

        Assert.assertEquals(configurationMock, configuration);
    }

    @Test(expected = NullPointerException.class)
    public void WhenCallSetEntryStrategyWithNullValue_Exception(){
        Map<String, Map<String, String>> settings = setSettings("entry", "entryStrategy", null);

        addBgxConfigurationUseCase.setEntryStrategy(configurationMock, settings);
    }

    @Test(expected = NullPointerException.class)
    public void WhenCallSetOrderStrategyWithNullValue_Exception(){
        Map<String, Map<String, String>> settings = setSettings("order", "orderStrategy", null);

        addBgxConfigurationUseCase.setOrderStrategy(configurationMock, settings);
    }

    @Test(expected = NumberFormatException.class)
    public void WhenCallSetStopLossFilterWithNotANumberValue_Exception(){
        Map<String, Map<String, String>> settings = setSettings("entry", "stopLossFilter", "  ");

        addBgxConfigurationUseCase.setEntryStrategy(configurationMock, settings);
    }

    @Test(expected = NullPointerException.class)
    public void WhenCallSetStopLossFilterWithNullValue_Exception(){
        Map<String, Map<String, String>> settings = setSettings("entry", "stopLossFilter", null);

        addBgxConfigurationUseCase.setEntryStrategy(configurationMock, settings);
    }

    @Test(expected = RuntimeException.class)
    public void WhenCallSetStopLossFilterWithCorrectValueContainingSpaces_CorrectResult(){
        Map<String, Map<String, String>> settings = setSettings("entry", "stopLossFilter", " 0.03 ");

        doThrow(new RuntimeException()).when(configurationMock).setStopLossFilter("0.03");
        addBgxConfigurationUseCase.setEntryStrategy(configurationMock, settings);
    }

    @Test(expected = NumberFormatException.class)
    public void WhenCallSetStopLossTargetWithNotANumberValue_Exception(){
        Map<String, Map<String, String>> settings = setSettings("entry", "target", "  ");

        addBgxConfigurationUseCase.setEntryStrategy(configurationMock, settings);
    }

    @Test(expected = NullPointerException.class)
    public void WhenCallSetTargetFilterWithNullValue_Exception(){
        Map<String, Map<String, String>> settings = setSettings("entry", "target", null);

        addBgxConfigurationUseCase.setEntryStrategy(configurationMock, settings);
    }

    @Test(expected = RuntimeException.class)
    public void WhenCallSetTargetWithCorrectValueContainingSpaces_CorrectResult(){
        Map<String, Map<String, String>> settings = setSettings("entry", "target", " 0.03 ");

        doThrow(new RuntimeException()).when(configurationMock).setTarget("0.03");
        addBgxConfigurationUseCase.setEntryStrategy(configurationMock, settings);
    }

    @Test(expected = NumberFormatException.class)
    public void WhenCallSetRSIFilterWithNotANumberValue_Exception(){
        Map<String, Map<String, String>> settings = setSettings("entry", "rsiFilter", "  ");

        addBgxConfigurationUseCase.setEntryStrategy(configurationMock, settings);
    }

    @Test(expected = NullPointerException.class)
    public void WhenCallSetRSIFilterFilterWithNullValue_Exception(){
        Map<String, Map<String, String>> settings = setSettings("entry", "rsiFilter", null);

        addBgxConfigurationUseCase.setEntryStrategy(configurationMock, settings);
    }

    @Test(expected = RuntimeException.class)
    public void WhenCallSetRSIFilterWithCorrectValueContainingSpaces_CorrectResult(){
        Map<String, Map<String, String>> settings = setSettings("entry", "rsiFilter", " 0.03 ");

        doThrow(new RuntimeException()).when(configurationMock).setRsiFilter("0.03");
        addBgxConfigurationUseCase.setEntryStrategy(configurationMock, settings);
    }

    @Test(expected = NumberFormatException.class)
    public void WhenCallSetEntryFilterWithNotANumberValue_Exception(){
        Map<String, Map<String, String>> settings = setSettings("entry", "entryFilter", "  ");

        addBgxConfigurationUseCase.setEntryStrategy(configurationMock, settings);
    }

    @Test(expected = NullPointerException.class)
    public void WhenCallSetEntryFilterWithNullValue_Exception(){
        Map<String, Map<String, String>> settings = setSettings("entry", "entryFilter", null);

        addBgxConfigurationUseCase.setEntryStrategy(configurationMock, settings);
    }

    @Test(expected = RuntimeException.class)
    public void WhenCallSetEntryFilterWithCorrectValueContainingSpaces_CorrectResult(){
        Map<String, Map<String, String>> settings = setSettings("entry", "entryFilter", " 0.03 ");

        doThrow(new RuntimeException()).when(configurationMock).setEntryFilter("0.03");
        addBgxConfigurationUseCase.setEntryStrategy(configurationMock, settings);
    }

    @Test(expected = NullPointerException.class)
    public void WhenCallSetExitStrategyWithNullValue_Exception(){
        Map<String, Map<String, String>> settings = setSettings("exit", "exitStrategy", null);

        addBgxConfigurationUseCase.setExitStrategy(configurationMock, settings);
    }

    @Test(expected = EmptyArgumentException.class)
    public void WhenCallSetExitGranularityWithNotANumberValue_Exception(){
        Map<String, Map<String, String>> settings = setSettings("exit", "exitGranularity", "  ");

        addBgxConfigurationUseCase.setExitStrategy(configurationMock, settings);
    }

    @Test(expected = EmptyArgumentException.class)
    public void WhenCallSetExitGranularityWithNullValue_Exception() {
        Map<String, Map<String, String>> settings = setSettings("exit", "exitGranularity", null);

        addBgxConfigurationUseCase.setExitStrategy(configurationMock, settings);
    }

    private Map<String, Map<String, String>> setSettings(String entryName, String keyName, String value) {
        Map<String, Map<String, String>> settings = new HashMap<>();
        Map<String, String> values = new HashMap<>();
        values.put(keyName, value);
        settings.put(entryName, values);
        return settings;
    }
}
