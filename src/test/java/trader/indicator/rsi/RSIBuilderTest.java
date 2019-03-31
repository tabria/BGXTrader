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
    public void testCreateNewBuilderWithDefaultPeriod() {
        assertEquals(DEFAULT_INDICATOR_PERIOD, getActualPeriod(builder));
    }

    @Test
    public void testCreateNewBuilderWithDefaultCandlestickPriceType() {
        assertEquals(DEFAULT_CANDLESTICK_PRICE_TYPE, getActualCandlePriceType(builder));
    }

    @Test
    public void WhenCallSetPeriod_ReturnCurrentObject(){
        assertEquals(builder, builder.setPeriod(""));
    }

    @Test
    public void WhenCallSetPeriodWithEmptyString_SetToDefaultPeriod(){
        builder.setPeriod("");
        assertEquals(DEFAULT_INDICATOR_PERIOD, getActualPeriod(builder));
    }

    @Test(expected = WrongIndicatorSettingsException.class)
    public void WhenCallSetPeriodWithNotParsableStringToLong_Exception(){
        builder.setPeriod("c");
    }

    @Test(expected = OutOfBoundaryException.class)
    public void WhenCallSetPeriodWithValueLessThanMINPeriod_Exception() {
        builder.setPeriod("0");
    }

    @Test(expected = OutOfBoundaryException.class)
    public void WhenCallSetPeriodWithValueGreaterThanMAXPeriod_Exception() {
        builder.setPeriod("1001");
    }

    @Test
    public void callSetPeriodWithCorrectValue_SuccessfulUpdate() {
        builder.setPeriod("11");

        assertEquals(11L, getActualPeriod(builder));
    }

    @Test
    public void WhenCallSetCandlestickPriceType_ReturnCurrentObject(){
        assertEquals(builder, builder.setCandlePriceType("Close"));
    }

    @Test
    public void WhenCallSetCandlestickPriceTypeWithEmptyString_SetToDefaultValue(){
        builder.setCandlePriceType("");

        assertEquals(DEFAULT_CANDLESTICK_PRICE_TYPE, getActualCandlePriceType(builder));
    }

    @Test(expected = WrongIndicatorSettingsException.class)
    public void WhenCallSetCandlestickPriceTypeWithBadValue_Exception(){
        builder.setCandlePriceType("Mor");
    }

    @Test
    public void WhenCallSetCandlestickPriceTypeWithCorrectValue_SuccessfulUpdate(){
        builder.setCandlePriceType("open");

        assertEquals("open".toUpperCase(), getActualCandlePriceType(builder).toString());
    }

    @Test
    public void WhenCallSetCandlestickPriceTypeWithCorrectMixedUpperAndLowerCaseLetters_SuccessfulUpdate(){
        builder.setCandlePriceType("HiGh");

        assertEquals("HiGh".toUpperCase(), getActualCandlePriceType(builder).toString());
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