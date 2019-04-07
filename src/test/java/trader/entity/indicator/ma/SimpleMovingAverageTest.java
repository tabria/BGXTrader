package trader.entity.indicator.ma;

import org.junit.Before;
import org.junit.Test;
import trader.entity.candlestick.Candlestick;
import trader.entity.candlestick.candle.Candle;
import trader.exception.BadRequestException;
import trader.exception.IndicatorPeriodTooBigException;
import trader.entity.indicator.BaseIndicatorTest;
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
        this.sma = new SimpleMovingAverage(this.period, this.candlePriceType,  this.granularity);
    }

    @Override
    public void WhenCallGetGranularity_CorrectResult() {
        assertEquals(granularity, sma.getGranularity());
    }

    @Test
    public void WhenCreatedThenGetMAValuesReturnEmptyList() {
        assertEquals(0, sma.getValues().size());
    }

    @Override
    @Test(expected = UnsupportedOperationException.class )
    public void getValuesReturnImmutableResult(){
        List<BigDecimal> values = this.sma.getValues();
        values.add(null);
    }

    @Override
    @Test
    public void TestToString(){
        String result = this.sma.toString();
        String expected = String.format("SimpleMovingAverage{period=%d, " +
                        "candlePriceType=%s, granularity=%s, indicatorValues=%s}",
                period, candlePriceType.toString(), granularity.toString(), sma.getValues().toString()
        );

        assertEquals(expected, result);
    }

    @Test
    public void WhenCallUpdateIndicatorCandlesticksListIsUpdated(){
        updateCandlestickList(sma);
    }

    @Test
    public void WhenCallUpdateIndicatorAndValuesCountIsZero_CorrectUpdate() {
        sma.updateIndicator(indicatorUpdateHelper.getFakeCandlestickListFullOfMock());
        List<BigDecimal> values = sma.getValues();

        assertEquals(BigDecimal.valueOf(1.16357), values.get(values.size()-1));
    }

    @Test
    public void WhenCallUpdateIndicatorAndValuesCountIsNotZero_SuccessfulUpdateWithSingleCandle() {
        this.sma.updateIndicator(indicatorUpdateHelper.getFakeCandlestickListFullOfMock());
        int oldSize = this.sma.getValues().size();
        BigDecimal oldLastValue = this.sma.getValues().get(oldSize-1);
        this.sma.updateIndicator(getListWithSingleNewCandle());
        int newSize = this.sma.getValues().size();
        BigDecimal newNextToLastValue = this.sma.getValues().get(newSize-2);

        assertEquals(oldSize + 1, newSize);
        assertEquals(oldLastValue, newNextToLastValue);
    }


    @Test(expected = IndicatorPeriodTooBigException.class)
    public void testPeriodBiggerThanCandlesCount(){
        this.period = 200;
        SimpleMovingAverage simpleMovingAverage = new SimpleMovingAverage(this.period,
                this.candlePriceType, this.granularity);
        simpleMovingAverage.updateIndicator(indicatorUpdateHelper.getFakeCandlestickListFullOfMock());
    }

    @Test(expected = BadRequestException.class)
    public void testCreatingEMAWithZeroCandles(){
        sma.updateIndicator(new ArrayList<>());
    }

    @Override
    protected BigDecimal getLastCandlestickPrice() {
        List<BigDecimal> maValues = this.sma.getValues();
        return maValues.get(maValues.size() - 1);
    }

//    private BigDecimal calculateExpectedSMAValue() {
//        List<Candlestick> candles = candlesUpdater.getCandles();
//        BigDecimal expectedValue = BigDecimal.ZERO;
//        for (int i = candles.size()-1; i > candles.size()-1-period ; i--)
//            expectedValue = expectedValue.add(candles.get(i).getClosePrice());
//        return expectedValue.divide(new BigDecimal(period), 5, BigDecimal.ROUND_HALF_UP);
//    }
}