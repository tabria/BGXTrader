package trader.broker.connector.oanda;

import com.oanda.v20.ExecuteException;
import com.oanda.v20.RequestException;
import com.oanda.v20.account.AccountID;
import com.oanda.v20.account.AccountProperties;
import org.junit.Before;
import org.junit.Test;
import trader.OandaAPIMock.OandaAPIMockAccount;
import trader.exception.AccountBalanceBelowMinimum;
import trader.exception.AccountDoNotExistException;
import trader.exception.UnableToExecuteRequest;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;


public class OandaAccountValidatorTest {

    private static final double MINIMUM_ACCOUNT_BALANCE = 0.9D;
    private static final double BALANCE = 333_333_33.9D;

    private OandaAccountValidator validator;
    private OandaAPIMockAccount oandaAPIMockAccount;
    private OandaConnector oandaMockConnector;

    @Before
    public void setUp() {
        oandaAPIMockAccount = new OandaAPIMockAccount();
        oandaMockConnector = mock(OandaConnector.class);
        validator = new OandaAccountValidator();
    }

    @Test(expected = UnableToExecuteRequest.class)
    public void validateAccountWithNullContext_Exception() {
        when(oandaMockConnector.getContext()).thenReturn(null);
        validator.validateAccount(oandaMockConnector);
    }

    @Test(expected = UnableToExecuteRequest.class)
    public void validateNullAccount_Exception(){
        setOandaMockConnector();
        oandaAPIMockAccount.setAccountListResponseToThrowException();
        validator.validateAccount(oandaMockConnector);
    }

    @Test(expected = AccountDoNotExistException.class)
    public void validateAccountWithBadAccount_Exception() {
        List<AccountProperties> accounts = setAccountPropertyList();
        oandaAPIMockAccount.setMockAccountPropertiesGetIdToReturnNewMock();
        oandaAPIMockAccount.setExtractAccountsFromContext(accounts);
        validator.validateAccount(oandaMockConnector);
    }

    @Test
    public void validateAccountWithCorrectInput() {
        List<AccountProperties> accounts = setAccountPropertyList();
        oandaAPIMockAccount.setExtractAccountsFromContext(accounts);
        oandaAPIMockAccount.setMockAccountUnitsDoubleValue(BALANCE);
        validator.validateAccount(oandaMockConnector);
        validator.validateAccountBalance(oandaMockConnector);
    }

    @Test(expected = UnableToExecuteRequest.class)
    public void validateBalanceWithNullContext_Exception() {
        when(oandaMockConnector.getContext()).thenReturn(null);
        validator.validateAccountBalance(oandaMockConnector);
    }

    @Test(expected = AccountBalanceBelowMinimum.class)
    public void accountWithBalanceLowerThanMinimum_Exception() {
        setOandaMockConnector();
        oandaAPIMockAccount.setMockAccountUnitsDoubleValue(MINIMUM_ACCOUNT_BALANCE);
        validator.validateAccountBalance(oandaMockConnector);
    }

    private void setOandaMockConnector() {
        when(oandaMockConnector.getContext()).thenReturn(oandaAPIMockAccount.getContext());
        when(oandaMockConnector.getAccountID()).thenReturn("1");
        oandaAPIMockAccount.setMockAccountPropertiesGetIdToReturnNewMock("1");
    }

    private List<AccountProperties> setAccountPropertyList() {
        List<AccountProperties> accounts = new ArrayList<>(1);
        accounts.add(oandaAPIMockAccount.getMockAccountProperties());
        setOandaMockConnector();
        return accounts;
    }
}
