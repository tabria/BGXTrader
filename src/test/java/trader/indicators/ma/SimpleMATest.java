package trader.indicators.ma;

import com.oanda.v20.instrument.*;
import com.oanda.v20.pricing_common.PriceValue;
import com.oanda.v20.primitives.DateTime;
import org.junit.Before;
import org.junit.Test;
import trader.candles.CandlesUpdater;
import trader.indicators.enums.CandlestickPriceType;
import trader.trades.entities.Point;

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

public class SimpleMATest {

    private List<String> candlesClosePrices = new ArrayList<>(Arrays.asList(
            "1.16114", "1.16214", "1.16314", "1.16414", "1.16514", "1.16614", "1.16714",
            "1.16814", "1.16914", "1.16114", "1.16214", "1.16314", "1.16414", "1.15714"
    ));
    private List<String> candlesDateTime = new ArrayList<>(Arrays.asList(
            "2018-08-01T09:39:00Z", "2018-08-01T09:40:00Z", "2018-08-01T09:41:00Z", "2018-08-01T09:42:00Z",
            "2018-08-01T09:43:00Z", "2018-08-01T09:44:00Z", "2018-08-01T09:45:00Z", "2018-08-01T09:46:00Z",
            "2018-08-01T09:47:00Z", "2018-08-01T09:48:00Z", "2018-08-01T09:49:00Z", "2018-08-01T09:50:00Z",
            "2018-08-01T09:51:00Z", "2018-08-01T09:52:00Z"

    ));


    private SimpleMA sma;
    private CandlesUpdater updater;

    private List<Candlestick> candlestickList;
    private CandlestickPriceType mockCandlestickPriceType;
    private long period;
    private DateTime mockDateTime;
    private BigDecimal bid;
    private BigDecimal ask;

    @Before
    public void before() {


        this.mockDateTime = mock(DateTime.class);
        this.bid = BigDecimal.ONE;
        this.ask = BigDecimal.TEN;

        this.mockCandlestickPriceType = mock(CandlestickPriceType.class);
        fillCandlestickList();
        setPeriod();

        this.updater = mock(CandlesUpdater.class);
        when(this.updater.getCandles()).thenReturn(this.candlestickList);
        when(this.updater.updateCandles(this.mockDateTime)).thenReturn(true);


//        this.mockRequest = mock(InstrumentCandlesRequest.class);
//
//        this.mockResponse = mock(InstrumentCandlesResponse.class);
//        when(this.mockResponse.getCandles()).thenReturn(this.candlestickList);
//
//        this.mockContext = mock(Context.class);
//        this.mockContext.instrument = mock(InstrumentContext.class);
//        when(this.mockContext.instrument.candles(this.mockRequest)).thenReturn(this.mockResponse);
//
//
//        this.mockMAType = mock(MAType.class);
//
//        this.dateTime = new DateTime("2018-08-01T09:53:00Z");
//        ZonedDateTime zd = dateTimeConversion(this.dateTime);
//
//        this.mockMA = mock(MovingAverage.class);
//        when(this.mockMA.getPeriod()).thenReturn(this.period);
//        when(this.mockMA.getCandles()).thenReturn(this.candlestickList);
//        when(this.mockMA.getAppliedPrice()).thenReturn(this.mockCandlestickPriceType);
//        when(this.mockMA.getLastCandleDateTime()).thenReturn(this.dateTime);
//        when(this.mockMA.nextCandleOpenTime(this.dateTime)).thenReturn(zd);


        this.sma = new SimpleMA(this.period, this.mockCandlestickPriceType, this.updater);

    }

    @Test
    public void WhenCallCreateThenReturnDifferentObject() {
        SimpleMA simpleMA = new SimpleMA(this.period, this.mockCandlestickPriceType, this.updater);

        assertNotEquals("Objects must not be equal ",this.sma, simpleMA);
    }

    @Test
    public void WhenGetMAValuesThenReturnCorrectResult() {
        this.sma.update(this.mockDateTime, this.ask, this.bid);
        List<BigDecimal> maValues = this.sma.getValues();
        assertEquals(9, maValues.size());
        int result = maValues.get(maValues.size()-1).compareTo(BigDecimal.valueOf(1.16281));
        assertEquals(0, result);
    }

    @Test
    public void WhenCallUpdateThenReturnCorrectValues() {

        this.sma.update(this.mockDateTime, this.ask, this.bid);

        DateTime newDt = mock(DateTime.class);

        this.candlesClosePrices.add("1.16814");
        this.candlesDateTime.add("2018-08-01T09:53:00Z");
        fillCandlestickList();

        when(this.updater.getCandles()).thenReturn(this.candlestickList);

        this.sma.update(newDt, this.ask, this.bid);

        List<BigDecimal> getMaValues = this.sma.getValues();

        assertEquals(9, getMaValues.size());

        BigDecimal bd = new BigDecimal("1.16281");
        int result = getMaValues.get(getMaValues.size()-1).compareTo(bd);
        assertEquals(0, result);
    }

    @Test
    public void WhenCallGetPointsThenReturnCorrectResult(){
        this.sma.update(this.mockDateTime, this.ask, this.bid);
        List<BigDecimal> values = this.sma.getValues();

        BigDecimal price1 = values.get(values.size()-4);
        BigDecimal price2 = values.get(values.size()-3);
        BigDecimal price3 = values.get(values.size()-2);

        List<Point> points = this.sma.getPoints();

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
        String result = this.sma.toString();
        String expected = String.format("SimpleMA{period=%d, candlestickPriceType=%s, maValues=[], points=[], isTradeGenerated=false}", this.period, this.mockCandlestickPriceType.toString());

        assertEquals(expected, result);
    }

    //total candles count for the given periods are calculated with this formula:
    // {@code period*2 + 2 => period = (number of candles - 2) /2}
    private void setPeriod(){
        long period = (candlesClosePrices.size() - 2)/2;
        if (period <= 0){
            throw new IllegalArgumentException("Prices in CANDLES_CLOSE_PRICES must be at least 4");
        }
        this.period = period;
    }

    private ZonedDateTime dateTimeConversion(DateTime dateTime){
        Instant instantDateTime = Instant.parse(dateTime.toString());
        ZoneId zoneId = ZoneId.of("UTC");
        return ZonedDateTime.ofInstant(instantDateTime, zoneId);
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

}