package trader.indicator.rsi;

import org.junit.Before;
import org.junit.Test;
import trader.CommonTestClassMembers;
import trader.candlestick.Candlestick;
import trader.candlestick.candle.CandlePriceType;
import trader.exception.OutOfBoundaryException;
import trader.exception.WrongIndicatorSettingsException;
import trader.indicator.Indicator;
import java.util.HashMap;
import java.util.List;
import static org.junit.Assert.*;

public class RSIBuilderTest {

    private static final long DEFAULT_INDICATOR_PERIOD = 14L;
    private static final CandlePriceType DEFAULT_CANDLESTICK_PRICE_TYPE = CandlePriceType.CLOSE;

    private RSIBuilder builder;
    private CommonTestClassMembers commonMembers;
    private HashMap<String, String> settings;

    @Before
    public void before(){
        commonMembers = new CommonTestClassMembers();
        settings = new HashMap<>();
        builder = new RSIBuilder();

    }

    @Test
    public void candlestickListMustNotBeNull(){
        assertNotEquals(getActualCandlestickList(builder), null);
    }

    @Test
    public void WhenCallSetPeriod_ReturnCurrentObject(){
        settings.clear();
        assertEquals(builder, builder.setPeriod(settings));
    }

    @Test
    public void WhenCallSetPeriodWithEmptyString_SetToDefaultPeriod(){
        settings.remove("period");
        builder.setPeriod(settings);
        assertEquals(DEFAULT_INDICATOR_PERIOD, getActualPeriod(builder));
    }

    @Test(expected = WrongIndicatorSettingsException.class)
    public void WhenCallSetPeriodWithNotParsableStringToLong_Exception(){
        settings.put("period", "c");
        builder.setPeriod(settings);
    }

    @Test(expected = OutOfBoundaryException.class)
    public void WhenCallSetPeriodWithValueLessThanMINPeriod_Exception() {
        settings.put("period", "0");
        builder.setPeriod(settings);
    }

    @Test(expected = OutOfBoundaryException.class)
    public void WhenCallSetPeriodWithValueGreaterThanMAXPeriod_Exception() {
        settings.put("period", "1001");
        builder.setPeriod(settings);
    }

    @Test
    public void callSetPeriodWithCorrectValue_SuccessfulUpdate() {
        settings.put("period", "11");
        builder.setPeriod(settings);

        assertEquals(11L, getActualPeriod(builder));
    }

    @Test
    public void WhenCallSetCandlestickPriceType_ReturnCurrentObject(){
        settings.put("candlePriceType", "Close");
        assertEquals(builder, builder.setCandlePriceType(settings));
    }

    @Test
    public void WhenCallSetCandlestickPriceTypeWithEmptyString_SetToDefaultValue(){
        settings.clear();
        builder.setCandlePriceType(settings);

        assertEquals(DEFAULT_CANDLESTICK_PRICE_TYPE, getActualCandlePriceType(builder));
    }

    @Test(expected = WrongIndicatorSettingsException.class)
    public void WhenCallSetCandlestickPriceTypeWithBadValue_Exception(){
        settings.put("candlePriceType", "Mor");
        builder.setCandlePriceType(settings);
    }

    @Test
    public void WhenCallSetCandlestickPriceTypeWithCorrectValue_SuccessfulUpdate(){
        settings.put("candlePriceType", "open");
        builder.setCandlePriceType(settings);

        assertEquals("open".toUpperCase(), getActualCandlePriceType(builder).toString());
    }

    @Test
    public void WhenCallSetCandlestickPriceTypeWithCorrectMixedUpperAndLowerCaseLetters_SuccessfulUpdate(){
        settings.put("candlePriceType", "HiGh");
        builder.setCandlePriceType(settings);

        assertEquals("HiGh".toUpperCase(), getActualCandlePriceType(builder).toString());
    }

    @Test(expected = WrongIndicatorSettingsException.class)
    public void WhenCallBuildWithMoreThanMaximumSettingCount_Exception(){
        settings.put("period", "13");
        settings.put("candlePriceType", "median");
        settings.put("price", "12");
        builder.build(settings);
    }

    @Test
    public void WhenCallBuildWithMoreThanZeroAndLessThanMaxSettingsCount_DefaultForNonPresentSettings(){
        settings.remove("period");
        Indicator rsi = builder.build(settings);

        assertEquals(DEFAULT_INDICATOR_PERIOD, getActualPeriod(rsi));

    }

    @Test
    public void WhenCallBuildWithCustomSettings_SuccessfulBuildWithCustomSettings(){
        settings.put("period", "13");
        settings.put("candlePriceType", "median");
        Indicator rsi = builder.build(settings);

        assertEquals("RelativeStrengthIndex", rsi.getClass().getSimpleName());
        assertEquals(0, getActualCandlestickList(rsi).size());
        assertEquals("median".toUpperCase(), getActualCandlePriceType(rsi).toString());
        assertEquals(13L, getActualPeriod(rsi));
    }

    @SuppressWarnings("unchecked")
    private List<Candlestick> getActualCandlestickList(Object object) {
        return (List<Candlestick>) commonMembers.extractFieldObject(object, "candlestickList");
    }

    private long getActualPeriod(Object object) {
        return (long) commonMembers.extractFieldObject(object, "indicatorPeriod");
    }

    private CandlePriceType getActualCandlePriceType(Object object) {
        return (CandlePriceType) commonMembers.extractFieldObject(object, "candlePriceType");
    }
}