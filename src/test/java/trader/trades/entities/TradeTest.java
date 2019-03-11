package trader.trades.entities;

import org.junit.Before;
import org.junit.Test;
import trader.trades.enums.Direction;

import java.math.BigDecimal;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TradeTest {


    private static final BigDecimal DEFAULT_SPREAD = BigDecimal.valueOf(0.0002);
    private static final BigDecimal DEFAULT_FILTER = BigDecimal.valueOf(0.0020);
    private static final BigDecimal DEFAULT_STOP_LOSS_FILTER = BigDecimal.valueOf(0.0005);
    private static final BigDecimal FIRST_TARGET = BigDecimal.valueOf(0.0050);

    private Trade trade;
    private Point mockPoint;

    @Before
    public void before() throws Exception {

        this.mockPoint = mock(Point.class);

    }

    @Test(expected = NullPointerException.class)
    public void WhenCreateNewSignalWithNullPointThenException(){
        this.trade = new Trade(null, Direction.DOWN, BigDecimal.ONE);
    }

    @Test(expected = NullPointerException.class)
    public void WhenCreateNewSignalWithNullDirectionThenException(){
        this.trade = new Trade(new Point.PointBuilder(BigDecimal.ONE).build(), null, BigDecimal.ONE);
    }

    @Test(expected = NullPointerException.class)
    public void WhenCreateNewSignalWithNullDailyOpenThenException(){
        this.trade = new Trade(new Point.PointBuilder(BigDecimal.ONE).build(), null, BigDecimal.ONE);
    }

    @Test
    public void WhenCreateTradeWithDirectionDownThenStopLossMustBeCalculatedCorrectly(){
        //stop loss if trade is down is equal to intersectionPointValue + Spread + StopLossFilter

        BigDecimal intersectionPrice = BigDecimal.valueOf(1.12345);
        when(this.mockPoint.getPrice()).thenReturn(intersectionPrice);
        BigDecimal dailyOpen = BigDecimal.valueOf(1.54321);
        Trade trade = new Trade(this.mockPoint, Direction.DOWN, dailyOpen);

        BigDecimal expected = intersectionPrice.add(DEFAULT_SPREAD).setScale(5, BigDecimal.ROUND_HALF_UP);
        expected = expected.add(DEFAULT_STOP_LOSS_FILTER).setScale(5, BigDecimal.ROUND_HALF_UP);

        BigDecimal stopLoss = trade.getStopLossPrice();
        int compare = expected.compareTo(stopLoss);
        assertEquals(0, compare);

    }

    @Test
    public void WhenCreateTradeWithDirectionUpThenStopLossMustBeCalculatedCorrectly(){
        //stop loss if trade is down is equal to intersectionPointValue + Spread + StopLossFilter

        BigDecimal intersectionPrice = BigDecimal.valueOf(1.12345);
        when(this.mockPoint.getPrice()).thenReturn(intersectionPrice);
        BigDecimal dailyOpen = BigDecimal.valueOf(1.54321);
        Trade trade = new Trade(this.mockPoint, Direction.UP, dailyOpen);

        BigDecimal expected = intersectionPrice.subtract(DEFAULT_STOP_LOSS_FILTER).setScale(5, BigDecimal.ROUND_HALF_UP);

        BigDecimal stopLoss = trade.getStopLossPrice();
        int compare = expected.compareTo(stopLoss);
        assertEquals(0, compare);

    }

    @Test
    public void WhenCreateTradeWithDirectionUpThenEntryPriceMustBeCalculatedCorrectly(){

        BigDecimal intersectionPrice = BigDecimal.valueOf(1.12345);
        when(this.mockPoint.getPrice()).thenReturn(intersectionPrice);
        BigDecimal dailyOpen = BigDecimal.valueOf(1.54321);
        Trade trade = new Trade(this.mockPoint, Direction.UP, dailyOpen);

        BigDecimal expected = intersectionPrice.add(DEFAULT_SPREAD).setScale(5, BigDecimal.ROUND_HALF_UP);
        expected = expected.add(DEFAULT_FILTER).setScale(5, BigDecimal.ROUND_HALF_UP);

        BigDecimal entryPrice = trade.getEntryPrice();
        int compare = expected.compareTo(entryPrice);
        assertEquals(0, compare);
    }

    @Test
    public void WhenCreateTradeWithDirectionDownThenEntryPriceMustBeCalculatedCorrectly(){

        BigDecimal intersectionPrice = BigDecimal.valueOf(1.12345);
        when(this.mockPoint.getPrice()).thenReturn(intersectionPrice);
        BigDecimal dailyOpen = BigDecimal.valueOf(1.54321);
        Trade trade = new Trade(this.mockPoint, Direction.DOWN, dailyOpen);

        BigDecimal expected = intersectionPrice.subtract(DEFAULT_FILTER).setScale(5, BigDecimal.ROUND_HALF_UP);

        BigDecimal entryPrice = trade.getEntryPrice();
        int compare = expected.compareTo(entryPrice);
        assertEquals(0, compare);
    }


    @Test
    public void WhenCreateTradeWithDirectionDownAndDailyOpenIsFiftyPipsBelowEntryPriceThenTradableTrue(){

        BigDecimal intersectionPrice = BigDecimal.valueOf(1.12345);
        when(this.mockPoint.getPrice()).thenReturn(intersectionPrice);
        BigDecimal dailyOpen = BigDecimal.valueOf(1.11595);
        Trade trade = new Trade(this.mockPoint, Direction.DOWN, dailyOpen);

       assertTrue(trade.getTradable());

    }
    @Test
    public void WhenCreateTradeWithDirectionUpAndDailyOpenIsFiftyPipsAboveEntryPriceThenTradableTrue(){

        BigDecimal intersectionPrice = BigDecimal.valueOf(1.12345);
        when(this.mockPoint.getPrice()).thenReturn(intersectionPrice);
        BigDecimal dailyOpen = BigDecimal.valueOf(1.13115);
        Trade trade = new Trade(this.mockPoint, Direction.UP, dailyOpen);

        assertTrue(trade.getTradable());

    }


    @Test
    public void WhenCreateTradeWithDirectionDownAndDailyOpenIsLessThanFiftyPipsBelowEntryPriceThenTradableFalse(){

        BigDecimal intersectionPrice = BigDecimal.valueOf(1.12345);
        when(this.mockPoint.getPrice()).thenReturn(intersectionPrice);
        BigDecimal dailyOpen = BigDecimal.valueOf(1.11696);
        Trade trade = new Trade(this.mockPoint, Direction.DOWN, dailyOpen);

        assertFalse(trade.getTradable());

    }
    @Test
    public void WhenCreateTradeWithDirectionUPAndDailyOpenIsLessThanFiftyPipsAboveEntryPriceThenTradableFalse(){

        BigDecimal intersectionPrice = BigDecimal.valueOf(1.12345);
        when(this.mockPoint.getPrice()).thenReturn(intersectionPrice);
        BigDecimal dailyOpen = BigDecimal.valueOf(1.12745);
        Trade trade = new Trade(this.mockPoint, Direction.UP, dailyOpen);

        assertFalse(trade.getTradable());

    }


    @Test
    public void WhenCreateTradeWithDirectionDownAndDailyOpenIsBetweenIntersectionPriceAndEntryPriceThenTradableTrue(){

        BigDecimal intersectionPrice = BigDecimal.valueOf(1.12345);
        when(this.mockPoint.getPrice()).thenReturn(intersectionPrice);
        //daily Open must be above filter intersectionPrice - DEFAULT_ENTRY_FILTER
        BigDecimal dailyOpen = BigDecimal.valueOf(1.12146);
        Trade trade = new Trade(this.mockPoint, Direction.DOWN, dailyOpen);

        assertTrue(trade.getTradable());

    }
    @Test
    public void WhenCreateTradeWithDirectionUPAndDailyOpenIsBetweenIntersectionPriceAndEntryPriceThenTradableTrue(){

        BigDecimal intersectionPrice = BigDecimal.valueOf(1.12345);
        when(this.mockPoint.getPrice()).thenReturn(intersectionPrice);
        BigDecimal dailyOpen = BigDecimal.valueOf(1.12535);
        Trade trade = new Trade(this.mockPoint, Direction.UP, dailyOpen);

        assertTrue(trade.getTradable());

    }


    @Test
    public void WhenCreateTradeWithDirectionDownAndDailyOpenIsAboveIntersectionPriceThenTradableTrue(){

        BigDecimal intersectionPrice = BigDecimal.valueOf(1.12345);
        when(this.mockPoint.getPrice()).thenReturn(intersectionPrice);
        BigDecimal dailyOpen = BigDecimal.valueOf(1.12346);
        Trade trade = new Trade(this.mockPoint, Direction.DOWN, dailyOpen);

        assertTrue(trade.getTradable());

    }
    @Test
    public void WhenCreateTradeWithDirectionUPAndDailyOpenIsBelowIntersectionPriceThenTradableTrue(){

        BigDecimal intersectionPrice = BigDecimal.valueOf(1.12345);
        when(this.mockPoint.getPrice()).thenReturn(intersectionPrice);
        BigDecimal dailyOpen = BigDecimal.valueOf(1.12344);
        Trade trade = new Trade(this.mockPoint, Direction.UP, dailyOpen);

        assertTrue(trade.getTradable());

    }

    @Test
    public void WhenCreateTradeWithDirectionFLATThenTradableFalse(){

        BigDecimal intersectionPrice = BigDecimal.valueOf(1.12345);
        when(this.mockPoint.getPrice()).thenReturn(intersectionPrice);
        BigDecimal dailyOpen = BigDecimal.valueOf(1.13114);
        Trade trade = new Trade(new Point.PointBuilder(BigDecimal.ONE).build(), Direction.FLAT, BigDecimal.ONE);

        assertFalse(trade.getTradable());

    }


}