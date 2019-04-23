package trader.strategy;

import trader.observer.Observer;
import trader.entity.price.Price;

public interface Observable {

    void registerObserver(Observer observer);
    void unregisterObserver(Observer observer);
    void notifyObservers(Price price);
    void execute();

}
