package trader.candles;

import com.oanda.v20.ExecuteException;
import com.oanda.v20.RequestException;
import com.oanda.v20.instrument.Candlestick;
import com.oanda.v20.instrument.CandlestickGranularity;
import com.oanda.v20.primitives.DateTime;
import org.junit.Before;
import org.junit.Test;
import trader.OandaAPI.OandaAPIMock;
import trader.indicators.IndicatorUpdateHelper;
import trader.indicators.enums.CandleGranularity;
import trader.indicators.enums.CandlestickPriceType;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CandlesUpdaterTest {

    private static final String DEFAULT_DATE_TIME = "2018-01-01T01:01:01Z";

    private IndicatorUpdateHelper indicatorUpdateHelper;
    private OandaAPIMock oandaAPIMock;
    private CandlesUpdater candlesUpdater;

    @Before
    public void setUp() throws Exception {
        initializeIndicatorUpdateHelper();
        initializeOandaAPIMock();
        candlesUpdater = new CandlesUpdater(oandaAPIMock.getContext(),
                                oandaAPIMock.getMockRequest(), CandleGranularity.M30);
    }

    @Test
    public void testGetCandlesForEquality(){
        assertThat(candlesUpdater.getCandles(), is(indicatorUpdateHelper.getCandlestickList()));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetCandlesForImmutability(){
        List<Candlestick> candles = candlesUpdater.getCandles();
        candles.add(mock(Candlestick.class));
    }

    @Test
    public void callUpdateCandlesWithBlankCandlestickList_UpdateFalse(){
        oandaAPIMock.setMockResponseToGetCandles(new ArrayList<>());
        CandlesUpdater candlesUpdater = new CandlesUpdater(oandaAPIMock.getContext(),
                oandaAPIMock.getMockRequest(), CandleGranularity.M30);
        DateTime currentCandleDateTime = mock(DateTime.class);
        when(currentCandleDateTime.toString()).thenReturn(DEFAULT_DATE_TIME);

        assertFalse(candlesUpdater.updateCandles(currentCandleDateTime));
    }

    @Test
    public void WhenCallUpdateCandlesWithLastSavedCandleDateTime_UpdateFalse(){
        DateTime lastCandleDateTime = mock(DateTime.class);
        String candleStringDateTime = getStringDateTimeForLastCandle();
        when(lastCandleDateTime.toString()).thenReturn(candleStringDateTime);

        assertFalse(this.candlesUpdater.updateCandles(lastCandleDateTime));
    }

    @Test
    public void candleDateTimeBetweenLastSavedCandleDateTimeAndFutureCandleDateTime_UpdateFalse(){
        DateTime currentCandleDateTime = mock(DateTime.class);
        when(currentCandleDateTime.toString()).thenReturn("2018-08-01T10:00:00Z");

        assertFalse(this.candlesUpdater.updateCandles(currentCandleDateTime));
    }

    @Test
    public void callUpdateCandlesWithCorrectDateTime_UpdateTrue(){
        DateTime currentCandleDateTime = mock(DateTime.class);
        when(currentCandleDateTime.toString()).thenReturn("2018-08-01T10:25:00Z");
        simulateCandlestickListUpdate();

        assertTrue(this.candlesUpdater.updateCandles(currentCandleDateTime));
    }

    private void initializeIndicatorUpdateHelper() {
        this.indicatorUpdateHelper = new IndicatorUpdateHelper(mock(CandlestickPriceType.class));
        this.indicatorUpdateHelper.fillCandlestickList();
    }

    private void initializeOandaAPIMock() throws RequestException, ExecuteException {
        oandaAPIMock = new OandaAPIMock();
        oandaAPIMock.setMockRequestToCandles();
        oandaAPIMock.setMockResponseToGetCandles(this.indicatorUpdateHelper.getCandlestickList());
    }

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
        this.oandaAPIMock.setMockResponseToGetCandles(this.indicatorUpdateHelper.getCandlestickList());
    }

}
