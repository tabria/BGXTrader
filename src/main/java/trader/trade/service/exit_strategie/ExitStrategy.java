package trader.trade.service.exit_strategie;

import com.oanda.v20.account.Account;
import com.oanda.v20.primitives.DateTime;

import java.math.BigDecimal;

public interface ExitStrategy {

    void execute(Account account, BigDecimal ask, BigDecimal bid, DateTime dateTime);

    default void execute(){};
}
