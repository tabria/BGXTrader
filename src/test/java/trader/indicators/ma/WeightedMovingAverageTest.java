package trader.indicators.ma;


import com.oanda.v20.primitives.DateTime;
import org.junit.Before;
import org.junit.Test;
import trader.indicators.BaseIndicatorTest;
import trader.trades.entities.Point;
import java.math.BigDecimal;
import java.util.List;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WeightedMovingAverageTest extends BaseIndicatorTest {

    private static final BigDecimal EXPECTED_CANDLESTICK_PRICE = BigDecimal.valueOf(1.16162);
    private static final BigDecimal UPDATED_CANDLESTICK_PRICE = BigDecimal.valueOf(1.16314);

    private WeightedMovingAverage wma;

    @Before
    public void before()  {
        super.before();
        this.wma = new WeightedMovingAverage(candlesticksQuantity, mockCandlestickPriceType, candlesUpdater) ;


    }

    @Override
    @Test
    public void getMAValuesReturnCorrectResult() {
        this.wma.updateIndicator(super.mockDateTime);
        BigDecimal lastCandlestickPrice = getLastCandlestickPrice();
        assertEquals(0, lastCandlestickPrice.compareTo(EXPECTED_CANDLESTICK_PRICE));
    }

    @Override
    @Test(expected = UnsupportedOperationException.class )
    public void getValuesReturnImmutableResult(){
        List<BigDecimal> values = this.wma.getValues();
        values.add(null);
    }

    @Override
    @Test
    public void testSuccessfulUpdate() {
        this.wma.updateIndicator(this.mockDateTime);
        updateCandlestickListInSuper();
        DateTime currentDateTime = mock(DateTime.class);
        when(currentDateTime.toString()).thenReturn("2018-08-01T10:25:00Z");
        this.wma.updateIndicator(currentDateTime);
        assertEquals(0, getLastCandlestickPrice().compareTo(UPDATED_CANDLESTICK_PRICE));
    }

    @Override
    @Test
    public void getPointsReturnCorrectResult(){
        this.wma.updateIndicator(this.mockDateTime);
        List<Point> points = this.wma.getPoints();
        List<BigDecimal> values = this.wma.getValues();

        testPointPrice(points, values);
        testPointTime(points, values);
    }

    @Override
    @Test
    public void TestToString(){
        String result = this.wma.toString();
        String expected = String.format("WeightedMovingAverage{candlesticksQuantity=%d, " +
                        "candlestickPriceType=%s, indicatorValues=[], points=[], isTradeGenerated=false}",
                candlesticksQuantity, this.mockCandlestickPriceType.toString());
        assertEquals(expected, result);
    }

    @Override
    protected BigDecimal getLastCandlestickPrice() {
        List<BigDecimal> maValues = this.wma.getValues();
        return maValues.get(maValues.size() - 1);
    }
}