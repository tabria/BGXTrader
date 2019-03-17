package trader.OandaAPI;

import com.oanda.v20.Context;
import com.oanda.v20.ExecuteException;
import com.oanda.v20.RequestException;
import com.oanda.v20.account.*;
import com.oanda.v20.primitives.AccountUnits;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OandaAPIMockAccount {

    private Context mockContext;
    private AccountContext mockAccountContext;
    private Account mockAccount ;
    private AccountUnits mockAccountUnits;
    private AccountID mockAccountID;
    private AccountGetResponse mockAccountGetResponse;
    private AccountListResponse mockAccountListResponse;
    private AccountProperties mockAccountProperties;


    public OandaAPIMockAccount(){
        mockAccountContext = mock(AccountContext.class);
        mockContext = mock(Context.class);
        mockContext.account = mockAccountContext;
        mockAccountGetResponse = mock(AccountGetResponse.class);
        mockAccount = mock(Account.class);
        mockAccountUnits = mock(AccountUnits.class);
        mockAccountID = mock(AccountID.class);
        mockAccountListResponse = mock(AccountListResponse.class);
        mockAccountProperties = mock(AccountProperties.class);
    }

    public Context getContext() {
        return mockContext;
    }

    public AccountID getMockAccountID(){
        return mockAccountID;
    }

    public AccountProperties getMockAccountProperties() {
        return mockAccountProperties;
    }

    public void setMockAccountUnitsDoubleValue(double newValue){
        when(mockAccountUnits.doubleValue()).thenReturn(newValue);
    }

    public void setMockAccountGetBalance(){
        when(mockAccount.getBalance()).thenReturn(mockAccountUnits);
    }

    public void setMockAccountGetResponse(){
        when(mockAccountGetResponse.getAccount()).thenReturn(mockAccount);
    }

    public void setMockedAccountFromMockedContext() throws ExecuteException, RequestException {
        when(mockAccountContext.get(mockAccountID)).thenReturn(mockAccountGetResponse);
        setMockAccountGetResponse();
    }

    public void setAccountListResponse() throws ExecuteException, RequestException {
        when(mockAccountContext.list()).thenReturn(mockAccountListResponse);
    }

    public void setListAccountProperties(List<AccountProperties> accounts){
        when(mockAccountListResponse.getAccounts())
                .thenReturn(accounts);
    }

    public void setExtractAccountsFromContext(List<AccountProperties> accounts) throws RequestException, ExecuteException {
        setMockedAccountFromMockedContext();
        setAccountListResponse();
        setListAccountProperties(accounts);
    }

    public void setMockAccountPropertiesGetId(){
        when( mockAccountProperties.getId()).thenReturn(mockAccountID);
    }

    public void setMockAccountPropertiesGetIdToReturnNewMock(){
        when( mockAccountProperties.getId()).thenReturn(mock(AccountID.class));
    }
}
