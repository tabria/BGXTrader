package trader.strategy.observable;

import trader.broker.BrokerConnector;
import trader.configuration.TradingStrategyConfiguration;
import trader.controller.Observer;
import trader.exception.NullArgumentException;
import trader.price.Price;
import trader.price.Pricing;
import trader.strategy.Observable;

import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;


public final class PriceObservable implements Observable {

    private long threadSleepInterval = 1000L;
    private BrokerConnector brokerConnector;
    private Pricing oldPrice;
    private CopyOnWriteArrayList<Observer> observers;
    private TradingStrategyConfiguration configuration;

    private PriceObservable(BrokerConnector brokerConnector, TradingStrategyConfiguration configuration){
        this.brokerConnector = brokerConnector;
        oldPrice = new Price.PriceBuilder().build();
        observers = new CopyOnWriteArrayList<>();
        this.configuration = configuration;
    }

    public static PriceObservable create(BrokerConnector brokerConnector, TradingStrategyConfiguration configuration){
        return new PriceObservable(brokerConnector, configuration);
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
            Pricing newPrice = brokerConnector.getPrice(configuration.getInstrument());
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
