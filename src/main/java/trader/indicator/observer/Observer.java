package trader.indicator.observer;

import com.oanda.v20.primitives.DateTime;
import trader.price.Pricing;

import java.math.BigDecimal;

public interface Observer {

    default void updateObserver(DateTime dateTime, BigDecimal ask, BigDecimal bid){};
    void updateObserver(Pricing price);
}
