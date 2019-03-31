package trader.strategy;

import trader.indicator.observer.Observer;
import trader.price.Pricing;

public interface Observable {

    void registerObserver(Observer observer);
    void unregisterObserver(Observer observer);
 //   void notifyObservers(DateTime dateTime,BigDecimal ask, BigDecimal bid);
    void notifyObservers(Pricing price);
    void execute();

}
