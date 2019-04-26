package trader.entity.indicator;

import org.junit.Before;
import org.junit.Test;
import trader.CommonTestClassMembers;
import trader.entity.candlestick.candle.CandleGranularity;
import trader.entity.candlestick.candle.CandlePriceType;
import trader.entity.candlestick.Candlestick;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public abstract class BaseIndicatorTest {

    private static final int DIVIDER = 2;
    private static final int MINIMUM_CANDLES_COUNT = 3;
    private static final String NEW_PRICE_ENTRY = "1.16814";
    public static final String NO_UPDATE_NEW_DATETIME_ENTRY = "2018-08-01T09:53:00Z";
    public static final String UPDATE_NEW_DATETIME_ENTRY = "2018-08-01T10:53:00Z";


    protected IndicatorUpdateHelper indicatorUpdateHelper;
    protected long period;
    protected String position;
    protected CandlePriceType candlePriceType = CandlePriceType.CLOSE;
    protected CandleGranularity granularity = CandleGranularity.M30;
    protected CommonTestClassMembers commonMembers;

    @Before
    public void before() {
        commonMembers = new CommonTestClassMembers();
        this.indicatorUpdateHelper = new IndicatorUpdateHelper();
        this.indicatorUpdateHelper.fillCandlestickList();
        setPeriod();
    }

    @Test
    public abstract void WhenCallGetGranularity_CorrectResult();

    @Test(expected = UnsupportedOperationException.class )
    public abstract void getValuesReturnImmutableResult();

    @Test
    public abstract void TestToString();

    protected abstract BigDecimal getLastCandlestickPrice();

    public List<Candlestick> getListWithSingleNewCandle(String newTime){
        Candlestick newCandle =mock(Candlestick.class);
        ZonedDateTime candleDateTime = ZonedDateTime
                .parse(newTime).withZoneSameInstant(ZoneId.of("UTC"));
        when(newCandle.getClosePrice()).thenReturn(new BigDecimal(NEW_PRICE_ENTRY));
        when(newCandle.getDateTime()).thenReturn(candleDateTime);
        List<Candlestick> candles = new ArrayList<>();
        candles.add(newCandle);
        return candles;
    }

    private void setPeriod(){
        long period =  indicatorUpdateHelper.getCandlesClosePrices().size()/ DIVIDER;
        if (period < MINIMUM_CANDLES_COUNT){
            throw new IllegalArgumentException("Prices in CANDLES_CLOSE_PRICES must be at least 3");
        }
        this.period = period;
    }

    protected void updateCandlestickList(Indicator indicator){
        int initialSize = getIndicatorCandlestickListSize(indicator);
        indicator.updateIndicator(indicatorUpdateHelper.getFakeCandlestickListFullOfMock());
        int sizeAfterFirstUpdate = getIndicatorCandlestickListSize(indicator);
        indicator.updateIndicator(getListWithSingleNewCandle(UPDATE_NEW_DATETIME_ENTRY));
        int sizeAfterSingleCandleUpdate = getIndicatorCandlestickListSize(indicator);

        assertNotEquals(initialSize, sizeAfterFirstUpdate);
        assertEquals(indicatorUpdateHelper.getFakeCandlestickListFullOfMock().size(), sizeAfterFirstUpdate);
        assertEquals(sizeAfterFirstUpdate + 1, sizeAfterSingleCandleUpdate);
    }

    @SuppressWarnings("unchecked")
    private int getIndicatorCandlestickListSize(Indicator indicator) {
        List<Candlestick> candlestickList = (List<Candlestick>) commonMembers.extractFieldObject(indicator, "candlestickList");
        return candlestickList.size();
    }
}
