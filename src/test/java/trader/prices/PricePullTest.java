package trader.prices;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.OngoingStubbing;
import trader.CommonTestClassMembers;
import trader.core.Observable;

import java.lang.reflect.Field;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PricePullTest {

    private String expected = "puller";
    private Observable observable;
    private PricePull pricePull;
    private CommonTestClassMembers commonMembers;

    @Before
    public void setUp() {
        observable = mock(PriceObservable.class);
        pricePull = new PricePull(expected, observable);
        commonMembers = new CommonTestClassMembers();
    }

    @Test
    public void testPricePullThreadName() {
        Thread thread = (Thread) commonMembers.extractFieldObject(pricePull, "thread");

        assertEquals(expected, thread.getName());
    }

    @Test
    public void testIfObservableIsSameObject(){
        Observable pricePullObservable = (Observable) commonMembers.extractFieldObject(pricePull, "observable");

        assertSame(observable, pricePullObservable);
    }

}




