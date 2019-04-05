package trader.controller;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import trader.entity.indicator.Indicator;
import trader.exception.NullArgumentException;
import trader.price.Price;

import java.lang.reflect.Field;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


public class IndicatorObserverTest {

    private static final String INDICATOR = "indicator";

    private Observer mockObserver;
    private Indicator mockMA;
    private UpdateIndicatorController mockController;

    @Before
    public void before(){

        this.mockMA = mock(Indicator.class);
        this.mockController = mock(UpdateIndicatorController.class);
        this.mockObserver = IndicatorObserver.create(this.mockMA, mockController);
    }

    @Test(expected = NullArgumentException.class)
    public void whenCreateIndicatorObserverWithNullIndicator_ThrowException(){
        IndicatorObserver.create(null, mockController);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreateIndicatorObserverWithNullUpdateIndicatorController_Exception(){
        IndicatorObserver.create(mockMA, null);
    }

    @Test
    public void whenCreateNewIndicatorObserver_IndicatorsMustMatch() throws NoSuchFieldException, IllegalAccessException {
        Observer indicatorObserver = IndicatorObserver.create(this.mockMA, mockController);
        assertSame(mockMA, extractIndicator(indicatorObserver));
    }

    @Test(expected = NullArgumentException.class)
    public void callUpdateObserverWithNullPrice_ThrowException() {
        mockObserver.updateObserver(null);
    }

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testUpdateObserverForCorrectExecution() {
        Price mockPrice = mock(Price.class);
        exception.expect(RuntimeException.class);
        exception.expectMessage("Update OK");

        doThrow(new RuntimeException("Update OK")).when(mockMA).updateIndicator();
        mockObserver.updateObserver(mockPrice);
    }

    private Indicator extractIndicator(Observer indicatorObserver) throws NoSuchFieldException, IllegalAccessException {
        Field indicatorField = indicatorObserver.getClass().getDeclaredField(INDICATOR);
        indicatorField.setAccessible(true);
        return (Indicator) indicatorField.get(indicatorObserver);
    }
}