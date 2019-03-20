package trader.indicators.ma;

import org.junit.Before;
import org.junit.Test;
import trader.CommonTestClassMembers;
import trader.candle.Candlestick;
import trader.indicators.BaseIndicatorTest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;
import static trader.strategies.BGXStrategy.StrategyConfig.SCALE;

public class ExponentialMovingAverageTest extends BaseIndicatorTest {

    private static final BigDecimal LAST_EMA_VALUE = BigDecimal.valueOf(1.16230).setScale(SCALE, BigDecimal.ROUND_HALF_UP);

    private ExponentialMovingAverage ema;
    private CommonTestClassMembers commonMembers;

    @Before
    public void before() {
        super.before();
        commonMembers = new CommonTestClassMembers();
        this.ema = new ExponentialMovingAverage(this.period,
                this.mockCandlestickPriceType, this.candlesUpdater);
    }

    @Test
    public void testInitializingEMAValues(){
        List<BigDecimal> values = this.ema.getValues();

        assertTrue(values.size()> 0);
    }

    @Override
    @Test(expected = UnsupportedOperationException.class )
    public void getValuesReturnImmutableResult(){
        List<BigDecimal> values = this.ema.getValues();
        values.add(null);
    }

    @Override
    @Test
    public void getMAValuesReturnCorrectResult() {
        List<BigDecimal> values = this.ema.getValues();
        assertEquals(LAST_EMA_VALUE, values.get(values.size()-1));
    }

    @Test
    public void testSMACalculation() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        commonMembers.changeFieldObject(ema, "indicatorValues", new ArrayList<Candlestick>());
        Method setSMAValue = commonMembers
                .getPrivateMethodForTest(ema, "setSMAValue", List.class);
        setSMAValue.invoke(ema, candlesUpdater.getCandles());

        assertEquals(1, ema.getValues().size());
        assertEquals(BigDecimal.valueOf(1.16414).setScale(SCALE,
                BigDecimal.ROUND_HALF_UP), ema.getValues().get(0));
    }

    @Override
    @Test
    public void testSuccessfulUpdate() {

        int oldSize = this.ema.getValues().size();
        BigDecimal oldLastValue = this.ema.getValues().get(oldSize-1);
        updateCandlestickListInSuper();
        this.ema.updateIndicator();
        int newSize = this.ema.getValues().size();
        BigDecimal newNextToLastValue = this.ema.getValues().get(newSize-2);

        assertEquals(oldSize + 1, newSize);
        assertEquals(oldLastValue, newNextToLastValue);



//        this.ema.updateIndicator();
//        updateCandlestickListInSuper();
//        DateTime currentDateTime = mock(DateTime.class);
//        when(currentDateTime.toString()).thenReturn("2018-08-01T10:25:00Z");
//        this.ema.updateIndicator();
//        assertEquals(0, getLastCandlestickPrice().compareTo(UPDATED_CANDLESTICK_PRICE));
    }

//    @Override
//    @Test
//    public void getPointsReturnCorrectResult(){
//        this.ema.updateIndicator();
//        List<Point> points = this.ema.getPoints();
//        List<BigDecimal> values = this.ema.getValues();
//
//        testPointPrice(points, values);
//        testPointTime(points, values);
//    }

    @Override
    @Test
    public void TestToString(){
        String result = this.ema.toString();
        String expected = String.format("ExponentialMovingAverage{period=%d, " +
                        "candlestickPriceType=%s, indicatorValues=%s}",
                         period, this.mockCandlestickPriceType.toString(), ema.getValues().toString());
        assertEquals(expected, result);
    }

    @Override
    protected BigDecimal getLastCandlestickPrice() {
        List<BigDecimal> maValues = this.ema.getValues();
        return maValues.get(maValues.size() - 1);
    }
}