package trader.entity.indicator.rsi;

import org.junit.Before;
import org.junit.Test;
import trader.exception.BadRequestException;
import trader.exception.IndicatorPeriodTooBigException;
import trader.entity.indicator.BaseIndicatorTest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class RelativeStrengthIndexTest extends BaseIndicatorTest {

    private RelativeStrengthIndex rsi;

    @Before
    public void before() {
        super.before();
        this.rsi = new RelativeStrengthIndex(this.period, this.candlePriceType, this.granularity);
    }

    @Test
    public void WhenCreateRSI_ThenNoValues(){
        assertEquals(0, rsi.getValues().size());
    }

    @Override
    @Test(expected = UnsupportedOperationException.class )
    public void getValuesReturnImmutableResult(){
        List<BigDecimal> values = this.rsi.getValues();
        values.add(null);
    }

    @Test
    public void TestToString(){
        String result = this.rsi.toString();
        String expected = String.format("RelativeStrengthIndex{period=%d, candlePriceType=%s, granularity=%s, rsiValues=%s}",
                period, this.candlePriceType.toString(), this.granularity.toString(), "[]");

        assertEquals(expected, result);
    }

//    @Test
//    public void testInitializingEMAValues(){
//        List<BigDecimal> values = this.rsi.getValues();
//
//        assertTrue(values.size()> 0);
//    }


    @Override
    @Test
    public void getMAValuesReturnCorrectResult() {
//        assertEquals(0, BigDecimal.valueOf(32.49993).compareTo(getLastCandlestickPrice()));
    }

    @Override
    @Test
    public void testSuccessfulUpdate() {
//        int oldSize = this.rsi.getValues().size();
//        BigDecimal oldLastValue = this.rsi.getValues().get(oldSize-1);
//        updateCandlestickListInSuper();
//        this.rsi.updateIndicator();
//        int newSize = this.rsi.getValues().size();
//        BigDecimal newNextToLastValue = this.rsi.getValues().get(newSize-2);
//
//        assertEquals(oldSize + 1, newSize);
//        assertEquals(oldLastValue, newNextToLastValue);
    }

//    @Test
//    public void testRSIWithZeroAverageGainsAndZeroAverageLosses() {
//
//        indicatorUpdateHelper.fillCandlestickListWithZeros();
//        when(this.candlesUpdater.getCandles()).thenReturn(indicatorUpdateHelper.getCandlestickList());
//        RelativeStrengthIndex newRsi = new RelativeStrengthIndex(this.period, this.candlePriceType, this.candlesUpdater);
//        int newSize = newRsi.getValues().size();
//        BigDecimal lastValue = newRsi.getValues().get(newSize-1);
//
//        assertEquals(7, newSize);
//        assertEquals(BigDecimal.valueOf(50), lastValue);
//    }



//    @Test(expected = IndicatorPeriodTooBigException.class)
//    public void testPeriodBiggerThanCandlesCount(){
//        this.period = 200;
//        new RelativeStrengthIndex(this.period,
//                this.candlePriceType, new ArrayList<>(), this.granularity);
//    }

//    @Test(expected = BadRequestException.class)
//    public void testCreatingEMAWithZeroCandles(){
//        when(candlesUpdater.getCandles()).thenReturn(new ArrayList<>());
//        new RelativeStrengthIndex(this.period,
//                this.candlePriceType, this.candlesUpdater);
//    }

    @Override
    protected BigDecimal getLastCandlestickPrice() {
        List<BigDecimal> rsiValues = this.rsi.getValues();
        return rsiValues.get(rsiValues.size() - 1);
    }
}