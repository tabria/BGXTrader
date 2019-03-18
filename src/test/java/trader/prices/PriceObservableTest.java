package trader.prices;

import org.junit.*;
import trader.CommonTestClassMembers;
import trader.OandaAPIMock.OandaAPIMock;
import trader.core.Observer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.ZonedDateTime;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
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
        priceObservable = PriceObservable.create(oandaAPIMock.getContext());
        commonMembers = new CommonTestClassMembers();
    }

    @Test(expected = PriceObservable.NullArgumentException.class)
    public void WhenTryToRegisterNullObserver_Exception() {
        this.priceObservable.registerObserver(null);
    }

    @Test
    public void testRegisteringCorrectObserver() {
        this.priceObservable.registerObserver(this.observer);
        CopyOnWriteArrayList<Observer> observers = getObservers();

        assertEquals( 1, observers.size());
    }

    @Test
    public void WhenTryToUnregisterNullObserverThenRemoveNothing() {
        this.priceObservable.registerObserver(this.observer);
        this.priceObservable.unregisterObserver(null);
        CopyOnWriteArrayList<Observer> observers = getObservers();

         assertEquals( 1, observers.size());
    }

    @Test
    public void WhenTryToUnregisterCorrectObserverThenRemoveObserver(){
        this.priceObservable.registerObserver(this.observer);
        this.priceObservable.unregisterObserver(this.observer);
        CopyOnWriteArrayList<Observer> observers = getObservers();

        assertEquals( 0, observers.size());
    }

    @Test
    public void WhenTryToUnregisterFromEmptyCollectionThenRemoveNothing(){
        this.priceObservable.unregisterObserver(this.observer);
        CopyOnWriteArrayList<Observer> observers = getObservers();

        assertEquals(0, observers.size());
    }

    @Test
    public void WhenCreateThenNewPriceObservable_DifferentObjectcs() {
        PriceObservable priceObservable2 = PriceObservable.create(oandaAPIMock.getContext());

        assertNotSame(priceObservable, priceObservable2);
    }

    @Test(expected = MockedObserverException.class)
    public void WhenCallNotifyObserversThenObserversAreUpdated() {
        doThrow(new MockedObserverException())
                .when(observer)
                .updateObserver(oandaAPIMock.getMockDateTime(), CommonTestClassMembers.ASK, CommonTestClassMembers.BID);
        priceObservable.registerObserver(observer);
        priceObservable.notifyObservers(oandaAPIMock.getMockDateTime(), CommonTestClassMembers.ASK, CommonTestClassMembers.BID);
    }


    @Test
    public void testNotifyEveryoneWithNotTradableNotEqualNewPrice() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Pricing mockPrice = mock(Pricing.class);
        when(mockPrice.isTradable()).thenReturn(false);
        Method notifyEveryone = getPrivateMethodForTest(priceObservable,"notifyEveryone", Pricing.class);
        notifyEveryone.invoke(priceObservable, mockPrice);
        Pricing oldPrice = (Pricing) commonMembers.extractFieldObject(priceObservable, "oldPrice");

        assertNotEquals(oldPrice, mockPrice);
    }

    @Test
    public void testNotifyEveryoneWithTradableEqualNewPrice() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Pricing expected = (Pricing) commonMembers.extractFieldObject(priceObservable, "oldPrice");
        Method notifyEveryone = getPrivateMethodForTest(priceObservable,"notifyEveryone", Pricing.class);
        notifyEveryone.invoke(priceObservable, expected);
        Pricing oldPrice = (Pricing) commonMembers.extractFieldObject(priceObservable, "oldPrice");

        assertEquals(expected, oldPrice);
    }

    @Test
    public void testNotifyEveryoneWithTradableNotEqualNewPrice() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Pricing expected = mock(Pricing.class);
        when(expected.isTradable()).thenReturn(true);
        Method notifyEveryone = getPrivateMethodForTest(priceObservable,"notifyEveryone", Pricing.class);
        Pricing oldPrice = (Pricing) commonMembers.extractFieldObject(priceObservable, "oldPrice");
        notifyEveryone.invoke(priceObservable, expected);
        Pricing changedOldPrice = (Pricing) commonMembers.extractFieldObject(priceObservable, "oldPrice");

        assertNotEquals(oldPrice, changedOldPrice);
        assertEquals(expected, changedOldPrice);
    }


//    @Test
//    public void testNotifyEveryone() throws NoSuchMethodException {
//        PriceObservable spyObservable = spy(PriceObservable.class);
//        Pricing mockPrice = mock(Pricing.class);
//        when(mockPrice.isTradable()).thenReturn(true);
//
//        Method notifyEveryone = PriceObservable.class.getDeclaredMethod("notifyEveryone", Pricing.class);
//        notifyEveryone.setAccessible(true);
//        notifyEveryone.invoke(spyObservable,)
//
//    }

//    @Test
//    public void testExecute(){
//
//        priceObservable.execute();
//    }


    private Method getPrivateMethodForTest(Object object, String methodName, Class<?>...params) throws NoSuchMethodException {
        Method notifyEveryone = object.getClass().getDeclaredMethod(methodName, params);
        notifyEveryone.setAccessible(true);
        return notifyEveryone;
    }

    private CopyOnWriteArrayList<Observer> getObservers() {
        return (CopyOnWriteArrayList<Observer>) commonMembers.extractFieldObject(this.priceObservable, "observers");
    }

    private class MockedObserverException extends RuntimeException{};

}