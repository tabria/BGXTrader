package trader.connectors.oanda;

import com.oanda.v20.ExecuteException;
import com.oanda.v20.RequestException;
import com.oanda.v20.account.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public class OandaAccountValidator {

    private double MIN_BALANCE = 1.0D;

    private OandaConnector oandaConnector;
    private AccountContext accountContext;
    private AccountID accountID;

    public OandaAccountValidator(OandaConnector connector) {
        oandaConnector = connector;
        accountContext = new AccountContext(oandaConnector.getContext());
        accountID = oandaConnector.getAccountID();
    }

    public void validateAccount() {
        for (AccountProperties account : extractAccounts()) {
            if (account.getId().equals(accountID))
                return;
        }
        throw new AccountDoNotExistException();
    }

    public void validateAccountBalance() {
        if (getAccount().getBalance().doubleValue() <= MIN_BALANCE) {
            throw new AccountBalanceBelowMinimum();
        }
    }

    private List<AccountProperties> extractAccounts() {
        try {
            AccountListResponse list = accountContext.list();
            return list.getAccounts();
        } catch (NullPointerException | RequestException | ExecuteException e) {
            throw new UnableToExecuteRequest();
        }
    }

    private Account getAccount() {
        try {
            return accountContext.get(accountID).getAccount();
        } catch (NullPointerException | RequestException | ExecuteException e) {
            throw new UnableToExecuteRequest();
        }
    }

    public class AccountDoNotExistException extends RuntimeException{};
    public class AccountBalanceBelowMinimum extends RuntimeException{};
    public class UnableToExecuteRequest extends RuntimeException{};


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