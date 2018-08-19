package trader.trades.services.exit_strategies;

import com.oanda.v20.Context;
import com.oanda.v20.account.Account;
import com.oanda.v20.instrument.*;
import com.oanda.v20.order.*;
import com.oanda.v20.pricing_common.PriceValue;
import com.oanda.v20.primitives.DateTime;
import com.oanda.v20.trade.*;
import org.junit.Before;
import org.junit.Test;
import trader.candles.CandlesUpdater;


import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BaseExitStrategyTest {

    private static final BigDecimal INITIAL_UNITS = BigDecimal.valueOf(100);
    private static final BigDecimal CURRENT_UNITS = BigDecimal.valueOf(50);
    private static final BigDecimal TRADE_PRICE = BigDecimal.valueOf(1.14345);

    private List<BigDecimal> candlesClosePrices = new ArrayList<>();
    private List<BigDecimal> candlesLowPrices = new ArrayList<>();
    private List<BigDecimal> candlesHighPrices = new ArrayList<>();

    private BaseExitStrategy baseExitStrategy;
    private List<Candlestick> candlestickList;
    private CandlestickGranularity candlestickGranularity;
    private Context mockContext;
    private Account mockAccount;
    private CandlesUpdater mockCandlesUpdater;
    private OrderCreateResponse mockOrderCreateResponse;
    private InstrumentCandlesResponse mockResponse;
    private TradeSummary mockTrade;
    private TradeID mockTradeID;
    private OrderID mockOrderID;
    private List<TradeSummary> trades;
    private List<Order> orders;
    private StopLossOrder mockOrder;
    private DateTime mockDateTime;
    private TradeSetDependentOrdersResponse mockTradeSetDependentOrdersResponse;



    @Before
    public void before() throws Exception {

        this.candlestickGranularity = CandlestickGranularity.M30;

        this.mockContext = mock(Context.class);

        //mock order
        this.mockContext.order = mock(OrderContext.class);
        this.mockOrderCreateResponse = mock(OrderCreateResponse.class);
        when(this.mockContext.order.create(any(OrderCreateRequest.class))).thenReturn(this.mockOrderCreateResponse);

        //mock instrument for candleUpdater
        this.mockResponse = mock(InstrumentCandlesResponse.class);
        when(this.mockResponse.getCandles()).thenReturn(new ArrayList<>());
        this.mockContext.instrument = mock(InstrumentContext.class);
        when(this.mockContext.instrument.candles(any(InstrumentCandlesRequest.class))).thenReturn(this.mockResponse);

        //mock trade
        this.mockContext.trade = mock(TradeContext.class);
        this.mockTradeSetDependentOrdersResponse = mock(TradeSetDependentOrdersResponse.class);
        when(this.mockContext.trade.setDependentOrders(any(TradeSetDependentOrdersRequest.class))).thenReturn(this.mockTradeSetDependentOrdersResponse);

        //mock trade
        this.mockTrade = mock(TradeSummary.class);
        this.mockTradeID = mock(TradeID.class);
        when(this.mockTrade.getId()).thenReturn(this.mockTradeID);

        this.mockOrderID = mock(OrderID.class);
        when(this.mockTrade.getStopLossOrderID()).thenReturn(this.mockOrderID);

        //trades
        this.trades = new ArrayList<>();
        this.trades.add(this.mockTrade);

        //orders
        this.orders = new ArrayList<>();
        this.mockOrder = mock(StopLossOrder.class);
        when(this.mockOrder.getType()).thenReturn(OrderType.STOP_LOSS);
        when(this.mockOrder.getId()).thenReturn(this.mockOrderID);
        this.orders.add(this.mockOrder);

        //dateTime
        this.mockDateTime = mock(DateTime.class);

        //mock account;
        this.mockAccount = mock(Account.class);
        when(this.mockAccount.getTrades()).thenReturn(this.trades);
        when(this.mockAccount.getOrders()).thenReturn(this.orders);

        //mock candlesUpdater
        this.mockCandlesUpdater = mock(CandlesUpdater.class);
        when(this.mockCandlesUpdater.updateCandles(this.mockDateTime)).thenReturn(true);
        when(this.mockCandlesUpdater.getCandles()).thenReturn(this.candlestickList);

        this.baseExitStrategy = new BaseExitStrategy(this.mockContext, this.candlestickGranularity);
    }

    @Test(expected = NullPointerException.class)
    public void WhenCreateBaseExitStrategyWithNullContextThenException(){
        new BaseExitStrategy(null, this.candlestickGranularity);
    }

    @Test(expected = NullPointerException.class)
    public void WhenCreateBaseExitStrategyWithNullCandlestickGranularityThenException(){
        new BaseExitStrategy(this.mockContext, null);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void WhenGetLastFullCandleHighWithDifferentNumberOfRequiredCandlesThenException(){
        //candlestickList is empty and required number of candles is 2
        BigDecimal result = this.baseExitStrategy.getLastFullCandleHigh();
    }
    
    @Test
    public void WhenGetLastFullCandleHighThenReturnCorrectValue() throws NoSuchFieldException, IllegalAccessException {

        BigDecimal max = BigDecimal.valueOf(1.14445);
        this.candlesClosePrices.add(max);
        this.candlesLowPrices.add(max);
        this.candlesHighPrices.add(max);

        BigDecimal min = BigDecimal.valueOf(1.11445);
        this.candlesClosePrices.add(min);
        this.candlesLowPrices.add(min);
        this.candlesHighPrices.add(min);

        this.setCandlesUpdaterToMock();
        fillCandlestickList();

        when(this.mockCandlesUpdater.getCandles()).thenReturn(this.candlestickList);

        BigDecimal result = this.baseExitStrategy.getLastFullCandleHigh();

        assertEquals(max, result);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void WhenGetLastFullCandleLowWithDifferentNumberOfRequiredCandlesThenException(){
        //candlestickList is empty and required number of candles is 2
        BigDecimal result = this.baseExitStrategy.getLastFullCandleLow();
    }

    @Test
    public void WhenGetLastFullCandleLowThenReturnCorrectValue() throws NoSuchFieldException, IllegalAccessException {
        BigDecimal low = BigDecimal.valueOf(1.11445);
        this.candlesClosePrices.add(low);
        this.candlesLowPrices.add(low);
        this.candlesHighPrices.add(low);

        BigDecimal high = BigDecimal.valueOf(1.14445);
        this.candlesClosePrices.add(high);
        this.candlesLowPrices.add(high);
        this.candlesHighPrices.add(high);

        this.setCandlesUpdaterToMock();
        fillCandlestickList();

        when(this.mockCandlesUpdater.getCandles()).thenReturn(this.candlestickList);

        BigDecimal result = this.baseExitStrategy.getLastFullCandleLow();

        assertEquals(low, result);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void WhenGetLastFullCandleCloseWithDifferentNumberOfRequiredCandlesThenException(){
        //candlestickList is empty and required number of candles is 2
        BigDecimal result = this.baseExitStrategy.getLastFullCandleLow();
    }

    @Test
    public void WhenGetLastFullCandleCloseThenReturnCorrectValue() throws NoSuchFieldException, IllegalAccessException {
        BigDecimal close = BigDecimal.valueOf(1.17445);
        this.candlesClosePrices.add(close);
        this.candlesLowPrices.add(close);
        this.candlesHighPrices.add(close);

        BigDecimal open = BigDecimal.valueOf(1.14445);
        this.candlesClosePrices.add(open);
        this.candlesLowPrices.add(open);
        this.candlesHighPrices.add(open);

        this.setCandlesUpdaterToMock();
        fillCandlestickList();

        when(this.mockCandlesUpdater.getCandles()).thenReturn(this.candlestickList);

        BigDecimal result = this.baseExitStrategy.getLastFullCandleClose();

        assertEquals(close, result);
    }

    @Test
    public void WhenUpdaterUpdateCandlesSuccessfullyThenReturnTrue() throws NoSuchFieldException, IllegalAccessException {

        setCandlesUpdaterToMock();
        boolean result = this.baseExitStrategy.updaterUpdateCandles(this.mockDateTime);
        assertTrue(result);
    }

    @Test
    public void WhenUpdaterUpdateCandlesUnSuccessfullyThenReturnFalse() throws NoSuchFieldException, IllegalAccessException {

        when(this.mockCandlesUpdater.updateCandles(this.mockDateTime)).thenReturn(false);
        setCandlesUpdaterToMock();
        boolean result = this.baseExitStrategy.updaterUpdateCandles(this.mockDateTime);
        assertFalse(result);
    }

    @Test(expected = NullPointerException.class)
    public void WhenChangeStopLossWithNullTradeIDThenException() {
        this.baseExitStrategy.changeStopLoss(null, BigDecimal.ONE);
    }

    @Test(expected = NullPointerException.class)
    public void WhenChangeStopLossWithNullNewStopPriceThenException(){
        this.baseExitStrategy.changeStopLoss(this.mockTradeID, null);
    }

    @Test
    public void WhenChangeStopLossWithCorrectValuesThenReturnResponse(){
        TradeSetDependentOrdersResponse tradeSetDependentOrdersResponse = this.baseExitStrategy.changeStopLoss(this.mockTradeID, BigDecimal.TEN);

        assertSame(this.mockTradeSetDependentOrdersResponse, tradeSetDependentOrdersResponse);
    }

    @Test(expected = NullPointerException.class)
    public void WhenGetStopLossOrderPriceByIDWithNullAccountThenException(){
        this.baseExitStrategy.getStopLossOrderPriceByID(null, this.mockOrderID);
    }

    @Test(expected = NullPointerException.class)
    public void WhenGetStopLossOrderPriceByIDWithNullOrderIdThenException(){
        this.baseExitStrategy.getStopLossOrderPriceByID(this.mockAccount, null);
    }

    @Test
    public void WhenGetStopLossOrderPriceByIDAndNoOrdersInAccountThenReturnZero(){

        this.orders.remove(0);
        BigDecimal stopLossOrderPriceByID = this.baseExitStrategy.getStopLossOrderPriceByID(this.mockAccount, this.mockOrderID);
        int compare = stopLossOrderPriceByID.compareTo(BigDecimal.ZERO);

        assertEquals(0, compare);
    }

    @Test
    public void WhenGetStopLossOrderPriceByIDThenReturnCorrectResult(){

        BigDecimal ask = BigDecimal.valueOf(1.14345);

        //set stopLoss price
        PriceValue stopPrice = mock(PriceValue.class);
        when(stopPrice.bigDecimalValue()).thenReturn(ask);
        when(this.mockOrder.getPrice()).thenReturn(stopPrice);

        BigDecimal stopLossOrderPriceByID = this.baseExitStrategy.getStopLossOrderPriceByID(this.mockAccount, this.mockOrderID);

        assertEquals(ask,stopLossOrderPriceByID);
    }

    @Test(expected = NullPointerException.class)
    public void WhenPartialTradeCloseWithNullCurrentUnitsThenException(){
        this.baseExitStrategy.partialTradeClose(null, BigDecimal.ONE);
    }

    @Test(expected = NullPointerException.class)
    public void WhenPartialTradeCloseWithNullPartsThenException(){
        this.baseExitStrategy.partialTradeClose(BigDecimal.ONE, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void WhenPartialTradeCloseWithZeroPartsThenException(){
        this.baseExitStrategy.partialTradeClose(BigDecimal.TEN, BigDecimal.ZERO);
    }

    @Test
    public void WhenPartialTradeCloseThenReturnCorrectResult(){

        OrderCreateResponse orderCreateResponse = this.baseExitStrategy.partialTradeClose(BigDecimal.TEN, BigDecimal.ONE);

        assertSame(this.mockOrderCreateResponse, orderCreateResponse);
    }

   @Test(expected = NullPointerException.class)
   public void WhenGetTradeWithNullAccountThenException(){
        this.baseExitStrategy.getTrade(null);
   }

   @Test
   public void WhenGetTradeThenReturnsCorrectResult(){
       TradeSummary trade = this.baseExitStrategy.getTrade(this.mockAccount);

       assertSame(this.mockTrade, trade);
   }

    private void setCandlesUpdaterToMock() throws NoSuchFieldException, IllegalAccessException {
        Field updater = this.baseExitStrategy.getClass().getDeclaredField("candlesUpdater");
        updater.setAccessible(true);
        updater.set(this.baseExitStrategy, this.mockCandlesUpdater);
    }

    //fill candlestick list with candles. Candles have only time and close price
    private void fillCandlestickList() {

        this.candlestickList = new ArrayList<>();

        for (int i = 0; i < candlesClosePrices.size(); i++) {
            //candle 1
            DateTime dateTime1 = mock(DateTime.class);
            //           when(dateTime1.toString()).thenReturn(candlesDateTime.get(i));

            PriceValue priceValue1 = mock(PriceValue.class);
            when(priceValue1.bigDecimalValue()).thenReturn(candlesClosePrices.get(i));

            PriceValue priceValue2 = mock(PriceValue.class);
            when(priceValue2.bigDecimalValue()).thenReturn(candlesHighPrices.get(i));

            PriceValue priceValue3 = mock(PriceValue.class);
            when(priceValue3.bigDecimalValue()).thenReturn(candlesLowPrices.get(i));

            CandlestickData candlestickData1 = mock(CandlestickData.class);
            when(candlestickData1.getC()).thenReturn(priceValue1);
            when(candlestickData1.getH()).thenReturn(priceValue2);
            when(candlestickData1.getL()).thenReturn(priceValue3);

            Candlestick candle1 = mock(Candlestick.class);
            when(candle1.getTime()).thenReturn(dateTime1);
            when(candle1.getMid()).thenReturn(candlestickData1);

            this.candlestickList.add(candle1);
        }
    }
}