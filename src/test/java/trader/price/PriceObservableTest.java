package trader.price;

import org.junit.*;
import trader.CommonTestClassMembers;
import trader.strategy.PriceConnector;
import trader.controller.Observer;
import trader.exception.NullArgumentException;
import trader.strategy.observable.PriceObservable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PriceObservableTest {

    private PriceObservable priceObservable;
    private Observer observer;
    private CommonTestClassMembers commonMembers;
    private PriceConnector mockPriceConnector;


    @Before
    public void before() {
        observer = mock(Observer.class);
        mockPriceConnector = mock(PriceConnector.class);
        priceObservable = PriceObservable.create(mockPriceConnector);
        commonMembers = new CommonTestClassMembers();
    }

    @Test
    public void WhenCreateThenNewPriceObservable_DifferentObjectcs() {
        PriceObservable priceObservable2 = PriceObservable.create(mockPriceConnector);

        assertNotSame(priceObservable, priceObservable2);
    }

    @Test
    public void WhenCreateNewObjectThenFieldsMustNotBeNull(){
        Object priceConnector = commonMembers.extractFieldObject(priceObservable, "priceConnector");
        Object oldPrice = commonMembers.extractFieldObject(priceObservable, "oldPrice");
        Object observers = commonMembers.extractFieldObject(priceObservable, "observers");

        assertNotNull(priceConnector);
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
    public void WhenCallNotifyEveryoneWithDifferentNotTradablePrice_NoCallsToNotifyObserver() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Pricing mockPrice = mock(Pricing.class);
        when(mockPrice.isTradable()).thenReturn(false);
        Method notifyEveryone = commonMembers.getPrivateMethodForTest(priceObservable,"notifyEveryone", Pricing.class);
        notifyEveryone.invoke(priceObservable, mockPrice);
        Pricing oldPrice = (Pricing) commonMembers.extractFieldObject(priceObservable, "oldPrice");

        assertNotEquals(oldPrice, mockPrice);
    }

    @Test
    public void WhenCallNotifyEveryoneWithTradablePriceEqualToTheOldOne_NoCallsToNotifyObservers() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Pricing expected = (Pricing) commonMembers.extractFieldObject(priceObservable, "oldPrice");
        Method notifyEveryone = commonMembers.getPrivateMethodForTest(priceObservable,"notifyEveryone", Pricing.class);
        notifyEveryone.invoke(priceObservable, expected);
        Pricing oldPrice = (Pricing) commonMembers.extractFieldObject(priceObservable, "oldPrice");

        assertEquals(expected, oldPrice);
    }

    @Test
    public void WhenCallNotifyEveryoneWithDifferentTradablePrice_CallsNotifyObservers() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

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
        Pricing mockPrice = mock(Pricing.class);
        when(mockPrice.isTradable()).thenReturn(true);
        when(mockPriceConnector.getPrice()).thenReturn(mockPrice);
        commonMembers.changeFieldObject(priceObservable,"threadSleepInterval",-1L);
        priceObservable.execute();
    }

    @SuppressWarnings(value = "unchecked")
    private CopyOnWriteArrayList<Observer> getObservers() {
        return (CopyOnWriteArrayList<Observer>) commonMembers.extractFieldObject(this.priceObservable, "observers");
    }

    private class MockedObserverException extends RuntimeException{};

}