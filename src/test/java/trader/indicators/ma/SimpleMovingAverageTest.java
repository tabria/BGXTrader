package trader.indicators.ma;

import com.oanda.v20.primitives.DateTime;
import org.junit.Before;
import org.junit.Test;
import trader.candle.Candlestick;
import trader.connectors.ApiConnector;
import trader.indicators.BaseIndicatorTest;
import trader.indicators.Indicator;
import trader.trades.entities.Point;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SimpleMovingAverageTest extends BaseIndicatorTest {

    private static final BigDecimal EXPECTED_CANDLESTICK_PRICE = BigDecimal.valueOf(1.16281);
    private static final BigDecimal UPDATED_CANDLESTICK_PRICE = BigDecimal.valueOf(1.16264);

    private SimpleMovingAverage sma;
    private ApiConnector apiConnector;

    @Before
    public void before() {
        super.before();
        apiConnector = mock(ApiConnector.class);
        this.sma = new SimpleMovingAverage(this.period, this.mockCandlestickPriceType, this.candlesUpdater);
    }

    @Override
    @Test
    public void getMAValuesReturnCorrectResult() {
        int size = candlesUpdater.getCandles().size();
        Candlestick candlestickMock = super.indicatorUpdateHelper.createCandlestickMock();
        when(candlesUpdater.getUpdateCandle()).thenReturn(candlestickMock);
        this.sma.updateIndicator();

        assertEquals(size + 1, candlesUpdater.getCandles().size());
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
        this.sma.updateIndicator();
        updateCandlestickListInSuper();
        DateTime currentDateTime = mock(DateTime.class);
        when(currentDateTime.toString()).thenReturn("2018-08-01T10:25:00Z");
        this.sma.updateIndicator();
        assertEquals(0, getLastCandlestickPrice().compareTo(UPDATED_CANDLESTICK_PRICE));
    }

    @Override
    @Test
    public void getPointsReturnCorrectResult(){
        this.sma.updateIndicator();
        List<Point> points = this.sma.getPoints();
        List<BigDecimal> values = this.sma.getValues();

        testPointPrice(points, values);
        testPointTime(points, values);
    }

    @Override
    @Test
    public void TestToString(){
        String result = this.sma.toString();
        String expected = String.format("SimpleMovingAverage{period=%d, " +
                        "candlestickPriceType=%s, indicatorValues=%s, points=%s}",
                         period, mockCandlestickPriceType.toString(), sma.getValues().toString(), sma.getPoints().toString());
        assertEquals(expected, result);
    }

    @Test
    public void testInitializingSMAValues(){
        List<BigDecimal> values = this.sma.getValues();
        assertTrue(values.size()> 0);

    }

    @Test
    public void testUpdatingSMA_CorrectResults(){
        List<BigDecimal> values = this.sma.getValues();
        this.sma.updateIndicator();
        List<BigDecimal> values1 = this.sma.getValues();

        assertEquals(values.size()+1, values1.size());
        assertEquals(values.get(values.size()-1), values1.get(values1.size()-2));
    }


    @Override
    protected BigDecimal getLastCandlestickPrice() {
        List<BigDecimal> maValues = this.sma.getValues();
        return maValues.get(maValues.size() - 1);
    }

    private void createMockCandle(){
        Candlestick mockCandle = mock(Candlestick.class);

    }
}