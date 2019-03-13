package trader.indicators.ma;

import com.oanda.v20.primitives.DateTime;
import org.junit.Before;
import org.junit.Test;
import trader.trades.entities.Point;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static trader.CommonConstants.ASK;
import static trader.CommonConstants.BID;

public class ExponentialMATest extends BaseMATest {

    private static final BigDecimal EXPECTED_CANDLESTICK_PRICE =BigDecimal.valueOf(1.16204);
    private static final String NEW_PRICE_ENTRY = "1.16814";
    private static final String NEW_DATETIME_ENTRY = "2018-08-01T09:53:00Z";

    private ExponentialMA ema;

    @Before
    public void before() {
        super.before();
        this.ema = new ExponentialMA(this.candlesticksQuantity,
                this.mockCandlestickPriceType, this.candlesUpdater);
    }

    @Test(expected = UnsupportedOperationException.class )
    public void getValuesReturnImmutableResult(){
        List<BigDecimal> values = this.ema.getValues();
        values.add(null);
    }

    @Test
    public void WhenGetMAValuesThenReturnCorrectResult() {
        this.ema.update(super.mockDateTime, ASK, BID);
        BigDecimal lastCandlestickPrice = getLastCandlestickPrice();
        assertEquals(0, lastCandlestickPrice.compareTo(EXPECTED_CANDLESTICK_PRICE));
    }

    @Test
    public void whenUpdateEMAPricesThenReturnCorrectResults() {
        this.ema.update(this.mockDateTime, ASK, BID);
        updateCandlestickListInSuper();
        this.ema.update(mock(DateTime.class),  ASK, BID);
        assertEquals(0, getLastCandlestickPrice().compareTo(EXPECTED_CANDLESTICK_PRICE));
    }


    @Test
    public void whenCallGetPointsThenReturnCorrectResult(){
        this.ema.update(this.mockDateTime,  ASK, BID);
        List<Point> points = this.ema.getPoints();
        List<BigDecimal> values = this.ema.getValues();

        testPointPrice(points, values);
        testPointTime(points, values);
    }

    private void testPointTime(List<Point> points, List<BigDecimal> values) {
        int pointPosition = 0;
        for (int candlePosition = 5  ; candlePosition < values.size()-1 ; candlePosition++) {
            BigDecimal pointExpectedTime = BigDecimal.valueOf(pointPosition + 1);
            BigDecimal pointResultTime = points.get(pointPosition).getTime();

            assertEquals( 0, pointResultTime.compareTo(pointExpectedTime));

            pointPosition++;
        }
    }

    private void testPointPrice(List<Point> points, List<BigDecimal> values) {
        int pointPosition = 0;
        for (int candlePosition = 5  ; candlePosition < values.size()-1 ; candlePosition++) {
            BigDecimal pointExpectedPrice = values.get(candlePosition);
            BigDecimal pointResultPrice = points.get(pointPosition++).getPrice();

            assertEquals( 0, pointResultPrice.compareTo(pointExpectedPrice));
        }
    }

    @Test
    public void TestToString(){
        String result = this.ema.toString();
        String expected = String.format("ExponentialMA{candlesticksQuantity=%d, candlestickPriceType=%s, maValues=[], points=[], isTradeGenerated=false}", candlesticksQuantity, this.mockCandlestickPriceType.toString());
        assertEquals(expected, result);
    }

    private BigDecimal getLastCandlestickPrice() {
        List<BigDecimal> maValues = this.ema.getValues();
        return maValues.get(maValues.size() - 1);
    }

    private void updateCandlestickListInSuper() {
        this.candlesClosePrices.add(NEW_PRICE_ENTRY);
        this.candlesDateTime.add(NEW_DATETIME_ENTRY);
        fillCandlestickList();
        when(this.candlesUpdater.getCandles()).thenReturn(this.candlestickList);
    }

    private ZonedDateTime dateTimeConversion(DateTime dateTime){
        Instant instantDateTime = Instant.parse(dateTime.toString());
        ZoneId zoneId = ZoneId.of("UTC");
        return ZonedDateTime.ofInstant(instantDateTime, zoneId);
    }
}