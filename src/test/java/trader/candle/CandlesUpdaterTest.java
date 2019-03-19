package trader.candle;

import org.junit.Before;
import org.junit.Test;
import trader.CommonTestClassMembers;
import trader.OandaAPIMock.OandaAPIMockInstrument;
import trader.connectors.ApiConnector;
import trader.indicators.IndicatorUpdateHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CandlesUpdaterTest {

    private static final int CANDLESTICK_LIST_SIZE = 5;

    private CandlesUpdater candlesUpdater;
    private ApiConnector mockApiConnector;
    private List<Candlestick> candlesticks;
    private Candlestick mockCandle;
    private CommonTestClassMembers commonMembers;
    private ZonedDateTime timeNow;

    @Before
    public void setUp() throws Exception {

        mockCandle = mock(Candlestick.class);
        candlesticks = new ArrayList<>();
        commonMembers = new CommonTestClassMembers();
        mockApiConnector = mock(ApiConnector.class);
        setMockApiConnector();
        candlesUpdater = new CandlesUpdater(mockApiConnector);
        timeNow = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC"));
        fillCandlestickList();
    }


    @SuppressWarnings("unchecked")
    @Test
    public void testInitializeToReturnCorrectQuantity() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method initialize = commonMembers.getPrivateMethodForTest(CandlesUpdater.class, "initialize");
        List<Candlestick> resultList = (List<Candlestick>) initialize.invoke(candlesUpdater);

        assertSame(candlesticks, resultList);
        assertEquals(candlesticks, resultList);
    }

    @Test
    public void tesGetCandlesToReturnCorrectObject(){
        commonMembers.changeFieldObject(candlesUpdater, "candlestickList", candlesticks);
        List<Candlestick> candleList = candlesUpdater.getCandles();
        assertEquals(candlesticks, candleList);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void tesGetCandlesToReturnUnmodifiable(){
        commonMembers.changeFieldObject(candlesUpdater, "candlestickList", candlesticks);
        List<Candlestick> candleList = candlesUpdater.getCandles();
        candleList.add(mockCandle);
    }

    @Test
    public void testGetUpdateCandleToReturnCorrectCandle(){
        Candlestick updateCandle = candlesUpdater.getUpdateCandle();

        assertEquals(mockCandle, updateCandle);
    }

    @Test
    public void whenCallUpdateCandlesWithNonUpdatableCandle_UpdateFalse(){
        when(mockCandle.isComplete()).thenReturn(false);
        boolean result = candlesUpdater.updateCandles();

        assertFalse(result);
    }

    @Test
    public void whenCallUpdateCandlesWithUpdatableCandleWithBiggerTime_UpdateTrue(){
        boolean result = candlesUpdater.updateCandles();

        assertTrue(result);
    }

    @Test
    public void whenCallUpdateCandles_AddNewCandleToList(){
        List<Candlestick> candleList = candlesUpdater.getCandles();
        int oldSize = candleList.size();
        boolean result = candlesUpdater.updateCandles();
        candleList = candlesUpdater.getCandles();

        assertTrue(result);
        assertEquals(oldSize + 1, candleList.size());
    }

    @Test
    public void whenCallGetLastCandlestick_CorrectCandlestick() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method getLastCandlestick = commonMembers.getPrivateMethodForTest(candlesUpdater, "getLastCandlestick");
        Candlestick actual = (Candlestick) getLastCandlestick.invoke(candlesUpdater);
        Candlestick expected = candlesticks.get(candlesticks.size()-1);

        assertSame(expected, actual);
    }

    @Test
    public void testIfUpdateCandleDateTimeIsBiggerThanDateTimeOfTheOldCandle() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method isDateTimeTradeable = commonMembers
                .getPrivateMethodForTest(candlesUpdater, "isDateTimeTradeable", Candlestick.class, Candlestick.class);
        Candlestick lastCandle = candlesticks.get(candlesticks.size()-1);
        assertTrue( (boolean) isDateTimeTradeable.invoke(candlesUpdater, mockCandle, lastCandle));
    }

    @Test(expected = InvocationTargetException.class)
    public void testIfUpdateCandleIsCompletedAndDateTimeIsLowerThanDateTimeOfTheOldCandle_LoopTillUpdate() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        setMockCandleTime(timeNow=timeNow.plusSeconds(-300));
        commonMembers.changeFieldObject(candlesUpdater,"sleepTimeMilliseconds", -1);

        Method isDateTimeTradeable = commonMembers
                .getPrivateMethodForTest(candlesUpdater, "isDateTimeTradeable", Candlestick.class, Candlestick.class);
        Candlestick lastCandle = candlesticks.get(candlesticks.size()-1);
        isDateTimeTradeable.invoke(candlesUpdater, mockCandle, lastCandle);
    }

    private void setMockApiConnector() {
        when(mockApiConnector.getInitialCandles()).thenReturn(candlesticks);
        when(mockApiConnector.getUpdateCandle()).thenReturn(mockCandle);
    }

    private void fillCandlestickList(){
        for (int i = 0; i < CANDLESTICK_LIST_SIZE; i++) {
            timeNow = timeNow.plusSeconds(30);
            Candlestick newCandlestick = mock(Candlestick.class);
            when(newCandlestick.getDateTime()).thenReturn(timeNow);
            candlesticks.add(newCandlestick);
            setMockCandleTime(timeNow = timeNow.plusSeconds(30));
        }

    }

    private void setMockCandleTime(ZonedDateTime time) {
        when(mockCandle.getDateTime()).thenReturn(time);
        when(mockCandle.isComplete()).thenReturn(true);
    }
}
