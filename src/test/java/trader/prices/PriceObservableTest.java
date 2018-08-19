package trader.prices;

import com.oanda.v20.Context;
import com.oanda.v20.pricing.ClientPrice;
import com.oanda.v20.pricing.PricingContext;
import com.oanda.v20.pricing.PricingGetRequest;
import com.oanda.v20.pricing.PricingGetResponse;
import com.oanda.v20.pricing_common.PriceBucket;
import com.oanda.v20.pricing_common.PriceValue;
import com.oanda.v20.primitives.DateTime;
import org.junit.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import trader.core.Observer;
import trader.indicators.Indicator;
import trader.indicators.IndicatorObserver;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PriceObservableTest {


    private static final double DEFAULT_ASK_PRICE = 1.1779;
    private static final double DEFAULT_BID_PRICE = 1.1121;

    private Context mockContext;
    private PricingGetRequest mockRequest;
    private PriceObservable observable;
    private Indicator mockMA;
    private Observer observer;
    private List<ClientPrice> clientPriceList;
    private BigDecimal ask;
    private BigDecimal bid;
    private DateTime mockDateTime;


    @Before
    public void before() throws Exception {

        this.ask = BigDecimal.ONE;
        this.bid = BigDecimal.TEN;

        this.clientPriceList = new ArrayList<>();
        this.mockRequest = mock(PricingGetRequest.class);

        this.mockDateTime = mock(DateTime.class);

        PricingGetResponse spyResponse = spy(createPricingResponse());
        when(spyResponse.getPrices()).thenReturn(this.clientPriceList);
        when(spyResponse.getTime()).thenReturn(this.mockDateTime);

        this.mockContext = mock(Context.class);
        this.mockContext.pricing = mock(PricingContext.class);
        when(this.mockContext.pricing.get(this.mockRequest)).thenReturn(spyResponse);

        this.observable = PriceObservable.create(mockContext);
        this.setPriceRequest();

        this.mockMA = mock(Indicator.class);
        this.observer = IndicatorObserver.create(this.mockMA);
    }

    /**
     * Test create method
     */
    @Test
    public void WhenCallCreateThenNewPriceObservable() {

        PriceObservable po1 = PriceObservable.create(this.mockContext);
        PriceObservable po2 = PriceObservable.create(this.mockContext);

        assertNotEquals(po1, po2);
    }

    @Test
    public void WhenTryToRegisterNullObserverThenNothingToAddToTheSet() throws NoSuchFieldException, IllegalAccessException {
        this.observable.registerObserver(null);
        Field observableField = this.observable.getClass().getDeclaredField("observers");
        observableField.setAccessible(true);

        //cast is ok, because the observers field is of the same type
        @SuppressWarnings("unchecked")
        CopyOnWriteArrayList<Observer> observers = (CopyOnWriteArrayList<Observer>) observableField.get(this.observable);

        int resultSize = observers.size();

        assertEquals("Error null observer has been added", 0, resultSize);

    }

    @Test
    public void WhenRegisterCorrectObserverThenAddToTheSet() throws NoSuchFieldException, IllegalAccessException {
        this.observable.registerObserver(this.observer);

        Field observableField = this.observable.getClass().getDeclaredField("observers");
        observableField.setAccessible(true);

        //cast is ok, because the observers field is of the same type
        @SuppressWarnings("unchecked")
        CopyOnWriteArrayList<Observer> observers = (CopyOnWriteArrayList<Observer>) observableField.get(this.observable);

        int resultSize = observers.size();

        assertEquals("Error Observer not added", 1, resultSize);
    }

    @Test
    public void WhenTryToUnregisterNullObserverThenRemoveNothing() throws NoSuchFieldException, IllegalAccessException {
        this.observable.registerObserver(this.observer);
        this.observable.unregisterObserver(null);
        Field observableField = this.observable.getClass().getDeclaredField("observers");
        observableField.setAccessible(true);

        //cast is ok, because the observers field is of the same type
        @SuppressWarnings("unchecked")
        CopyOnWriteArrayList<Observer> observers = (CopyOnWriteArrayList<Observer>) observableField.get(this.observable);

        int resultSize = observers.size();

        assertEquals("Error Null Observer removed", 1, resultSize);

    }

    @Test
    public void WhenTryToUnregisterCorrectObserverThenRemoveObserver() throws NoSuchFieldException, IllegalAccessException {
        this.observable.registerObserver(this.observer);
        this.observable.unregisterObserver(this.observer);
        Field observableField = this.observable.getClass().getDeclaredField("observers");
        observableField.setAccessible(true);

        //cast is ok, because the observers field is of the same type
        @SuppressWarnings("unchecked")
        CopyOnWriteArrayList<Observer> observers = (CopyOnWriteArrayList<Observer>) observableField.get(this.observable);

        int resultSize = observers.size();

        assertEquals("Error Observer not removed", 0, resultSize);

    }

    @Test
    public void WhenTryToUnregisterCorrectObserverFromEmptySetThenRemoveNothing() throws NoSuchFieldException, IllegalAccessException {
        this.observable.unregisterObserver(this.observer);
        Field observableField = this.observable.getClass().getDeclaredField("observers");
        observableField.setAccessible(true);

        //cast is ok, because the observers field is of the same type
        @SuppressWarnings("unchecked")
        CopyOnWriteArrayList<Observer> observers = (CopyOnWriteArrayList<Observer>) observableField.get(this.observable);

        int resultSize = observers.size();

        assertEquals("Error Observer removed from empty set", 0, resultSize);

    }

    @Test
    public void WhenCallNotifyObserversThenObserversAreUpdated() {

        DateTime dt = mock(DateTime.class);
        final String[] result = {"", ""};

        Observer observer1 = mock(IndicatorObserver.class);
        doAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
               // System.out.println("Updated Observer1");
                result[0] = "Observer1";
                return "Observer1";
            }
        }).when(observer1).updateObserver(dt, this.ask, this.bid);

        Observer observer2 = mock(IndicatorObserver.class);
        doAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
               // System.out.println("Updated Observer2");
                result[1] = "Observer2";
                return "Observer2";
            }
        }).when(observer2).updateObserver(dt, this.ask, this.bid);

        this.observable.registerObserver(observer1);
        this.observable.registerObserver(observer2);

        this.observable.notifyObservers(dt, this.ask, this.bid);


        assertEquals("Observer1 is not notified", "Observer1", result[0]);
        assertEquals("Observer2 is not notified", "Observer2", result[1]);

    }

    //updateObserver() throw exception if executed
    @Test
    public void WhenExecuteThenObserversMustBeNotified() {

        ClientPrice spyClientPrice = spy(new ClientPrice());

        PriceBucket spyAskPriceBucket = spy(new PriceBucket());
        PriceBucket spyBidPriceBucket = spy(new PriceBucket());

        PriceValue spyAskPriceValue = spy(new PriceValue(BigDecimal.valueOf(DEFAULT_ASK_PRICE)));
        PriceValue spyBidPriceValue = spy(new PriceValue(BigDecimal.valueOf(DEFAULT_BID_PRICE)));

        spyAskPriceBucket.setPrice(spyAskPriceValue);
        spyBidPriceBucket.setPrice(spyBidPriceValue);

        List<PriceBucket> askPb = new ArrayList<>(Arrays.asList(spyAskPriceBucket));
        List<PriceBucket> bidPb = new ArrayList<>(Arrays.asList(spyBidPriceBucket));

        spyClientPrice.setTradeable(true);
        spyClientPrice.setAsks(askPb);
        spyClientPrice.setBids(bidPb);

        this.clientPriceList.add(spyClientPrice);

        Observer observer1 = spy(IndicatorObserver.create(this.mockMA));
        doThrow(new IllegalArgumentException("From mock observer")).when(observer1).updateObserver(any(DateTime.class), any(BigDecimal.class), any(BigDecimal.class));

        this.observable.registerObserver(observer1);

        try{
            this.observable.execute();
        } catch (IllegalArgumentException ie){
            assertEquals("From mock observer", ie.getMessage());
        }

    }

    //PricingGetResponse class have private constructor. This method create new object using reflection
    private PricingGetResponse createPricingResponse() throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException, InvocationTargetException, InstantiationException {

        Class<?> aClass = Class.forName(com.oanda.v20.pricing.PricingGetResponse.class.getName());

        Constructor<?> objConstructor = aClass.getDeclaredConstructors()[0];
        objConstructor.setAccessible(true);

        return (PricingGetResponse) objConstructor.newInstance();
    }

    //Set request field in PriceObservable object to the mocked one from this.request
    private void setPriceRequest() throws NoSuchFieldException, IllegalAccessException {

        Field request = this.observable.getClass().getDeclaredField("request");
        request.setAccessible(true);
        request.set(this.observable, this.mockRequest);
    }
}