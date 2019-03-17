package trader.connectors;

import com.oanda.v20.ExecuteException;
import com.oanda.v20.RequestException;
import com.oanda.v20.account.AccountContext;
import com.oanda.v20.account.AccountID;
import org.junit.Before;
import org.junit.Test;
import trader.OandaAPI.OandaAPIMock;
import trader.connectors.oanda.OandaAccountValidator;
import trader.connectors.oanda.OandaAccountValidator.*;
import trader.connectors.oanda.OandaConnector;

import java.lang.reflect.Field;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class OandaAccountValidatorTest {

    public static final double MINIMUM_ACCOUNT_BALANCE = 2_345_323_233.0D;
    OandaAccountValidator validator;
    OandaAPIMock oandaAPIMock;
    OandaConnector oandaMockConnector;

    @Before
    public void setUp() throws Exception {
        oandaAPIMock = new OandaAPIMock();
        oandaMockConnector = mock(OandaConnector.class);
        validator = new OandaAccountValidator(oandaMockConnector);
    }

    @Test(expected = UnableToExecuteRequest.class)
    public void validateAccountWithNullContext_Exception(){
        changeFieldObject("accountContext", null);
        validator.runValidator();
    }

    @Test(expected = UnableToExecuteRequest.class)
    public void validateBalanceWithNullContext_Exception(){
        changeFieldObject("accountContext", null);
        validator.runValidator();
    }

    @Test(expected = UnableToExecuteRequest.class)
    public void validateNullAccount_Exception(){
        changeFieldObject("accountID", null);
        validator.runValidator();
    }

    @Test(expected = UnableToExecuteRequest.class)
    public void validateBalanceNullAccount_Exception(){
        changeFieldObject("accountID", null);
        validator.runValidator();
    }

    @Test
    public void validateAccount(){
        ApiConnector oandaConnector = ApiConnectors.create("Oanda");
        validator = new OandaAccountValidator((OandaConnector) oandaConnector);
        validator.runValidator();
    }

    @Test(expected = AccountDoNotExistException.class)
    public void validateAccountWithNotExistingAccount_Exception(){
        ApiConnector oandaConnector = ApiConnectors.create("Oanda");
        validator = new OandaAccountValidator((OandaConnector) oandaConnector);
        changeFieldObject("accountID", new AccountID("001-004-1942536-124"));
        validator.runValidator();
    }

    @Test(expected = AccountBalanceBelowMinimum.class)
    public void accountWithBalanceLowerThanMinimum_Exception() throws ExecuteException, RequestException {
        ApiConnector oandaConnector = ApiConnectors.create("Oanda");
        validator = new OandaAccountValidator((OandaConnector) oandaConnector);
        changeFieldObject("MIN_BALANCE", MINIMUM_ACCOUNT_BALANCE );
        validator.runValidator();
    }

    private AccountContext setMockOandaAccountContext(AccountID accountID) throws ExecuteException, RequestException {
        OandaAPIMock oandaAPIMock = new OandaAPIMock();
        oandaAPIMock.setMockAccountUnitsDoubleValue(MINIMUM_ACCOUNT_BALANCE - 0.01);
        oandaAPIMock.setMockAccountGetBalance();
        oandaAPIMock.setMockAccountContextGet(accountID);
        oandaAPIMock.setMockAccountGetResponse();
        return  oandaAPIMock.getMockAccountContext();
    }

    private Object changeFieldObject(String fieldName, Object value) {
        try {
            Field field = validator.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(validator, value);
            return field.get(validator);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
