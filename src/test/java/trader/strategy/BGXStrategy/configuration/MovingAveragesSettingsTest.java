package trader.strategy.BGXStrategy.configuration;

import org.junit.Test;
import trader.candlestick.candle.CandlePriceType;
import trader.indicator.Indicator;
import trader.indicator.ma.SimpleMovingAverage;
import trader.indicator.ma.WeightedMovingAverage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class MovingAveragesSettingsTest extends BaseIndicatorConfigurationTest  {

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
