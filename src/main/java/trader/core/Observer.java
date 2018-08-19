package trader.core;

import com.oanda.v20.primitives.DateTime;

import java.math.BigDecimal;

public interface Observer {

    void updateObserver(DateTime dateTime, BigDecimal ask, BigDecimal bid);

}
