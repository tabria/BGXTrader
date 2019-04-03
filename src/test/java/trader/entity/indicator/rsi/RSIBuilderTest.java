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

    @Test
    public void WhenCallBuildWithZeroSettingsCount_DefaultSettings(){
        Indicator indicator = builder.build(settings);

        assertEquals(DEFAULT_INDICATOR_PERIOD, getActualPeriod(indicator));
        assertEquals(DEFAULT_CANDLESTICK_PRICE_TYPE, getActualCandlePriceType(indicator));
        assertEquals(DEFAULT_GRANULARITY, getActualGranularity(indicator));
    }

    @Test
    public void WhenCallBuildOnlyWithGranularityAndCandlePriceType_DefaultForPeriod(){
        settings.put(CANDLE_GRANULARITY, "M5");
        settings.put(CANDLE_PRICE_TYPE, "median");
        Indicator indicator = builder.build(settings);

        assertEquals(DEFAULT_INDICATOR_PERIOD, getActualPeriod(indicator));
    }

    @Test
    public void WhenCallBuildOnlyWithPeriodAndGranularity_DefaultForCandlePriceType(){
        settings.put(PERIOD, "16");
        settings.put(CANDLE_GRANULARITY, "M5");
        Indicator indicator = builder.build(settings);

        assertEquals(DEFAULT_CANDLESTICK_PRICE_TYPE, getActualCandlePriceType(indicator));
    }

    @Test
    public void WhenCallBuildOnlyWithPeriodAndCandlePriceType_DefaultForGranularity(){
        settings.put(PERIOD, "13");
        settings.put(CANDLE_PRICE_TYPE, "median");
        Indicator indicator = builder.build(settings);

        assertEquals(DEFAULT_GRANULARITY, getActualGranularity(indicator));
    }


    @Test
    public void WhenCallBuildWithCustomSettings_SuccessfulBuildWithCustomSettings(){
        settings.put(PERIOD, "7");
        settings.put(CANDLE_PRICE_TYPE, "median");
        settings.put(CANDLE_GRANULARITY, "W");
        Indicator rsi = builder.build(settings);

        assertEquals("RelativeStrengthIndex", rsi.getClass().getSimpleName());
        assertEquals(0, getActualCandlestickList(rsi).size());
        assertEquals("median".toUpperCase(), getActualCandlePriceType(rsi).toString());
        assertEquals(7L, getActualPeriod(rsi));
        assertEquals("W".toUpperCase(), getActualGranularity(rsi).toString());
    }

}