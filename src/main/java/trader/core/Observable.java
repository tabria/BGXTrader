package trader.core;

import com.oanda.v20.primitives.DateTime;

import java.math.BigDecimal;

public interface Observable {

    void registerObserver(Observer observer);
    void unregisterObserver(Observer observer);
    void notifyObservers(DateTime dateTime,BigDecimal ask, BigDecimal bid);
    void execute();

}
