package trader.strategy.observable;

import trader.broker.BrokerGateway;
import trader.configuration.TradingStrategyConfiguration;
import trader.observer.Observer;
import trader.exception.NullArgumentException;
import trader.price.Price;
import trader.price.PriceImpl;
import trader.strategy.Observable;

import java.util.concurrent.CopyOnWriteArrayList;


public final class PriceObservable implements Observable {

    private long threadSleepInterval = 1000L;
    private BrokerGateway brokerGateway;
    private Price oldPrice;
    private CopyOnWriteArrayList<Observer> observers;
    private TradingStrategyConfiguration configuration;

    private PriceObservable(BrokerGateway brokerGateway, TradingStrategyConfiguration configuration){
        this.brokerGateway = brokerGateway;
        oldPrice = new PriceImpl.PriceBuilder().build();
        observers = new CopyOnWriteArrayList<>();
        this.configuration = configuration;
    }

    public static PriceObservable create(BrokerGateway brokerGateway, TradingStrategyConfiguration configuration){
        return new PriceObservable(brokerGateway, configuration);
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
    public void notifyObservers(Price price) {
        for (Observer observer : this.observers)
            observer.updateObserver(price);
    }

    @Override
    public void execute() {
        while(true){
            Price newPrice = brokerGateway.getPrice(configuration.getInstrument());
            notifyEveryone(newPrice);
            sleepThread(threadSleepInterval);
        }
    }

    private void notifyEveryone(Price newPrice) {
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
