package trader.indicators;

import com.oanda.v20.instrument.CandlestickData;
import com.oanda.v20.pricing_common.PriceValue;
import org.junit.Before;
import org.junit.Test;
import trader.indicators.enums.AppliedPrice;

import java.math.BigDecimal;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class AppliedPriceTest {

    private static final BigDecimal CANDLE_OPEN_PRICE = new BigDecimal(1.16796);
    private static final BigDecimal CANDLE_CLOSE_PRICE = new BigDecimal(1.17245);
    private static final BigDecimal CANDLE_HIGH_PRICE = new BigDecimal(1.17380);
    private static final BigDecimal CANDLE_LOW_PRICE = new BigDecimal(1.16635);

    private CandlestickData mockCandlestickData;

    /**
     * Create fake candle with the data from the constants
     */

    @Before
    public void setUp() {

        this.mockCandlestickData = mock(CandlestickData.class);
        doReturn(new PriceValue(CANDLE_OPEN_PRICE)).when(this.mockCandlestickData).getO();
        doReturn(new PriceValue(CANDLE_CLOSE_PRICE)).when(this.mockCandlestickData).getC();
        doReturn(new PriceValue(CANDLE_HIGH_PRICE)).when(this.mockCandlestickData).getH();
        doReturn(new PriceValue(CANDLE_LOW_PRICE)).when(this.mockCandlestickData).getL();
    }

    //Test if the mocked object will return correct result
    @Test
    public void ReturnsCorrectResultFromTheFakeCandle(){

        int result = CANDLE_OPEN_PRICE.compareTo(this.mockCandlestickData.getO().bigDecimalValue());
        assertEquals("Not equal", 0, result);

        result = CANDLE_CLOSE_PRICE.compareTo(this.mockCandlestickData.getC().bigDecimalValue());
        assertEquals("Not equal", 0, result);

        result = CANDLE_HIGH_PRICE.compareTo(this.mockCandlestickData.getH().bigDecimalValue());
        assertEquals("Not equal", 0, result);

        result = CANDLE_LOW_PRICE.compareTo(this.mockCandlestickData.getL().bigDecimalValue());
        assertEquals("Not equal", 0, result);
    }

    @Test
    public void ApplyReturnsCorrectValueForOpen(){
        BigDecimal openPrice = AppliedPrice.OPEN.apply(this.mockCandlestickData);
        int result = openPrice.compareTo(CANDLE_OPEN_PRICE);
        assertEquals("Open prices are not equal", 0, result);
    }

    @Test
    public void ApplyReturnsCorrectValueForClose(){
        BigDecimal closePrice = AppliedPrice.CLOSE.apply(this.mockCandlestickData);
        int result = closePrice.compareTo(CANDLE_CLOSE_PRICE);
        assertEquals("Close prices are not equal", 0, result);
    }

    @Test
    public void ApplyReturnsCorrectValueForHigh(){
    BigDecimal highPrice = AppliedPrice.HIGH.apply(this.mockCandlestickData);
        int result = highPrice.compareTo(CANDLE_HIGH_PRICE);
        assertEquals("High prices are not equal", 0, result);
    }

    @Test
    public void ApplyReturnsCorrectValueForLow(){
        BigDecimal lowPrice = AppliedPrice.LOW.apply(this.mockCandlestickData);
        int result = lowPrice.compareTo(CANDLE_LOW_PRICE);
        assertEquals("Low prices are not equal", 0, result);
    }

    @Test
    public void ApplyReturnsCorrectValueForMedian(){
        BigDecimal medianPrice = AppliedPrice.MEDIAN.apply(this.mockCandlestickData);

        BigDecimal high = this.mockCandlestickData.getH().bigDecimalValue();
        BigDecimal low = this.mockCandlestickData.getL().bigDecimalValue();
        BigDecimal mid = high.add(low).divide(new BigDecimal(2), BigDecimal.ROUND_HALF_UP);

        int result = medianPrice.compareTo(mid);
        assertEquals("Median prices are not equal", 0, result);
    }

}