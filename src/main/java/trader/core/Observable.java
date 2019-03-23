package trader.core;

import com.oanda.v20.primitives.DateTime;
import trader.price.Price;
import trader.price.Pricing;

import java.math.BigDecimal;

public interface Observable {

    void registerObserver(Observer observer);
    void unregisterObserver(Observer observer);
    void notifyObservers(DateTime dateTime,BigDecimal ask, BigDecimal bid);
    void notifyObservers(Pricing price);
    void execute();

}
