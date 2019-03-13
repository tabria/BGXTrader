package trader.indicators.ma;

import com.oanda.v20.instrument.Candlestick;
import com.oanda.v20.instrument.CandlestickData;
import com.oanda.v20.pricing_common.PriceValue;
import com.oanda.v20.primitives.DateTime;
import org.junit.Before;
import trader.candles.CandlesUpdater;
import trader.indicators.IndicatorUpdateHelper;
import trader.indicators.enums.CandlestickPriceType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BaseMATest {

    private static final int DIVIDER = 2;
    private static final int IGNORED_CANDLES = 2;
    private static final int MINIMUM_CANDLES_COUNT = 3;

    protected List<String> candlesClosePrices;
    protected List<String> candlesDateTime;
    protected CandlesUpdater candlesUpdater;
    protected CandlestickPriceType mockCandlestickPriceType;
    protected IndicatorUpdateHelper indicatorUpdateHelper;
    protected long candlesticksQuantity;
    protected DateTime mockDateTime;

    @Before
    public void before() {
        this.mockDateTime = mock(DateTime.class);
        this.mockCandlestickPriceType = mock(CandlestickPriceType.class);
        this.indicatorUpdateHelper = new IndicatorUpdateHelper(this.mockCandlestickPriceType);
        this.candlesClosePrices = indicatorUpdateHelper.getCandlesClosePrices();
        this.candlesDateTime = indicatorUpdateHelper.getCandlesDateTime();
        this.indicatorUpdateHelper.fillCandlestickList();
        setCandlesticksQuantity();
        setCandlesUpdater();
    }

    private void setCandlesticksQuantity(){
        long candlesQuantity = (candlesClosePrices.size() - IGNORED_CANDLES)/ DIVIDER;
        if (candlesQuantity < MINIMUM_CANDLES_COUNT){
            throw new IllegalArgumentException("Prices in CANDLES_CLOSE_PRICES must be at least 3");
        }
        this.candlesticksQuantity = candlesQuantity;
    }

    private void setCandlesUpdater() {
        this.candlesUpdater = mock(CandlesUpdater.class);
        List<Candlestick> candlestickList = this.indicatorUpdateHelper.getCandlestickList();
        when(this.candlesUpdater.getCandles()).thenReturn(candlestickList);
        when(this.candlesUpdater.updateCandles(this.mockDateTime)).thenReturn(true);
    }
}
