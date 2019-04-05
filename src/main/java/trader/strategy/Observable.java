package trader.strategy;

import trader.controller.Observer;
import trader.price.Price;

public interface Observable {

    void registerObserver(Observer observer);
    void unregisterObserver(Observer observer);
 //   void notifyObservers(DateTime dateTime,BigDecimal ask, BigDecimal bid);
    void notifyObservers(Price price);
    void execute();

}
