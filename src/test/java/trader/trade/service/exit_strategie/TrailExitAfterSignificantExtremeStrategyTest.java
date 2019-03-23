package trader.trade.service.exit_strategie;

import com.oanda.v20.Context;
import com.oanda.v20.account.Account;
import com.oanda.v20.instrument.InstrumentCandlesRequest;
import com.oanda.v20.instrument.InstrumentCandlesResponse;
import com.oanda.v20.instrument.InstrumentContext;
import com.oanda.v20.order.*;
import com.oanda.v20.pricing_common.PriceValue;
import com.oanda.v20.primitives.DateTime;
import com.oanda.v20.primitives.DecimalNumber;
import com.oanda.v20.trade.*;
import org.junit.Before;
import org.junit.Test;
import trader.config.Config;
import trader.candlestick.candle.CandleGranularity;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TrailExitAfterSignificantExtremeStrategyTest {

    private static final BigDecimal INITIAL_UNITS = BigDecimal.valueOf(100);
    private static final BigDecimal CURRENT_UNITS = BigDecimal.valueOf(50);
    private static final BigDecimal TRADE_PRICE = BigDecimal.valueOf(1.14345);
    private static final BigDecimal INITIAL_STOP_PRICE = BigDecimal.valueOf(1.14095);
    private static final BigDecimal INITIAL_STOP_PRICE_SHORT = BigDecimal.valueOf(1.14595);

    private Context mockContext;
    private Account mockAccount;
    private OrderCreateResponse mockOrderCreateResponse;
    private TradeSetDependentOrdersResponse mockTradeSetDependentOrdersResponse;
    private CandleGranularity candlestickGranularity;
    private TrailExitAfterSignificantExtremeStrategy trailExitAfterSignificantExtremeStrategy;
    private StopLossOrder mockOrder;
    private TradeSummary mockTrade;
    private TradeID mockTradeID;
    private OrderID mockOrderID;
    private DecimalNumber mockInitialUnits;
    private DecimalNumber mockCurrentUnits;
    private PriceValue mockPriceValue;
    private InstrumentCandlesResponse mockResponse;
    private DateTime mockDateTime;
    private BaseExitStrategy mockBaseExitStrategy;

    @Before
    public void before() throws Exception {

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

        this.trailExitAfterSignificantExtremeStrategy = new TrailExitAfterSignificantExtremeStrategy(this.mockContext, this.candlestickGranularity);

    }

    @Test
    public void WhenUnitsSizePositiveAndPriceBelowTargetThenNoChangeToTrade() throws NoSuchFieldException, IllegalAccessException {

        //tradeSetDependentOrdersRequest must be null

        when(this.mockBaseExitStrategy.getLastFullCandleHigh()).thenReturn(BigDecimal.valueOf(1.14351));
        when(this.mockBaseExitStrategy.getLastFullCandleLow()).thenReturn(BigDecimal.valueOf(1.14346));
        when(this.mockCurrentUnits.bigDecimalValue()).thenReturn(INITIAL_UNITS);
        when(this.mockBaseExitStrategy.getStopLossOrderPriceByID(any(Account.class), any(OrderID.class))).thenReturn(INITIAL_STOP_PRICE);
        BigDecimal ask = BigDecimal.valueOf(1.14345);
        //bid not needed
        BigDecimal bid = BigDecimal.ONE;

        this.setBaseExitStrategyToMock();

        this.trailExitAfterSignificantExtremeStrategy.execute(this.mockAccount, ask, bid, this.mockDateTime);

        TradeSetDependentOrdersResponse tradeStopResponse = this.getTradeSetStopResponse();

        assertNull(tradeStopResponse);

    }

    @Test
    public void WhenUnitsSizePositiveAndPriceOverTargetAndSignificantLowLowerThanFirstStopPriceThenChangeStopToFirstStopPrice() throws NoSuchFieldException, IllegalAccessException {

        BigDecimal lastFullCandleHigh = BigDecimal.valueOf(1.14686);
        BigDecimal lastFullCandleLow = BigDecimal.valueOf(1.14346);
        BigDecimal firstStopPrice = BigDecimal.valueOf(1.14525);
        BigDecimal ask = BigDecimal.valueOf(1.14687);
        //bid not needed
        BigDecimal bid = BigDecimal.ONE;

        TradeSetDependentOrdersResponse ordersResponse = mock(TradeSetDependentOrdersResponse.class);

        when(this.mockBaseExitStrategy.getLastFullCandleHigh()).thenReturn(lastFullCandleHigh);
        when(this.mockBaseExitStrategy.getLastFullCandleLow()).thenReturn(lastFullCandleLow);
        when(this.mockBaseExitStrategy.getStopLossOrderPriceByID(any(Account.class), any(OrderID.class))).thenReturn(INITIAL_STOP_PRICE);
        //will return different object if changeStopLoss to firstStopPrice
        when(this.mockBaseExitStrategy.changeStopLoss(this.mockTradeID,firstStopPrice)).thenReturn(ordersResponse);

        when(this.mockCurrentUnits.bigDecimalValue()).thenReturn(INITIAL_UNITS);

        this.setBaseExitStrategyToMock();

        this.trailExitAfterSignificantExtremeStrategy.execute(this.mockAccount, ask, bid, this.mockDateTime);

        TradeSetDependentOrdersResponse tradeStopResponse = this.getTradeSetStopResponse();


        assertSame(ordersResponse, tradeStopResponse);

    }

    //Simulating 3 candlestick:
    // 1st is higher than open price,
    // 2nd is lower than 1st and high than firstStopPrice
    // 3-rd is higher than first.
    // In this way the lastSignificant low will be bigger than firstStopPrice and will be confirmed with new significantHigh(from 3-rd candlestick), which broke previous significantHigh(from 1-st candlestick)
    @Test
    public void WhenUnitsSizePositiveAndPriceOverTargetAndSignificantLowHigherThanFirstStopPriceThenChangeStopToSignificantLow() throws NoSuchFieldException, IllegalAccessException {
        //first higher candlestick
        BigDecimal lastFullCandleHigh = BigDecimal.valueOf(1.14886);
        BigDecimal lastFullCandleLow = BigDecimal.valueOf(1.14806);
        BigDecimal ask = BigDecimal.valueOf(1.14887);
        //bid not needed
        BigDecimal bid = BigDecimal.ONE;

        TradeSetDependentOrdersResponse ordersResponse = mock(TradeSetDependentOrdersResponse.class);

        when(this.mockBaseExitStrategy.getLastFullCandleHigh()).thenReturn(lastFullCandleHigh);
        when(this.mockBaseExitStrategy.getLastFullCandleLow()).thenReturn(lastFullCandleLow);
        when(this.mockBaseExitStrategy.getStopLossOrderPriceByID(any(Account.class), any(OrderID.class))).thenReturn(INITIAL_STOP_PRICE);

        when(this.mockCurrentUnits.bigDecimalValue()).thenReturn(INITIAL_UNITS);

        this.setBaseExitStrategyToMock();

        this.trailExitAfterSignificantExtremeStrategy.execute(this.mockAccount, ask, bid, this.mockDateTime);

        //first lower candlestick
        lastFullCandleHigh = BigDecimal.valueOf(1.14786);
        lastFullCandleLow = BigDecimal.valueOf(1.14626);
        ask = BigDecimal.valueOf(1.14688);
        when(this.mockBaseExitStrategy.getLastFullCandleHigh()).thenReturn(lastFullCandleHigh);
        when(this.mockBaseExitStrategy.getLastFullCandleLow()).thenReturn(lastFullCandleLow);
        when(this.mockBaseExitStrategy.getStopLossOrderPriceByID(any(Account.class), any(OrderID.class))).thenReturn(BigDecimal.valueOf(1.14525));

        //when call changeStopLoss with significantLow higher than stopLossPrice the response must be ordersResponse;
        when(this.mockBaseExitStrategy.changeStopLoss(this.mockTradeID, lastFullCandleLow.subtract(Config.SPREAD))).thenReturn(ordersResponse);

        this.trailExitAfterSignificantExtremeStrategy.execute(this.mockAccount, ask, bid, this.mockDateTime);

        //second higher candlestick
        lastFullCandleHigh = BigDecimal.valueOf(1.14986);
        lastFullCandleLow = BigDecimal.valueOf(1.14806);
        ask = BigDecimal.valueOf(1.14988);
        when(this.mockBaseExitStrategy.getLastFullCandleHigh()).thenReturn(lastFullCandleHigh);
        when(this.mockBaseExitStrategy.getLastFullCandleLow()).thenReturn(lastFullCandleLow);

        this.trailExitAfterSignificantExtremeStrategy.execute(this.mockAccount, ask, bid, this.mockDateTime);


        TradeSetDependentOrdersResponse tradeStopResponse = this.getTradeSetStopResponse();

        assertSame(ordersResponse, tradeStopResponse);

    }

    @Test
    public void WhenUnitsSizeNegativeAndPriceAboveTargetThenNoChangeToTrade() throws NoSuchFieldException, IllegalAccessException {

        //tradeSetDependentOrdersRequest must be null

        when(this.mockBaseExitStrategy.getLastFullCandleHigh()).thenReturn(BigDecimal.valueOf(1.14344));
        when(this.mockBaseExitStrategy.getLastFullCandleLow()).thenReturn(BigDecimal.valueOf(1.14027));
        when(this.mockCurrentUnits.bigDecimalValue()).thenReturn(INITIAL_UNITS.multiply(BigDecimal.valueOf(-1)));
        when(this.mockBaseExitStrategy.getStopLossOrderPriceByID(any(Account.class), any(OrderID.class))).thenReturn(INITIAL_STOP_PRICE);
        BigDecimal bid = BigDecimal.valueOf(1.14026);
        //ask not needed
        BigDecimal ask = BigDecimal.ONE;

        this.setBaseExitStrategyToMock();

        this.trailExitAfterSignificantExtremeStrategy.execute(this.mockAccount, ask, bid, this.mockDateTime);

        TradeSetDependentOrdersResponse tradeStopResponse = this.getTradeSetStopResponse();

        assertNull(tradeStopResponse);

    }

    @Test
    public void WhenUnitsSizeNegativeAndPriceBelowTargetAndSignificantHighHigherThanFirstStopPriceThenChangeStopToFirstStopPrice() throws NoSuchFieldException, IllegalAccessException {

        BigDecimal lastFullCandleHigh = BigDecimal.valueOf(1.14346);
        BigDecimal lastFullCandleLow = BigDecimal.valueOf(1.14004);
        BigDecimal firstStopPrice = BigDecimal.valueOf(1.14165);
        BigDecimal bid = BigDecimal.valueOf(1.14003);
        //ask not needed
        BigDecimal ask = BigDecimal.ONE;

        TradeSetDependentOrdersResponse ordersResponse = mock(TradeSetDependentOrdersResponse.class);

        when(this.mockBaseExitStrategy.getLastFullCandleHigh()).thenReturn(lastFullCandleHigh);
        when(this.mockBaseExitStrategy.getLastFullCandleLow()).thenReturn(lastFullCandleLow);
        when(this.mockBaseExitStrategy.getStopLossOrderPriceByID(any(Account.class), any(OrderID.class))).thenReturn(INITIAL_STOP_PRICE_SHORT);
        //will return different object if changeStopLoss to firstStopPrice
        when(this.mockBaseExitStrategy.changeStopLoss(this.mockTradeID,firstStopPrice)).thenReturn(ordersResponse);

        when(this.mockCurrentUnits.bigDecimalValue()).thenReturn(INITIAL_UNITS.multiply(BigDecimal.valueOf(-1)));

        this.setBaseExitStrategyToMock();

        this.trailExitAfterSignificantExtremeStrategy.execute(this.mockAccount, ask, bid, this.mockDateTime);

        TradeSetDependentOrdersResponse tradeStopResponse = this.getTradeSetStopResponse();


        assertSame(ordersResponse, tradeStopResponse);

    }


    //Simulating 3 candlestick:
    // 1st is lower than open price,
    // 2nd is higher than 1st and lower than firstStopPrice
    // 3-rd is lower than first.
    // In this way the lastSignificantHigh will be lower than firstStopPrice and will be confirmed with new significantLow(from 3-rd candlestick), which broke previous significantLow(from 1-st candlestick)
    @Test
    public void WhenUnitsSizeNegativeAndPriceUnderTargetAndSignificantHighLowerThanFirstStopPriceThenChangeStopToSignificantHigh() throws NoSuchFieldException, IllegalAccessException {
        //first lower candlestick
        BigDecimal lastFullCandleHigh = BigDecimal.valueOf(1.13976);
        BigDecimal lastFullCandleLow = BigDecimal.valueOf(1.13916);
        BigDecimal bid = BigDecimal.valueOf(1.13906);
        //ask not needed
        BigDecimal ask = BigDecimal.ONE;

        TradeSetDependentOrdersResponse ordersResponse = mock(TradeSetDependentOrdersResponse.class);

        when(this.mockBaseExitStrategy.getLastFullCandleHigh()).thenReturn(lastFullCandleHigh);
        when(this.mockBaseExitStrategy.getLastFullCandleLow()).thenReturn(lastFullCandleLow);
        when(this.mockBaseExitStrategy.getStopLossOrderPriceByID(any(Account.class), any(OrderID.class))).thenReturn(INITIAL_STOP_PRICE_SHORT);

        when(this.mockCurrentUnits.bigDecimalValue()).thenReturn(INITIAL_UNITS.multiply(BigDecimal.valueOf(-1)));

        this.setBaseExitStrategyToMock();

        this.trailExitAfterSignificantExtremeStrategy.execute(this.mockAccount, ask, bid, this.mockDateTime);

        //first higher candlestick
        lastFullCandleHigh = BigDecimal.valueOf(1.14105);
        lastFullCandleLow = BigDecimal.valueOf(1.14015);
        bid = BigDecimal.valueOf(1.14010);
        when(this.mockBaseExitStrategy.getLastFullCandleHigh()).thenReturn(lastFullCandleHigh);
        when(this.mockBaseExitStrategy.getLastFullCandleLow()).thenReturn(lastFullCandleLow);
        when(this.mockBaseExitStrategy.getStopLossOrderPriceByID(any(Account.class), any(OrderID.class))).thenReturn(BigDecimal.valueOf(1.14165));

        //when call changeStopLoss with significantHigh lower than stopLossPrice the response must be ordersResponse;
        when(this.mockBaseExitStrategy.changeStopLoss(this.mockTradeID, lastFullCandleHigh.add(Config.SPREAD))).thenReturn(ordersResponse);

        this.trailExitAfterSignificantExtremeStrategy.execute(this.mockAccount, ask, bid, this.mockDateTime);

        //second lower candlestick
        lastFullCandleHigh = BigDecimal.valueOf(1.1401);
        lastFullCandleLow = BigDecimal.valueOf(1.13891);
        bid = BigDecimal.valueOf(1.13881);
        when(this.mockBaseExitStrategy.getLastFullCandleHigh()).thenReturn(lastFullCandleHigh);
        when(this.mockBaseExitStrategy.getLastFullCandleLow()).thenReturn(lastFullCandleLow);

        this.trailExitAfterSignificantExtremeStrategy.execute(this.mockAccount, ask, bid, this.mockDateTime);


        TradeSetDependentOrdersResponse tradeStopResponse = this.getTradeSetStopResponse();

        assertSame(ordersResponse, tradeStopResponse);

    }

    private TradeSetDependentOrdersResponse getTradeSetStopResponse() throws NoSuchFieldException, IllegalAccessException {
        Field halfTradeResponse = this.trailExitAfterSignificantExtremeStrategy.getClass().getDeclaredField("tradeSetDependentOrdersResponse");
        halfTradeResponse.setAccessible(true);
        return (TradeSetDependentOrdersResponse) halfTradeResponse.get(this.trailExitAfterSignificantExtremeStrategy);

    }

    private void setBaseExitStrategyToMock() throws NoSuchFieldException, IllegalAccessException {
        Field updater = this.trailExitAfterSignificantExtremeStrategy.getClass().getDeclaredField("baseExitStrategy");
        updater.setAccessible(true);
        updater.set(this.trailExitAfterSignificantExtremeStrategy, this.mockBaseExitStrategy);
    }
}