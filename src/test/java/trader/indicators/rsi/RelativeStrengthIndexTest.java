package trader.indicators.rsi;

import com.oanda.v20.instrument.Candlestick;
import com.oanda.v20.primitives.DateTime;
import org.junit.Before;
import org.junit.Test;
import trader.candle.CandlesUpdater;
import trader.indicators.BaseIndicatorTest;
import trader.candle.CandleGranularity;
import trader.candle.CandlestickPriceType;
import trader.trades.entities.Point;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RelativeStrengthIndexTest extends BaseIndicatorTest {


    private static final long DEFAULT_PERIOD = 14L;
    private static final CandleGranularity DEFAULT_TIME_FRAME = CandleGranularity.H4;
    private static final BigDecimal EXPECTED_CANDLESTICK_PRICE = BigDecimal.valueOf(29.18298);
    private static final BigDecimal UPDATED_CANDLESTICK_PRICE = BigDecimal.valueOf(61.80903);

//    private List<String> candlesClosePrices = new ArrayList<>(Arrays.asList(
//        "1.44359", "1.44383", "1.4444", "1.44516", "1.44348", "1.44248", "1.44251", "1.44277", "1.44277", "1.44133", "1.44026",  "1.43954", "1.44066", "1.43901","1.44107", "1.43992", "1.44075", "1.44193", "1.44016", "1.43726", "1.43696", "1.43699", "1.43421"
//
//        ));

    private RelativeStrengthIndex rsi;
//    private Context mockContext;
//    private long candlesQuantity; //
//    private CandlestickPriceType mockCandlestickPriceType; //
//    private CandleGranularity timeFrame;
//    private InstrumentCandlesRequest mockRequest;
    private List<Candlestick> candlestickList;
//    private CandlesUpdater mockUpdater;//
//    private BigDecimal ask;
//    private BigDecimal bid;
//    private DateTime mockDateTime;

    @Before
    public void before() {

        super.before();
        this.rsi = new RelativeStrengthIndex(this.candlesticksQuantity, this.mockCandlestickPriceType, this.candlesUpdater);

//        this.mockDateTime = mock(DateTime.class);
//
//        this.ask = BigDecimal.ONE;
//        this.bid = BigDecimal.TEN;
//
//        this.mockContext = mock(Context.class);
//        this.mockCandlestickPriceType = mock(CandlestickPriceType.class);
//
//        this.timeFrame = DEFAULT_TIME_FRAME;
//        this.mockRequest = mock(InstrumentCandlesRequest.class);
//
//        fillCandlestickList();
//        setPeriod();
//
//        this.mockUpdater = mock(CandlesUpdater.class);
//        when(this.mockUpdater.getCandles()).thenReturn(this.candlestickList);
//        when(this.mockUpdater.updateCandles(this.mockDateTime)).thenReturn(true);

 //       this.rsi = createRSI();

    }

    @Override
    @Test(expected = UnsupportedOperationException.class )
    public void getValuesReturnImmutableResult(){
        List<BigDecimal> values = this.rsi.getValues();
        values.add(null);
    }
    @Override
    @Test
    public void getMAValuesReturnCorrectResult() {
        this.rsi.updateIndicator(mockDateTime);
        BigDecimal lastCandlestickPrice = getLastCandlestickPrice();
        assertEquals(0, lastCandlestickPrice.compareTo(EXPECTED_CANDLESTICK_PRICE));
    }

    @Override
    @Test
    public void testSuccessfulUpdate() {
        this.rsi.updateIndicator(this.mockDateTime);
        updateCandlestickListInSuper();
        DateTime currentDateTime = mock(DateTime.class);
        when(currentDateTime.toString()).thenReturn("2018-08-01T10:25:00Z");
        this.rsi.updateIndicator(currentDateTime);
        assertEquals(0, getLastCandlestickPrice().compareTo(UPDATED_CANDLESTICK_PRICE));
    }

    @Override
    @Test
    public void getPointsReturnCorrectResult() {
        this.rsi.updateIndicator(this.mockDateTime);
        List<Point> points = this.rsi.getPoints();
        List<BigDecimal> values = this.rsi.getValues();

        testPointPrice(points, values);
        testPointTime(points, values);
    }

//    @Test
//    public void WhenUpdateThenReturnCorrectResult(){
//        this.candlesClosePrices.remove(this.candlesClosePrices.size()-1);
//        this.fillCandlestickList();
//        assertEquals(22, candlestickList.size());
//
//        this.rsi.updateIndicator(this.mockDateTime);
//
//        List<BigDecimal> preUpdateValues = this.rsi.getValues();
//        BigDecimal rsiPreUpdateLastValue = preUpdateValues.get(preUpdateValues.size()-1);
//
//        this.candlesClosePrices.add("1.48814");
//        fillCandlestickList();
//
//        when(candlesUpdater.getCandles()).thenReturn(this.candlestickList);
//        this.rsi.updateIndicator(mockDateTime);
//
//        List<BigDecimal> afterUpdateValues = this.rsi.getValues();
//        BigDecimal rsiAfterUpdateValue = afterUpdateValues .get(afterUpdateValues .size()-1);
//
//        int resultComparePreAndAfter = rsiPreUpdateLastValue.compareTo(rsiAfterUpdateValue);
//        int trueResult = BigDecimal.valueOf(86.24454).compareTo(rsiAfterUpdateValue);
//
//
//        assertNotEquals(0,resultComparePreAndAfter);
//        assertEquals(0, trueResult);
//
//
//    }

    @Test
    public void TestToString(){
        String result = this.rsi.toString();
        String expected = String.format("RelativeStrengthIndex{candlesticksQuantity=%d, " +
                "candlestickPriceType=%s, rsiValues=[], points=[], " +
                "isTradeGenerated=false}", candlesticksQuantity, this.mockCandlestickPriceType.toString());

        assertEquals(expected, result);
    }

    @Override
    protected BigDecimal getLastCandlestickPrice() {
        List<BigDecimal> rsiValues = this.rsi.getValues();
        return rsiValues.get(rsiValues.size() - 1);
    }

    private RelativeStrengthIndex createRSI() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<?> objClass = RelativeStrengthIndex.class;
        Constructor<?> cons = objClass.getDeclaredConstructor(long.class, CandlestickPriceType.class, CandlesUpdater.class);
        return (RelativeStrengthIndex) cons.newInstance(candlesticksQuantity, this.mockCandlestickPriceType, candlesUpdater);

    }

