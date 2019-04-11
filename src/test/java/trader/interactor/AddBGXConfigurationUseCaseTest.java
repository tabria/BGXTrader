package trader.interactor;

import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.yaml.snakeyaml.error.YAMLException;
import trader.exception.BadRequestException;
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
        HashMap<String, HashMap<String, String>> settings = setSettings("initial", "candlesQuantity", null);

        addBgxConfigurationUseCase.setCandlesQuantities(configurationMock, settings);
    }

    @Test(expected = NumberFormatException.class)
    public void WhenCallSetCandlesQuantityWithEmptyValue_Exception(){
        HashMap<String, HashMap<String, String>> settings = setSettings("update", "candlesQuantity", "");

        addBgxConfigurationUseCase.setCandlesQuantities(configurationMock, settings);
    }

    @Test(expected = NullPointerException.class)
    public void WhenCallSetCandlesQuantityWithCorrectInitialValues_CorrectResult(){
        long expectedInitial = 200L;
        HashMap<String, HashMap<String, String>> settings = setSettings("initial", "candlesQuantity", "200");
        doThrow(NullPointerException.class).when(configurationMock).setInitialCandlesQuantity(expectedInitial);

        addBgxConfigurationUseCase.setCandlesQuantities(configurationMock, settings);

    }

    @Test(expected = NullPointerException.class)
    public void WhenCallSetCandlesQuantityWithCorrectUpdateValues_CorrectResult(){
        long expectedUpdate = 100L;
        HashMap<String, HashMap<String, String>> settings = setSettings("update", "candlesQuantity", "100");
        doThrow(NullPointerException.class).when(configurationMock).setUpdateCandlesQuantity(expectedUpdate);

        addBgxConfigurationUseCase.setCandlesQuantities(configurationMock, settings);

    }

    @Test(expected = NullPointerException.class)
    public void WhenCallSetCandlesQuantityWithCorrectInitialValuesContainingSpaces_CorrectResult(){
        long expectedInitial = 200L;
        HashMap<String, HashMap<String, String>> settings = setSettings("initial", "candlesQuantity", " 200 ");
        doThrow(NullPointerException.class).when(configurationMock).setInitialCandlesQuantity(expectedInitial);

        addBgxConfigurationUseCase.setCandlesQuantities(configurationMock, settings);

    }

    @Test(expected = NullPointerException.class)
    public void WhenCallSetCandlesQuantityWithCorrectUpdateValuesContainingSpaces_CorrectResult(){
        long expectedUpdate = 100L;
        HashMap<String, HashMap<String, String>> settings = setSettings("update", "candlesQuantity", " 100 ");
        doThrow(NullPointerException.class).when(configurationMock).setUpdateCandlesQuantity(expectedUpdate);

        addBgxConfigurationUseCase.setCandlesQuantities(configurationMock, settings);

    }

    @Test(expected = NumberFormatException.class)
    public void WhenCallSetSetRiskPerTradeWithNotANumberValue_Exception(){
        HashMap<String, HashMap<String, String>> settings = setSettings("riskPerTrade", "risk", "  ");

        addBgxConfigurationUseCase.setRiskPerTrade(configurationMock, settings);
    }

    @Test(expected = NullPointerException.class)
    public void WhenCallSetSetRiskPerTradeWithNullValue_Exception(){
        HashMap<String, HashMap<String, String>> settings = setSettings("riskPerTrade", "risk", null);

        addBgxConfigurationUseCase.setRiskPerTrade(configurationMock, settings);
    }

    @Test(expected = RuntimeException.class)
    public void WhenCallSetSetRiskPerTradeWithCorrectValueContainingSpaces_CorrectResult(){
        HashMap<String, HashMap<String, String>> settings = setSettings("riskPerTrade", "risk", " 0.03 ");

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
        HashMap<String, HashMap<String, String>> settings = setSettings("entryStrategy", "entry", null);

        addBgxConfigurationUseCase.setEntryStrategy(configurationMock, settings);
    }

    @Test(expected = NullPointerException.class)
    public void WhenCallSetOrderStrategyWithNullValue_Exception(){
        HashMap<String, HashMap<String, String>> settings = setSettings("orderStrategy", "order", null);

        addBgxConfigurationUseCase.setOrderStrategy(configurationMock, settings);
    }

    @Test(expected = NullPointerException.class)
    public void WhenCallSetExitStrategyWithNullValue_Exception(){
        HashMap<String, HashMap<String, String>> settings = setSettings("exitStrategy", "exit", null);

        addBgxConfigurationUseCase.setExitStrategy(configurationMock, settings);
    }

    private HashMap<String, HashMap<String, String>> setSettings(String keyName, String entryName, String value) {
        HashMap<String, HashMap<String, String>> settings = new HashMap<>();
        HashMap<String, String> values = new HashMap<>();
        values.put(keyName, value);
        settings.put(entryName, values);
        return settings;
    }
}
