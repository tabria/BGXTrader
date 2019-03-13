package trader.indicators.ma;

import com.oanda.v20.instrument.Candlestick;
import com.oanda.v20.instrument.CandlestickData;
import com.oanda.v20.pricing_common.PriceValue;
import com.oanda.v20.primitives.DateTime;
import org.junit.Before;
import trader.candles.CandlesUpdater;
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

    protected List<String> candlesClosePrices = new ArrayList<>(Arrays.asList(
            "1.16114", "1.16214", "1.16314", "1.16414", "1.16514", "1.16614", "1.16714",
            "1.16814", "1.16914", "1.16114", "1.16214", "1.16314", "1.16414", "1.15714"
    ));
    protected List<String> candlesDateTime = new ArrayList<>(Arrays.asList(
            "2018-08-01T09:39:00Z", "2018-08-01T09:40:00Z", "2018-08-01T09:41:00Z", "2018-08-01T09:42:00Z",
            "2018-08-01T09:43:00Z", "2018-08-01T09:44:00Z", "2018-08-01T09:45:00Z", "2018-08-01T09:46:00Z",
            "2018-08-01T09:47:00Z", "2018-08-01T09:48:00Z", "2018-08-01T09:49:00Z", "2018-08-01T09:50:00Z",
            "2018-08-01T09:51:00Z", "2018-08-01T09:52:00Z"

    ));
    protected CandlesUpdater candlesUpdater;
    protected List<Candlestick> candlestickList;
    protected CandlestickPriceType mockCandlestickPriceType;
    protected long candlesticksQuantity;
    protected DateTime mockDateTime;

    @Before
    public void before() {
        this.mockDateTime = mock(DateTime.class);
        this.mockCandlestickPriceType = mock(CandlestickPriceType.class);
        fillCandlestickList();
        setCandlesticksQuantity();
        setCandlesUpdater();
    }

    protected void fillCandlestickList(){
        this.candlestickList = new ArrayList<>();
        for (int candlestickNumber = 0; candlestickNumber < candlesClosePrices.size() ; candlestickNumber++)
            addCandlestick(candlestickNumber);
    }
        private void addCandlestick(int candlestickNumber) {
            Candlestick candlestick = mock(Candlestick.class);
            setCandlestickDateTime(candlestick, candlestickNumber);
            setCandlestickPrice(candlestick, candlestickNumber);
            this.candlestickList.add(candlestick);
        }

        private void setCandlestickDateTime(Candlestick candlestick, int candlestickNumber) {
            DateTime candleDateTime = createCandleDateTime(candlestickNumber);
            when(candlestick.getTime()).thenReturn(candleDateTime);
        }

            private DateTime createCandleDateTime(int candlestickNumber) {
                DateTime candleDateTime = mock(DateTime.class);
                when(candleDateTime.toString()).thenReturn(candlesDateTime.get(candlestickNumber));
                return candleDateTime;
            }

        private void setCandlestickPrice(Candlestick candlestick, int candlestickNumber) {
            CandlestickData candlestickPriceData = createCandlestickPriceData(candlestickNumber);
            when(candlestick.getMid()).thenReturn(candlestickPriceData);
            when(this.mockCandlestickPriceType.extractPrice(candlestickPriceData))
                    .thenReturn(new BigDecimal(candlesClosePrices.get(candlestickNumber)));
        }

            private CandlestickData createCandlestickPriceData(int candlestickNumber) {
                PriceValue candlestickPriceValue = createCandlePriceValue(candlestickNumber);

                CandlestickData candlestick = mock(CandlestickData.class);
                when(candlestick.getC()).thenReturn(candlestickPriceValue);
                return candlestick;
            }

                private PriceValue createCandlePriceValue(int candlestickNumber) {
                    PriceValue candlestickPriceValue = mock(PriceValue.class);
                    when(candlestickPriceValue.toString()).thenReturn(candlesClosePrices.get(candlestickNumber));
                    return candlestickPriceValue;
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
        when(this.candlesUpdater.getCandles()).thenReturn(this.candlestickList);
        when(this.candlesUpdater.updateCandles(this.mockDateTime)).thenReturn(true);
    }
}
