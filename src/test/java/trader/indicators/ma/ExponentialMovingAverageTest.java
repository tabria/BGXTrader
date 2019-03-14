package trader.indicators.ma;

import com.oanda.v20.primitives.DateTime;
import org.junit.Before;
import org.junit.Test;
import trader.trades.entities.Point;
import java.math.BigDecimal;
import java.util.List;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static trader.CommonConstants.ASK;
import static trader.CommonConstants.BID;

public class ExponentialMovingAverageTest extends BaseMATest {

    private static final BigDecimal EXPECTED_CANDLESTICK_PRICE = BigDecimal.valueOf(1.16204);

    private ExponentialMovingAverage ema;

    @Before
    public void before() {
        super.before();
        this.ema = new ExponentialMovingAverage(this.candlesticksQuantity,
                this.mockCandlestickPriceType, this.candlesUpdater);
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
        this.ema.updateMovingAverage(super.mockDateTime);
        BigDecimal lastCandlestickPrice = getLastCandlestickPrice();
        assertEquals(0, lastCandlestickPrice.compareTo(EXPECTED_CANDLESTICK_PRICE));
    }

    @Override
    @Test
    public void testSuccessfulUpdate() {
        this.ema.updateMovingAverage(this.mockDateTime);
        updateCandlestickListInSuper();
        this.ema.updateMovingAverage(mock(DateTime.class));
        assertEquals(0, getLastCandlestickPrice().compareTo(EXPECTED_CANDLESTICK_PRICE));
    }

    @Override
    @Test
    public void getPointsReturnCorrectResult(){
        this.ema.updateMovingAverage(this.mockDateTime);
        List<Point> points = this.ema.getPoints();
        List<BigDecimal> values = this.ema.getValues();

        testPointPrice(points, values);
        testPointTime(points, values);
    }

    @Override
    @Test
    public void TestToString(){
        String result = this.ema.toString();
        String expected = String.format("ExponentialMovingAverage{candlesticksQuantity=%d, " +
                        "candlestickPriceType=%s, maValues=[], points=[], isTradeGenerated=false}",
                         candlesticksQuantity, this.mockCandlestickPriceType.toString());
        assertEquals(expected, result);
    }

    @Override
    protected BigDecimal getLastCandlestickPrice() {
        List<BigDecimal> maValues = this.ema.getValues();
        return maValues.get(maValues.size() - 1);
    }

}