package trader.interactor;

import org.junit.Before;
import org.junit.Test;
import trader.exception.*;
import trader.requestor.Request;
import trader.requestor.RequestBuilder;
import trader.entity.indicator.ma.SimpleMovingAverage;
import trader.entity.indicator.rsi.RelativeStrengthIndex;
import trader.configuration.BGXConfigurationImpl;
import trader.requestor.RequestBuilderImpl;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class RequestBuilderImplTest {

    private static final String BGX_STRATEGY_CONFIG_FILE_NAME = "bgxStrategyConfig.yaml";
    private static final String BROKER_CONFIG_FILE_NAME = "oandaBrokerConfig.yaml";
    private static final String CONNECTOR_NAME = "Oanda";

    private RequestBuilder requestBuilder;
    private HashMap<String, String> settings;

    @Before
    public void setUp(){
        requestBuilder = new RequestBuilderImpl();
        settings = new HashMap<>();

    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallBuildWithNullDataStructureName_Exception(){
        requestBuilder.build(null, null);
    }

    @Test(expected = EmptyArgumentException.class)
    public void WhenCallBuildWithEmptyDataStructureName_Exception(){
        requestBuilder.build("",new HashMap<>());
    }

    @Test(expected = NullArgumentException.class)
    public void whenCallBuildWithNullSettings_Exception(){
        requestBuilder.build("tr", null);
    }

    @Test(expected = NoSuchDataStructureException.class)
    public void whenCallBuildWithNotExistingDataStructureName_Exception(){
        requestBuilder.build("trah", settings);
    }

    @Test(expected = EmptyArgumentException.class)
    public void whenBuildIndicatorWithZeroLengthSettings_Exception(){
        requestBuilder.build("CreateIndicatorController", settings);
    }

    @Test(expected = WrongIndicatorSettingsException.class)
    public void whenBuildIndicatorWithoutTypeInSettings_Exception(){
        settings.put("ro", "ds");
        requestBuilder.build("CreateIndicatorController", settings);
    }

    @Test(expected = WrongIndicatorSettingsException.class)
    public void WhenBuildIndicatorWithNullTypeValue_Exception(){
        settings.put("type", null);
        requestBuilder.build("CreateIndicatorController", settings);
    }

    @Test(expected = WrongIndicatorSettingsException.class)
    public void WhenBuildIndicatorWithEmptyTypeValue_Exception(){
        settings.put("type", "");
        requestBuilder.build("CreateIndicatorController", settings);
    }

    @Test
    public void whenCallBuildWithRSIIndicatorDataStructureName_ReturnRSIRequest(){
        settings.put("type", "rsi");
        Request<?> rsiIndicatorRequest = requestBuilder.build("CreateIndicatorController", settings);

        assertEquals(RelativeStrengthIndex.class, rsiIndicatorRequest.getRequestDataStructure().getClass());
    }

    //sma
    @Test
    public void whenCallBuildWithMovingAverageIndicatorDataStructureName_ReturnMARequest(){
        settings.put("type", "simple");
        settings.put("position", "middle");
        Request<?> smaIndicator = requestBuilder.build("CreateIndicatorController", settings);

        assertEquals(SimpleMovingAverage.class, smaIndicator.getRequestDataStructure().getClass());
    }

    @Test
    public void whenCallBuildWithBGXConfigurationControllerName_ReturnBGXConfigurationRequest() {
        settings.put("location", BGX_STRATEGY_CONFIG_FILE_NAME);
        Request<?> bgxConfigurationRequest = requestBuilder.build("AddBGXConfigurationController", settings);

        assertEquals(BGXConfigurationImpl.class, bgxConfigurationRequest.getRequestDataStructure().getClass());
    }

    @Test
    public void whenCallBuildWithBrokerConfigurationControllerName_ReturnBrokerConfigurationRequest() {
        settings.put("brokerName", CONNECTOR_NAME);
        settings.put("location", BROKER_CONFIG_FILE_NAME);
        Request<?> brokerConnectorRequest = requestBuilder.build("AddBrokerConnectorController", settings);
        String actual = brokerConnectorRequest.getRequestDataStructure().getClass().getSimpleName();
        String expected = CONNECTOR_NAME + "Connector";

        assertEquals(expected, actual);
    }

}
