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
        HashMap<String, HashMap<String, String>> quantities = new HashMap<>();
        HashMap<String, String> values = new HashMap<>();
        values.put("initial", null);
        quantities.put("candlesQuantity", values);

        addBgxConfigurationUseCase.setCandlesQuantities(configurationMock, quantities);
    }

    @Test(expected = NumberFormatException.class)
    public void WhenCallSetCandlesQuantityWithEmptyValue_Exception(){
        HashMap<String, HashMap<String, String>> quantities = new HashMap<>();
        HashMap<String, String> values = new HashMap<>();
        values.put("update", "");
        quantities.put("candlesQuantity", values);

        addBgxConfigurationUseCase.setCandlesQuantities(configurationMock, quantities);
    }

    @Test(expected = NullPointerException.class)
    public void WhenCallSetCandlesQuantityWithCorrectInitialValues_CorrectResult(){
        long expectedInitial = 200L;
        HashMap<String, HashMap<String, String>> quantities = new HashMap<>();
        HashMap<String, String> values = new HashMap<>();
        values.put("initial", "200");
        quantities.put("candlesQuantity", values);
        doThrow(NullPointerException.class).when(configurationMock).setInitialCandlesQuantity(expectedInitial);

        addBgxConfigurationUseCase.setCandlesQuantities(configurationMock, quantities);

    }

    @Test(expected = NullPointerException.class)
    public void WhenCallSetCandlesQuantityWithCorrectUpdateValues_CorrectResult(){
        long expectedUpdate = 100L;
        HashMap<String, HashMap<String, String>> quantities = new HashMap<>();
        HashMap<String, String> values = new HashMap<>();
        values.put("update", "100");
        quantities.put("candlesQuantity", values);
        doThrow(NullPointerException.class).when(configurationMock).setUpdateCandlesQuantity(expectedUpdate);

        addBgxConfigurationUseCase.setCandlesQuantities(configurationMock, quantities);

    }

    @Test(expected = NullPointerException.class)
    public void WhenCallSetCandlesQuantityWithCorrectInitialValuesContainingSpaces_CorrectResult(){
        long expectedInitial = 200L;
        HashMap<String, HashMap<String, String>> quantities = new HashMap<>();
        HashMap<String, String> values = new HashMap<>();
        values.put("initial", " 200 ");
        quantities.put("candlesQuantity", values);
        doThrow(NullPointerException.class).when(configurationMock).setInitialCandlesQuantity(expectedInitial);

        addBgxConfigurationUseCase.setCandlesQuantities(configurationMock, quantities);

    }

    @Test(expected = NullPointerException.class)
    public void WhenCallSetCandlesQuantityWithCorrectUpdateValuesContainingSpaces_CorrectResult(){
        long expectedUpdate = 100L;
        HashMap<String, HashMap<String, String>> quantities = new HashMap<>();
        HashMap<String, String> values = new HashMap<>();
        values.put("update", " 100 ");
        quantities.put("candlesQuantity", values);
        doThrow(NullPointerException.class).when(configurationMock).setUpdateCandlesQuantity(expectedUpdate);

        addBgxConfigurationUseCase.setCandlesQuantities(configurationMock, quantities);

    }

    @Test(expected = NumberFormatException.class)
    public void WhenCallSetSetRiskPerTradeWithNotANumberValue_Exception(){
        HashMap<String, HashMap<String, String>> quantities = new HashMap<>();
        HashMap<String, String> values = new HashMap<>();
        values.put("riskPerTrade", "  ");
        quantities.put("risk", values);

        addBgxConfigurationUseCase.setRiskPerTrade(configurationMock, quantities);
    }

    @Test(expected = NullPointerException.class)
    public void WhenCallSetSetRiskPerTradeWithNullValue_Exception(){
        HashMap<String, HashMap<String, String>> quantities = new HashMap<>();
        HashMap<String, String> values = new HashMap<>();
        values.put("riskPerTrade", null);
        quantities.put("risk", values);

        addBgxConfigurationUseCase.setRiskPerTrade(configurationMock, quantities);
    }

    @Test(expected = RuntimeException.class)
    public void WhenCallSetSetRiskPerTradeWithCorrectValueContainingSpaces_CorrectResult(){
        HashMap<String, HashMap<String, String>> quantities = new HashMap<>();
        HashMap<String, String> values = new HashMap<>();
        values.put("riskPerTrade", " 0.03 ");
        quantities.put("risk", values);

        doThrow(new RuntimeException()).when(configurationMock).setRiskPerTrade(BigDecimal.valueOf(0.03));
        addBgxConfigurationUseCase.setRiskPerTrade(configurationMock, quantities);
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
}
