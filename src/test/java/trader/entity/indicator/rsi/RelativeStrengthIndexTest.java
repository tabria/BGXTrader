package trader.entity.indicator.rsi;

import org.junit.Before;
import org.junit.Test;
import trader.entity.candlestick.Candlestick;
import trader.entity.indicator.IndicatorUpdateHelper;
import trader.exception.BadRequestException;
import trader.exception.IndicatorPeriodTooBigException;
import trader.entity.indicator.BaseIndicatorTest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class RelativeStrengthIndexTest extends BaseIndicatorTest {

    private static final String DEFAULT_POSITION = "rsi";

    private RelativeStrengthIndex rsi;

    @Before
    public void before() {
        super.before();
        this.rsi = new RelativeStrengthIndex(this.period, this.candlePriceType, this.granularity);
    }

    @Override
    public void WhenCallGetGranularity_CorrectResult() {
        assertEquals(granularity, rsi.getGranularity());
    }

    @Test
    public void WhenCreateRSI_ThenNoValues(){
        assertEquals(0, rsi.getValues().size());
    }

    @Test
    public void WhenCreateRSI_PositionMustHaveDefaultValue(){
        assertEquals(DEFAULT_POSITION, rsi.getPosition());
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

    @Test
    public void WhenCallUpdateIndicatorCandlesticksListIsUpdated(){
        updateCandlestickList(rsi);
    }

    @Test
    public void WhenCallUpdateIndicatorAndValuesCountIsZero_CorrectUpdate() {
        rsi.updateIndicator(indicatorUpdateHelper.getFakeCandlestickListFullOfMock());

        assertEquals(0, BigDecimal.valueOf(32.49993).compareTo(getLastCandlestickPrice()));
    }

    @Test
    public void WhenCallUpdateIndicatorAndValuesCountIsNotZero_SuccessfulUpdateWithSingleCandle() {
        this.rsi.updateIndicator(indicatorUpdateHelper.getFakeCandlestickListFullOfMock());
        int oldSize = this.rsi.getValues().size();
        BigDecimal oldLastValue = this.rsi.getValues().get(oldSize-1);
        this.rsi.updateIndicator(indicatorUpdateHelper.getFakeCandlestickListFullOfMock());
        int newSize = this.rsi.getValues().size();
        BigDecimal newNextToLastValue = this.rsi.getValues().get(newSize-2);

        assertEquals(oldSize + 1, newSize);
        assertEquals(oldLastValue, newNextToLastValue);
    }

    @Test
    public void testRSIWithZeroAverageGainsAndZeroAverageLosses() {
        rsi.updateIndicator(indicatorUpdateHelper.getFakeCandlestickListFullOfZeros());
        int size = rsi.getValues().size();

        assertEquals(7,size);
        assertEquals(BigDecimal.valueOf(50), rsi.getValues().get(size-1));
    }

    @Test(expected = IndicatorPeriodTooBigException.class)
    public void testPeriodBiggerThanCandlesCount(){
        this.period = 200;
        RelativeStrengthIndex relativeStrengthIndex = new RelativeStrengthIndex(this.period,
                this.candlePriceType, this.granularity);
        relativeStrengthIndex.updateIndicator(indicatorUpdateHelper.getFakeCandlestickListFullOfMock());

    }

    @Test(expected = BadRequestException.class)
    public void testCreatingIndicatorWithZeroCandles(){
        rsi.updateIndicator(new ArrayList<>());
    }

    @Override
    protected BigDecimal getLastCandlestickPrice() {
        List<BigDecimal> rsiValues = this.rsi.getValues();
        return rsiValues.get(rsiValues.size() - 1);
    }
}