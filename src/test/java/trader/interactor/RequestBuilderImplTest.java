package trader.interactor;

import org.junit.Before;
import org.junit.Test;
import trader.entity.trade.Direction;
import trader.entity.trade.Trade;
import trader.entry.EntryStrategy;
import trader.entry.standard.StandardEntryStrategy;
import trader.exception.*;
import trader.order.OrderStrategy;
import trader.order.StandardOrderStrategy;
import trader.requestor.Request;
import trader.requestor.RequestBuilder;
import trader.entity.indicator.ma.SimpleMovingAverage;
import trader.entity.indicator.rsi.RelativeStrengthIndex;
import trader.configuration.BGXConfigurationImpl;
import trader.requestor.RequestBuilderImpl;

import java.math.BigDecimal;
import java.util.HashMap;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

    @Test(expected = NoSuchDataStructureException.class)
    public void whenCallBuildWithUnknownIndicator_Exception(){
        settings.put("type", "mxxxx");
        requestBuilder.build("CreateIndicatorController", settings);
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

    @Test
    public void WhenCallBuildWithCreateTradeControllerWithEmptySettings_DefaultTrade(){
        settings.clear();
        Request<?> createTradeRequest = requestBuilder.build("CreateTradeController", settings);
        Trade trade = (Trade) createTradeRequest.getRequestDataStructure();

        assertFalse(trade.getTradable());
        assertEquals(Direction.FLAT, trade.getDirection());
        assertEquals(BigDecimal.valueOf(0.0001) ,trade.getEntryPrice());
        assertEquals(BigDecimal.valueOf(0.0001) ,trade.getStopLossPrice());
    }

    @Test
    public void WhenCallBuildWithCreateTradeControllerWithCorrectCustomSettings_CorrectTrade(){
        settings.put("direction", "down");
        settings.put("tradable", "true");
        settings.put("entryPrice", "1.12345");
        settings.put("stopLossPrice", "5.1234");
        Request<?> createTradeRequest = requestBuilder.build("CreateTradeController", settings);
        Trade trade = (Trade) createTradeRequest.getRequestDataStructure();

        assertTrue(trade.getTradable());
        assertEquals(Direction.DOWN, trade.getDirection());
        assertEquals(BigDecimal.valueOf(1.12345) ,trade.getEntryPrice());
        assertEquals(BigDecimal.valueOf(5.1234) ,trade.getStopLossPrice());
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallBuildWithAddEntryStrategyControllerWithBadKeyNameInSettings_Exception(){
        settings.put("entry", "standard");
        requestBuilder.build("AddEntryStrategyController", settings);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallBuildWithAddEntryStrategyControllerWithNullValueInSettings_Exception(){
        settings.put("entryStrategy", null);
        requestBuilder.build("AddEntryStrategyController", settings);
    }

    @Test(expected = NoSuchStrategyException.class)
    public void WhenCallBuildWithAddEntryStrategyControllerWithEmptyValueInSettings_Exception(){
        settings.put("entryStrategy", "   ");
        requestBuilder.build("AddEntryStrategyController", settings);
    }

    @Test(expected = NoSuchStrategyException.class)
    public void WhenCallBuildWithAddEntryStrategyControllerWithENonExistingStrtegy_Exception(){
        settings.put("entryStrategy", "non");
        requestBuilder.build("AddEntryStrategyController", settings);
    }

    @Test
    public void WhenCallBuildWithAddEntryStrategyControllerWithCorrectSettings_CorrectEntryStrategy(){
        settings.put("entryStrategy", " standard ");
        Request<?> entryStrategyRequest = requestBuilder.build("AddEntryStrategyController", settings);
        EntryStrategy entryStrategy = (EntryStrategy) entryStrategyRequest.getRequestDataStructure();

        assertEquals(StandardEntryStrategy.class, entryStrategy.getClass());
    }




    @Test(expected = NullArgumentException.class)
    public void WhenCallBuildWithAddOrderStrategyControllerWithBadKeyNameInSettings_Exception(){
        settings.put("order", "standard");
        requestBuilder.build("AddOrderStrategyController", settings);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallBuildWithAddOrderStrategyControllerWithNullValueInSettings_Exception(){
        settings.put("orderStrategy", null);
        requestBuilder.build("AddOrderStrategyController", settings);
    }

    @Test(expected = NoSuchStrategyException.class)
    public void WhenCallBuildWithAddOrderStrategyControllerWithEmptyValueInSettings_Exception(){
        settings.put("orderStrategy", "   ");
        requestBuilder.build("AddOrderStrategyController", settings);
    }

    @Test(expected = NoSuchStrategyException.class)
    public void WhenCallBuildWithAddOrderStrategyControllerWithENonExistingStrtegy_Exception(){
        settings.put("orderStrategy", "non");
        requestBuilder.build("AddOrderStrategyController", settings);
    }

    @Test
    public void WhenCallBuildWithAddOrderStrategyControllerWithCorrectSettings_CorrectEntryStrategy(){
        settings.put("orderStrategy", " standard ");
        Request<?> orderStrategyRequest = requestBuilder.build("AddOrderStrategyController", settings);
        OrderStrategy orderStrategy = (OrderStrategy) orderStrategyRequest.getRequestDataStructure();

        assertEquals(StandardOrderStrategy.class, orderStrategy.getClass());
    }
}
