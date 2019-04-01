package trader.entity.candlestick.candle;


import org.junit.Before;
import org.junit.Test;
import trader.entity.candlestick.Candlestick;

import java.math.BigDecimal;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static trader.strategy.bgxstrategy.configuration.StrategyConfig.SCALE;

public class CandlePriceTypeTest {

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
        BigDecimal openPrice = CandlePriceType.OPEN.extractPrice(this.mockCandlestick);
        assertEquals("Open price are not equal", 0, comparePrices(openPrice, CANDLE_OPEN_PRICE));
    }

    @Test
    public void testExtractPriceForClose(){
        BigDecimal closePrice = CandlePriceType.CLOSE.extractPrice(this.mockCandlestick);
        assertEquals("Close price are not equal", 0, comparePrices(closePrice, CANDLE_CLOSE_PRICE));
    }

    @Test
    public void testExtractPriceForHigh(){
        BigDecimal highPrice = CandlePriceType.HIGH.extractPrice(this.mockCandlestick);
        assertEquals("High price are not equal", 0, comparePrices(highPrice, CANDLE_HIGH_PRICE));
    }

    @Test
    public void testExtractPriceForLow(){
        BigDecimal lowPrice = CandlePriceType.LOW.extractPrice(this.mockCandlestick);
        assertEquals("Low price are not equal", 0, comparePrices(lowPrice, CANDLE_LOW_PRICE));
    }

    @Test
    public void testExtractPriceForMedian(){
        BigDecimal medianPrice = CandlePriceType.MEDIAN.extractPrice(this.mockCandlestick);
        assertEquals("Median price are not equal", 0, comparePrices(medianPrice, averagePrice()));
    }

    private BigDecimal averagePrice() {
        BigDecimal high = this.mockCandlestick.getHighPrice();
        BigDecimal low = this.mockCandlestick.getLowPrice();
        return high.add(low).divide(new BigDecimal(2), SCALE, BigDecimal.ROUND_HALF_UP);
    }

    private int comparePrices(BigDecimal closePrice, BigDecimal candleClosePrice) {
        return closePrice.compareTo(candleClosePrice);
    }

}