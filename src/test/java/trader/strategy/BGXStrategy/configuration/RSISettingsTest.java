package trader.strategy.BGXStrategy.configuration;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import trader.candlestick.candle.CandlePriceType;
import trader.indicator.Indicator;
import trader.indicator.rsi.RelativeStrengthIndex;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class RSISettingsTest extends BaseIndicatorConfigurationTest {

    private Indicator rsi;

    @Before
    public void setUp() {
        rsi = RSISettings.RSI_SETTINGS.build(connector);
    }

    @Test
    public void testRSIIndicatorTypesAfterBuilding() {
        int size = rsi.getValues().size();

        assertEquals(RelativeStrengthIndex.class, rsi.getClass());
        assertNotEquals(0, size);
    }

    @Test
    public void testUpdatingRSIIndicator(){
        int size = rsi.getValues().size();
        setUpdateCandle();
        rsi.updateIndicator();
        int newSize = rsi.getValues().size();

        assertEquals(size + 1, newSize);
    }

    @Test
    public void testMovingAveragesSettings(){
        long period = (long) commonMembers.extractFieldObject(rsi, "indicatorPeriod");
        CandlePriceType candlePriceType = (CandlePriceType) commonMembers.extractFieldObject(rsi, "candlePriceType");

        assertEquals(14L, period);
        assertEquals(CandlePriceType.CLOSE, candlePriceType);

    }
}
