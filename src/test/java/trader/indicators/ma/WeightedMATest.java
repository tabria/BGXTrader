package trader.indicators.ma;

import com.oanda.v20.instrument.*;
import com.oanda.v20.pricing_common.PriceValue;
import com.oanda.v20.primitives.DateTime;
import com.sun.istack.internal.NotNull;
import org.junit.Before;
import org.junit.Test;
import trader.candles.CandlesUpdater;
import trader.trades.entities.Point;
import trader.indicators.enums.AppliedPrice;

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

public class WeightedMATest {

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


    private WeightedMA wma;
    private CandlesUpdater updater;
    private List<Candlestick> candlestickList;
    private AppliedPrice mockAppliedPrice;
    private long period;
    private DateTime mockDateTime;
    private BigDecimal ask;
    private BigDecimal bid;

    @Before
    public void before() throws Exception {

        this.ask = BigDecimal.ONE;
        this.bid = BigDecimal.TEN;

        this.mockDateTime = mock(DateTime.class);
        //this.candlestickData = mock(CandlestickData.class);

        this.mockAppliedPrice = mock(AppliedPrice.class);
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
//        when(this.mockMA.getAppliedPrice()).thenReturn(this.mockAppliedPrice);
//        when(this.mockMA.getLastCandleDateTime()).thenReturn(this.dateTime);
//        when(this.mockMA.nextCandleOpenTime(this.dateTime)).thenReturn(zd);


        this.wma = new WeightedMA(this.period, this.mockAppliedPrice, this.updater) ;


    }

    @Test
    public void WhenCallCreateThenReturnDifferenObject() {
        WeightedMA weightedMA = new WeightedMA(this.period, this.mockAppliedPrice, this.updater);

        assertNotEquals("Objects must not be equal ",this.wma, weightedMA);
    }

    @Test
    public void getMAValues() {
        this.wma.update(this.mockDateTime, this.ask, this.bid);
        List<BigDecimal> maValues = this.wma.getValues();
        assertEquals(9, maValues.size());
        int result = maValues.get(maValues.size()-1).compareTo(BigDecimal.valueOf(1.16162));
        assertEquals(0, result);
    }

    @Test
    public void WhenCallUpdateThenReturnCorrectValues() {

        this.wma.update(this.mockDateTime, this.ask, this.bid);

        DateTime newDt = mock(DateTime.class);

        this.candlesClosePrices.add("1.16814");
        this.candlesDateTime.add("2018-08-01T09:53:00Z");
        fillCandlestickList();

        when(this.updater.getCandles()).thenReturn(this.candlestickList);

        this.wma.update(newDt, this.ask, this.bid);

        List<BigDecimal> getMaValues = this.wma.getValues();

        assertEquals(9, getMaValues.size());

        BigDecimal bd = new BigDecimal("1.16162");
        int result = getMaValues.get(getMaValues.size()-1).compareTo(bd);
        assertEquals(0, result);
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

            when(this.mockAppliedPrice.apply(candlestickData1)).thenReturn(new BigDecimal(candlesClosePrices.get(i)));

            this.candlestickList.add(candle1);
        }
    }

    @Test
    public void WhenCallGetPointsThenReturnCorrectResult(){
        this.wma.update(this.mockDateTime, this.ask, this.bid);
        List<BigDecimal> values = this.wma.getValues();

        BigDecimal price1 = values.get(values.size()-4);
        BigDecimal price2 = values.get(values.size()-3);
        BigDecimal price3 = values.get(values.size()-2);

        List<Point> points = this.wma.getPoints();

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
    //total candles count for the given periods are calculated with this formula:
    // {@code period*2 + 2 => period = (number of candles - 2) /2}
    private void setPeriod(){
        long period = (candlesClosePrices.size() - 2)/2;
        if (period <= 0){
            throw new IllegalArgumentException("Prices in CANDLES_CLOSE_PRICES must be at least 3");
        }
        this.period = period;
    }

    private ZonedDateTime dateTimeConversion(@NotNull DateTime dateTime){
        Instant instantDateTime = Instant.parse(dateTime.toString());
        ZoneId zoneId = ZoneId.of("UTC");
        return ZonedDateTime.ofInstant(instantDateTime, zoneId);
    }
}