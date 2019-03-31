package trader.exit.exit_strategie;

import com.oanda.v20.Context;
import com.oanda.v20.ExecuteException;
import com.oanda.v20.RequestException;
import com.oanda.v20.account.Account;
import com.oanda.v20.instrument.*;
import com.oanda.v20.order.*;
import com.oanda.v20.pricing_common.PriceValue;
import com.oanda.v20.primitives.DateTime;
import com.oanda.v20.primitives.DecimalNumber;
import com.oanda.v20.trade.*;
import org.junit.Before;
import org.junit.Test;
import trader.indicator.updater.CandlesUpdater;
import trader.candlestick.candle.CandleGranularity;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HalfCloseTrailExitStrategyTest {

    private static final BigDecimal INITIAL_UNITS = BigDecimal.valueOf(100);
    private static final BigDecimal CURRENT_UNITS = BigDecimal.valueOf(50);
    private static final BigDecimal TRADE_PRICE = BigDecimal.valueOf(1.14345);


    private Context mockContext;
    private Account mockAccount;
    private OrderCreateResponse mockOrderCreateResponse;
    private TradeSetDependentOrdersResponse mockTradeSetDependentOrdersResponse;
    private CandleGranularity candlestickGranularity;
    private HalfCloseTrailExitStrategy halfCloseTrailExitStrategy;
    private StopLossOrder mockOrder;
    private TradeSummary mockTrade;
    private TradeID mockTradeID;
    private OrderID mockOrderID;
    private DecimalNumber mockInitialUnits;
    private DecimalNumber mockCurrentUnits;
    private PriceValue mockPriceValue;
    private InstrumentCandlesResponse mockResponse;
    private DateTime mockDateTime;
    private CandlesUpdater mockCandlesUpdater;
    private BaseExitStrategy mockBaseExitStrategy;

    @Before
    public void before() throws ExecuteException, RequestException {

        this.candlestickGranularity = CandleGranularity.M30;

        //mock order
        this.mockContext = mock(Context.class);
        this.mockContext.order = mock(OrderContext.class);
        this.mockOrderCreateResponse = mock(OrderCreateResponse.class);
        when(this.mockContext.order.create(any(OrderCreateRequest.class))).thenReturn(this.mockOrderCreateResponse);

        //mock trade
        this.mockContext.trade = mock(TradeContext.class);
        this.mockTradeSetDependentOrdersResponse = mock(TradeSetDependentOrdersResponse.class);
        when(this.mockContext.trade.setDependentOrders(any(TradeSetDependentOrdersRequest.class))).thenReturn(this.mockTradeSetDependentOrdersResponse);

        //mock instrument for candleUpdater
        this.mockResponse = mock(InstrumentCandlesResponse.class);
        when(this.mockResponse.getCandles()).thenReturn(new ArrayList<>());
        this.mockContext.instrument = mock(InstrumentContext.class);
        when(this.mockContext.instrument.candles(any(InstrumentCandlesRequest.class))).thenReturn(this.mockResponse);

        //mock trade
        this.mockTrade = mock(TradeSummary.class);
        this.mockTradeID = mock(TradeID.class);
        when(this.mockTrade.getId()).thenReturn(this.mockTradeID);

        this.mockInitialUnits = mock(DecimalNumber.class);
        when(this.mockInitialUnits.bigDecimalValue()).thenReturn(INITIAL_UNITS);

        this.mockCurrentUnits = mock(DecimalNumber.class);
        when(this.mockCurrentUnits.bigDecimalValue()).thenReturn(CURRENT_UNITS);

        this.mockPriceValue = mock(PriceValue.class);
        when(this.mockPriceValue.bigDecimalValue()).thenReturn(TRADE_PRICE);


        when(this.mockTrade.getInitialUnits()).thenReturn(this.mockInitialUnits);
        when(this.mockTrade.getCurrentUnits()).thenReturn(this.mockCurrentUnits);
        when(this.mockTrade.getPrice()).thenReturn(this.mockPriceValue);

        this.mockOrderID = mock(OrderID.class);
        when(this.mockTrade.getStopLossOrderID()).thenReturn(this.mockOrderID);

        //orders
        this.mockOrder = mock(StopLossOrder.class);
        when(this.mockOrder.getType()).thenReturn(OrderType.STOP_LOSS);
        when(this.mockOrder.getId()).thenReturn(this.mockOrderID);

        //mock account;
        this.mockAccount = mock(Account.class);


        this.mockDateTime = mock(DateTime.class);

        //mock baseExitStrategy
        this.mockBaseExitStrategy = mock(BaseExitStrategy.class);
        when(this.mockBaseExitStrategy.updaterUpdateCandles(any(DateTime.class))).thenReturn(true);
        when(this.mockBaseExitStrategy.getTrade(any(Account.class))).thenReturn(this.mockTrade);
        when(this.mockBaseExitStrategy.changeStopLoss(any(TradeID.class), any(BigDecimal.class))).thenReturn(this.mockTradeSetDependentOrdersResponse);
        when(this.mockBaseExitStrategy.partialTradeClose(any(BigDecimal.class), any(BigDecimal.class))).thenReturn(this.mockOrderCreateResponse);

        this.halfCloseTrailExitStrategy = new HalfCloseTrailExitStrategy(this.mockContext, this.candlestickGranularity);
    }

    @Test
    public void WhenFullUnitsSizeAndUnitsSizePositiveAndPriceOverBreakEvenPriceThenMoveStopToBreakEven() throws NoSuchFieldException, IllegalAccessException {

        BigDecimal bid = BigDecimal.valueOf(1.14595);
        BigDecimal breakEvenTargetPrice = BigDecimal.valueOf(1.14344);

        when(this.mockCurrentUnits.bigDecimalValue()).thenReturn(INITIAL_UNITS);

        //ask not needed
        BigDecimal ask = BigDecimal.ONE;

        when(this.mockBaseExitStrategy.getStopLossOrderPriceByID(any(Account.class), any(OrderID.class))).thenReturn(breakEvenTargetPrice);

        this.setBaseExitStrategyToMock();

        this.halfCloseTrailExitStrategy.execute(this.mockAccount, ask, bid, this.mockDateTime);

        TradeSetDependentOrdersResponse stopResponse = this.getTradeSetStopResponse();

        assertSame(this.mockTradeSetDependentOrdersResponse, stopResponse);

    }

    @Test
    public void WhenFullUnitsSizeAndUnitsSizePositiveAndStopOverOrAtBreakEvenPriceThenDoNotMoveStopToBreakEven() throws NoSuchFieldException, IllegalAccessException {

        BigDecimal bid = BigDecimal.valueOf(1.14365);
        BigDecimal breakEvenTargetPrice = BigDecimal.valueOf(1.14345);

        when(this.mockCurrentUnits.bigDecimalValue()).thenReturn(INITIAL_UNITS);

        //ask not needed
        BigDecimal ask = BigDecimal.ONE;

        when(this.mockBaseExitStrategy.getStopLossOrderPriceByID(any(Account.class), any(OrderID.class))).thenReturn(breakEvenTargetPrice);

        this.setBaseExitStrategyToMock();

        this.halfCloseTrailExitStrategy.execute(this.mockAccount, ask, bid, this.mockDateTime);

        TradeSetDependentOrdersResponse stopResponse = this.getTradeSetStopResponse();

        assertNull(stopResponse);

    }

    @Test
    public void WhenFullUnitsSizeAndUnitsSizeNegativeAndPriceBelowBreakEvenPriceThenMoveStopToBreakEven() throws NoSuchFieldException, IllegalAccessException {

        BigDecimal ask = BigDecimal.valueOf(1.14095);
        BigDecimal breakEvenTargetPrice = BigDecimal.valueOf(1.14346);

        when(this.mockInitialUnits.bigDecimalValue()).thenReturn(INITIAL_UNITS.multiply(BigDecimal.valueOf(-1)));
        when(this.mockCurrentUnits.bigDecimalValue()).thenReturn(INITIAL_UNITS.multiply(BigDecimal.valueOf(-1)));

        //bid not needed
        BigDecimal bid = BigDecimal.ONE;

        when(this.mockBaseExitStrategy.getStopLossOrderPriceByID(any(Account.class), any(OrderID.class))).thenReturn(breakEvenTargetPrice);

        this.setBaseExitStrategyToMock();

        this.halfCloseTrailExitStrategy.execute(this.mockAccount, ask, bid, this.mockDateTime);

        TradeSetDependentOrdersResponse stopResponse = this.getTradeSetStopResponse();

        assertSame(this.mockTradeSetDependentOrdersResponse, stopResponse);

    }

    @Test
    public void WhenFullUnitsSizeAndUnitsSizeNegativeAndStopAtOrBelowBreakEvenPriceThenNoMoveStopToBreakEven() throws NoSuchFieldException, IllegalAccessException {

        BigDecimal ask = BigDecimal.valueOf(1.14095);
        BigDecimal breakEvenTargetPrice = BigDecimal.valueOf(1.14345);

        when(this.mockInitialUnits.bigDecimalValue()).thenReturn(INITIAL_UNITS.multiply(BigDecimal.valueOf(-1)));
        when(this.mockCurrentUnits.bigDecimalValue()).thenReturn(INITIAL_UNITS.multiply(BigDecimal.valueOf(-1)));

        //bid not needed
        BigDecimal bid = BigDecimal.ONE;

        when(this.mockBaseExitStrategy.getStopLossOrderPriceByID(any(Account.class), any(OrderID.class))).thenReturn(breakEvenTargetPrice);

        this.setBaseExitStrategyToMock();

        this.halfCloseTrailExitStrategy.execute(this.mockAccount, ask, bid, this.mockDateTime);

        TradeSetDependentOrdersResponse stopResponse = this.getTradeSetStopResponse();

        assertNull(stopResponse);

    }

    @Test
    public void WhenFullUnitsSizeAndUnitsSizePositiveAndBidPriceBelowTargetThenNoChangeToTrade() throws NoSuchFieldException, IllegalAccessException {

        //orderCreateResponse must be null
        //tradeSetDependentOrdersRequest must be null

        when(this.mockCurrentUnits.bigDecimalValue()).thenReturn(INITIAL_UNITS);
        BigDecimal bid = BigDecimal.valueOf(1.14345);
        //ask not needed
        BigDecimal ask = BigDecimal.ONE;

        when(this.mockBaseExitStrategy.getStopLossOrderPriceByID( any(Account.class), any(OrderID.class))).thenReturn(TRADE_PRICE);

        this.setBaseExitStrategyToMock();

        this.halfCloseTrailExitStrategy.execute(this.mockAccount, ask, bid, this.mockDateTime);

        OrderCreateResponse orderCreateResponse = this.getOrderCreateResponse();
        TradeSetDependentOrdersResponse tradeStopResponse = this.getTradeSetStopResponse();

        assertNull(orderCreateResponse);
        assertNull(tradeStopResponse);

    }

    @Test
    public void WhenFullUnitsSizeAndUnitsSizePositiveAndPriceOverOrOnTargetThenCloseHalf() throws NoSuchFieldException, IllegalAccessException {

        when(this.mockCurrentUnits.bigDecimalValue()).thenReturn(INITIAL_UNITS);
        BigDecimal bid = BigDecimal.valueOf(1.14665);
        //ask not needed
        BigDecimal ask = BigDecimal.ONE;

        when(this.mockBaseExitStrategy.getStopLossOrderPriceByID( any(Account.class), any(OrderID.class))).thenReturn(TRADE_PRICE);

        this.setBaseExitStrategyToMock();

        this.halfCloseTrailExitStrategy.execute(this.mockAccount, ask, bid, this.mockDateTime);

        OrderCreateResponse orderCreateResponse = this.getOrderCreateResponse();

        assertSame(this.mockOrderCreateResponse, orderCreateResponse);

    }

    @Test
    public void WhenFullUnitsSizeAndUnitsSizeNegativeAndAskPriceAboveTargetThenNoChangeToTrade() throws NoSuchFieldException, IllegalAccessException {

        //orderCreateResponse must be null
        //tradeSetDependentOrdersRequest must be null

        when(this.mockInitialUnits.bigDecimalValue()).thenReturn(INITIAL_UNITS.multiply(BigDecimal.valueOf(-1)));
        when(this.mockCurrentUnits.bigDecimalValue()).thenReturn(INITIAL_UNITS.multiply(BigDecimal.valueOf(-1)));
        BigDecimal ask = BigDecimal.valueOf(1.14345);
        //bid not needed
        BigDecimal bid = BigDecimal.ONE;

        when(this.mockBaseExitStrategy.getStopLossOrderPriceByID( any(Account.class), any(OrderID.class))).thenReturn(TRADE_PRICE);

        this.setBaseExitStrategyToMock();

        this.halfCloseTrailExitStrategy.execute(this.mockAccount, ask, bid, this.mockDateTime);

        OrderCreateResponse orderCreateResponse = this.getOrderCreateResponse();
        TradeSetDependentOrdersResponse tradeStopResponse = this.getTradeSetStopResponse();

        assertNull(orderCreateResponse);
        assertNull(tradeStopResponse);

    }

    @Test
    public void WhenFullUnitsSizeAndUnitsSizeNegativeAndAskPriceBelowOrOnTargetThenCloseHalf() throws NoSuchFieldException, IllegalAccessException {


        when(this.mockInitialUnits.bigDecimalValue()).thenReturn(INITIAL_UNITS.multiply(BigDecimal.valueOf(-1)));
        when(this.mockCurrentUnits.bigDecimalValue()).thenReturn(INITIAL_UNITS.multiply(BigDecimal.valueOf(-1)));
        BigDecimal ask = BigDecimal.valueOf(1.14025);
        //bid not needed
        BigDecimal bid = BigDecimal.ONE;

        when(this.mockBaseExitStrategy.getStopLossOrderPriceByID( any(Account.class), any(OrderID.class))).thenReturn(TRADE_PRICE);

        this.setBaseExitStrategyToMock();

        this.halfCloseTrailExitStrategy.execute(this.mockAccount, ask, bid, this.mockDateTime);

        OrderCreateResponse orderCreateResponse = this.getOrderCreateResponse();

        assertSame(this.mockOrderCreateResponse, orderCreateResponse);

    }

    @Test
    public void WhenHalfClosedAndNegativeUnitsAndNoNewExitBarThenNoChangeToTrade() throws NoSuchFieldException, IllegalAccessException {
        // exit bar - if the last bar close is lower than prev to last bar low
        // last bar high is lower than prev to last bar high

        BigDecimal max = BigDecimal.valueOf(1.14445);
        when(this.mockBaseExitStrategy.getLastFullCandleHigh()).thenReturn(max);
        when(this.mockBaseExitStrategy.getLastFullCandleLow()).thenReturn(max);
        when(this.mockBaseExitStrategy.getLastFullCandleClose()).thenReturn(max);



        when(this.mockInitialUnits.bigDecimalValue()).thenReturn(INITIAL_UNITS.multiply(BigDecimal.valueOf(-1)));
        when(this.mockCurrentUnits.bigDecimalValue()).thenReturn(CURRENT_UNITS.multiply(BigDecimal.valueOf(-1)));
        BigDecimal ask = BigDecimal.valueOf(1.14345);

        when(this.mockBaseExitStrategy.getStopLossOrderPriceByID( any(Account.class), any(OrderID.class))).thenReturn(ask);
        //ask not needed
        BigDecimal bid = BigDecimal.ONE;

        this.setBaseExitStrategyToMock();

        this.halfCloseTrailExitStrategy.execute(this.mockAccount, ask, bid, this.mockDateTime);

        TradeSetDependentOrdersResponse tradeStopResponse = this.getTradeSetStopResponse();

        assertNull(tradeStopResponse);
    }


    @Test
    public void WhenHalfClosedAndNegativeUnitsAndNewExitBarThenChangeStop() throws NoSuchFieldException, IllegalAccessException {
        // exit bar - if the last bar close is lower than prev to last bar low
        // last bar high is lower than prev to last bar high

        when(this.mockBaseExitStrategy.getLastFullCandleHigh()).thenReturn(BigDecimal.valueOf(1.14211));
        when(this.mockBaseExitStrategy.getLastFullCandleLow()).thenReturn(BigDecimal.valueOf(1.14211));
        when(this.mockBaseExitStrategy.getLastFullCandleClose()).thenReturn(BigDecimal.valueOf(1.143111));


        when(this.mockInitialUnits.bigDecimalValue()).thenReturn(INITIAL_UNITS.multiply(BigDecimal.valueOf(-1)));
        when(this.mockCurrentUnits.bigDecimalValue()).thenReturn(CURRENT_UNITS.multiply(BigDecimal.valueOf(-1)));
        BigDecimal ask = BigDecimal.valueOf(1.14345);
        //bid not needed
        BigDecimal bid = BigDecimal.ONE;

        //set stopLoss price
        when(this.mockBaseExitStrategy.getStopLossOrderPriceByID(any(Account.class), any(OrderID.class))).thenReturn(ask);

        this.setBaseExitStrategyToMock();

        this.halfCloseTrailExitStrategy.execute(this.mockAccount, ask, bid, this.mockDateTime);

        TradeSetDependentOrdersResponse tradeStopResponse = this.getTradeSetStopResponse();

        assertSame(this.mockTradeSetDependentOrdersResponse, tradeStopResponse);
    }

    @Test
    public void WhenHalfClosedAndPositiveUnitsAndNoNewExitBarThenNoChangeToTrade() throws NoSuchFieldException, IllegalAccessException {
        // exit bar is if the last bar close is lower than prev to last bar low
        // last bar high is lower than prev to last bar high

        when(this.mockBaseExitStrategy.getLastFullCandleHigh()).thenReturn(BigDecimal.valueOf(1.14233));
        when(this.mockBaseExitStrategy.getLastFullCandleLow()).thenReturn(BigDecimal.valueOf(1.14233));
        when(this.mockBaseExitStrategy.getLastFullCandleClose()).thenReturn(BigDecimal.valueOf(1.14233));


        when(this.mockInitialUnits.bigDecimalValue()).thenReturn(INITIAL_UNITS);
        when(this.mockCurrentUnits.bigDecimalValue()).thenReturn(CURRENT_UNITS);
        BigDecimal bid = BigDecimal.valueOf(1.14345);
        //ask not needed
        BigDecimal ask = BigDecimal.ONE;

        //set stopLoss price
        when(this.mockBaseExitStrategy.getStopLossOrderPriceByID(any(Account.class), any(OrderID.class))).thenReturn(bid);

        this.setBaseExitStrategyToMock();

        this.halfCloseTrailExitStrategy.execute(this.mockAccount, ask, bid, this.mockDateTime);

        TradeSetDependentOrdersResponse tradeStopResponse = this.getTradeSetStopResponse();

        assertNull(tradeStopResponse);
    }

    @Test
    public void WhenHalfClosedAndPositiveUnitsAndNewExitBarThenChangeStop() throws NoSuchFieldException, IllegalAccessException {
        // exit bar - if the last bar close is lower than prev to last bar low
        // last bar high is lower than prev to last bar high

        when(this.mockBaseExitStrategy.getLastFullCandleHigh()).thenReturn(BigDecimal.valueOf(1.14351));
        when(this.mockBaseExitStrategy.getLastFullCandleLow()).thenReturn(BigDecimal.valueOf(1.14346));
        when(this.mockBaseExitStrategy.getLastFullCandleClose()).thenReturn(BigDecimal.valueOf(1.14371));


        when(this.mockInitialUnits.bigDecimalValue()).thenReturn(INITIAL_UNITS);
        when(this.mockCurrentUnits.bigDecimalValue()).thenReturn(CURRENT_UNITS);
        BigDecimal bid = BigDecimal.valueOf(1.14345);
        //ask not needed
        BigDecimal ask = BigDecimal.ONE;

        //set stopLoss price
        when(this.mockBaseExitStrategy.getStopLossOrderPriceByID(any(Account.class), any(OrderID.class))).thenReturn(bid);

        this.setBaseExitStrategyToMock();

        this.halfCloseTrailExitStrategy.execute(this.mockAccount, ask, bid, this.mockDateTime);

        TradeSetDependentOrdersResponse tradeStopResponse = this.getTradeSetStopResponse();

        assertSame(this.mockTradeSetDependentOrdersResponse, tradeStopResponse);
    }



    private OrderCreateResponse getOrderCreateResponse() throws NoSuchFieldException, IllegalAccessException {
        Field halfTradeResponse = this.halfCloseTrailExitStrategy.getClass().getDeclaredField("halfTradeResponse");
        halfTradeResponse.setAccessible(true);
        return (OrderCreateResponse) halfTradeResponse.get(this.halfCloseTrailExitStrategy);

    }

    private TradeSetDependentOrdersResponse getTradeSetStopResponse() throws NoSuchFieldException, IllegalAccessException {
        Field halfTradeResponse = this.halfCloseTrailExitStrategy.getClass().getDeclaredField("tradeSetDependentOrdersResponse");
        halfTradeResponse.setAccessible(true);
        return (TradeSetDependentOrdersResponse) halfTradeResponse.get(this.halfCloseTrailExitStrategy);

    }

    private void setBaseExitStrategyToMock() throws NoSuchFieldException, IllegalAccessException {
        Field updater = this.halfCloseTrailExitStrategy.getClass().getDeclaredField("baseExitStrategy");
        updater.setAccessible(true);
        updater.set(this.halfCloseTrailExitStrategy, this.mockBaseExitStrategy);
    }
}