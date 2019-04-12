package trader.entry.standard.service;

import org.junit.Before;
import org.junit.Test;
import trader.configuration.TradingStrategyConfiguration;
import trader.entity.trade.Trade;
import trader.entity.trade.point.Point;
import trader.entity.trade.Direction;
import trader.exception.NullArgumentException;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TradeCalculationServiceTest {

    private BigDecimal INTERSECTION_PRICE = BigDecimal.valueOf(1.2000)
            .setScale(5, RoundingMode.HALF_UP);
    private static final BigDecimal DEFAULT_ENTRY_FILTER = BigDecimal.valueOf(0.0020)
            .setScale(5, RoundingMode.HALF_UP);
    private static final BigDecimal DEFAULT_SPREAD = BigDecimal.valueOf(0.0002)
            .setScale(5, RoundingMode.HALF_UP);
    private static final BigDecimal DEFAULT_STOP_LOSS_FILTER = BigDecimal.valueOf(0.0005)
            .setScale(5, RoundingMode.HALF_UP);
    private static final BigDecimal FIRST_TARGET = BigDecimal.valueOf(0.0050)
            .setScale(5, RoundingMode.HALF_UP);

    private TradeCalculationService tradeCalculationService;
    private TradingStrategyConfiguration configurationMock;
    private Point pointMock;



    @Before
    public void setUp() throws Exception {
        pointMock = mock(Point.class);
        when(pointMock.getPrice()).thenReturn(INTERSECTION_PRICE);
        configurationMock = mock(TradingStrategyConfiguration.class);
        tradeCalculationService = new TradeCalculationService();
        setConfiguration();
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallCalculateEntryPriceWithNullPoint_Exception(){
        tradeCalculationService.calculateEntryPrice(null, Direction.FLAT);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallCalculateEntryPriceWithNullDirection_Exception(){
        tradeCalculationService.calculateEntryPrice(pointMock, null);
    }

    @Test
    public void WhenCallCalculateEntryPriceWithDownDirection_CorrectResult(){
        BigDecimal expected = INTERSECTION_PRICE.subtract(configurationMock.getEntryFilter());
        BigDecimal entryPriceValue = tradeCalculationService.calculateEntryPrice(pointMock, Direction.DOWN);

        assertEquals(expected, entryPriceValue);
    }

    @Test
    public void WhenCallCalculateEntryPriceWithUPDirection_CorrectResult(){
        BigDecimal expected = INTERSECTION_PRICE.add(configurationMock.getEntryFilter()).add(configurationMock.getSpread());
        BigDecimal entryPriceValue = tradeCalculationService.calculateEntryPrice(pointMock, Direction.UP);

        assertEquals(expected, entryPriceValue);
    }

    @Test
    public void WhenCallCalculateEntryPriceWithFLATDirection_CorrectResult(){
        BigDecimal expected = INTERSECTION_PRICE.add(configurationMock.getEntryFilter()).add(configurationMock.getSpread());
        BigDecimal entryPriceValue = tradeCalculationService.calculateEntryPrice(pointMock, Direction.FLAT);

        assertEquals(expected, entryPriceValue);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallCalculateStopLossPriceWithNullPoint(){
        tradeCalculationService.calculateStopLossPrice(null, Direction.FLAT);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallCalculateStopLossPriceWithNullDirection(){
        tradeCalculationService.calculateStopLossPrice(pointMock, null);
    }

    @Test
    public void WhenCallCalculateStopLossPriceWithDownDirection_CorrectResult(){
        BigDecimal expected = INTERSECTION_PRICE.add(configurationMock.getStopLossFilter()).add(configurationMock.getSpread());
        BigDecimal stopLossPriceValue = tradeCalculationService.calculateStopLossPrice(pointMock, Direction.DOWN);

        assertEquals(expected, stopLossPriceValue);
    }

    @Test
    public void WhenCallCalculateStopLossPriceWithUpDirection_CorrectResult(){
        BigDecimal expected = INTERSECTION_PRICE.subtract(configurationMock.getStopLossFilter());
        BigDecimal stopLossPriceValue = tradeCalculationService.calculateStopLossPrice(pointMock, Direction.UP);

        assertEquals(expected, stopLossPriceValue);
    }

    @Test
    public void WhenCallCalculateStopLossPriceWithFlatDirection_CorrectResult(){
        BigDecimal expected = INTERSECTION_PRICE.subtract(configurationMock.getStopLossFilter());
        BigDecimal stopLossPriceValue = tradeCalculationService.calculateStopLossPrice(pointMock, Direction.FLAT);

        assertEquals(expected, stopLossPriceValue);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallIsTradableWithNullPoint_Exception(){
        tradeCalculationService.setTradable(null, Direction.FLAT,  BigDecimal.valueOf(1.2070), INTERSECTION_PRICE);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallIsTradableWithNullDirection_Exception(){
        tradeCalculationService.setTradable(pointMock, null,  BigDecimal.valueOf(1.2070), INTERSECTION_PRICE);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallIsTradableWithNullDailyOpenPrice_Exception(){
        tradeCalculationService.setTradable(pointMock, Direction.FLAT, null, INTERSECTION_PRICE);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallIsTradableWithNullEntryPrice_Exception(){
        tradeCalculationService.setTradable(pointMock, Direction.FLAT,  BigDecimal.valueOf(1.2070), null);
    }

    @Test
    public void WhenCallIsTradableWithDownDirectionAndDailyOpenPriceAboveAllPrices_TradableTrue(){
        boolean tradable = tradeCalculationService.setTradable(pointMock, Direction.DOWN,  BigDecimal.valueOf(1.2070), BigDecimal.valueOf(1.1950));

        assertTrue(tradable);
    }

    @Test
    public void WhenCallIsTradableWithDownDirectionAndDailyOpenOnIntersectionPriceAndEntryPriceBelowDailyOpen_TradableTrue(){
        boolean tradable = tradeCalculationService.setTradable(pointMock, Direction.DOWN, INTERSECTION_PRICE, BigDecimal.valueOf(1.1950));

        assertTrue(tradable);
    }

    @Test
    public void WhenCallIsTradableWithDownDirectionAndDailyOpenOnIntersectionPriceAndEntryPriceONDailyOpen_TradableTrue(){
        boolean tradable = tradeCalculationService.setTradable(pointMock, Direction.DOWN, INTERSECTION_PRICE, INTERSECTION_PRICE);

        assertTrue(tradable);
    }

    @Test
    public void WhenCallIsTradableWithDownDirectionAndDailyOpenOnIntersectionPriceAndEntryPriceAboveDailyOpen_TradableFalse(){
        boolean tradable = tradeCalculationService.setTradable(pointMock, Direction.DOWN, INTERSECTION_PRICE, INTERSECTION_PRICE.add(BigDecimal.valueOf(0.050)));

        assertFalse(tradable);
    }

    @Test
    public void WhenCallIsTradableWithDownDirectionAndDailyOpenBetweenIntersectionPriceAndEntryPrice_TradableTrue(){
        boolean tradable = tradeCalculationService.setTradable(pointMock, Direction.DOWN, INTERSECTION_PRICE.subtract(BigDecimal.valueOf(0.0010)), INTERSECTION_PRICE.subtract(configurationMock.getEntryFilter()));

        assertTrue(tradable);
    }

    @Test
    public void WhenCallIsTradableWithDownDirectionAndDailyOpenBelowIntersectionPriceAndOnEntryPrice_TradableTrue(){
        boolean tradable = tradeCalculationService.setTradable(pointMock, Direction.DOWN, INTERSECTION_PRICE.subtract(configurationMock.getEntryFilter()), INTERSECTION_PRICE.subtract(configurationMock.getEntryFilter()));

        assertTrue(tradable);
    }

    @Test
    public void WhenCallIsTradableWithDownDirectionAndDailyOpenBelowIntersectionPriceAndBelowEntryPriceButAboveFirstTargetPrice_TradableFalse(){

        boolean tradable = tradeCalculationService.setTradable(pointMock, Direction.DOWN, INTERSECTION_PRICE.subtract(BigDecimal.valueOf(0.0069)), INTERSECTION_PRICE.subtract(configurationMock.getEntryFilter()));

        assertFalse(tradable);
    }

    @Test
    public void WhenCallIsTradableWithDownDirectionAndDailyOpenBelowIntersectionPriceAndBelowEntryPriceButOnOrBelowFirstTargetPrice_TradableTrue(){

        boolean tradableOn = tradeCalculationService.setTradable(pointMock, Direction.DOWN, INTERSECTION_PRICE.subtract(BigDecimal.valueOf(0.0070)), INTERSECTION_PRICE.subtract(configurationMock.getEntryFilter()));
        boolean tradableBelow = tradeCalculationService.setTradable(pointMock, Direction.DOWN, INTERSECTION_PRICE.subtract(BigDecimal.valueOf(0.0071)), INTERSECTION_PRICE.subtract(configurationMock.getEntryFilter()));

        assertTrue(tradableOn);
        assertTrue(tradableBelow);
    }

    @Test
    public void WhenCallIsTradableWithUpDirectionAndDailyOpenPriceBelowAllPrices_TradableTrue(){
        boolean tradable = tradeCalculationService.setTradable(pointMock, Direction.UP,  BigDecimal.valueOf(1.1920), BigDecimal.valueOf(1.2070));

        assertTrue(tradable);
    }

    @Test
    public void WhenCallIsTradableWithUpDirectionAndDailyOpenOnIntersectionPriceAndEntryPriceAboveDailyOpen_TradableTrue(){
        boolean tradable = tradeCalculationService.setTradable(pointMock, Direction.UP, INTERSECTION_PRICE, BigDecimal.valueOf(1.2020));

        assertTrue(tradable);
    }

    @Test
    public void WhenCallIsTradableWithUpDirectionAndDailyOpenOnIntersectionPriceAndEntryPriceONDailyOpen_TradableTrue(){
        boolean tradable = tradeCalculationService.setTradable(pointMock, Direction.UP, INTERSECTION_PRICE, INTERSECTION_PRICE);

        assertTrue(tradable);
    }

    @Test
    public void WhenCallIsTradableWithUpDirectionAndDailyOpenOnIntersectionPriceAndEntryPriceBelowDailyOpen_TradableFalse(){
        boolean tradable = tradeCalculationService.setTradable(pointMock, Direction.UP, INTERSECTION_PRICE, INTERSECTION_PRICE.subtract(BigDecimal.valueOf(0.050)));

        assertFalse(tradable);
    }

    @Test
    public void WhenCallIsTradableWithUpDirectionAndDailyOpenBetweenIntersectionPriceAndEntryPrice_TradableTrue(){
        boolean tradable = tradeCalculationService.setTradable(pointMock, Direction.UP, INTERSECTION_PRICE.add(BigDecimal.valueOf(0.0010)), INTERSECTION_PRICE.add(configurationMock.getEntryFilter()));

        assertTrue(tradable);
    }

    @Test
    public void WhenCallIsTradableWithUpDirectionAndDailyOpenBelowIntersectionPriceAndOnEntryPrice_TradableTrue(){
        boolean tradable = tradeCalculationService.setTradable(pointMock, Direction.UP, INTERSECTION_PRICE.add(configurationMock.getEntryFilter()), INTERSECTION_PRICE.add(configurationMock.getEntryFilter()));

        assertTrue(tradable);
    }

    @Test
    public void WhenCallIsTradableWithUpDirectionAndDailyOpenAboveIntersectionPriceAndAboveEntryPriceButBelowFirstTargetPrice_TradableFalse(){

        boolean tradable = tradeCalculationService.setTradable(pointMock, Direction.UP, INTERSECTION_PRICE.add(BigDecimal.valueOf(0.0069)), INTERSECTION_PRICE.add(configurationMock.getEntryFilter()));

        assertFalse(tradable);
    }

    @Test
    public void WhenCallIsTradableWithUpDirectionAndDailyOpenAboveIntersectionPriceAndAboveEntryPriceButOnOrAboveFirstTargetPrice_TradableTrue(){

        boolean tradableOn = tradeCalculationService.setTradable(pointMock, Direction.UP, INTERSECTION_PRICE.add(BigDecimal.valueOf(0.0070)), INTERSECTION_PRICE.add(configurationMock.getEntryFilter()));
        boolean tradableBelow = tradeCalculationService.setTradable(pointMock, Direction.UP, INTERSECTION_PRICE.add(BigDecimal.valueOf(0.0071)), INTERSECTION_PRICE.add(configurationMock.getEntryFilter()));

        assertTrue(tradableOn);
        assertTrue(tradableBelow);
    }

    private void setConfiguration() {
        tradeCalculationService.setConfiguration(configurationMock);
        when(configurationMock.getSpread()).thenReturn(DEFAULT_SPREAD);
        when(configurationMock.getEntryFilter()).thenReturn(DEFAULT_ENTRY_FILTER);
        when(configurationMock.getStopLossFilter()).thenReturn(DEFAULT_STOP_LOSS_FILTER);
        when(configurationMock.getTarget()).thenReturn(FIRST_TARGET);
    }

}
