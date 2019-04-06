package trader.broker.connector.oanda;

import com.oanda.v20.Context;
import com.oanda.v20.ExecuteException;
import com.oanda.v20.RequestException;
import com.oanda.v20.account.*;
import trader.broker.connector.BrokerConnector;
import trader.exception.AccountBalanceBelowMinimum;
import trader.exception.AccountDoNotExistException;
import trader.exception.NullArgumentException;
import trader.exception.UnableToExecuteRequest;

import java.util.List;

public class OandaAccountValidator {

    private static final double MIN_BALANCE = 1.0D;

    public OandaAccountValidator() {}

    public void validateAccount(BrokerConnector connector, Context context) {
        validateInput(connector, context);
        AccountID accountId = new AccountID(connector.getAccountID());
        for (AccountProperties account : extractAccounts(context)) {
            if (account.getId().equals(accountId))
                return;
        }
        throw new AccountDoNotExistException();
    }


    public void validateAccountBalance(BrokerConnector connector, Context context) {
        validateInput(connector, context);
        if (isBalanceBelowMinimum(connector, context))
            throw new AccountBalanceBelowMinimum();
    }

    private void validateInput(BrokerConnector connector, Context context) {
        if(connector == null || context == null)
            throw new NullArgumentException();
    }

    private List<AccountProperties> extractAccounts(Context context) {
        try {
            AccountListResponse response = getAccountContext(context).list();
            return response.getAccounts();
        } catch (NullPointerException | RequestException | ExecuteException e) {
            throw new UnableToExecuteRequest();
        }
    }

    private Account getAccount(BrokerConnector connector, Context context) {
        try {
            AccountID accountId = new AccountID(connector.getAccountID());
            return  getAccountContext(context).get(accountId).getAccount();
        } catch (NullPointerException | RequestException | ExecuteException e) {
            throw new UnableToExecuteRequest();
        }
    }

    private AccountContext getAccountContext(Context context) {
        return context.account;
    }

    private boolean isBalanceBelowMinimum(BrokerConnector connector, Context context) {
        return getAccount(connector, context).getBalance().doubleValue() <= MIN_BALANCE;
    }
}