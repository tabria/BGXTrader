package trader.indicator;

import com.oanda.v20.primitives.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import trader.core.Observer;
import java.lang.reflect.Field;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static trader.CommonTestClassMembers.ASK;
import static trader.CommonTestClassMembers.BID;

public class IndicatorObserverTest {

    private static final String INDICATOR = "indicator";

    private Observer mockObserver;
    private Indicator mockMA;

    @Before
    public void before(){

        this.mockMA = mock(Indicator.class);
        this.mockObserver = IndicatorObserver.create(this.mockMA);
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenCreateIndicatorObserverWithNull_ThrowException(){
        IndicatorObserver.create(null);
    }

    @Test
    public void whenCreateNewIndicatorObserver_IndicatorsMustMatch() throws NoSuchFieldException, IllegalAccessException {
        Observer indicatorObserver = IndicatorObserver.create(this.mockMA);
        assertSame(mockMA, extractIndicator(indicatorObserver));
    }

    @Test(expected = NullPointerException.class)
    public void callUpdateObserverWithNullDateTime_ThrowException() {
        mockObserver.updateObserver(null, ASK, BID);
    }

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testUpdateObserver() {

        exception.expect(RuntimeException.class);
        exception.expectMessage("Update OK");

        DateTime lastCandleTime = new DateTime("2018-07-29T18:46:19Z");
        doThrow(new RuntimeException("Update OK")).when(mockMA).updateIndicator();
        mockObserver.updateObserver(lastCandleTime, ASK, BID);
    }

    private Indicator extractIndicator(Observer indicatorObserver) throws NoSuchFieldException, IllegalAccessException {
        Field indicatorField = indicatorObserver.getClass().getDeclaredField(INDICATOR);
        indicatorField.setAccessible(true);
        return (Indicator) indicatorField.get(indicatorObserver);
    }
}