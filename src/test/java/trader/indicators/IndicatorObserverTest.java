package trader.indicators;

import com.oanda.v20.primitives.DateTime;
import org.junit.Before;
import org.junit.Test;
import trader.OandaAPI.OandaContextMock;
import trader.core.Observer;


import java.lang.reflect.Field;
import java.math.BigDecimal;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class IndicatorObserverTest {

    private Observer observer;
    private OandaContextMock oandaContextMock;
    private Indicator mockMA;
    private BigDecimal ask;
    private BigDecimal bid;

    @Before
    public void before(){

        this.ask = BigDecimal.ONE;
        this.bid = BigDecimal.TEN;

        this.mockMA = mock(Indicator.class);
        this.oandaContextMock = new OandaContextMock();
        this.observer = IndicatorObserver.create(this.mockMA);
    }

    @Test
    public void WhenCreateThenReturnNewObject() {
        Observer observer2 = IndicatorObserver.create(this.mockMA);

        assertNotSame(observer, observer2);
    }

    @Test(expected = NullPointerException.class)
    public void WhenUpdateObserverWithNullDateTimeThenException() {
        observer.updateObserver(null, this.ask, this.bid);

    }

    @Test
    public void WhenUpdateObserverWithNotNullDateTimeThenCorrectResult() throws NoSuchFieldException, IllegalAccessException {


        DateTime dt = new DateTime("2018-07-29T18:46:19Z");
        Indicator mockMA = mock(Indicator.class);
        doThrow(new RuntimeException("Update OK")).when(mockMA).update(dt, this.ask, this.bid);

        Field field = this.observer.getClass().getDeclaredField("indicator");
        field.setAccessible(true);
        field.set(this.observer, mockMA);

        try{
            observer.updateObserver(dt, this.ask, this.bid);
        }catch (RuntimeException re) {
            assertEquals("Update OK", re.getMessage());
        }
    }
}