package trader.interactor;

import org.junit.Before;
import org.junit.Test;
import trader.entity.point.Point;
import trader.entity.trade.Direction;
import trader.entity.trade.Trade;
import trader.exception.*;
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

    @Test
    public void whenCallBuildWithCreatePointControllerName_ReturnPointRequest() {
        String price = "1.2020";
        String time = "1";
        settings.put("price", price);
        settings.put("time", time);
        Request<?> pointRequest = requestBuilder.build("CreatePointController", settings);
        Point point = (Point) pointRequest.getRequestDataStructure();

        assertEquals(new BigDecimal(price), point.getPrice());
        assertEquals(new BigDecimal(time), point.getTime());
    }

    @Test(expected = NoSuchDataStructureException.class)
    public void whenCallBuildWithCreatePointControllerWithLessThanMinSettings_Exception() {
        settings.clear();
        requestBuilder.build("CreatePointController", settings);
    }

    @Test(expected = NoSuchDataStructureException.class)
    public void whenCallBuildWithCreatePointControllerWithSettingsWithoutPriceKeyName_Exception() {
        settings.put("pra", "1.22");
        requestBuilder.build("CreatePointController", settings);
    }

    @Test(expected = NullPointerException.class)
    public void whenCallBuildWithCreatePointControllerWithSettingsWithoutTimeKeyName_Exception() {
        settings.put("price", "1.22");
        settings.put("rr", "1");
        requestBuilder.build("CreatePointController", settings);
    }


    @Test(expected = NumberFormatException.class)
    public void whenCallBuildWithCreatePointControllerWithPriceThatIsNotNumber_Exception() {
        settings.put("price", "price");
        requestBuilder.build("CreatePointController", settings);
    }

    @Test(expected = NumberFormatException.class)
    public void whenCallBuildWithCreatePointControllerWithTimeThatIsNotNumbers_Exception() {
        settings.put("price", "1.12");
        settings.put("time", "time");
        requestBuilder.build("CreatePointController", settings);
    }


}
