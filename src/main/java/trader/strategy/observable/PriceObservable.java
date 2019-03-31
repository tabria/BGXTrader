package trader.strategy.observable;

import trader.indicator.observer.Observer;
import trader.exception.NullArgumentException;
import trader.price.Price;
import trader.price.Pricing;
import trader.strategy.Observable;
import trader.strategy.PriceConnector;

import java.util.concurrent.CopyOnWriteArrayList;


public final class PriceObservable implements Observable {

    private long threadSleepInterval = 1000L;
    private PriceConnector priceConnector;
    private Pricing oldPrice;
    private CopyOnWriteArrayList<Observer> observers;

    private PriceObservable(PriceConnector priceConnector){
        this.priceConnector = priceConnector;
        oldPrice = new Price.PriceBuilder().build();
        observers = new CopyOnWriteArrayList<>();
    }

    public static PriceObservable create(PriceConnector priceConnector){
        return new PriceObservable(priceConnector);
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
            Pricing newPrice = priceConnector.getPrice();
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
