package trader.controller;

import com.oanda.v20.primitives.DateTime;
import trader.price.Price;

import java.math.BigDecimal;

public interface Observer {

    default void updateObserver(DateTime dateTime, BigDecimal ask, BigDecimal bid){};
    void updateObserver(Price price);
}
