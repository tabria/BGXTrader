package trader.candle;

import org.junit.Before;
import org.junit.Test;
import trader.CommonTestClassMembers;
import trader.OandaAPIMock.OandaAPIMockInstrument;
import trader.indicators.IndicatorUpdateHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class CandlesUpdaterTest {

    private static final String DEFAULT_DATE_TIME = "2018-01-01T01:01:01Z";

    private IndicatorUpdateHelper indicatorUpdateHelper;
    private OandaAPIMockInstrument oandaInstrument;
    private CandlesUpdater candlesUpdater;

    /////////////////////////
    private CommonTestClassMembers commonMembers;

    @Before
    public void setUp() throws Exception {
        initializeIndicatorUpdateHelper();
 //       initializeOandaAPIMock();
//        candlesUpdater = new CandlesUpdater(oandaInstrument.getContext(),
//                                oandaInstrument.getMockRequest(), CandleGranularity.M30);
        ///////////////////////////////
        commonMembers = new CommonTestClassMembers();
        //candlesUpdater = new CandlesUpdater()
    }

    @Test
    public void testInitializeToReturnCorrectQuantity() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        long expected = 100L;
        Method initialize = commonMembers.getPrivateMethodForTest(CandlesUpdater.class, "initialize", long.class);
        List<trader.candle.Candlestick> resultList = (List<trader.candle.Candlestick>) initialize.invoke(candlesUpdater, expected);
        assertEquals(expected, resultList.size());
    }


//    @Test
//    public void testGetCandlesForEquality(){
//        assertThat(candlesUpdater.getCandles(), is(indicatorUpdateHelper.getCandlestickList()));
//    }
//
//    @Test(expected = UnsupportedOperationException.class)
//    public void testGetCandlesForImmutability(){
//        List<Candlestick> candles = candlesUpdater.getCandles();
//        candles.add(mock(Candlestick.class));
//    }
//
//    @Test
//    public void callUpdateCandlesWithBlankCandlestickList_UpdateFalse(){
//        oandaInstrument.setMockResponseToGetCandles(new ArrayList<>());
//        CandlesUpdater candlesUpdater = new CandlesUpdater(oandaInstrument.getContext(),
//                oandaInstrument.getMockRequest(), CandleGranularity.M30);
//        DateTime currentCandleDateTime = mock(DateTime.class);
//        when(currentCandleDateTime.toString()).thenReturn(DEFAULT_DATE_TIME);
//
//        assertFalse(candlesUpdater.updateCandles(currentCandleDateTime));
//    }
//
//    @Test
//    public void WhenCallUpdateCandlesWithLastSavedCandleDateTime_UpdateFalse(){
//        DateTime lastCandleDateTime = mock(DateTime.class);
//        String candleStringDateTime = getStringDateTimeForLastCandle();
//        when(lastCandleDateTime.toString()).thenReturn(candleStringDateTime);
//
//        assertFalse(this.candlesUpdater.updateCandles(lastCandleDateTime));
//    }
//
//    @Test
//    public void candleDateTimeBetweenLastSavedCandleDateTimeAndFutureCandleDateTime_UpdateFalse(){
//        DateTime currentCandleDateTime = mock(DateTime.class);
//        when(currentCandleDateTime.toString()).thenReturn("2018-08-01T10:00:00Z");
//
//        assertFalse(this.candlesUpdater.updateCandles(currentCandleDateTime));
//    }
//
//    @Test
//    public void callUpdateCandlesWithCorrectDateTime_UpdateTrue(){
//        DateTime currentCandleDateTime = mock(DateTime.class);
//        when(currentCandleDateTime.toString()).thenReturn("2018-08-01T10:25:00Z");
//        simulateCandlestickListUpdate();
//
//        assertTrue(this.candlesUpdater.updateCandles(currentCandleDateTime));
//    }

    private void initializeIndicatorUpdateHelper() {
        this.indicatorUpdateHelper = new IndicatorUpdateHelper(mock(CandlestickPriceType.class));
        this.indicatorUpdateHelper.fillCandlestickList();
    }

//    private void initializeOandaAPIMock() throws RequestException, ExecuteException {
//        oandaInstrument = new OandaAPIMock();
//        oandaInstrument.setMockRequestToCandles();
//        oandaInstrument.setMockResponseToGetCandles(this.indicatorUpdateHelper.getCandlestickList());
//    }

    private String getStringDateTimeForLastCandle() {
        List<String> candlesDateTime = indicatorUpdateHelper.getCandlesDateTime();
        return candlesDateTime.get(candlesDateTime.size() - 1);
    }

    private void updateLastCandleDateTime() {
        this.indicatorUpdateHelper.setCandlesDateTime("2018-08-01T10:11:00Z",
                this.indicatorUpdateHelper.getCandlesDateTime().size()-1);
    }

    private void simulateCandlestickListUpdate() {
        updateLastCandleDateTime();
        this.indicatorUpdateHelper.fillCandlestickList();
        this.oandaInstrument.setMockResponseToGetCandles(this.indicatorUpdateHelper.getCandlestickList());
    }

}
