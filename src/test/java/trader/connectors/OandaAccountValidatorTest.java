package trader.connectors;

import com.oanda.v20.ExecuteException;
import com.oanda.v20.RequestException;
import com.oanda.v20.account.AccountID;
import org.junit.Before;
import org.junit.Test;
import trader.CommonTestClassMembers;
import trader.OandaAPI.OandaAPIMock;
import trader.connectors.oanda.OandaAccountValidator;
import trader.connectors.oanda.OandaAccountValidator.*;
import trader.connectors.oanda.OandaConnector;
import static org.mockito.Mockito.*;


public class OandaAccountValidatorTest {

    public static final double MINIMUM_ACCOUNT_BALANCE = 2_345_323_233.0D;
    OandaAccountValidator validator;
    OandaAPIMock oandaAPIMock;
    OandaConnector oandaMockConnector;
    CommonTestClassMembers commonMembers;

    @Before
    public void setUp() throws Exception {
        oandaAPIMock = new OandaAPIMock();
        oandaMockConnector = mock(OandaConnector.class);
        validator = new OandaAccountValidator(oandaMockConnector);
        commonMembers = new CommonTestClassMembers();
    }

    @Test(expected = UnableToExecuteRequest.class)
    public void validateAccountWithNullContext_Exception(){
        commonMembers.changeFieldObject(validator,"accountContext", null);
        validator.validateAccount();
    }

    @Test(expected = UnableToExecuteRequest.class)
    public void validateBalanceWithNullContext_Exception(){
        commonMembers.changeFieldObject(validator,"accountContext", null);
        validator.validateAccountBalance();
    }

    @Test(expected = UnableToExecuteRequest.class)
    public void validateNullAccount_Exception(){
        commonMembers.changeFieldObject(validator,"accountID", null);
        validator.validateAccount();
    }

    @Test(expected = UnableToExecuteRequest.class)
    public void validateBalanceNullAccount_Exception(){
        commonMembers.changeFieldObject(validator,"accountID", null);
        validator.validateAccountBalance();
    }

    @Test
    public void validateAccount(){
        ApiConnector oandaConnector = ApiConnectors.create("Oanda");
        validator = new OandaAccountValidator((OandaConnector) oandaConnector);
        validator.validateAccount();
        validator.validateAccountBalance();
    }

    @Test(expected = AccountDoNotExistException.class)
    public void validateAccountWithNotExistingAccount_Exception(){
        ApiConnector oandaConnector = ApiConnectors.create("Oanda");
        validator = new OandaAccountValidator((OandaConnector) oandaConnector);
        commonMembers.changeFieldObject(validator,"accountID", new AccountID("001-004-1942536-124"));
        validator.validateAccount();
    }

    @Test(expected = AccountBalanceBelowMinimum.class)
    public void accountWithBalanceLowerThanMinimum_Exception() throws ExecuteException, RequestException {
        ApiConnector oandaConnector = ApiConnectors.create("Oanda");
        validator = new OandaAccountValidator((OandaConnector) oandaConnector);
        commonMembers.changeFieldObject(validator,"MIN_BALANCE", MINIMUM_ACCOUNT_BALANCE );
        validator.validateAccountBalance();
    }

    @Test(expected = UnableToExecuteRequest.class)
    public void badAccountId_Exception(){
        commonMembers.changeFieldObject(validator, "accountID", oandaAPIMock.getMockAccountID());
        validator.validateAccountBalance();
    }
}
