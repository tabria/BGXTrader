package trader.indicators;

import com.oanda.v20.ExecuteException;
import com.oanda.v20.RequestException;
import org.junit.Before;
import org.junit.Test;
import trader.OandaAPIMock.OandaAPIMockInstrument;
import trader.candle.CandlesUpdater;
import trader.candle.CandleGranularity;
import trader.candle.Candlestick;
import trader.candle.CandlestickPriceType;
import trader.trades.entities.Point;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public abstract class BaseIndicatorTest {

    private static final int DIVIDER = 2;
    private static final int IGNORED_CANDLES = 2;
    private static final int MINIMUM_CANDLES_COUNT = 3;
    private static final String NEW_PRICE_ENTRY = "1.16814";
    private static final String NEW_DATETIME_ENTRY = "2018-08-01T09:53:00Z";


    private List<String> candlesClosePrices;
    private List<String> candlesDateTime;
    protected CandlesUpdater candlesUpdater;
    protected CandlestickPriceType mockCandlestickPriceType = CandlestickPriceType.CLOSE;
    protected IndicatorUpdateHelper indicatorUpdateHelper;
    protected long period;
    protected Candlestick newCandle;

//    protected DateTime mockDateTime;
    private OandaAPIMockInstrument oandaInstrument;

    @Before
    public void before() {

//        this.mockDateTime = mock(DateTime.class);
//        when(this.mockDateTime.toString()).thenReturn("2018-07-01T09:53:00Z");
        this.candlesUpdater = mock(CandlesUpdater.class);
        //this.mockCandlestickPriceType = mock(CandlestickPriceType.class);
        this.indicatorUpdateHelper = new IndicatorUpdateHelper(this.mockCandlestickPriceType);

    //    initializeOandaAPIMock();

        this.candlesClosePrices = indicatorUpdateHelper.getCandlesClosePrices();
        this.candlesDateTime = indicatorUpdateHelper.getCandlesDateTime();
        this.indicatorUpdateHelper.fillCandlestickList();
        this.newCandle = mock(Candlestick.class);
        setNewCandle();
        setPeriod();
        setCandlesUpdater();
    }

    @Test(expected = UnsupportedOperationException.class )
    public abstract void getValuesReturnImmutableResult();

    @Test
    public abstract void getMAValuesReturnCorrectResult();

    @Test
    public abstract void testSuccessfulUpdate();

    @Test
    public abstract void getPointsReturnCorrectResult();

    @Test
    public abstract void TestToString();

    protected abstract BigDecimal getLastCandlestickPrice();


    private void setNewCandle(){
        when(newCandle.getClosePrice()).thenReturn(new BigDecimal(NEW_PRICE_ENTRY));
        when(newCandle.getDateTime())
                .thenReturn(ZonedDateTime.parse(NEW_DATETIME_ENTRY).withZoneSameInstant(ZoneId.of("UTC")));
        when(candlesUpdater.getUpdateCandle()).thenReturn(newCandle);
    }



    protected void updateCandlestickListInSuper() {
        this.candlesUpdater = mock(CandlesUpdater.class);
        this.candlesClosePrices.add(NEW_PRICE_ENTRY);
        this.candlesDateTime.add(NEW_DATETIME_ENTRY);
        this.indicatorUpdateHelper.fillCandlestickList();
        List<Candlestick> candlestickList = this.indicatorUpdateHelper.getCandlestickList();
 //       when(this.candlesUpdater.getCandles()).thenReturn(candlestickList);
    }

    protected void testPointTime(List<Point> points, List<BigDecimal> values) {
        int pointPosition = 0;
        for (int candlePosition = 5  ; candlePosition < values.size()-1 ; candlePosition++) {
            BigDecimal pointExpectedTime = BigDecimal.valueOf(pointPosition + 1);
            BigDecimal pointResultTime = points.get(pointPosition).getTime();

            assertEquals( 0, pointResultTime.compareTo(pointExpectedTime));

            pointPosition++;
        }
    }

    protected void testPointPrice(List<Point> points, List<BigDecimal> values) {
        int pointPosition = 0;
        for (int candlePosition = values.size()-3  ; candlePosition < values.size() ; candlePosition++) {
            BigDecimal pointExpectedPrice = values.get(candlePosition);
            BigDecimal pointResultPrice = points.get(pointPosition++).getPrice();

            assertEquals( 0, pointResultPrice.compareTo(pointExpectedPrice));
        }
    }

    private void setPeriod(){
        long period = candlesClosePrices.size()/ DIVIDER;
 //       long period = (candlesClosePrices.size() - IGNORED_CANDLES)/ DIVIDER;
        if (period < MINIMUM_CANDLES_COUNT){
            throw new IllegalArgumentException("Prices in CANDLES_CLOSE_PRICES must be at least 3");
        }
        this.period = period;
    }

    private void setCandlesUpdater() {

//        this.candlesUpdater = spy(new CandlesUpdater(oandaInstrument.getContext(),
//                oandaInstrument.getMockRequest(),  CandleGranularity.M30));

        List<Candlestick> candlestickList = this.indicatorUpdateHelper.getCandlestickList();
        doReturn(candlestickList).when(this.candlesUpdater).getCandles();
    }

//    private void initializeOandaAPIMockInstrument()  {
//        try {
//            oandaInstrument = new OandaAPIMockInstrument();
//            oandaInstrument.setMockRequestToCandles();
//            oandaInstrument.setMockResponseToGetCandles(this.indicatorUpdateHelper.getCandlestickList());
//        } catch (RequestException | ExecuteException e) {
//            e.printStackTrace();
//        }
//
//    }
}
