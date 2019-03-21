package trader.connector.oanda;

import com.oanda.v20.ExecuteException;
import com.oanda.v20.RequestException;
import com.oanda.v20.account.*;
import trader.exception.AccountBalanceBelowMinimum;
import trader.exception.AccountDoNotExistException;
import trader.exception.UnableToExecuteRequest;

import java.util.List;

public class OandaAccountValidator {

    private static final double MIN_BALANCE = 1.0D;

    private OandaConnector oandaConnector;

    public OandaAccountValidator(OandaConnector connector) {
        oandaConnector = connector;
    }

    public void validateAccount() {
        for (AccountProperties account : extractAccounts()) {
            if (account.getId().equals(oandaConnector.getAccountID()))
                return;
        }
        throw new AccountDoNotExistException();
    }

    public void validateAccountBalance() {
        if (isBalanceBelowMinimum())
            throw new AccountBalanceBelowMinimum();
    }

    private List<AccountProperties> extractAccounts() {
        try {
            AccountListResponse list = getAccountContext().list();
            return list.getAccounts();
        } catch (NullPointerException | RequestException | ExecuteException e) {
            throw new UnableToExecuteRequest();
        }
    }

    private Account getAccount() {
        try {
            return  getAccountContext().get(oandaConnector.getAccountID()).getAccount();
        } catch (NullPointerException | RequestException | ExecuteException e) {
            throw new UnableToExecuteRequest();
        }
    }


    private AccountContext getAccountContext() {
        return oandaConnector.getContext().account;
    }

    private boolean isBalanceBelowMinimum() {
        return getAccount().getBalance().doubleValue() <= MIN_BALANCE;
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