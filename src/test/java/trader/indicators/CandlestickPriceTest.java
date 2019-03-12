package trader.indicators;

import com.oanda.v20.instrument.CandlestickData;
import com.oanda.v20.pricing_common.PriceValue;
import org.junit.Before;
import org.junit.Test;
import trader.indicators.enums.CandlestickPrice;

import java.math.BigDecimal;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class CandlestickPriceTest {

    private static final BigDecimal CANDLE_OPEN_PRICE = new BigDecimal(1.16796);
    private static final BigDecimal CANDLE_CLOSE_PRICE = new BigDecimal(1.17245);
    private static final BigDecimal CANDLE_HIGH_PRICE = new BigDecimal(1.17380);
    private static final BigDecimal CANDLE_LOW_PRICE = new BigDecimal(1.16635);

    private CandlestickData mockCandlestickData;

    @Before
    public void setUp() {

        this.mockCandlestickData = mock(CandlestickData.class);
        doReturn(new PriceValue(CANDLE_OPEN_PRICE)).when(this.mockCandlestickData).getO();
        doReturn(new PriceValue(CANDLE_CLOSE_PRICE)).when(this.mockCandlestickData).getC();
        doReturn(new PriceValue(CANDLE_HIGH_PRICE)).when(this.mockCandlestickData).getH();
        doReturn(new PriceValue(CANDLE_LOW_PRICE)).when(this.mockCandlestickData).getL();
    }

    @Test
    public void testExtractPriceForOpen(){
        BigDecimal openPrice = CandlestickPrice.OPEN.extractPrice(this.mockCandlestickData);
        assertEquals("Open prices are not equal", 0, comparePrices(openPrice, CANDLE_OPEN_PRICE));
    }

    @Test
    public void testExtractPriceForClose(){
        BigDecimal closePrice = CandlestickPrice.CLOSE.extractPrice(this.mockCandlestickData);
        assertEquals("Close prices are not equal", 0, comparePrices(closePrice, CANDLE_CLOSE_PRICE));
    }

    @Test
    public void testExtractPriceForHigh(){
        BigDecimal highPrice = CandlestickPrice.HIGH.extractPrice(this.mockCandlestickData);
        assertEquals("High prices are not equal", 0, comparePrices(highPrice, CANDLE_HIGH_PRICE));
    }

    @Test
    public void testExtractPriceForLow(){
        BigDecimal lowPrice = CandlestickPrice.LOW.extractPrice(this.mockCandlestickData);
        assertEquals("Low prices are not equal", 0, comparePrices(lowPrice, CANDLE_LOW_PRICE));
    }

    @Test
    public void testExtractPriceForMedian(){
        BigDecimal medianPrice = CandlestickPrice.MEDIAN.extractPrice(this.mockCandlestickData);
        assertEquals("Median prices are not equal", 0, comparePrices(medianPrice, averagePrice()));
    }

    private BigDecimal averagePrice() {
        BigDecimal high = this.mockCandlestickData.getH().bigDecimalValue();
        BigDecimal low = this.mockCandlestickData.getL().bigDecimalValue();
        return high.add(low).divide(new BigDecimal(2), BigDecimal.ROUND_HALF_UP);
    }

    private int comparePrices(BigDecimal closePrice, BigDecimal candleClosePrice) {
        return closePrice.compareTo(candleClosePrice);
    }

}