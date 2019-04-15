package trader.interactor;

import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.yaml.snakeyaml.error.YAMLException;
import trader.controller.AddExitStrategyController;
import trader.exception.BadRequestException;
import trader.exception.EmptyArgumentException;
import trader.exception.NullArgumentException;
import trader.requestor.Request;
import trader.responder.Response;
import trader.configuration.TradingStrategyConfiguration;

import java.math.BigDecimal;
import java.util.HashMap;

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

    @Test(expected = NullArgumentException.class)
    public void WhenCallExecuteWithNull_Exception(){
        addBgxConfigurationUseCase.execute(null);
    }

    @Test(expected = NullPointerException.class)
    public void WhenCallSetCandlesQuantityWithNullValue_Exception(){
        HashMap<String, HashMap<String, String>> settings = setSettings("candlesQuantity", "initial", null);

        addBgxConfigurationUseCase.setCandlesQuantities(configurationMock, settings);
    }

    @Test(expected = NumberFormatException.class)
    public void WhenCallSetCandlesQuantityWithEmptyValue_Exception(){
        HashMap<String, HashMap<String, String>> settings = setSettings("candlesQuantity", "update", "");

        addBgxConfigurationUseCase.setCandlesQuantities(configurationMock, settings);
    }

    @Test(expected = NullPointerException.class)
    public void WhenCallSetCandlesQuantityWithCorrectInitialValues_CorrectResult(){
        long expectedInitial = 200L;
        HashMap<String, HashMap<String, String>> settings = setSettings("candlesQuantity", "initial", "200");
        doThrow(NullPointerException.class).when(configurationMock).setInitialCandlesQuantity(expectedInitial);

        addBgxConfigurationUseCase.setCandlesQuantities(configurationMock, settings);

    }

    @Test(expected = NullPointerException.class)
    public void WhenCallSetCandlesQuantityWithCorrectUpdateValues_CorrectResult(){
        long expectedUpdate = 100L;
        HashMap<String, HashMap<String, String>> settings = setSettings("candlesQuantity", "update", "100");
        doThrow(NullPointerException.class).when(configurationMock).setUpdateCandlesQuantity(expectedUpdate);

        addBgxConfigurationUseCase.setCandlesQuantities(configurationMock, settings);

    }

    @Test(expected = NullPointerException.class)
    public void WhenCallSetCandlesQuantityWithCorrectInitialValuesContainingSpaces_CorrectResult(){
        long expectedInitial = 200L;
        HashMap<String, HashMap<String, String>> settings = setSettings("candlesQuantity", "initial", " 200 ");
        doThrow(NullPointerException.class).when(configurationMock).setInitialCandlesQuantity(expectedInitial);

        addBgxConfigurationUseCase.setCandlesQuantities(configurationMock, settings);

    }

    @Test(expected = NullPointerException.class)
    public void WhenCallSetCandlesQuantityWithCorrectUpdateValuesContainingSpaces_CorrectResult(){
        long expectedUpdate = 100L;
        HashMap<String, HashMap<String, String>> settings = setSettings("candlesQuantity", "update", " 100 ");
        doThrow(NullPointerException.class).when(configurationMock).setUpdateCandlesQuantity(expectedUpdate);

        addBgxConfigurationUseCase.setCandlesQuantities(configurationMock, settings);

    }

    @Test(expected = NumberFormatException.class)
    public void WhenCallSetSetRiskPerTradeWithNotANumberValue_Exception(){
        HashMap<String, HashMap<String, String>> settings = setSettings("risk", "riskPerTrade", "  ");

        addBgxConfigurationUseCase.setRiskPerTrade(configurationMock, settings);
    }

    @Test(expected = NullPointerException.class)
    public void WhenCallSetSetRiskPerTradeWithNullValue_Exception(){
        HashMap<String, HashMap<String, String>> settings = setSettings("risk", "riskPerTrade", null);

        addBgxConfigurationUseCase.setRiskPerTrade(configurationMock, settings);
    }

    @Test(expected = RuntimeException.class)
    public void WhenCallSetSetRiskPerTradeWithCorrectValueContainingSpaces_CorrectResult(){
        HashMap<String, HashMap<String, String>> settings = setSettings("risk", "riskPerTrade", " 0.03 ");

        doThrow(new RuntimeException()).when(configurationMock).setRiskPerTrade(BigDecimal.valueOf(0.03));
        addBgxConfigurationUseCase.setRiskPerTrade(configurationMock, settings);
    }

    @Test
    public void whenCallExecuteWithBadFileLocation_Exception(){
        exception.expect(BadRequestException.class);
        exception.expectCause(IsInstanceOf.instanceOf(YAMLException.class));

        when(configurationMock.getFileLocation()).thenReturn("ggs.yaml");
        when(requestMock.getRequestDataStructure()).thenReturn(configurationMock);

        addBgxConfigurationUseCase.execute(requestMock);
    }

    @Test
    public void WhenCallExecuteWithCorrectRequest_CorrectResult(){
        when(configurationMock.getFileLocation()).thenReturn(DEFAULT_BGX_CONFIG_FILE_LOCATION);
        when(requestMock.getRequestDataStructure()).thenReturn(configurationMock);
        Response<TradingStrategyConfiguration> bgxConfigurationResponse = addBgxConfigurationUseCase.execute(requestMock);

        TradingStrategyConfiguration configuration = bgxConfigurationResponse.getResponseDataStructure();

        Assert.assertEquals(configurationMock, configuration);
    }

    @Test(expected = NullPointerException.class)
    public void WhenCallSetEntryStrategyWithNullValue_Exception(){
        HashMap<String, HashMap<String, String>> settings = setSettings("entry", "entryStrategy", null);

        addBgxConfigurationUseCase.setEntryStrategy(configurationMock, settings);
    }

    @Test(expected = NullPointerException.class)
    public void WhenCallSetOrderStrategyWithNullValue_Exception(){
        HashMap<String, HashMap<String, String>> settings = setSettings("order", "orderStrategy", null);

        addBgxConfigurationUseCase.setOrderStrategy(configurationMock, settings);
    }

    private HashMap<String, HashMap<String, String>> setSettings(String entryName, String keyName, String value) {
        HashMap<String, HashMap<String, String>> settings = new HashMap<>();
        HashMap<String, String> values = new HashMap<>();
        values.put(keyName, value);
        settings.put(entryName, values);
        return settings;
    }

    @Test(expected = NumberFormatException.class)
    public void WhenCallSetStopLossFilterWithNotANumberValue_Exception(){
        HashMap<String, HashMap<String, String>> settings = setSettings("entry", "stopLossFilter", "  ");

        addBgxConfigurationUseCase.setEntryStrategy(configurationMock, settings);
    }

    @Test(expected = NullPointerException.class)
    public void WhenCallSetStopLossFilterWithNullValue_Exception(){
        HashMap<String, HashMap<String, String>> settings = setSettings("entry", "stopLossFilter", null);

        addBgxConfigurationUseCase.setEntryStrategy(configurationMock, settings);
    }

    @Test(expected = RuntimeException.class)
    public void WhenCallSetStopLossFilterWithCorrectValueContainingSpaces_CorrectResult(){
        HashMap<String, HashMap<String, String>> settings = setSettings("entry", "stopLossFilter", " 0.03 ");

        doThrow(new RuntimeException()).when(configurationMock).setStopLossFilter(BigDecimal.valueOf(0.03));
        addBgxConfigurationUseCase.setEntryStrategy(configurationMock, settings);
    }

    @Test(expected = NumberFormatException.class)
    public void WhenCallSetStopLossTargetWithNotANumberValue_Exception(){
        HashMap<String, HashMap<String, String>> settings = setSettings("entry", "target", "  ");

        addBgxConfigurationUseCase.setEntryStrategy(configurationMock, settings);
    }

    @Test(expected = NullPointerException.class)
    public void WhenCallSetTargetFilterWithNullValue_Exception(){
        HashMap<String, HashMap<String, String>> settings = setSettings("entry", "target", null);

        addBgxConfigurationUseCase.setEntryStrategy(configurationMock, settings);
    }

    @Test(expected = RuntimeException.class)
    public void WhenCallSetTargetWithCorrectValueContainingSpaces_CorrectResult(){
        HashMap<String, HashMap<String, String>> settings = setSettings("entry", "target", " 0.03 ");

        doThrow(new RuntimeException()).when(configurationMock).setTarget(BigDecimal.valueOf(0.03));
        addBgxConfigurationUseCase.setEntryStrategy(configurationMock, settings);
    }

    @Test(expected = NumberFormatException.class)
    public void WhenCallSetRSIFilterWithNotANumberValue_Exception(){
        HashMap<String, HashMap<String, String>> settings = setSettings("entry", "rsiFilter", "  ");

        addBgxConfigurationUseCase.setEntryStrategy(configurationMock, settings);
    }

    @Test(expected = NullPointerException.class)
    public void WhenCallSetRSIFilterFilterWithNullValue_Exception(){
        HashMap<String, HashMap<String, String>> settings = setSettings("entry", "rsiFilter", null);

        addBgxConfigurationUseCase.setEntryStrategy(configurationMock, settings);
    }

    @Test(expected = RuntimeException.class)
    public void WhenCallSetRSIFilterWithCorrectValueContainingSpaces_CorrectResult(){
        HashMap<String, HashMap<String, String>> settings = setSettings("entry", "rsiFilter", " 0.03 ");

        doThrow(new RuntimeException()).when(configurationMock).setRsiFilter(BigDecimal.valueOf(0.03));
        addBgxConfigurationUseCase.setEntryStrategy(configurationMock, settings);
    }

    @Test(expected = NumberFormatException.class)
    public void WhenCallSetEntryFilterWithNotANumberValue_Exception(){
        HashMap<String, HashMap<String, String>> settings = setSettings("entry", "entryFilter", "  ");

        addBgxConfigurationUseCase.setEntryStrategy(configurationMock, settings);
    }

    @Test(expected = NullPointerException.class)
    public void WhenCallSetEntryFilterWithNullValue_Exception(){
        HashMap<String, HashMap<String, String>> settings = setSettings("entry", "entryFilter", null);

        addBgxConfigurationUseCase.setEntryStrategy(configurationMock, settings);
    }

    @Test(expected = RuntimeException.class)
    public void WhenCallSetEntryFilterWithCorrectValueContainingSpaces_CorrectResult(){
        HashMap<String, HashMap<String, String>> settings = setSettings("entry", "entryFilter", " 0.03 ");

        doThrow(new RuntimeException()).when(configurationMock).setEntryFilter(BigDecimal.valueOf(0.03));
        addBgxConfigurationUseCase.setEntryStrategy(configurationMock, settings);
    }

    @Test(expected = NullPointerException.class)
    public void WhenCallSetExitStrategyWithNullValue_Exception(){
        HashMap<String, HashMap<String, String>> settings = setSettings("exit", "exitStrategy", null);

        addBgxConfigurationUseCase.setExitStrategy(configurationMock, settings);
    }

    @Test(expected = EmptyArgumentException.class)
    public void WhenCallSetExitGranularityWithNotANumberValue_Exception(){
        HashMap<String, HashMap<String, String>> settings = setSettings("exit", "exitGranularity", "  ");

        addBgxConfigurationUseCase.setExitStrategy(configurationMock, settings);
    }

    @Test(expected = EmptyArgumentException.class)
    public void WhenCallSetExitGranularityWithNullValue_Exception() {
        HashMap<String, HashMap<String, String>> settings = setSettings("exit", "exitGranularity", null);

        addBgxConfigurationUseCase.setExitStrategy(configurationMock, settings);
    }
}
