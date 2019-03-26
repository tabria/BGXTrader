package trader.indicator;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import trader.core.Observer;
import trader.exception.NullArgumentException;
import trader.price.Pricing;
import java.lang.reflect.Field;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


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

    @Test(expected = NullArgumentException.class)
    public void callUpdateObserverWithNullPrice_ThrowException() {
        mockObserver.updateObserver(null);
    }

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testUpdateObserver() {
        Pricing mockPricing = mock(Pricing.class);
        exception.expect(RuntimeException.class);
        exception.expectMessage("Update OK");

        doThrow(new RuntimeException("Update OK")).when(mockMA).updateIndicator();
        mockObserver.updateObserver(mockPricing);
    }

    private Indicator extractIndicator(Observer indicatorObserver) throws NoSuchFieldException, IllegalAccessException {
        Field indicatorField = indicatorObserver.getClass().getDeclaredField(INDICATOR);
        indicatorField.setAccessible(true);
        return (Indicator) indicatorField.get(indicatorObserver);
    }
}