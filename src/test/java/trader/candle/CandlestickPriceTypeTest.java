package trader.candle;


import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static trader.strategies.BGXStrategy.StrategyConfig.BIG_DECIMAL_SCALE;

public class CandlestickPriceTypeTest {

    private static final BigDecimal CANDLE_OPEN_PRICE = new BigDecimal(1.16796);
    private static final BigDecimal CANDLE_CLOSE_PRICE = new BigDecimal(1.17245);
    private static final BigDecimal CANDLE_HIGH_PRICE = new BigDecimal(1.17380);
    private static final BigDecimal CANDLE_LOW_PRICE = new BigDecimal(1.16635);

    private Candlestick mockCandlestick;

    @Before
    public void setUp() {

        this.mockCandlestick = mock(Candlestick.class);
        doReturn(CANDLE_OPEN_PRICE).when(this.mockCandlestick).getOpenPrice();
        doReturn(CANDLE_CLOSE_PRICE).when(this.mockCandlestick).getClosePrice();
        doReturn(CANDLE_HIGH_PRICE).when(this.mockCandlestick).getHighPrice();
        doReturn(CANDLE_LOW_PRICE).when(this.mockCandlestick).getLowPrice();
    }

    @Test
    public void testExtractPriceForOpen(){
        BigDecimal openPrice = CandlestickPriceType.OPEN.extractPrice(this.mockCandlestick);
        assertEquals("Open prices are not equal", 0, comparePrices(openPrice, CANDLE_OPEN_PRICE));
    }

    @Test
    public void testExtractPriceForClose(){
        BigDecimal closePrice = CandlestickPriceType.CLOSE.extractPrice(this.mockCandlestick);
        assertEquals("Close prices are not equal", 0, comparePrices(closePrice, CANDLE_CLOSE_PRICE));
    }

    @Test
    public void testExtractPriceForHigh(){
        BigDecimal highPrice = CandlestickPriceType.HIGH.extractPrice(this.mockCandlestick);
        assertEquals("High prices are not equal", 0, comparePrices(highPrice, CANDLE_HIGH_PRICE));
    }

    @Test
    public void testExtractPriceForLow(){
        BigDecimal lowPrice = CandlestickPriceType.LOW.extractPrice(this.mockCandlestick);
        assertEquals("Low prices are not equal", 0, comparePrices(lowPrice, CANDLE_LOW_PRICE));
    }

    @Test
    public void testExtractPriceForMedian(){
        BigDecimal medianPrice = CandlestickPriceType.MEDIAN.extractPrice(this.mockCandlestick);
        assertEquals("Median prices are not equal", 0, comparePrices(medianPrice, averagePrice()));
    }

    private BigDecimal averagePrice() {
        BigDecimal high = this.mockCandlestick.getHighPrice();
        BigDecimal low = this.mockCandlestick.getLowPrice();
        return high.add(low).divide(new BigDecimal(2), BIG_DECIMAL_SCALE , BigDecimal.ROUND_HALF_UP);
    }

    private int comparePrices(BigDecimal closePrice, BigDecimal candleClosePrice) {
        return closePrice.compareTo(candleClosePrice);
    }

}