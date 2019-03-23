package trader.indicator.ma;

import org.junit.Before;
import org.junit.Test;
import trader.candlestick.Candlestick;
import trader.exception.BadRequestException;
import trader.exception.IndicatorPeriodTooBigException;
import trader.indicator.BaseIndicatorTest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;


public class SimpleMovingAverageTest extends BaseIndicatorTest {

    private SimpleMovingAverage sma;

    @Before
    public void before() {
        super.before();
        this.sma = new SimpleMovingAverage(this.period, this.candlePriceType, this.candlesUpdater);
    }

    @Override
    @Test
    public void getMAValuesReturnCorrectResult() {
        BigDecimal expectedValue = calculateExpectedSMAValue();
        List<BigDecimal> values = this.sma.getValues();

        assertEquals(expectedValue, values.get(values.size()-1));
    }

    @Override
    @Test(expected = UnsupportedOperationException.class )
    public void getValuesReturnImmutableResult(){
        List<BigDecimal> values = this.sma.getValues();
        values.add(null);
    }

    @Override
    @Test
    public void testSuccessfulUpdate() {
        int oldSize = this.sma.getValues().size();
        BigDecimal oldLastValue = this.sma.getValues().get(oldSize-1);
        updateCandlestickListInSuper();
        this.sma.updateIndicator();
        int newSize = this.sma.getValues().size();
        BigDecimal newNextToLastValue = this.sma.getValues().get(newSize-2);

        assertEquals(oldSize + 1, newSize);
        assertEquals(oldLastValue, newNextToLastValue);
    }

    @Override
    @Test
    public void TestToString(){
        String result = this.sma.toString();
        String expected = String.format("SimpleMovingAverage{period=%d, " +
                        "candlePriceType=%s, indicatorValues=%s}",
                period, candlePriceType.toString(), sma.getValues().toString()
        );

        assertEquals(expected, result);
    }

    @Test
    public void testInitializingSMAValues(){
        List<BigDecimal> values = this.sma.getValues();

        assertTrue(values.size()> 0);
    }

    @Test(expected = BadRequestException.class)
    public void testCreatingEMAWithZeroCandles(){
        when(candlesUpdater.getCandles()).thenReturn(new ArrayList<>());
        new SimpleMovingAverage(this.period,
                this.candlePriceType, this.candlesUpdater);
    }

    @Test(expected = IndicatorPeriodTooBigException.class)
    public void testPeriodBiggerThanCandlesCount(){
        this.period = 200;
        new SimpleMovingAverage(this.period,
                this.candlePriceType, this.candlesUpdater);
    }

    @Override
    protected BigDecimal getLastCandlestickPrice() {
        List<BigDecimal> maValues = this.sma.getValues();
        return maValues.get(maValues.size() - 1);
    }

    private BigDecimal calculateExpectedSMAValue() {
        List<Candlestick> candles = candlesUpdater.getCandles();
        BigDecimal expectedValue = BigDecimal.ZERO;
        for (int i = candles.size()-1; i > candles.size()-1-period ; i--)
            expectedValue = expectedValue.add(candles.get(i).getClosePrice());
        return expectedValue.divide(new BigDecimal(period), 5, BigDecimal.ROUND_HALF_UP);
    }
}