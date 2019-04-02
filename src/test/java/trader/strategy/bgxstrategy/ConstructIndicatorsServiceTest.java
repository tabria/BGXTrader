package trader.strategy.bgxstrategy;

import org.junit.Before;
import org.junit.Test;
import trader.CommonTestClassMembers;
import trader.entity.candlestick.Candlestick;
import trader.connector.ApiConnector;
import trader.controller.IndicatorObserver;
import trader.entity.indicator.ma.SimpleMovingAverage;
import trader.entity.indicator.ma.WeightedMovingAverage;
import trader.entity.indicator.rsi.RelativeStrengthIndex;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConstructIndicatorsServiceTest {

    private static final int CANDLESTICK_LIST_SIZE = 170;
    private static final BigDecimal DEFAULT_PRICE = new BigDecimal(0.00001).setScale(5, BigDecimal.ROUND_HALF_UP);

    private CommonTestClassMembers commonMembers;
    private ConstructIndicatorsService constructIS;
    private ApiConnector apiConnector;
    private ZonedDateTime timeNow;
    private List<Candlestick> candlesticks;
    private Candlestick mockCandle;


    @Before
    public void setUp() throws Exception {
        mockCandle = mock(Candlestick.class);
        commonMembers = new CommonTestClassMembers();
        apiConnector = mock(ApiConnector.class);
        timeNow = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC"));
        candlesticks = new ArrayList<>();
        init();
        constructIS = new ConstructIndicatorsService(apiConnector);

    }

    @Test(expected = UnsupportedOperationException.class)
    public void whenCallGetIndicatorObservers_UnmodifiableList(){
        List<IndicatorObserver> indicatorObservers = constructIS.getIndicatorObservers();
        indicatorObservers.add(mock(IndicatorObserver.class));
    }

    @Test
    public void whenCreateClassThenAllIndicatorsAreInitialized() {

        List<IndicatorObserver> indicatorObservers = constructIS.getIndicatorObservers();

        Object indicator1 = commonMembers.extractFieldObject(indicatorObservers.get(0), "indicator");
        Object indicator2 = commonMembers.extractFieldObject(indicatorObservers.get(1), "indicator");
        Object indicator3 = commonMembers.extractFieldObject(indicatorObservers.get(2), "indicator");
        Object indicator4 = commonMembers.extractFieldObject(indicatorObservers.get(3), "indicator");
        Object indicator5 = commonMembers.extractFieldObject(indicatorObservers.get(4), "indicator");
        Object indicator6 = commonMembers.extractFieldObject(indicatorObservers.get(5), "indicator");


        assertEquals(6, indicatorObservers.size());
        assertEquals(SimpleMovingAverage.class, indicator1.getClass());
        assertEquals(SimpleMovingAverage.class, indicator2.getClass());
        assertEquals(WeightedMovingAverage.class, indicator3.getClass());
        assertEquals(WeightedMovingAverage.class, indicator4.getClass());
        assertEquals(WeightedMovingAverage.class, indicator5.getClass());
        assertEquals(RelativeStrengthIndex.class, indicator6.getClass());
    }

    private void init() {
        when(apiConnector.getInitialCandles()).thenReturn(candlesticks);
        when(apiConnector.updateCandle()).thenReturn(mockCandle);
        fillCandlestickList();
    }

    private void fillCandlestickList(){
        for (int i = 0; i < CANDLESTICK_LIST_SIZE; i++) {
            timeNow = timeNow.plusSeconds(30);
            candlesticks.add(createCandlestickMock());
            setMockCandleTime(timeNow = timeNow.plusSeconds(30));
        }
    }

    private Candlestick createCandlestickMock() {
        Candlestick newCandlestick = mock(Candlestick.class);
        when(newCandlestick.getDateTime()).thenReturn(timeNow);
        when(newCandlestick.getOpenPrice()).thenReturn(DEFAULT_PRICE);
        when(newCandlestick.getHighPrice()).thenReturn(DEFAULT_PRICE);
        when(newCandlestick.getLowPrice()).thenReturn(DEFAULT_PRICE);
        when(newCandlestick.getClosePrice()).thenReturn(DEFAULT_PRICE);
        return newCandlestick;
    }

    private void setMockCandleTime(ZonedDateTime time) {
        when(mockCandle.getDateTime()).thenReturn(time);
        when(mockCandle.isComplete()).thenReturn(true);
    }
}
