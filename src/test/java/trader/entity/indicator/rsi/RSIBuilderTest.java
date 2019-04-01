package trader.entity.indicator.rsi;

import org.junit.Before;
import org.junit.Test;
import trader.exception.WrongIndicatorSettingsException;
import trader.entity.indicator.BaseBuilderTest;
import trader.entity.indicator.Indicator;
import static org.junit.Assert.*;

public class RSIBuilderTest extends BaseBuilderTest {

    private RSIBuilder builder;

    @Before
    public void before(){
        builder = new RSIBuilder();
        setBuilder(builder);
    }

    @Test(expected = WrongIndicatorSettingsException.class)
    public void WhenCallBuildWithMoreThanMaximumSettingCount_Exception(){
        settings.put(PERIOD, "13");
        settings.put(CANDLE_PRICE_TYPE, "median");
        settings.put("price", "12");
        builder.build(settings);
    }

    @Test
    public void WhenCallBuildWithMoreThanZeroAndLessThanMaxSettingsCount_DefaultForNonPresentSettings(){
        settings.remove(PERIOD);
        Indicator indicator = builder.build(settings);

        assertEquals(DEFAULT_INDICATOR_PERIOD, getActualPeriod(indicator));
    }

    @Test
    public void WhenCallBuildWithCustomSettings_SuccessfulBuildWithCustomSettings(){
        settings.put(PERIOD, "13");
        settings.put(CANDLE_PRICE_TYPE, "median");
        Indicator rsi = builder.build(settings);

        assertEquals("RelativeStrengthIndex", rsi.getClass().getSimpleName());
        assertEquals(0, getActualCandlestickList(rsi).size());
        assertEquals("median".toUpperCase(), getActualCandlePriceType(rsi).toString());
        assertEquals(13L, getActualPeriod(rsi));
    }

}