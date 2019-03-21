package trader.indicator.ma;



import org.junit.Before;
import org.junit.Test;
import trader.exception.BadRequestException;
import trader.exception.IndicatorPeriodTooBigException;
import trader.indicator.BaseIndicatorTest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static trader.strategie.BGXStrategy.StrategyConfig.SCALE;

public class WeightedMovingAverageTest extends BaseIndicatorTest {

    private static final BigDecimal LAST_WMA_VALUE = BigDecimal.valueOf(1.16210).setScale(SCALE, BigDecimal.ROUND_HALF_UP);;

    private WeightedMovingAverage wma;

    @Before
    public void before()  {
        super.before();
        this.wma = new WeightedMovingAverage(period, candlestickPriceType, candlesUpdater) ;
    }

    @Test
    public void testInitializingEMAValues(){
        List<BigDecimal> values = this.wma.getValues();

        assertTrue(values.size()> 0);
    }

    @Override
    @Test(expected = UnsupportedOperationException.class )
    public void getValuesReturnImmutableResult(){
        List<BigDecimal> values = this.wma.getValues();
        values.add(null);
    }


    @Override
    @Test
    public void getMAValuesReturnCorrectResult() {
        List<BigDecimal> values = this.wma.getValues();
        assertEquals(LAST_WMA_VALUE, values.get(values.size()-1));
    }

    @Override
    @Test
    public void testSuccessfulUpdate() {
        int oldSize = this.wma.getValues().size();
        BigDecimal oldLastValue = this.wma.getValues().get(oldSize-1);
        updateCandlestickListInSuper();
        this.wma.updateIndicator();
        int newSize = this.wma.getValues().size();
        BigDecimal newNextToLastValue = this.wma.getValues().get(newSize-2);

        assertEquals(oldSize + 1, newSize);
        assertEquals(oldLastValue, newNextToLastValue);
    }

    @Override
    @Test
    public void TestToString(){
        String result = this.wma.toString();
        String expected = String.format("WeightedMovingAverage{period=%d, " +
                        "candlestickPriceType=%s, indicatorValues=%s}",
                period, this.candlestickPriceType.toString(), wma.getValues().toString());
        assertEquals(expected, result);
    }

    @Test(expected = IndicatorPeriodTooBigException.class)
    public void testPeriodBiggerThanCandlesCount(){
        this.period = 200;
        new WeightedMovingAverage(this.period,
                this.candlestickPriceType, this.candlesUpdater);
    }

    @Test(expected = BadRequestException.class)
    public void testCreatingEMAWithZeroCandles(){
        when(candlesUpdater.getCandles()).thenReturn(new ArrayList<>());
        new WeightedMovingAverage(this.period,
                this.candlestickPriceType, this.candlesUpdater);
    }

    @Override
    protected BigDecimal getLastCandlestickPrice() {
        List<BigDecimal> maValues = this.wma.getValues();
        return maValues.get(maValues.size() - 1);
    }
}