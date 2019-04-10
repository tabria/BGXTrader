package trader.entity.indicator.ma;

import org.junit.Before;
import org.junit.Test;
import trader.exception.WrongIndicatorSettingsException;
import trader.entity.indicator.BaseBuilderTest;
import trader.entity.indicator.Indicator;
import trader.entity.indicator.ma.enums.MAType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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

    @Test(expected = WrongIndicatorSettingsException.class)
    public void WhenCallBuildWIthNull_Exception(){
        builder.build(null);
    }

    @Test(expected = WrongIndicatorSettingsException.class)
    public void WhenCallSetPositionWithNull_Exception(){
        builder.setPosition(null);
    }

    @Test(expected = WrongIndicatorSettingsException.class)
    public void WhenCallSetPositionWithBadKeyValueInSettings_Exception(){
        settings.put("xxxx", null);
        builder.setPosition(settings);
    }

    @Test(expected = WrongIndicatorSettingsException.class)
    public void WhenCallSetPositionWithNullValueInSettings_Exception(){
        settings.put(POSITION, null);
        builder.setPosition(settings);
    }

    @Test(expected = WrongIndicatorSettingsException.class)
    public void WhenCallSetPositionWithEmptyValueInSettings_Exception(){
        settings.put(POSITION, "    ");
        builder.setPosition(settings);
    }

    @Test
    public void WhenCallSetPositionWithCorrectValueInSettings_CorrectResult(){
        settings.put(POSITION, "slow");
        builder.setPosition(settings);

        assertEquals("slow", getActualPosition(builder));
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

    @Test
    public void WhenCallSetMATypeWithCorrectSettingWithExtraSpaces_TrimAndSuccessfulUpdate(){
        settings.put(MA_TYPE, "   simple   ");
        builder.setMAType(settings);

        assertEquals("simple".toUpperCase(), getActualMAType(builder).toString());
    }

    @Test
    public void testForAllDefaultSettings(){
        settings.put(POSITION, "slow");
        Indicator indicator = builder.build(settings);
        String indicatorName = indicator.getClass().getSimpleName();

        assertTrue(indicatorName.toUpperCase().contains(DEFAULT_MA_TYPE.toString()));
        assertEquals(DEFAULT_INDICATOR_PERIOD, getActualPeriod(indicator));
        assertEquals(DEFAULT_CANDLESTICK_PRICE_TYPE, getActualCandlePriceType(indicator));
        assertEquals(DEFAULT_GRANULARITY, getActualGranularity(indicator));
    }

    @Test
    public void testDefaultForPeriod(){
        settings.put(CANDLE_GRANULARITY, "M5");
        settings.put(CANDLE_PRICE_TYPE, "median");
        settings.put(POSITION, "fast");
        Indicator indicator = builder.build(settings);

        assertEquals(DEFAULT_INDICATOR_PERIOD, getActualPeriod(indicator));
    }

    @Test
    public void testDefaultForCandlePriceType(){
        settings.put(PERIOD, "16");
        settings.put(CANDLE_GRANULARITY, "M5");
        settings.put(POSITION, "slow");
        Indicator indicator = builder.build(settings);

        assertEquals(DEFAULT_CANDLESTICK_PRICE_TYPE, getActualCandlePriceType(indicator));
    }

    @Test
    public void testDefaultValueForGranularity(){
        settings.put(PERIOD, "13");
        settings.put(CANDLE_PRICE_TYPE, "median");
        settings.put(POSITION, "fast");
        Indicator indicator = builder.build(settings);

        assertEquals(DEFAULT_GRANULARITY, getActualGranularity(indicator));
    }


    @Test
    public void WhenCallBuildWithCustomSettings_SuccessfulBuildWithCustomSettings(){
        settings.put(PERIOD, "9");
        settings.put(CANDLE_PRICE_TYPE, "median");
        settings.put(CANDLE_GRANULARITY, "h4");
        settings.put(MA_TYPE, "weighted");
        settings.put(POSITION, "slow");
        Indicator indicator = builder.build(settings);

        assertEquals("WeightedMovingAverage", indicator.getClass().getSimpleName());
        assertEquals(0, getActualCandlestickList(indicator).size());
        assertEquals("median".toUpperCase(), getActualCandlePriceType(indicator).toString());
        assertEquals(9L, getActualPeriod(indicator));
        assertEquals("h4".toUpperCase(), getActualGranularity(indicator).toString());
        assertEquals("slow", getActualPosition(indicator));
    }

    protected String getActualPosition(Object object) {
        return (String) commonMembers.extractFieldObject(object, "position");
    }

    protected MAType getActualMAType(Object object) {
        return (MAType) commonMembers.extractFieldObject(object, "maType");
    }

}