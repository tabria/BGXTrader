package trader.broker.connector.oanda;

import com.oanda.v20.account.AccountProperties;
import org.junit.Before;
import org.junit.Test;
import trader.OandaAPIMock.OandaAPIMockAccount;
import trader.broker.connector.BrokerConnector;
import trader.exception.AccountBalanceBelowMinimum;
import trader.exception.AccountDoNotExistException;
import trader.exception.NullArgumentException;
import trader.exception.UnableToExecuteRequest;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public class OandaAccountValidatorTest {

    private static final double MINIMUM_ACCOUNT_BALANCE = 0.9D;
    private static final double BALANCE = 333_333_33.9D;

    private OandaAccountValidator validator;
    private OandaAPIMockAccount oandaAPIMockAccount;
    private BrokerConnector oandaMockConnector;

    @Before
    public void setUp() {
        oandaAPIMockAccount = new OandaAPIMockAccount();
        oandaMockConnector = mock(BrokerConnector.class);
        validator = new OandaAccountValidator();
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallValidateAccountWithNullContext_Exception() {
        validator.validateAccount(oandaMockConnector, null);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallValidateAccountWithNullConnector_Exception(){
        validator.validateAccount(null, oandaAPIMockAccount.getContext());
    }

    @Test(expected = UnableToExecuteRequest.class)
    public void WhenCallValidateAccountWithNullAccount_Exception(){
        setOandaMockConnector();
        oandaAPIMockAccount.setAccountListResponseToThrowException();
        validator.validateAccount(oandaMockConnector, oandaAPIMockAccount.getContext());
    }

    @Test(expected = AccountDoNotExistException.class)
    public void WhenCallValidateAccountWithBadAccount_Exception() {
        List<AccountProperties> accounts = setAccountPropertyList();
        oandaAPIMockAccount.setMockAccountPropertiesGetIdToReturnNewMock();
        oandaAPIMockAccount.setExtractAccountsFromContext(accounts);
        validator.validateAccount(oandaMockConnector, oandaAPIMockAccount.getContext());
    }

    @Test
    public void WhenCallValidateAccountWithCorrectInput() {
        List<AccountProperties> accounts = setAccountPropertyList();
        oandaAPIMockAccount.setExtractAccountsFromContext(accounts);
        oandaAPIMockAccount.setMockAccountUnitsDoubleValue(BALANCE);
        validator.validateAccount(oandaMockConnector, oandaAPIMockAccount.getContext());
        validator.validateAccountBalance(oandaMockConnector, oandaAPIMockAccount.getContext());
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallValidateBalanceWithNullContext_Exception() {
        validator.validateAccountBalance(oandaMockConnector, null);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallValidateBalanceWithNullConnector_Exception(){
        validator.validateAccountBalance(null, oandaAPIMockAccount.getContext());
    }

    @Test(expected = AccountBalanceBelowMinimum.class)
    public void WhenCallAccountWithBalanceLowerThanMinimum_Exception() {
        setOandaMockConnector();
        oandaAPIMockAccount.setMockAccountUnitsDoubleValue(MINIMUM_ACCOUNT_BALANCE);
        validator.validateAccountBalance(oandaMockConnector, oandaAPIMockAccount.getContext());
    }

    @Test(expected = UnableToExecuteRequest.class)
    public void WhenCallValidateAccountBalanceWithNullAccount_Exception(){
        setOandaMockConnector();
        oandaAPIMockAccount.setMockAccountGetResponseToThrowException();
        validator.validateAccountBalance(oandaMockConnector, oandaAPIMockAccount.getContext());
    }

    private void setOandaMockConnector() {
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
