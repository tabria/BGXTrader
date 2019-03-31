package trader.candlestick;

import org.junit.Before;
import org.junit.Test;
import trader.CommonTestClassMembers;
import trader.indicator.CandlesUpdatable;
import trader.indicator.updater.CandlesUpdater;
import trader.indicator.CandlesUpdaterConnector;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CandlesUpdaterTest {

    private static final int CANDLESTICK_LIST_SIZE = 5;

    private CandlesUpdatable candlesUpdater;
    private CandlesUpdaterConnector mockConnector;
    private List<Candlestick> candlesticks;
    private Candlestick mockCandle;
    private CommonTestClassMembers commonMembers;
    private ZonedDateTime timeNow;

    @Before
    public void setUp() throws Exception {

        mockCandle = mock(Candlestick.class);
        candlesticks = new ArrayList<>();
        commonMembers = new CommonTestClassMembers();
        mockConnector = mock(CandlesUpdaterConnector.class);
        setMockApiConnector();
        candlesUpdater = new CandlesUpdater(mockConnector);
        timeNow = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC"));
        fillCandlestickList();
    }


    @SuppressWarnings("unchecked")
    @Test
    public void testInitializeToReturnCorrectQuantity() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method initialize = commonMembers
                .getPrivateMethodForTest(CandlesUpdater.class, "initialize");
        List<Candlestick> resultList = (List<Candlestick>) initialize.invoke(candlesUpdater);

        assertSame(candlesticks, resultList);
        assertEquals(candlesticks, resultList);
    }

    @Test
    public void tesGetCandlesToReturnCorrectObject(){
        changeCandlestickListFieldValue();

        assertEquals(candlesticks, candlesUpdater.getCandles());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void tesGetCandlesToReturnUnmodifiable(){
        changeCandlestickListFieldValue();
        List<Candlestick> candleList = candlesUpdater.getCandles();
        candleList.add(mockCandle);
    }

    @Test
    public void testGetUpdateCandleToReturnCorrectCandle(){
        Candlestick updateCandle = candlesUpdater.getUpdatedCandle();

        assertEquals(mockCandle, updateCandle);
    }

    @Test
    public void whenCallGetUpdateCandleWithNonUpdatableCandle_UpdateFalse(){
        when(mockCandle.isComplete()).thenReturn(false);
        int expected = candlesUpdater.getCandles().size();
        candlesUpdater.getUpdatedCandle();
        int actual = candlesUpdater.getCandles().size();
        assertEquals(expected, actual);
    }

    @Test
    public void whenCallGetUpdateCandleWithUpdatableCandleWithBiggerTime_UpdateTrue(){
        int expected = candlesUpdater.getCandles().size();
        candlesUpdater.getUpdatedCandle();
        int actual = candlesUpdater.getCandles().size();
        assertEquals(expected+1, actual);
   //     assertTrue(result);
    }

    @Test
    public void whenCallGetLastCandlestick_CorrectCandlestick() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method getLastCandlestick = commonMembers
                .getPrivateMethodForTest(candlesUpdater, "getLastCandlestick");
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
        commonMembers
                .changeFieldObject(candlesUpdater,"sleepTimeMilliseconds", -1);
        Method isDateTimeTradeable = commonMembers
                .getPrivateMethodForTest(candlesUpdater, "isDateTimeTradeable", Candlestick.class, Candlestick.class);
        Candlestick lastCandle = candlesticks.get(candlesticks.size()-1);
        isDateTimeTradeable.invoke(candlesUpdater, mockCandle, lastCandle);
    }

    private void changeCandlestickListFieldValue() {
        commonMembers.changeFieldObject(candlesUpdater, "candlestickList", candlesticks);
    }

    private void setMockApiConnector() {
        when(mockConnector.getInitialCandles()).thenReturn(candlesticks);
        when(mockConnector.updateCandle()).thenReturn(mockCandle);
    }

    private void fillCandlestickList(){
        for (int i = 0; i < CANDLESTICK_LIST_SIZE; i++) {
            timeNow = timeNow.plusSeconds(30);
            candlesticks.add(createCandlestickMockWithDateTime());
            setMockCandleTime(timeNow = timeNow.plusSeconds(30));
        }
    }

    private Candlestick createCandlestickMockWithDateTime() {
        Candlestick newCandlestick = mock(Candlestick.class);
        when(newCandlestick.getDateTime()).thenReturn(timeNow);
        return newCandlestick;
    }

    private void setMockCandleTime(ZonedDateTime time) {
        when(mockCandle.getDateTime()).thenReturn(time);
        when(mockCandle.isComplete()).thenReturn(true);
    }
}
