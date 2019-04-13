package trader.entity.price;

import org.junit.*;
import trader.CommonTestClassMembers;
import trader.broker.BrokerGateway;
import trader.configuration.TradingStrategyConfiguration;
import trader.exception.BadRequestException;
import trader.observer.Observer;
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
    private BrokerGateway mockBrokerGateway;
    private TradingStrategyConfiguration configurationMock;
    private Price mockPrice;


    @Before
    public void before() {
        observer = mock(Observer.class);
        mockBrokerGateway = mock(BrokerGateway.class);
        configurationMock = mock(TradingStrategyConfiguration.class);
        priceObservable = PriceObservable.create(mockBrokerGateway, configurationMock);
        commonMembers = new CommonTestClassMembers();
        mockPrice = mock(Price.class);
    }

    @Test
    public void WhenCreateThenNewPriceObservable_DifferentObjectcs() {
        PriceObservable priceObservable2 = PriceObservable.create(mockBrokerGateway, configurationMock);

        assertNotSame(priceObservable, priceObservable2);
    }

    @Test
    public void WhenCreateNewObjectThenFieldsMustNotBeNull(){
        Object priceConnector = commonMembers.extractFieldObject(priceObservable, "brokerGateway");
        Object oldPrice = commonMembers.extractFieldObject(priceObservable, "oldPrice");
        Object observers = commonMembers.extractFieldObject(priceObservable, "observers");
        Object configuration = commonMembers.extractFieldObject(priceObservable, "configuration");

        assertNotNull(priceConnector);
        assertNotNull(oldPrice);
        assertNotNull(observers);
        assertNotNull(configuration);
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
        Price mockPrice = mock(Price.class);
        doThrow(new MockedObserverException())
                .when(observer).updateObserver(mockPrice);
        priceObservable.registerObserver(observer);
        priceObservable.notifyObservers(mockPrice);
    }

    @Test
    public void WhenCallNotifyEveryoneWithDifferentNoNTradablePrice_NoCallToNotifyObserver() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        when(mockPrice.isTradable()).thenReturn(false);
        Method notifyEveryone = extractNotify(priceObservable);
        notifyEveryone.invoke(priceObservable, mockPrice);
        Price oldPrice = extractPrice(priceObservable);

        assertNotEquals(oldPrice, mockPrice);
    }

    @Test
    public void WhenCallNotifyEveryoneWithTradablePriceEqualToTheOldOne_NoCallToNotifyObservers() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        PriceObservable spyObservable = spy(priceObservable);
        Price expected = extractPrice(spyObservable);
        doThrow(BadRequestException.class).when(spyObservable).notifyObservers(expected);
        Method notifyEveryone = extractNotify(spyObservable);
        notifyEveryone.invoke(spyObservable, expected);
        Price oldPrice = extractPrice(spyObservable);

        assertEquals(expected, oldPrice);
    }

    @Test
    public void WhenCallNotifyEveryoneWithDifferentTradablePrice_CallNotifyObservers() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        when(mockPrice.isTradable()).thenReturn(true);
        Price oldPrice = extractPrice(priceObservable);
        Method notifyEveryone = extractNotify(priceObservable);
        notifyEveryone.invoke(priceObservable, mockPrice);
        Price newPrice = extractPrice(priceObservable);

        assertNotEquals(oldPrice, newPrice);
        assertEquals(mockPrice, newPrice);
    }

    @Test
    public void testSleepThreadForCorrectSleepInterval() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        long expected = 100L;

        assertTrue(expected <= elapsedTime(expected));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExecuteForCorrectActions() {
        String instrument = "EUR_USD";
        when(mockPrice.isTradable()).thenReturn(true);
        when(configurationMock.getInstrument()).thenReturn(instrument);
        when(mockBrokerGateway.getPrice(instrument)).thenReturn(mockPrice);
        commonMembers.changeFieldObject(priceObservable,"threadSleepInterval",-1L);
        priceObservable.execute();
    }

    private Method extractNotify(PriceObservable observable) throws NoSuchMethodException {
        return commonMembers.getPrivateMethodForTest(observable,"notifyEveryone", Price.class);
    }

    private Price extractPrice(PriceObservable spyObservable) {
        return (Price) commonMembers.extractFieldObject(spyObservable,
                "oldPrice");
    }

    @SuppressWarnings(value = "unchecked")
    private CopyOnWriteArrayList<Observer> getObservers() {
        return (CopyOnWriteArrayList<Observer>) commonMembers.extractFieldObject(this.priceObservable, "observers");
    }

    private long elapsedTime(long expected) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method sleepThread = commonMembers.getPrivateMethodForTest(priceObservable, "sleepThread", long.class);
        long startTime = System.currentTimeMillis();
        sleepThread.invoke(priceObservable, expected);
        return System.currentTimeMillis() - startTime;
    }


    private class MockedObserverException extends RuntimeException{};

}