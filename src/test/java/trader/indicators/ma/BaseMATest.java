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

    private void setCandlesUpdater() {
        this.candlesUpdater = mock(CandlesUpdater.class);
        when(this.candlesUpdater.getCandles()).thenReturn(this.candlestickList);
        when(this.candlesUpdater.updateCandles(this.mockDateTime)).thenReturn(true);
    }

    protected void fillCandlestickList(){

        this.candlestickList = new ArrayList<>();

        for (int candlestickNumber = 0; candlestickNumber < candlesClosePrices.size() ; candlestickNumber++) {
            
            Candlestick candlestick = mock(Candlestick.class);
            
     //       DateTime candlestickDateTime = createCandlestickDateTime(candlestickNumber);
            PriceValue candlestickPriceValue = createCandlestickPriceValue(candlestickNumber);

            CandlestickData candlestickData1 = mock(CandlestickData.class);
            when(candlestickData1.getC()).thenReturn(candlestickPriceValue);


            setCandlestickDateTime(candlestick, candlestickNumber);
            when(candlestick.getMid()).thenReturn(candlestickData1);

            when(this.mockCandlestickPriceType.extractPrice(candlestickData1)).thenReturn(new BigDecimal(candlesClosePrices.get(candlestickNumber)));

            this.candlestickList.add(candlestick);
        }
    }

    private void setCandlestickDateTime(Candlestick candlestick, int candlestickNumber) {
        DateTime candleDateTime = mock(DateTime.class);;
        when(candlestick.getTime()).thenReturn(candleDateTime);
    }

    private PriceValue createCandlestickPriceValue(int candlestickNumber) {
        PriceValue priceValue1 = mock(PriceValue.class);
        when(priceValue1.toString()).thenReturn(candlesClosePrices.get(candlestickNumber));
        return priceValue1;
    }

    private DateTime createCandlestickDateTime(int candlestickNumber) {
        DateTime candleDateTime = mock(DateTime.class);
        when(candleDateTime.toString()).thenReturn(candlesDateTime.get(candlestickNumber));
        return candleDateTime;
    }

    //total candles count for the given periods are calculated with this formula:
    // {@code candlesticksQuantity*2 + 2 => candlesticksQuantity = (number of candles - 2) /2}
    private void setCandlesticksQuantity(){
        long period = (candlesClosePrices.size() - 2)/2;
        if (period <= 0){
            throw new IllegalArgumentException("Prices in CANDLES_CLOSE_PRICES must be at least 3");
        }
        this.candlesticksQuantity = period;
    }
}
