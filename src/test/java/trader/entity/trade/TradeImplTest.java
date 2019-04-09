package trader.entity.trade;

import org.junit.Before;
import org.junit.Test;
import trader.entity.point.PointImpl;
import trader.exception.NegativeNumberException;

import java.math.BigDecimal;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TradeImplTest {


    private static final BigDecimal DEFAULT_SPREAD = BigDecimal.valueOf(0.0002);
    private static final BigDecimal DEFAULT_FILTER = BigDecimal.valueOf(0.0020);
    private static final BigDecimal DEFAULT_STOP_LOSS_FILTER = BigDecimal.valueOf(0.0005);
    private static final BigDecimal FIRST_TARGET = BigDecimal.valueOf(0.0050);

    private static final BigDecimal DEFAULT_PRICE = BigDecimal.valueOf(0.0001);

    private Trade trade;
    private PointImpl mockPoint;

    @Before
    public void before() throws Exception {

        trade = new TradeImpl();
        this.mockPoint = mock(PointImpl.class);

    }

    @Test
    public void WhenCreateThenDefaultValues(){
        assertFalse(trade.getTradable());
        assertEquals(Direction.FLAT, trade.getDirection());
        assertEquals(DEFAULT_PRICE, trade.getEntryPrice());
        assertEquals(DEFAULT_PRICE, trade.getStopLossPrice());
    }

    @Test
    public void WhenTryToSetFieldsToNull_Default(){
        setWithNull(null);

        assertDefaultValues();
    }

    @Test
    public void WhenTryToSetFieldsWithEmptyValue_Default(){
        setWithNull("   ");

        assertDefaultValues();
    }


    @Test
    public void WhenTryToSetDirectionWithCorrectValue_CorrectUpdate(){
        trade.setDirection("up");

        assertEquals(Direction.UP, trade.getDirection());
    }

    @Test
    public void WhenTryToSetTradableWithCorrectValue_CorrectUpdate(){
        trade.setTradable("true");

        assertTrue(trade.getTradable());
    }

    @Test
    public void WhenTryToSetEntryPriceWithCorrectValue_CorrectUpdate(){
        trade.setEntryPrice("12");

        assertEquals(BigDecimal.valueOf(12), trade.getEntryPrice());
    }

    @Test
    public void WhenTryToSetStopLossPriceWithCorrectValue_CorrectUpdate(){
        trade.setStopLossPrice("0.045");

        assertEquals(BigDecimal.valueOf(0.045), trade.getStopLossPrice());
    }


    @Test(expected = IllegalArgumentException.class)
    public void WhenTryToSetDirectionWithNotExistingDirection_Exception(){
        trade.setDirection("bop");

        assertEquals(Direction.UP, trade.getDirection());
    }

    @Test
    public void WhenTryToSetTradableWithNotExistingValue_Default(){
        trade.setTradable("rrrr");
    }

    @Test(expected = NumberFormatException.class)
    public void WhenTryToSetEntryPriceWithNotExistingValue_Exception(){
        trade.setEntryPrice("rrrr");
    }

    @Test(expected = NegativeNumberException.class)
    public void WhenTryToSetEntryPriceWithValueLessThan0_Exception(){
        trade.setEntryPrice("-0.002");
    }

    @Test(expected = NumberFormatException.class)
    public void WhenTryToSetStopPriceWithNotExistingValue_Exception(){
        trade.setEntryPrice("rrrr");
    }

    @Test(expected = NegativeNumberException.class)
    public void WhenTryToSetStopLossPriceWithValueLessThan0_Exception(){
        trade.setEntryPrice("-0.1222");
    }



    private void setWithNull(String o) {
        trade.setDirection(o);
        trade.setTradable(o);
        trade.setEntryPrice(o);
        trade.setStopLossPrice(o);
    }

    private void assertDefaultValues() {
        assertEquals(Direction.FLAT, trade.getDirection());
        assertFalse(trade.getTradable());
        assertEquals(DEFAULT_PRICE ,trade.getEntryPrice());
        assertEquals(DEFAULT_PRICE ,trade.getStopLossPrice());
    }

//    @Test
//    public void WhenTryToSetTradableWithNull_Default(){
//
//    }

    @Test(expected = NullPointerException.class)
    public void WhenCreateNewSignalWithNullPointThenException(){
        this.trade = new TradeImpl(null, Direction.DOWN, BigDecimal.ONE);
    }

    @Test(expected = NullPointerException.class)
    public void WhenCreateNewSignalWithNullDirectionThenException(){
        this.trade = new TradeImpl(new PointImpl.PointBuilder(BigDecimal.ONE).build(), null, BigDecimal.ONE);
    }

    @Test(expected = NullPointerException.class)
    public void WhenCreateNewSignalWithNullDailyOpenThenException(){
        this.trade = new TradeImpl(new PointImpl.PointBuilder(BigDecimal.ONE).build(), null, BigDecimal.ONE);
    }

    @Test
    public void WhenCreateTradeWithDirectionDownThenStopLossMustBeCalculatedCorrectly(){
        //stop loss if tradeImpl is down is equal to intersectionPointValue + Spread + StopLossFilter

        BigDecimal intersectionPrice = BigDecimal.valueOf(1.12345);
        when(this.mockPoint.getPrice()).thenReturn(intersectionPrice);
        BigDecimal dailyOpen = BigDecimal.valueOf(1.54321);
        TradeImpl tradeImpl = new TradeImpl(this.mockPoint, Direction.DOWN, dailyOpen);

        BigDecimal expected = intersectionPrice.add(DEFAULT_SPREAD).setScale(5, BigDecimal.ROUND_HALF_UP);
        expected = expected.add(DEFAULT_STOP_LOSS_FILTER).setScale(5, BigDecimal.ROUND_HALF_UP);

        BigDecimal stopLoss = tradeImpl.getStopLossPrice();
        int compare = expected.compareTo(stopLoss);
        assertEquals(0, compare);

    }

    @Test
    public void WhenCreateTradeWithDirectionUpThenStopLossMustBeCalculatedCorrectly(){
        //stop loss if tradeImpl is down is equal to intersectionPointValue + Spread + StopLossFilter

        BigDecimal intersectionPrice = BigDecimal.valueOf(1.12345);
        when(this.mockPoint.getPrice()).thenReturn(intersectionPrice);
        BigDecimal dailyOpen = BigDecimal.valueOf(1.54321);
        TradeImpl tradeImpl = new TradeImpl(this.mockPoint, Direction.UP, dailyOpen);

        BigDecimal expected = intersectionPrice.subtract(DEFAULT_STOP_LOSS_FILTER).setScale(5, BigDecimal.ROUND_HALF_UP);

        BigDecimal stopLoss = tradeImpl.getStopLossPrice();
        int compare = expected.compareTo(stopLoss);
        assertEquals(0, compare);

    }

    @Test
    public void WhenCreateTradeWithDirectionUpThenEntryPriceMustBeCalculatedCorrectly(){

        BigDecimal intersectionPrice = BigDecimal.valueOf(1.12345);
        when(this.mockPoint.getPrice()).thenReturn(intersectionPrice);
        BigDecimal dailyOpen = BigDecimal.valueOf(1.54321);
        TradeImpl tradeImpl = new TradeImpl(this.mockPoint, Direction.UP, dailyOpen);

        BigDecimal expected = intersectionPrice.add(DEFAULT_SPREAD).setScale(5, BigDecimal.ROUND_HALF_UP);
        expected = expected.add(DEFAULT_FILTER).setScale(5, BigDecimal.ROUND_HALF_UP);

        BigDecimal entryPrice = tradeImpl.getEntryPrice();
        int compare = expected.compareTo(entryPrice);
        assertEquals(0, compare);
    }

    @Test
    public void WhenCreateTradeWithDirectionDownThenEntryPriceMustBeCalculatedCorrectly(){

        BigDecimal intersectionPrice = BigDecimal.valueOf(1.12345);
        when(this.mockPoint.getPrice()).thenReturn(intersectionPrice);
        BigDecimal dailyOpen = BigDecimal.valueOf(1.54321);
        TradeImpl tradeImpl = new TradeImpl(this.mockPoint, Direction.DOWN, dailyOpen);

        BigDecimal expected = intersectionPrice.subtract(DEFAULT_FILTER).setScale(5, BigDecimal.ROUND_HALF_UP);

        BigDecimal entryPrice = tradeImpl.getEntryPrice();
        int compare = expected.compareTo(entryPrice);
        assertEquals(0, compare);
    }


    @Test
    public void WhenCreateTradeWithDirectionDownAndDailyOpenIsFiftyPipsBelowEntryPriceThenTradableTrue(){

        BigDecimal intersectionPrice = BigDecimal.valueOf(1.12345);
        when(this.mockPoint.getPrice()).thenReturn(intersectionPrice);
        BigDecimal dailyOpen = BigDecimal.valueOf(1.11595);
        TradeImpl tradeImpl = new TradeImpl(this.mockPoint, Direction.DOWN, dailyOpen);

       assertTrue(tradeImpl.getTradable());

    }
    @Test
    public void WhenCreateTradeWithDirectionUpAndDailyOpenIsFiftyPipsAboveEntryPriceThenTradableTrue(){

        BigDecimal intersectionPrice = BigDecimal.valueOf(1.12345);
        when(this.mockPoint.getPrice()).thenReturn(intersectionPrice);
        BigDecimal dailyOpen = BigDecimal.valueOf(1.13115);
        TradeImpl tradeImpl = new TradeImpl(this.mockPoint, Direction.UP, dailyOpen);

        assertTrue(tradeImpl.getTradable());

    }


    @Test
    public void WhenCreateTradeWithDirectionDownAndDailyOpenIsLessThanFiftyPipsBelowEntryPriceThenTradableFalse(){

        BigDecimal intersectionPrice = BigDecimal.valueOf(1.12345);
        when(this.mockPoint.getPrice()).thenReturn(intersectionPrice);
        BigDecimal dailyOpen = BigDecimal.valueOf(1.11696);
        TradeImpl tradeImpl = new TradeImpl(this.mockPoint, Direction.DOWN, dailyOpen);

        assertFalse(tradeImpl.getTradable());

    }
    @Test
    public void WhenCreateTradeWithDirectionUPAndDailyOpenIsLessThanFiftyPipsAboveEntryPriceThenTradableFalse(){

        BigDecimal intersectionPrice = BigDecimal.valueOf(1.12345);
        when(this.mockPoint.getPrice()).thenReturn(intersectionPrice);
        BigDecimal dailyOpen = BigDecimal.valueOf(1.12745);
        TradeImpl tradeImpl = new TradeImpl(this.mockPoint, Direction.UP, dailyOpen);

        assertFalse(tradeImpl.getTradable());

    }


    @Test
    public void WhenCreateTradeWithDirectionDownAndDailyOpenIsBetweenIntersectionPriceAndEntryPriceThenTradableTrue(){

        BigDecimal intersectionPrice = BigDecimal.valueOf(1.12345);
        when(this.mockPoint.getPrice()).thenReturn(intersectionPrice);
        //daily Open must be above filter intersectionPrice - DEFAULT_ENTRY_FILTER
        BigDecimal dailyOpen = BigDecimal.valueOf(1.12146);
        TradeImpl tradeImpl = new TradeImpl(this.mockPoint, Direction.DOWN, dailyOpen);

        assertTrue(tradeImpl.getTradable());

    }
    @Test
    public void WhenCreateTradeWithDirectionUPAndDailyOpenIsBetweenIntersectionPriceAndEntryPriceThenTradableTrue(){

        BigDecimal intersectionPrice = BigDecimal.valueOf(1.12345);
        when(this.mockPoint.getPrice()).thenReturn(intersectionPrice);
        BigDecimal dailyOpen = BigDecimal.valueOf(1.12535);
        TradeImpl tradeImpl = new TradeImpl(this.mockPoint, Direction.UP, dailyOpen);

        assertTrue(tradeImpl.getTradable());

    }


    @Test
    public void WhenCreateTradeWithDirectionDownAndDailyOpenIsAboveIntersectionPriceThenTradableTrue(){

        BigDecimal intersectionPrice = BigDecimal.valueOf(1.12345);
        when(this.mockPoint.getPrice()).thenReturn(intersectionPrice);
        BigDecimal dailyOpen = BigDecimal.valueOf(1.12346);
        TradeImpl tradeImpl = new TradeImpl(this.mockPoint, Direction.DOWN, dailyOpen);

        assertTrue(tradeImpl.getTradable());

    }
    @Test
    public void WhenCreateTradeWithDirectionUPAndDailyOpenIsBelowIntersectionPriceThenTradableTrue(){

        BigDecimal intersectionPrice = BigDecimal.valueOf(1.12345);
        when(this.mockPoint.getPrice()).thenReturn(intersectionPrice);
        BigDecimal dailyOpen = BigDecimal.valueOf(1.12344);
        TradeImpl tradeImpl = new TradeImpl(this.mockPoint, Direction.UP, dailyOpen);

        assertTrue(tradeImpl.getTradable());

    }

    @Test
    public void WhenCreateTradeWithDirectionFLATThenTradableFalse(){

        BigDecimal intersectionPrice = BigDecimal.valueOf(1.12345);
        when(this.mockPoint.getPrice()).thenReturn(intersectionPrice);
        BigDecimal dailyOpen = BigDecimal.valueOf(1.13114);
        TradeImpl tradeImpl = new TradeImpl(new PointImpl.PointBuilder(BigDecimal.ONE).build(), Direction.FLAT, BigDecimal.ONE);

        assertFalse(tradeImpl.getTradable());

    }


}