package trader.price;

import org.junit.*;
import trader.CommonTestClassMembers;
import trader.OandaAPIMock.OandaAPIMock;
import trader.core.Observer;
import trader.exception.NullArgumentException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PriceObservableTest {

    OandaAPIMock oandaAPIMock;
    PriceObservable priceObservable;
    Observer observer;
    CommonTestClassMembers commonMembers;


    @Before
    public void before() throws Exception {

        oandaAPIMock = new OandaAPIMock();
        observer = mock(Observer.class);
        priceObservable = PriceObservable.create("Oanda");
        commonMembers = new CommonTestClassMembers();
    }

    @Test
    public void WhenCreateThenNewPriceObservable_DifferentObjectcs() {
        PriceObservable priceObservable2 = PriceObservable.create("Oanda");

        assertNotSame(priceObservable, priceObservable2);
    }

    @Test
    public void WhenCreateThenNotNullFieldMembers(){
        Object apiConnector = commonMembers.extractFieldObject(priceObservable, "apiConnector");
        Object oldPrice = commonMembers.extractFieldObject(priceObservable, "oldPrice");
        Object observers = commonMembers.extractFieldObject(priceObservable, "observers");

        assertNotNull(apiConnector);
        assertNotNull(oldPrice);
        assertNotNull(observers);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenTryToRegisterNullObserver_Exception() {
        priceObservable.registerObserver(null);
    }

    @Test
    public void testRegisteringCorrectObserver() {
        priceObservable.registerObserver(observer);
        CopyOnWriteArrayList<Observer> observers = getObservers();

        assertEquals( 1, observers.size());
    }

    @Test
    public void WhenTryToUnregisterNullObserverThenRemoveNothing() {
        priceObservable.registerObserver(this.observer);
        priceObservable.unregisterObserver(null);
        CopyOnWriteArrayList<Observer> observers = getObservers();

         assertEquals( 1, observers.size());
    }

    @Test
    public void WhenTryToUnregisterCorrectObserverThenRemoveObserver(){
        priceObservable.registerObserver(this.observer);
        priceObservable.unregisterObserver(this.observer);
        CopyOnWriteArrayList<Observer> observers = getObservers();

        assertEquals( 0, observers.size());
    }

    @Test
    public void WhenTryToUnregisterFromEmptyCollectionThenRemoveNothing(){
        priceObservable.unregisterObserver(this.observer);
        CopyOnWriteArrayList<Observer> observers = getObservers();

        assertEquals(0, observers.size());
    }

    @Test(expected = MockedObserverException.class)
    public void WhenCallNotifyObserversThenObserversAreUpdated() {
        Pricing mockPrice = mock(Pricing.class);
        doThrow(new MockedObserverException())
                .when(observer).updateObserver(mockPrice);
        priceObservable.registerObserver(observer);
        priceObservable.notifyObservers(mockPrice);
    }


    @Test
    public void testNotifyEveryoneWithNotTradableNotEqualNewPrice() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Pricing mockPrice = mock(Pricing.class);
        when(mockPrice.isTradable()).thenReturn(false);
        Method notifyEveryone = commonMembers.getPrivateMethodForTest(priceObservable,"notifyEveryone", Pricing.class);
        notifyEveryone.invoke(priceObservable, mockPrice);
        Pricing oldPrice = (Pricing) commonMembers.extractFieldObject(priceObservable, "oldPrice");

        assertNotEquals(oldPrice, mockPrice);
    }

    @Test
    public void testNotifyEveryoneWithTradableEqualNewPrice() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Pricing expected = (Pricing) commonMembers.extractFieldObject(priceObservable, "oldPrice");
        Method notifyEveryone = commonMembers.getPrivateMethodForTest(priceObservable,"notifyEveryone", Pricing.class);
        notifyEveryone.invoke(priceObservable, expected);
        Pricing oldPrice = (Pricing) commonMembers.extractFieldObject(priceObservable, "oldPrice");

        assertEquals(expected, oldPrice);
    }

    @Test
    public void testNotifyEveryoneWithTradableNotEqualNewPrice() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Pricing expected = mock(Pricing.class);
        when(expected.isTradable()).thenReturn(true);
        Method notifyEveryone = commonMembers.getPrivateMethodForTest(priceObservable,"notifyEveryone", Pricing.class);
        Pricing oldPrice = (Pricing) commonMembers.extractFieldObject(priceObservable, "oldPrice");
        notifyEveryone.invoke(priceObservable, expected);
        Pricing changedOldPrice = (Pricing) commonMembers.extractFieldObject(priceObservable, "oldPrice");

        assertNotEquals(oldPrice, changedOldPrice);
        assertEquals(expected, changedOldPrice);
    }

    @Test
    public void testSleepThreadForCorrectSleepInterval() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        long expected = 1234L;
        Method sleepThread = commonMembers.getPrivateMethodForTest(priceObservable, "sleepThread", long.class);
        long startTime = System.currentTimeMillis();
        sleepThread.invoke(priceObservable, expected);
        long endTime = System.currentTimeMillis();

        assertTrue(expected <= endTime - startTime);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExecute() {
        commonMembers.changeFieldObject(priceObservable,"threadSleepInterval",-1L);
        priceObservable.execute();
    }

    private CopyOnWriteArrayList<Observer> getObservers() {
        return (CopyOnWriteArrayList<Observer>) commonMembers.extractFieldObject(this.priceObservable, "observers");
    }

    private class MockedObserverException extends RuntimeException{};

}