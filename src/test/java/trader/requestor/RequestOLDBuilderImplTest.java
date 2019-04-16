package trader.requestor;

import org.junit.Before;
import org.junit.Test;
import trader.entity.trade.Direction;
import trader.entity.trade.Trade;
import trader.entry.EntryStrategy;
import trader.entry.standard.StandardEntryStrategy;
import trader.exception.*;
import trader.exit.ExitStrategy;
import trader.exit.fullclose.FullCloseExitStrategy;
import trader.order.OrderStrategy;
import trader.order.standard.StandardOrderStrategy;
import trader.entity.indicator.ma.SimpleMovingAverage;
import trader.entity.indicator.rsi.RelativeStrengthIndex;
import trader.configuration.BGXConfigurationImpl;

import java.math.BigDecimal;
import java.util.HashMap;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RequestOLDBuilderImplTest {

    private static final String BGX_STRATEGY_CONFIG_FILE_NAME = "bgxStrategyConfig.yaml";
    private static final String BROKER_CONFIG_FILE_NAME = "oandaBrokerConfig.yaml";
    private static final String CONNECTOR_NAME = "Oanda";

    private RequestOLDBuilder requestOLDBuilder;
    private HashMap<String, String> settings;

    @Before
    public void setUp(){
        requestOLDBuilder = new RequestBuilderImpl();
        settings = new HashMap<>();

    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallBuildWithNullDataStructureName_Exception(){
        requestOLDBuilder.build(null, null);
    }

    @Test(expected = EmptyArgumentException.class)
    public void WhenCallBuildWithEmptyDataStructureName_Exception(){
        requestOLDBuilder.build("",new HashMap<>());
    }

    @Test(expected = NullArgumentException.class)
    public void whenCallBuildWithNullSettings_Exception(){
        requestOLDBuilder.build("tr", null);
    }

    @Test(expected = NoSuchDataStructureException.class)
    public void whenCallBuildWithNotExistingDataStructureName_Exception(){
        requestOLDBuilder.build("trah", settings);
    }

    @Test(expected = EmptyArgumentException.class)
    public void whenBuildIndicatorWithZeroLengthSettings_Exception(){
        requestOLDBuilder.build("CreateIndicatorController", settings);
    }

    @Test(expected = WrongIndicatorSettingsException.class)
    public void whenBuildIndicatorWithoutTypeInSettings_Exception(){
        settings.put("ro", "ds");
        requestOLDBuilder.build("CreateIndicatorController", settings);
    }

    @Test(expected = WrongIndicatorSettingsException.class)
    public void WhenBuildIndicatorWithNullTypeValue_Exception(){
        settings.put("type", null);
        requestOLDBuilder.build("CreateIndicatorController", settings);
    }

    @Test(expected = WrongIndicatorSettingsException.class)
    public void WhenBuildIndicatorWithEmptyTypeValue_Exception(){
        settings.put("type", "");
        requestOLDBuilder.build("CreateIndicatorController", settings);
    }

    @Test
    public void whenCallBuildWithRSIIndicatorDataStructureName_ReturnRSIRequest(){
        settings.put("type", "rsi");
        Request<?> rsiIndicatorRequest = requestOLDBuilder.build("CreateIndicatorController", settings);

        assertEquals(RelativeStrengthIndex.class, rsiIndicatorRequest.getBody().getClass());
    }

    //sma
    @Test
    public void whenCallBuildWithMovingAverageIndicatorDataStructureName_ReturnMARequest(){
        settings.put("type", "simple");
        settings.put("position", "middle");
        Request<?> smaIndicator = requestOLDBuilder.build("CreateIndicatorController", settings);

        assertEquals(SimpleMovingAverage.class, smaIndicator.getBody().getClass());
    }

    @Test(expected = NoSuchDataStructureException.class)
    public void whenCallBuildWithUnknownIndicator_Exception(){
        settings.put("type", "mxxxx");
        requestOLDBuilder.build("CreateIndicatorController", settings);
    }

    @Test
    public void whenCallBuildWithBGXConfigurationControllerName_ReturnBGXConfigurationRequest() {
        settings.put("location", BGX_STRATEGY_CONFIG_FILE_NAME);
        Request<?> bgxConfigurationRequest = requestOLDBuilder.build("AddBGXConfigurationController", settings);

        assertEquals(BGXConfigurationImpl.class, bgxConfigurationRequest.getBody().getClass());
    }

    @Test
    public void whenCallBuildWithBrokerConfigurationControllerName_ReturnBrokerConfigurationRequest() {
        settings.put("brokerName", CONNECTOR_NAME);
        settings.put("location", BROKER_CONFIG_FILE_NAME);
        Request<?> brokerConnectorRequest = requestOLDBuilder.build("AddBrokerConnectorController", settings);
        String actual = brokerConnectorRequest.getBody().getClass().getSimpleName();
        String expected = CONNECTOR_NAME + "Connector";

        assertEquals(expected, actual);
    }

    @Test
    public void WhenCallBuildWithCreateTradeControllerWithEmptySettings_DefaultTrade(){
        settings.clear();
        Request<?> createTradeRequest = requestOLDBuilder.build("CreateTradeController", settings);
        Trade trade = (Trade) createTradeRequest.getBody();

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
        Request<?> createTradeRequest = requestOLDBuilder.build("CreateTradeController", settings);
        Trade trade = (Trade) createTradeRequest.getBody();

        assertTrue(trade.getTradable());
        assertEquals(Direction.DOWN, trade.getDirection());
        assertEquals(BigDecimal.valueOf(1.12345) ,trade.getEntryPrice());
        assertEquals(BigDecimal.valueOf(5.1234) ,trade.getStopLossPrice());
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallBuildWithAddEntryStrategyControllerWithBadKeyNameInSettings_Exception(){
        settings.put("entry", "standard");
        requestOLDBuilder.build("AddEntryStrategyController", settings);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallBuildWithAddEntryStrategyControllerWithNullValueInSettings_Exception(){
        settings.put("entryStrategy", null);
        requestOLDBuilder.build("AddEntryStrategyController", settings);
    }

    @Test(expected = NoSuchStrategyException.class)
    public void WhenCallBuildWithAddEntryStrategyControllerWithEmptyValueInSettings_Exception(){
        settings.put("entryStrategy", "   ");
        requestOLDBuilder.build("AddEntryStrategyController", settings);
    }

    @Test(expected = NoSuchStrategyException.class)
    public void WhenCallBuildWithAddEntryStrategyControllerWithENonExistingStrtegy_Exception(){
        settings.put("entryStrategy", "non");
        requestOLDBuilder.build("AddEntryStrategyController", settings);
    }

    @Test
    public void WhenCallBuildWithAddEntryStrategyControllerWithCorrectSettings_CorrectEntryStrategy(){
        settings.put("entryStrategy", " standard ");
        Request<?> entryStrategyRequest = requestOLDBuilder.build("AddEntryStrategyController", settings);
        EntryStrategy entryStrategy = (EntryStrategy) entryStrategyRequest.getBody();

        assertEquals(StandardEntryStrategy.class, entryStrategy.getClass());
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallBuildWithAddOrderStrategyControllerWithBadKeyNameInSettings_Exception(){
        settings.put("order", "standard");
        requestOLDBuilder.build("AddOrderStrategyController", settings);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallBuildWithAddOrderStrategyControllerWithNullValueInSettings_Exception(){
        settings.put("orderStrategy", null);
        requestOLDBuilder.build("AddOrderStrategyController", settings);
    }

    @Test(expected = NoSuchStrategyException.class)
    public void WhenCallBuildWithAddOrderStrategyControllerWithEmptyValueInSettings_Exception(){
        settings.put("orderStrategy", "   ");
        requestOLDBuilder.build("AddOrderStrategyController", settings);
    }

    @Test(expected = NoSuchStrategyException.class)
    public void WhenCallBuildWithAddOrderStrategyControllerWithENonExistingStrtegy_Exception(){
        settings.put("orderStrategy", "non");
        requestOLDBuilder.build("AddOrderStrategyController", settings);
    }

    @Test
    public void WhenCallBuildWithAddOrderStrategyControllerWithCorrectSettings_CorrectOrderStrategy(){
        settings.put("orderStrategy", " standard ");
        Request<?> orderStrategyRequest = requestOLDBuilder.build("AddOrderStrategyController", settings);
        OrderStrategy orderStrategy = (OrderStrategy) orderStrategyRequest.getBody();

        assertEquals(StandardOrderStrategy.class, orderStrategy.getClass());
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallBuildWithAddExitStrategyControllerWithBadKeyNameInSettings_Exception(){
        settings.put("exit", "fullClose");
        requestOLDBuilder.build("AddExitStrategyController", settings);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallBuildWithAddExitStrategyControllerWithNullValueInSettings_Exception(){
        settings.put("exitStrategy", null);
        requestOLDBuilder.build("AddExitStrategyController", settings);
    }

    @Test(expected = NoSuchStrategyException.class)
    public void WhenCallBuildWithAddExitStrategyControllerWithEmptyValueInSettings_Exception(){
        settings.put("exitStrategy", "   ");
        requestOLDBuilder.build("AddExitStrategyController", settings);
    }

    @Test(expected = NoSuchStrategyException.class)
    public void WhenCallBuildWithAddExitStrategyControllerWithENonExistingStrategy_Exception(){
        settings.put("exitStrategy", "non");
        requestOLDBuilder.build("AddExitStrategyController", settings);
    }

    @Test
    public void WhenCallBuildWithAddExitStrategyControllerWithCorrectSettings_CorrectExitStrategy(){
        settings.put("exitStrategy", " fullClose ");
        Request<?> exitStrategyRequest = requestOLDBuilder.build("AddExitStrategyController", settings);
        ExitStrategy exitStrategy = (ExitStrategy) exitStrategyRequest.getBody();

        assertEquals(FullCloseExitStrategy.class, exitStrategy.getClass());
    }


}
