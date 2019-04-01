package trader.indicator.ma;

import org.junit.Before;
import org.junit.Test;
import trader.exception.WrongIndicatorSettingsException;
import trader.indicator.BaseBuilderTest;
import trader.indicator.Indicator;
import trader.indicator.ma.enums.MAType;
import static org.junit.Assert.*;

public class MovingAverageBuilderTest extends BaseBuilderTest {

    private static final MAType DEFAULT_MA_TYPE = MAType.SIMPLE;
    private static final String MA_TYPE = "maType";

    private MovingAverageBuilder builder;

    @Before
    public void setUp() {
        this.builder = new MovingAverageBuilder();
        setBuilder(builder);
    }

    @Test
    public void WhenCallSetMAType_ReturnCurrentObject(){
        settings.put(MA_TYPE, "Exponential");
        assertEquals(builder, builder.setMAType(settings));
    }

    @Test
    public void WhenCallSetMATypeWithEmptyString_SetToDefaultValue(){
        settings.clear();
        builder.setMAType(settings);

        assertEquals(DEFAULT_MA_TYPE, getActualMAType(builder));
    }

    @Test(expected = WrongIndicatorSettingsException.class)
    public void WhenCallSetMATypeWithBadValue_Exception(){
        settings.put(MA_TYPE, "Mor");
        builder.setMAType(settings);
    }

    @Test
    public void WhenCallSetMATypeWithCorrectValue_SuccessfulUpdate(){
        settings.put(MA_TYPE, "weighted");
        builder.setMAType(settings);

        assertEquals("weighted".toUpperCase(), getActualMAType(builder).toString());
    }

    @Test
    public void WhenCallSetMATypeWithCorrectMixedUpperAndLowerCaseLetters_SuccessfulUpdate(){
        settings.put(MA_TYPE, "ExpoNeNTiaL");
        builder.setMAType(settings);

        assertEquals("exponential".toUpperCase(), getActualMAType(builder).toString());
    }

    @Test(expected = WrongIndicatorSettingsException.class)
    public void WhenCallBuildWithMoreThanMaximumSettingCount_Exception(){
        settings.put(PERIOD, "13");
        settings.put(CANDLE_PRICE_TYPE, "median");
        settings.put("price", "12");
        settings.put("doh", null);
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
        settings.put(PERIOD, "9");
        settings.put(CANDLE_PRICE_TYPE, "median");
        settings.put(MA_TYPE, "weighted");
        Indicator indicator = builder.build(settings);

        assertEquals("WeightedMovingAverage", indicator.getClass().getSimpleName());
        assertEquals(0, getActualCandlestickList(indicator).size());
        assertEquals("median".toUpperCase(), getActualCandlePriceType(indicator).toString());
        assertEquals(9L, getActualPeriod(indicator));
    }

    protected MAType getActualMAType(Object object) {
        return (MAType) commonMembers.extractFieldObject(object, "maType");
    }

}