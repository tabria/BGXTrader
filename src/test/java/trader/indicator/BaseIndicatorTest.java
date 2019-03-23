package trader.indicator;

import org.junit.Before;
import org.junit.Test;
import trader.candlestick.CandlesUpdatable;
import trader.candlestick.candle.CandlePriceType;
import trader.candlestick.updater.CandlesUpdater;
import trader.candlestick.Candlestick;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import static org.mockito.Mockito.*;

public abstract class BaseIndicatorTest {

    private static final int DIVIDER = 2;
    private static final int MINIMUM_CANDLES_COUNT = 3;
    private static final String NEW_PRICE_ENTRY = "1.16814";
    private static final String NEW_DATETIME_ENTRY = "2018-08-01T09:53:00Z";

    protected CandlesUpdatable candlesUpdater;
    protected CandlePriceType candlePriceType = CandlePriceType.CLOSE;
    protected IndicatorUpdateHelper indicatorUpdateHelper;
    protected long period;
    protected Candlestick newCandle;

    @Before
    public void before() {
        this.candlesUpdater = mock(CandlesUpdater.class);
        this.indicatorUpdateHelper = new IndicatorUpdateHelper(this.candlePriceType);
        this.indicatorUpdateHelper.fillCandlestickList();
        this.newCandle = mock(Candlestick.class);
        setNewCandle();
        setPeriod();
        setCandlesUpdater();
    }

    @Test(expected = UnsupportedOperationException.class )
    public abstract void getValuesReturnImmutableResult();

    @Test
    public abstract void getMAValuesReturnCorrectResult();

    @Test
    public abstract void testSuccessfulUpdate();

    @Test
    public abstract void TestToString();

    protected abstract BigDecimal getLastCandlestickPrice();

    protected void updateCandlestickListInSuper() {
        this.indicatorUpdateHelper.fillCandlestickList();
        List<Candlestick> candlestickList = this.indicatorUpdateHelper.getCandlestickList();
        when(this.candlesUpdater.getCandles()).thenReturn(candlestickList);
    }

    private void setNewCandle(){
        when(newCandle.getClosePrice()).thenReturn(new BigDecimal(NEW_PRICE_ENTRY));
        when(newCandle.getDateTime())
                .thenReturn(ZonedDateTime.parse(NEW_DATETIME_ENTRY).withZoneSameInstant(ZoneId.of("UTC")));
        when(candlesUpdater.getUpdatedCandle()).thenReturn(newCandle);
    }

    private void setPeriod(){
        long period =  indicatorUpdateHelper.getCandlesClosePrices().size()/ DIVIDER;
        if (period < MINIMUM_CANDLES_COUNT){
            throw new IllegalArgumentException("Prices in CANDLES_CLOSE_PRICES must be at least 3");
        }
        this.period = period;
    }

    private void setCandlesUpdater() {
        List<Candlestick> candlestickList = this.indicatorUpdateHelper.getCandlestickList();
        doReturn(candlestickList).when(this.candlesUpdater).getCandles();
    }
}
