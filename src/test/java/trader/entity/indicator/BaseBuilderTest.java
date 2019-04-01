package trader.entity.indicator;

import org.junit.Test;
import trader.CommonTestClassMembers;
import trader.entity.candlestick.Candlestick;
import trader.entity.candlestick.candle.CandlePriceType;
import trader.exception.OutOfBoundaryException;
import trader.exception.WrongIndicatorSettingsException;

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public abstract class BaseBuilderTest {
    protected static final long DEFAULT_INDICATOR_PERIOD = 14L;
    private static final long MIN_INDICATOR_PERIOD = 1L;
    private static final long MAX_INDICATOR_PERIOD = 4000L;
    protected static final CandlePriceType DEFAULT_CANDLESTICK_PRICE_TYPE = CandlePriceType.CLOSE;
    protected static final String PERIOD = "period";
    protected static final String CANDLE_PRICE_TYPE = "candlePriceType";

    protected CommonTestClassMembers commonMembers;
    protected HashMap<String, String> settings;
    protected BaseIndicatorBuilder builder;


    public BaseBuilderTest() {
        this.commonMembers = new CommonTestClassMembers();
        this.settings = new HashMap<>();
    }

    protected void setBuilder(BaseIndicatorBuilder builder) {
        this.builder = builder;
    }

    @Test
    public void candlestickListMustNotBeNull(){
        assertNotEquals(builder.candlestickList, null);
    }

    @Test
    public void WhenCallSetPeriod_ReturnCurrentObject(){
        settings.clear();
        assertEquals(builder, builder.setPeriod(settings));
    }

    @Test
    public void WhenCallSetPeriodWithNull_SetDefaultPeriod(){
        settings.put(PERIOD, null);
        builder.setPeriod(settings);

        assertEquals(DEFAULT_INDICATOR_PERIOD, getActualPeriod(builder));
    }

    @Test
    public void WhenCallSetPeriodWithEmptyString_SetToDefaultPeriod(){
        settings.remove(PERIOD);
        builder.setPeriod(settings);
        assertEquals(DEFAULT_INDICATOR_PERIOD, getActualPeriod(builder));
    }

    @Test(expected = WrongIndicatorSettingsException.class)
    public void WhenCallSetPeriodWithNotParsableStringToLong_Exception(){
        settings.put(PERIOD, "c");
        builder.setPeriod(settings);
    }

    @Test(expected = OutOfBoundaryException.class)
    public void WhenCallSetPeriodWithValueLessThanMINPeriod_Exception() {
        settings.put(PERIOD, String.valueOf(MIN_INDICATOR_PERIOD - 1));
        builder.setPeriod(settings);
    }

    @Test(expected = OutOfBoundaryException.class)
    public void WhenCallSetPeriodWithValueGreaterThanMAXPeriod_Exception() {
        settings.put(PERIOD, String.valueOf(MAX_INDICATOR_PERIOD+1));
        builder.setPeriod(settings);
    }

    @Test
    public void callSetPeriodWithCorrectValue_SuccessfulUpdate() {
        settings.put(PERIOD, "11");
        builder.setPeriod(settings);

        assertEquals(11L, getActualPeriod(builder));
    }

    @Test
    public void WhenCallPeriodWithCorrectValueWithExtraSpaces_TrimSpacesAndSetCorrectValue(){
        settings.put(PERIOD, "  11  ");
        builder.setPeriod(settings);

        assertEquals(11L, getActualPeriod(builder));
    }

    @Test
    public void WhenCallSetCandlestickPriceType_ReturnCurrentObject(){
        settings.put(CANDLE_PRICE_TYPE, "Close");
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
        settings.put(CANDLE_PRICE_TYPE, "Mor");
        builder.setCandlePriceType(settings);
    }

    @Test
    public void WhenCallSetCandlestickPriceTypeWithCorrectValue_SuccessfulUpdate(){
        settings.put(CANDLE_PRICE_TYPE, "open");
        builder.setCandlePriceType(settings);

        assertEquals("open".toUpperCase(), getActualCandlePriceType(builder).toString());
    }

    @Test
    public void WhenCallSetCandlestickPriceTypeWithCorrectMixedUpperAndLowerCaseLetters_SuccessfulUpdate(){
        settings.put(CANDLE_PRICE_TYPE, "HiGh");
        builder.setCandlePriceType(settings);

        assertEquals("HiGh".toUpperCase(), getActualCandlePriceType(builder).toString());
    }

    @Test
    public void WhenCallSetCandlestickPriceTypeWithCorrectValueWithExtraSpaces_TrimAndSuccessfulUpdate(){
        settings.put(CANDLE_PRICE_TYPE, "  HiGh   ");
        builder.setCandlePriceType(settings);

        assertEquals("HiGh".toUpperCase(), getActualCandlePriceType(builder).toString());
    }

    @SuppressWarnings("unchecked")
    protected List<Candlestick> getActualCandlestickList(Object object) {
        return (List<Candlestick>) commonMembers.extractFieldObject(object, "candlestickList");
    }

    protected long getActualPeriod(Object object) {
        return (long) commonMembers.extractFieldObject(object, "indicatorPeriod");
    }

    protected CandlePriceType getActualCandlePriceType(Object object) {
        return (CandlePriceType) commonMembers.extractFieldObject(object, "candlePriceType");
    }
}
