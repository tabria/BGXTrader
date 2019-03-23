package trader.strategy.BGXStrategy.configuration;

import org.junit.Before;
import trader.CommonTestClassMembers;
import trader.candlestick.Candlestick;
import trader.candlestick.candle.CandlePriceType;
import trader.connector.CandlesUpdaterConnector;
import trader.indicator.Indicator;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class BaseIndicatorConfigurationTest {
    private static final long DEFAULT_CANDLE_TIME_FRAME_IN_SECONDS = 1_800L;
    private static final long DEFAULT_VOLUME = 1L;
    private static final BigDecimal DEFAULT_PRICE = new BigDecimal(0.00001).setScale(5, BigDecimal.ROUND_HALF_UP);
    private static final ZonedDateTime DEFAULT_ZONED_DATE_TIME = ZonedDateTime.parse("2012-06-30T12:30:40Z[UTC]");
    protected CandlesUpdaterConnector connector;
    protected CommonTestClassMembers commonMembers;
    private List<Candlestick> candlesticks;
    private Candlestick candle;

    @Before
    public void setUp() {
        connector = mock(CandlesUpdaterConnector.class);
        commonMembers = new CommonTestClassMembers();
        candlesticks = new ArrayList<>();
        candle = mock(Candlestick.class);
        setCandle(DEFAULT_ZONED_DATE_TIME);
        setCandlesticks();
        when(connector.getInitialCandles()).thenReturn(candlesticks);
    }

    protected void setCandle(ZonedDateTime dateTime) {
        when(candle.getClosePrice()).thenReturn(DEFAULT_PRICE);
        when(candle.getHighPrice()).thenReturn(DEFAULT_PRICE);
        when(candle.getLowPrice()).thenReturn(DEFAULT_PRICE);
        when(candle.getOpenPrice()).thenReturn(DEFAULT_PRICE);
        when(candle.getDateTime()).thenReturn(dateTime);
        when(candle.isComplete()).thenReturn(true);
        when(candle.getVolume()).thenReturn(DEFAULT_VOLUME);
        when(candle.getTimeFrame()).thenReturn(DEFAULT_CANDLE_TIME_FRAME_IN_SECONDS);
    }

    private void setCandlesticks() {
        for (int i = 0; i <200 ; i++) {
            candlesticks.add(candle);
        }
    }

    protected void setUpdateCandle() {
        Candlestick newCandle = mock(Candlestick.class);
        setCandle(ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC")));
        when(connector.updateCandle()).thenReturn(newCandle);
    }
}
