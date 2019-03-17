package trader.prices;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.OngoingStubbing;
import trader.core.Observable;

import java.lang.reflect.Field;

import static org.mockito.Mockito.*;

public class PricePullTest {

    Observable observable;

    @Before
    public void setUp() throws Exception {
        observable = mock(PriceObservable.class);
    }

    @Test
    public void testPricePullThreadName() throws NoSuchFieldException {
        String expected = "puller";
        PricePull newPricePull = new PricePull(expected, observable);
        Field thread = newPricePull.getClass().getDeclaredField("thread");

        Assert.assertEquals(expected, thread.getName());

    }

}




