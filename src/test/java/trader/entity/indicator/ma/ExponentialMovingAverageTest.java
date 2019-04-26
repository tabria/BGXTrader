package trader.entity.indicator.ma;

import org.junit.Before;
import org.junit.Test;
import trader.CommonTestClassMembers;
import trader.entity.candlestick.Candlestick;
import trader.exception.BadRequestException;
import trader.exception.IndicatorPeriodTooBigException;
import trader.entity.indicator.BaseIndicatorTest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;

public class ExponentialMovingAverageTest extends BaseIndicatorTest {

    private static final BigDecimal LAST_EMA_VALUE = BigDecimal.valueOf(1.16230).setScale(5, BigDecimal.ROUND_HALF_UP);

    private ExponentialMovingAverage ema;
    private CommonTestClassMembers commonMembers;

    @Before
    public void before() {
        super.before();
        commonMembers = new CommonTestClassMembers();
        this.ema = new ExponentialMovingAverage(this.period,
                this.candlePriceType, this.granularity, this.position);
    }

    @Override
    public void WhenCallGetGranularity_CorrectResult() {
        assertEquals(granularity, ema.getGranularity());
    }

    @Test
    public void WhenCreatedThenGetMAValuesReturnEmptyList() {
        assertEquals(0, ema.getValues().size());
    }

    @Override
    @Test(expected = UnsupportedOperationException.class )
    public void getValuesReturnImmutableResult(){
        List<BigDecimal> values = this.ema.getValues();
        values.add(null);
    }

    @Override
    @Test
    public void TestToString(){
        String result = this.ema.toString();
        String expected = String.format("ExponentialMovingAverage{period=%d, " +
                        "candlePriceType=%s, granularity=%s, indicatorValues=%s}",
                period, this.candlePriceType.toString(), granularity.toString(), ema.getValues().toString());
        assertEquals(expected, result);
    }

    @Test
    public void WhenCallUpdateIndicatorCandlesticksListIsUpdated(){
        updateCandlestickList(ema);
    }

    private int getIndicatorCandlestickListSize() {
        List<Candlestick> candlestickList = (List<Candlestick>) commonMembers.extractFieldObject(ema, "candlestickList");
        return candlestickList.size();
    }

    @Test
    public void WhenCallUpdateIndicatorAndValuesCountIsZero_CorrectUpdate() {
        ema.updateIndicator(indicatorUpdateHelper.getFakeCandlestickListFullOfMock());
        List<BigDecimal> values = ema.getValues();

        assertEquals(LAST_EMA_VALUE, values.get(values.size()-1));
    }

    @Test
    public void testSMACalculationForCorrectness() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        commonMembers.changeFieldObject(ema, "indicatorValues", new ArrayList<Candlestick>());
        Method setSMAValue = commonMembers
                .getPrivateMethodForTest(ema, "setSMAValue", List.class);
        setSMAValue.invoke(ema, indicatorUpdateHelper.getFakeCandlestickListFullOfMock());

        assertEquals(1, ema.getValues().size());
        assertEquals(BigDecimal.valueOf(1.16414).setScale(5,
                BigDecimal.ROUND_HALF_UP), ema.getValues().get(0));
    }

    @Test
    public void WhenCallUpdateIndicatorAndValuesCountIsNotZero_SuccessfulUpdateWithSingleCandle() {
        this.ema.updateIndicator(indicatorUpdateHelper.getFakeCandlestickListFullOfMock());
        int oldSize = this.ema.getValues().size();
        BigDecimal oldLastValue = this.ema.getValues().get(oldSize-1);
        this.ema.updateIndicator(getListWithSingleNewCandle(UPDATE_NEW_DATETIME_ENTRY));
        int newSize = this.ema.getValues().size();
        BigDecimal newNextToLastValue = this.ema.getValues().get(newSize-2);

        assertEquals(oldSize + 1, newSize);
        assertEquals(oldLastValue, newNextToLastValue);
    }

    @Test(expected = IndicatorPeriodTooBigException.class)
    public void testPeriodBiggerThanCandlesCount(){
        this.period = 200;
        ExponentialMovingAverage exponentialMovingAverage = new ExponentialMovingAverage(this.period,
                this.candlePriceType, this.granularity, this.position);
        exponentialMovingAverage.updateIndicator(indicatorUpdateHelper.getFakeCandlestickListFullOfMock());
    }

    @Test(expected = BadRequestException.class)
    public void testCreatingEMAWithZeroCandles(){
        ema.updateIndicator(new ArrayList<>());
    }


    @Test
    public void WhenCallUpdateIndicatorAndNewPriceTimeIsLessThanNextUpdateTime_ThenNoUpdate(){
        this.ema.updateIndicator(indicatorUpdateHelper.getFakeCandlestickListFullOfMock());

        int oldSize = ema.getValues().size();
        this.ema.updateIndicator(getListWithSingleNewCandle(NO_UPDATE_NEW_DATETIME_ENTRY));
        int newSize = ema.getValues().size();

        assertEquals(oldSize, newSize);
    }

    @Test
    public void WhenCallUpdateIndicatorAndNewPriceTimeIsAboveTheNextUpdateTime_ThenUpdate(){
        this.ema.updateIndicator(indicatorUpdateHelper.getFakeCandlestickListFullOfMock());

        int oldSize = ema.getValues().size();
        this.ema.updateIndicator(getListWithSingleNewCandle(UPDATE_NEW_DATETIME_ENTRY));
        int newSize = ema.getValues().size();

        assertEquals(oldSize + 1, newSize);
    }

    @Override
    protected BigDecimal getLastCandlestickPrice() {
        List<BigDecimal> maValues = this.ema.getValues();
        return maValues.get(maValues.size() - 1);
    }


}