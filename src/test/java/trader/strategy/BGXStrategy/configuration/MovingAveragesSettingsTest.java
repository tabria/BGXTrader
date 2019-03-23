package trader.strategy.BGXStrategy.configuration;

import org.junit.Before;
import org.junit.Test;
import trader.CommonTestClassMembers;
import trader.candlestick.Candlestick;
import trader.candlestick.candle.CandlePriceType;
import trader.connector.CandlesUpdaterConnector;
import trader.indicator.Indicator;
import trader.indicator.ma.SimpleMovingAverage;
import trader.indicator.ma.WeightedMovingAverage;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MovingAveragesSettingsTest {

    private static final long DEFAULT_CANDLE_TIME_FRAME_IN_SECONDS = 1_800L;
    private static final long DEFAULT_VOLUME = 1L;
    private static final BigDecimal DEFAULT_PRICE = new BigDecimal(0.00001).setScale(5, BigDecimal.ROUND_HALF_UP);
    private static final ZonedDateTime DEFAULT_ZONED_DATE_TIME = ZonedDateTime.parse("2012-06-30T12:30:40Z[UTC]");

    private CandlesUpdaterConnector connector;
    private List<Candlestick> candlesticks;
    private Candlestick candle;
    private CommonTestClassMembers commonMembers;

    @Before
    public void setUp() throws Exception {
        connector = mock(CandlesUpdaterConnector.class);
        commonMembers = new CommonTestClassMembers();
        candlesticks = new ArrayList<>();
        candle = mock(Candlestick.class);
        setCandle(DEFAULT_ZONED_DATE_TIME);
        setCandlesticks();
        when(connector.getInitialCandles()).thenReturn(candlesticks);
//        when(connector.getUpdatedCandle()).thenReturn(candle);
    }

    @Test
    public void testMovingAveragesTypesAfterBuilding() {
        assertMovingAverageType(MovingAveragesSettings.PRICE_SMA_SETTINGS, SimpleMovingAverage.class);
        assertMovingAverageType(MovingAveragesSettings.DAILY_SMA_SETTINGS, SimpleMovingAverage.class);
        assertMovingAverageType(MovingAveragesSettings.FAST_WMA_SETTINGS, WeightedMovingAverage.class);
        assertMovingAverageType(MovingAveragesSettings.MIDDLE_WMA_SETTINGS, WeightedMovingAverage.class);
        assertMovingAverageType(MovingAveragesSettings.SLOW_WMA_SETTINGS, WeightedMovingAverage.class);
    }

    @Test
    public void testUpdatingMovingAverages(){
        assertSizeAfterUpdate(MovingAveragesSettings.PRICE_SMA_SETTINGS);
        assertSizeAfterUpdate(MovingAveragesSettings.DAILY_SMA_SETTINGS);
        assertSizeAfterUpdate(MovingAveragesSettings.FAST_WMA_SETTINGS);
        assertSizeAfterUpdate(MovingAveragesSettings.MIDDLE_WMA_SETTINGS);
        assertSizeAfterUpdate(MovingAveragesSettings.SLOW_WMA_SETTINGS);
    }

    @Test
    public void testMovingAveragesSettings(){
        assertSettingsAfterBuild(MovingAveragesSettings.PRICE_SMA_SETTINGS, 1L, CandlePriceType.CLOSE);
        assertSettingsAfterBuild(MovingAveragesSettings.DAILY_SMA_SETTINGS, 1L, CandlePriceType.OPEN);
        assertSettingsAfterBuild(MovingAveragesSettings.FAST_WMA_SETTINGS, 5L, CandlePriceType.CLOSE);
        assertSettingsAfterBuild(MovingAveragesSettings.MIDDLE_WMA_SETTINGS, 20L, CandlePriceType.CLOSE);
        assertSettingsAfterBuild(MovingAveragesSettings.SLOW_WMA_SETTINGS, 100L, CandlePriceType.CLOSE);

    }

    private void setUpdateCandle() {
        Candlestick newCandle = mock(Candlestick.class);
        setCandle(ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC")));
        when(connector.updateCandle()).thenReturn(newCandle);
    }

    private void setCandle(ZonedDateTime dateTime) {
        when(candle.getClosePrice()).thenReturn(DEFAULT_PRICE);
        when(candle.getHighPrice()).thenReturn(DEFAULT_PRICE);
        when(candle.getLowPrice()).thenReturn(DEFAULT_PRICE);
        when(candle.getOpenPrice()).thenReturn(DEFAULT_PRICE);
        when(candle.getDateTime()).thenReturn(dateTime);
        when(candle.isComplete()).thenReturn(true);
        when(candle.getVolume()).thenReturn(DEFAULT_VOLUME);
        when(candle.getTimeFrame()).thenReturn(DEFAULT_CANDLE_TIME_FRAME_IN_SECONDS);
    }

    private void setCandlesticks() {
        for (int i = 0; i <200 ; i++) {
            candlesticks.add(candle);
        }
    }

    private void assertMovingAverageType(MovingAveragesSettings maSettings, Class<?> expectedClass) {
        Indicator ma = maSettings.build(connector);

        int size = ma.getValues().size();

        assertEquals(expectedClass, ma.getClass());
        assertNotEquals(0, size);
    }

    private void assertSizeAfterUpdate(MovingAveragesSettings dailySmaSettings) {
        Indicator priceSMA = dailySmaSettings.build(connector);
        int size = priceSMA.getValues().size();
        setUpdateCandle();
        priceSMA.updateIndicator();
        int newSize = priceSMA.getValues().size();

        assertEquals(size + 1, newSize);
    }

    private void assertSettingsAfterBuild(MovingAveragesSettings maSettings, long expectedPeriod, CandlePriceType expectedCandlePriceType) {
        Indicator ma = maSettings.build(connector);
        long period = (long) commonMembers.extractFieldObject(ma, "indicatorPeriod");
        CandlePriceType candlePriceType = (CandlePriceType) commonMembers.extractFieldObject(ma, "candlePriceType");

        assertEquals(expectedPeriod, period);
        assertEquals(expectedCandlePriceType, candlePriceType);
    }
}
