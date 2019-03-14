package trader.indicators.ma;

import com.oanda.v20.primitives.DateTime;
import org.junit.Before;
import org.junit.Test;
import trader.trades.entities.Point;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class SimpleMovingAverageTest extends BaseMATest {

    private static final BigDecimal EXPECTED_CANDLESTICK_PRICE = BigDecimal.valueOf(1.16281);

    private SimpleMovingAverage sma;

    @Before
    public void before() {
        super.before();
        this.sma = new SimpleMovingAverage(this.candlesticksQuantity, this.mockCandlestickPriceType, this.candlesUpdater);
    }

    @Override
    @Test
    public void getMAValuesReturnCorrectResult() {
        this.sma.updateMovingAverage(super.mockDateTime);
        BigDecimal lastCandlestickPrice = getLastCandlestickPrice();
        assertEquals(0, lastCandlestickPrice.compareTo(EXPECTED_CANDLESTICK_PRICE));
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
        this.sma.updateMovingAverage(this.mockDateTime);
        updateCandlestickListInSuper();
        this.sma.updateMovingAverage(mock(DateTime.class));
        assertEquals(0, getLastCandlestickPrice().compareTo(EXPECTED_CANDLESTICK_PRICE));
    }

    @Override
    @Test
    public void getPointsReturnCorrectResult(){
        this.sma.updateMovingAverage(this.mockDateTime);
        List<Point> points = this.sma.getPoints();
        List<BigDecimal> values = this.sma.getValues();

        testPointPrice(points, values);
        testPointTime(points, values);
    }

    @Override
    @Test
    public void TestToString(){
        String result = this.sma.toString();
        String expected = String.format("SimpleMovingAverage{candlesticksQuantity=%d, " +
                        "candlestickPriceType=%s, maValues=[], points=[], isTradeGenerated=false}",
                         candlesticksQuantity, this.mockCandlestickPriceType.toString());
        assertEquals(expected, result);
    }

    @Override
    protected BigDecimal getLastCandlestickPrice() {
        List<BigDecimal> maValues = this.sma.getValues();
        return maValues.get(maValues.size() - 1);
    }
}