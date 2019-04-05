package trader.broker.connector.oanda;

import com.oanda.v20.ExecuteException;
import com.oanda.v20.RequestException;
import com.oanda.v20.account.*;
import trader.exception.AccountBalanceBelowMinimum;
import trader.exception.AccountDoNotExistException;
import trader.exception.UnableToExecuteRequest;

import java.util.List;

public class OandaAccountValidator {

    private static final double MIN_BALANCE = 1.0D;

    public OandaAccountValidator() {}

    public void validateAccount(OandaConnector connector) {
        AccountID accountId = new AccountID(connector.getAccountID());
        for (AccountProperties account : extractAccounts(connector)) {
            if (account.getId().equals(accountId))
                return;
        }
        throw new AccountDoNotExistException();
    }

    public void validateAccountBalance(OandaConnector connector) {
        if (isBalanceBelowMinimum(connector))
            throw new AccountBalanceBelowMinimum();
    }

    private List<AccountProperties> extractAccounts(OandaConnector connector) {
        try {
            AccountListResponse response = getAccountContext(connector).list();
            return response.getAccounts();
        } catch (NullPointerException | RequestException | ExecuteException e) {
            throw new UnableToExecuteRequest();
        }
    }

    private Account getAccount(OandaConnector connector) {
        try {
            AccountID accountId = new AccountID(connector.getAccountID());
            return  getAccountContext(connector).get(accountId).getAccount();
        } catch (NullPointerException | RequestException | ExecuteException e) {
            throw new UnableToExecuteRequest();
        }
    }

    private AccountContext getAccountContext(OandaConnector connector) {
        return connector.getContext().account;
    }

    private boolean isBalanceBelowMinimum(OandaConnector connector) {
        return getAccount(connector).getBalance().doubleValue() <= MIN_BALANCE;
    }

//    private void serverDown(RequestException re) {
//        if (re.getStatus() == 504 || re.getStatus() == 503){
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                throw  new RuntimeException(e);
//            }
//        }
//    }
}