//    //fill candlestick list with candle. Candles have only time and close price
//    private void fillCandlestickList(){
//
//        this.candlestickList = new ArrayList<>();
//
//        for (String candlesClosePrice : candlesClosePrices) {
//            //candle 1
//            // DateTime dateTime1 = mock(DateTime.class);
//            //   when(dateTime1.toString()).thenReturn(candlesDateTime.get(i));
//
//            PriceValue priceValue1 = mock(PriceValue.class);
//            when(priceValue1.toString()).thenReturn(candlesClosePrice);
//
//            CandlestickData candlestickData1 = mock(CandlestickData.class);
//            when(candlestickData1.getC()).thenReturn(priceValue1);
//
//            Candlestick candle1 = mock(Candlestick.class);
//            //    when(candle1.getTime()).thenReturn(dateTime1);
//            when(candle1.getMid()).thenReturn(candlestickData1);
//
//            when(this.mockCandlestickPriceType.extractPrice(candlestickData1)).thenReturn(new BigDecimal(candlesClosePrice));
//
//            this.candlestickList.add(candle1);
//        }
//    }
    //total candle count for the given periods are calculated with this formula:
    private void setPeriod(){
        candlesticksQuantity = RelativeStrengthIndexTest.DEFAULT_PERIOD;
    }

}