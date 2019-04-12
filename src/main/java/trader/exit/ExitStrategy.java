package trader.exit;

import com.oanda.v20.account.Account;
import com.oanda.v20.primitives.DateTime;
import trader.price.Price;

import java.math.BigDecimal;

public interface ExitStrategy {

    void execute(Account account, BigDecimal ask, BigDecimal bid, DateTime dateTime);

    void execute(Price price);

    default void execute(){};
}
