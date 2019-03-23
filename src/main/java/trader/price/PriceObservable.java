package trader.price;

import com.oanda.v20.Context;
import com.oanda.v20.primitives.DateTime;
import trader.connector.BaseConnector;
import trader.connector.PriceConnector;
import trader.core.Observable;
import trader.core.Observer;
import trader.exception.NullArgumentException;

import java.math.BigDecimal;
import java.util.concurrent.CopyOnWriteArrayList;


public final class PriceObservable implements Observable, PriceConnector {

    private long threadSleepInterval = 1000L;
    private BaseConnector baseConnector;
    private Pricing oldPrice;
    private CopyOnWriteArrayList<Observer> observers;

    ////////////////// to be removed /////////////////////////
    private PriceObservable(Context context){
        baseConnector =  BaseConnector.create("Oanda");
        oldPrice = new Price.PriceBuilder().build();
        observers = new CopyOnWriteArrayList<>();
    }

    public static PriceObservable create(Context context){
        return new PriceObservable(context);

    }

    @Override
    public void notifyObservers(DateTime dateTime, BigDecimal ask, BigDecimal bid) {
        for (Observer observer : this.observers)
            observer.updateObserver(dateTime, ask, bid);
    }

    @Override
    public Price getPrice(){
        return null;
    }

    ////////////////////////////////////////////////////////////

    private PriceObservable(String apiConnectorName){
        baseConnector = BaseConnector.create(apiConnectorName);
        oldPrice = new Price.PriceBuilder().build();
        observers = new CopyOnWriteArrayList<>();
    }

    public static PriceObservable create(String apiConnectorName){
        return new PriceObservable(apiConnectorName);

    }

    @Override
    public void registerObserver(Observer observer) {
        if (observer == null)
            throw new NullArgumentException();
        this.observers.add(observer);
    }

    @Override
    public void unregisterObserver(Observer observer) {
        if (observer != null)
            this.observers.remove(observer);
    }

    @Override
    public void notifyObservers(Pricing price) {
        for (Observer observer : this.observers)
            observer.updateObserver(price);
    }

    @Override
    public void execute() {
        while(true){
            Pricing newPrice = baseConnector.getPrice();
            notifyEveryone(newPrice);
            sleepThread(threadSleepInterval);
        }
    }

    private void notifyEveryone(Pricing newPrice) {
        if (newPrice.isTradable() && !newPrice.equals(oldPrice)) {
            oldPrice = newPrice;
            this.notifyObservers(newPrice);
        }
    }

    private void sleepThread(long sleepInterval){
        try {
            Thread.sleep(sleepInterval);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
