package trader.connectors;

import com.oanda.v20.ExecuteException;
import com.oanda.v20.RequestException;
import com.oanda.v20.account.AccountContext;
import com.oanda.v20.account.AccountID;
import org.junit.Before;
import org.junit.Test;
import trader.OandaAPI.OandaAPIMock;
import trader.connectors.oanda.OandaAccountValidator;
import trader.connectors.oanda.OandaConnector;

import java.lang.reflect.Field;

public class OandaConnectorTest {

//    public static final double MINIMUM_ACCOUNT_BALANCE = 1.0D;
//    OandaAccountValidator validator;
//
//    @Before
//    public void setUp() throws Exception {
//        oandaConnector = ApiConnectors.create("Oanda");
//    }
//
//    @Test(expected = OandaConnector.UnableToExecuteRequest.class)
//    public void validateAccountWithNullContext_Exception(){
//        AccountContext context = (AccountContext) changeFieldObject("accountContext", null);
//        oandaConnector.validateAccount();
//    }
//
//    @Test(expected = OandaConnector.AccountDoNotExistException.class)
//    public void validateNullAccount_Exception(){
//        changeFieldObject("accountID", null);
//        oandaConnector.validateAccount();
//    }
//
//    @Test
//    public void validateAccount(){
//        ApiConnector oandaConnector = ApiConnectors.create("Oanda");
//        oandaConnector.validateAccount();
//    }
//
//    @Test(expected = OandaConnector.AccountDoNotExistException.class)
//    public void validateAccountWithNotExistingAccount_Exception(){
//        changeFieldObject("accountID", new AccountID("001-004-1942536-124"));
//        oandaConnector.validateAccount();
//    }
//
//    @Test(expected = OandaConnector.AccountBalanceBelowMinimum.class)
//    public void accountWithBalanceLowerThanMinimum_Exception() throws ExecuteException, RequestException {
//        AccountID accountID = new AccountID("001-004-1942536-001");
//        changeFieldObject("accountID", accountID);
//        changeFieldObject("accountContext", setMockOandaAccountContext(accountID));
//        oandaConnector.validateAccountBalance();
//    }
//
//    private AccountContext setMockOandaAccountContext(AccountID accountID) throws ExecuteException, RequestException {
//        OandaAPIMock oandaAPIMock = new OandaAPIMock();
//        oandaAPIMock.setMockAccountUnitsDoubleValue(MINIMUM_ACCOUNT_BALANCE - 0.01);
//        oandaAPIMock.setMockAccountGetBalance();
//        oandaAPIMock.setMockAccountContextGet(accountID);
//        oandaAPIMock.setMockAccountGetResponse();
//        return  oandaAPIMock.getMockAccountContext();
//    }
//
//    private Object changeFieldObject(String fieldName, Object value) {
//        try {
//            Field field = oandaConnector.getClass().getDeclaredField(fieldName);
//            field.setAccessible(true);
//            field.set(oandaConnector, value);
//            return field.get(oandaConnector);
//        } catch (NoSuchFieldException | IllegalAccessException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    private Object extractFieldObject(String fieldName) {
//        try {
//            Field field = oandaConnector.getClass().getDeclaredField(fieldName);
//            field.setAccessible(true);
//            return field.get(oandaConnector);
//        } catch (NoSuchFieldException | IllegalAccessException e) {
//            throw new RuntimeException(e);
//        }
//    }

}
