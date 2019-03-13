package trader.indicators.ma;

import com.oanda.v20.instrument.*;
import com.oanda.v20.pricing_common.PriceValue;
import com.oanda.v20.primitives.DateTime;
import org.junit.Before;
import org.junit.Test;
import trader.candles.CandlesUpdater;
import trader.trades.entities.Point;
import trader.indicators.enums.CandlestickPriceType;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static trader.CommonConstants.ASK;
import static trader.CommonConstants.BID;

public class ExponentialMATest extends BaseMATest {


    private ExponentialMA ema;
    private CandlesUpdater updater;
    private List<Candlestick> candlestickList;
    private CandlestickPriceType mockCandlestickPriceType;
    private long period;
    private DateTime mockDateTime;


    @Before
    public void before() {

        this.mockDateTime = mock(DateTime.class);

        this.mockCandlestickPriceType = mock(CandlestickPriceType.class);
        fillCandlestickList();
        setPeriod();
        this.updater = mock(CandlesUpdater.class);
        when(this.updater.getCandles()).thenReturn(this.candlestickList);
        when(this.updater.updateCandles(this.mockDateTime)).thenReturn(true);

        this.ema = new ExponentialMA(this.period, this.mockCandlestickPriceType, this.updater);

    }

    @Test
    public void WhenCreateThenReturnNewObject() {
        ExponentialMA exponentialMA2 = new ExponentialMA(this.period, this.mockCandlestickPriceType, this.updater);

        assertNotEquals("Objects must not be equal ",this.ema, exponentialMA2);
    }

    @Test
    public void WhenGetMAValuesThenReturnCorrectResult() {
        this.ema.update(this.mockDateTime, ASK, BID);
        List<BigDecimal> maValues = this.ema.getValues();
        assertEquals(9, maValues.size());
        int result = maValues.get(maValues.size()-1).compareTo(BigDecimal.valueOf(1.16204));
        assertEquals(0, result);
    }

    @Test
    public void WhenCallUpdateThenCorrectResults() {
        this.ema.update(this.mockDateTime, ASK, BID);

        DateTime newDt = mock(DateTime.class);

        this.candlesClosePrices.add("1.16814");
        this.candlesDateTime.add("2018-08-01T09:53:00Z");
        fillCandlestickList();

        when(this.updater.getCandles()).thenReturn(this.candlestickList);

        this.ema.update(newDt,  ASK, BID);

        List<BigDecimal> getMaValues = this.ema.getValues();

        assertEquals(9, getMaValues.size());

        BigDecimal bd = new BigDecimal("1.16204");
        int result = getMaValues.get(getMaValues.size()-1).compareTo(bd);
        assertEquals(0, result);

    }

    @Test
    public void WhenCallGetPointsThenReturnCorrectResult(){
        this.ema.update(this.mockDateTime,  ASK, BID);
        List<BigDecimal> values = this.ema.getValues();

        BigDecimal price1 = values.get(values.size()-4);
        BigDecimal price2 = values.get(values.size()-3);
        BigDecimal price3 = values.get(values.size()-2);

        List<Point> points = this.ema.getPoints();

        //point 1
        BigDecimal point1Price = points.get(0).getPrice();
        BigDecimal point1Time = points.get(0).getTime();

        assertEquals( 0, point1Price.compareTo(price1));
        assertEquals( 0, point1Time.compareTo(BigDecimal.ONE));

        //point 2
        BigDecimal point2Price = points.get(1).getPrice();
        BigDecimal point2Time = points.get(1).getTime();

        assertEquals( 0, point2Price.compareTo(price2));
        assertEquals( 0, point2Time.compareTo(BigDecimal.valueOf(2)));

        //point 3
        BigDecimal point3Price = points.get(2).getPrice();
        BigDecimal point3Time = points.get(2).getTime();


        assertEquals( 0, point3Price.compareTo(price3));
        assertEquals( 0, point3Time.compareTo(BigDecimal.valueOf(3)));

    }

    @Test
    public void TestToString(){
        String result = this.ema.toString();
        String expected = String.format("ExponentialMA{period=%d, candlestickPriceType=%s, maValues=[], points=[], isTradeGenerated=false}", period, this.mockCandlestickPriceType.toString());
        assertEquals(expected, result);
    }


    //fill candlestick list with candles. Candles have only time and close price
    private void fillCandlestickList(){

        this.candlestickList = new ArrayList<>();

        for (int i = 0; i < candlesClosePrices.size() ; i++) {
            //candle 1
            DateTime dateTime1 = mock(DateTime.class);
            when(dateTime1.toString()).thenReturn(candlesDateTime.get(i));

            PriceValue priceValue1 = mock(PriceValue.class);
            when(priceValue1.toString()).thenReturn(candlesClosePrices.get(i));

            CandlestickData candlestickData1 = mock(CandlestickData.class);
            when(candlestickData1.getC()).thenReturn(priceValue1);

            Candlestick candle1 = mock(Candlestick.class);
            when(candle1.getTime()).thenReturn(dateTime1);
            when(candle1.getMid()).thenReturn(candlestickData1);

            when(this.mockCandlestickPriceType.extractPrice(candlestickData1)).thenReturn(new BigDecimal(candlesClosePrices.get(i)));

            this.candlestickList.add(candle1);
        }
    }
    //total candles count for the given periods are calculated with this formula:
    // {@code period*2 + 2 => period = (number of candles - 2) /2}
    private void setPeriod(){
        long period = (candlesClosePrices.size() - 2)/2;
        if (period <= 0){
            throw new IllegalArgumentException("Prices in CANDLES_CLOSE_PRICES must be at least 3");
        }
        this.period = period;
    }

    private ZonedDateTime dateTimeConversion(DateTime dateTime){
        Instant instantDateTime = Instant.parse(dateTime.toString());
        ZoneId zoneId = ZoneId.of("UTC");
        return ZonedDateTime.ofInstant(instantDateTime, zoneId);
    }
}