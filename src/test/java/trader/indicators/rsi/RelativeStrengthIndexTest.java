package trader.indicators.rsi;

import com.oanda.v20.Context;
import com.oanda.v20.instrument.Candlestick;
import com.oanda.v20.instrument.CandlestickData;
import com.oanda.v20.instrument.CandlestickGranularity;
import com.oanda.v20.instrument.InstrumentCandlesRequest;
import com.oanda.v20.pricing_common.PriceValue;
import com.oanda.v20.primitives.DateTime;
import org.junit.Before;
import org.junit.Test;
import trader.candles.CandlesUpdater;
import trader.indicators.enums.CandlestickPriceType;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RelativeStrengthIndexTest {



    private static final long DEFAULT_PERIOD = 14L;
    private static final CandlestickGranularity DEFAULT_TIME_FRAME = CandlestickGranularity.H4;

    private List<String> candlesClosePrices = new ArrayList<>(Arrays.asList(
        "1.44359", "1.44383", "1.4444", "1.44516", "1.44348", "1.44248", "1.44251", "1.44277", "1.44277", "1.44133", "1.44026",             "1.43954", "1.44066", "1.43901","1.44107", "1.43992", "1.44075", "1.44193", "1.44016", "1.43726", "1.43696", "1.43699",             "1.43421"

        ));

    private RelativeStrengthIndex rsi;
    private Context mockContext;
    private long period;
    private CandlestickPriceType mockCandlestickPriceType;
    private CandlestickGranularity timeFrame;
    private InstrumentCandlesRequest mockRequest;
    private List<Candlestick> candlestickList;
    private CandlesUpdater mockUpdater;
    private BigDecimal ask;
    private BigDecimal bid;
    private DateTime mockDateTime;

    @Before
    public void before() throws Exception {

        this.mockDateTime = mock(DateTime.class);

        this.ask = BigDecimal.ONE;
        this.bid = BigDecimal.TEN;

        this.mockContext = mock(Context.class);
        this.mockCandlestickPriceType = mock(CandlestickPriceType.class);

        this.timeFrame = DEFAULT_TIME_FRAME;
        this.mockRequest = mock(InstrumentCandlesRequest.class);

        fillCandlestickList();
        setPeriod();

        this.mockUpdater = mock(CandlesUpdater.class);
        when(this.mockUpdater.getCandles()).thenReturn(this.candlestickList);
        when(this.mockUpdater.updateCandles(this.mockDateTime)).thenReturn(true);

        this.rsi = createRSI();

    }


    @Test
    public void WhenGetValuesThenReturnCorrectResult(){
        this.rsi.updateMovingAverage(this.mockDateTime);

        List<BigDecimal> values = this.rsi.getValues();
        assertEquals(9, values.size());

        int result = values.get(8).compareTo(BigDecimal.valueOf(26.54890));
        assertEquals(0, result);

    }

    @Test
    public void WhenUpdateThenReturnCorrectResult(){
        this.candlesClosePrices.remove(this.candlesClosePrices.size()-1);
        this.fillCandlestickList();
        assertEquals(22, this.candlestickList.size());

        this.rsi.updateMovingAverage(this.mockDateTime);

        List<BigDecimal> preUpdateValues = this.rsi.getValues();
        BigDecimal rsiPreUpdateLastValue = preUpdateValues.get(preUpdateValues.size()-1);

        this.candlesClosePrices.add("1.48814");
        fillCandlestickList();

        when(this.mockUpdater.getCandles()).thenReturn(this.candlestickList);
        this.rsi.updateMovingAverage(mockDateTime);

        List<BigDecimal> afterUpdateValues = this.rsi.getValues();
        BigDecimal rsiAfterUpdateValue = afterUpdateValues .get(afterUpdateValues .size()-1);

        int resultComparePreAndAfter = rsiPreUpdateLastValue.compareTo(rsiAfterUpdateValue);
        int trueResult = BigDecimal.valueOf(86.24454).compareTo(rsiAfterUpdateValue);


        assertNotEquals(0,resultComparePreAndAfter);
        assertEquals(0, trueResult);


    }

    @Test
    public void TestToString(){
        String result = this.rsi.toString();
        String expected = String.format("RelativeStrengthIndex{candlesticksQuantity=%d, candlestickPriceType=%s, rsiValues=[], points=[], isTradeGenerated=false}", this.period, this.mockCandlestickPriceType.toString());

        assertEquals(expected, result);
    }

    private RelativeStrengthIndex createRSI() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<?> objClass = RelativeStrengthIndex.class;
        Constructor<?> cons = objClass.getDeclaredConstructor(long.class, CandlestickPriceType.class, CandlesUpdater.class);
        return (RelativeStrengthIndex) cons.newInstance(this.period, this.mockCandlestickPriceType, this.mockUpdater);

    }

    //fill candlestick list with candles. Candles have only time and close price
    private void fillCandlestickList(){

        this.candlestickList = new ArrayList<>();

        for (String candlesClosePrice : candlesClosePrices) {
            //candle 1
            // DateTime dateTime1 = mock(DateTime.class);
            //   when(dateTime1.toString()).thenReturn(candlesDateTime.get(i));

            PriceValue priceValue1 = mock(PriceValue.class);
            when(priceValue1.toString()).thenReturn(candlesClosePrice);

            CandlestickData candlestickData1 = mock(CandlestickData.class);
            when(candlestickData1.getC()).thenReturn(priceValue1);

            Candlestick candle1 = mock(Candlestick.class);
            //    when(candle1.getTime()).thenReturn(dateTime1);
            when(candle1.getMid()).thenReturn(candlestickData1);

            when(this.mockCandlestickPriceType.extractPrice(candlestickData1)).thenReturn(new BigDecimal(candlesClosePrice));

            this.candlestickList.add(candle1);
        }
    }
    //total candles count for the given periods are calculated with this formula:
    private void setPeriod(){
        this.period = RelativeStrengthIndexTest.DEFAULT_PERIOD;
    }

}