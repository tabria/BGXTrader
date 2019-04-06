package trader.OandaAPIMock;

import com.oanda.v20.Context;
import com.oanda.v20.ExecuteException;
import com.oanda.v20.RequestException;
import com.oanda.v20.account.*;
import com.oanda.v20.order.Order;
import com.oanda.v20.primitives.AccountUnits;
import com.oanda.v20.trade.TradeSummary;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OandaAPIMockAccount extends OandaAPIMock {

    private AccountContext mockAccountContext;
    private Account mockAccount ;
    private AccountUnits mockAccountUnits;
    private AccountID mockAccountID;
    private AccountGetResponse mockAccountGetResponse;
    private AccountListResponse mockAccountListResponse;
    private AccountProperties mockAccountProperties;


    public OandaAPIMockAccount(Context context){
        this();
        setMockContext(context);
        mockContext.account = mockAccountContext;
    }

    public OandaAPIMockAccount(){
        mockAccountContext = mock(AccountContext.class);
        mockContext.account = mockAccountContext;
        mockAccountGetResponse = mock(AccountGetResponse.class);
        mockAccount = mock(Account.class);
        mockAccountUnits = mock(AccountUnits.class);
        mockAccountID = mock(AccountID.class);
        mockAccountListResponse = mock(AccountListResponse.class);
        mockAccountProperties = mock(AccountProperties.class);

        try {
            init();
        } catch (ExecuteException | RequestException e) {
           throw new RuntimeException();
        }
    }

    public AccountID getMockAccountID(){
        return mockAccountID;
    }

    public Account getMockAccount(){
        return mockAccount;
    }

    public AccountProperties getMockAccountProperties() {
        return mockAccountProperties;
    }

    public void setMockAccountUnitsDoubleValue(double newValue){
        when(mockAccountUnits.doubleValue())
                .thenReturn(newValue);
    }

    public void setListAccountProperties(List<AccountProperties> accounts){
        when(mockAccountListResponse.getAccounts())
                .thenReturn(accounts);
    }

    public void setAccountListResponseToThrowException(){
        when(mockAccountListResponse.getAccounts())
                .thenThrow(NullPointerException.class);
    }

    public void setExtractAccountsFromContext(List<AccountProperties> accounts) {
        setListAccountProperties(accounts);
    }

    public void setMockAccountPropertiesGetIdToReturnNewMock(){
        when( mockAccountProperties.getId())
                .thenReturn(mock(AccountID.class));
    }

    public void setMockAccountPropertiesGetIdToReturnNewMock(String id){
        when( mockAccountProperties.getId())
                .thenReturn(new AccountID(id));
    }

    public void setMockAccountTradeSummary(List<TradeSummary> tradeSummaries){
        when(mockAccount.getTrades())
                .thenReturn(tradeSummaries);
    }
    public void setMockAccountOrders(List<Order> orders){
        when(mockAccount.getOrders())
                .thenReturn(orders);
    }

    public void setMockAccountGetResponseToThrowException(){
        when(mockAccountGetResponse.getAccount()).thenThrow(NullPointerException.class);
    }

    private void init() throws ExecuteException, RequestException {
        when(mockAccountGetResponse.getAccount())
                .thenReturn(mockAccount);
        when(mockAccount.getBalance())
                .thenReturn(mockAccountUnits);
        when(mockAccountContext.get(any(AccountID.class)))
                .thenReturn(mockAccountGetResponse);
//        when(mockAccountContext.get(mockAccountID)).thenReturn(mockAccountGetResponse);
        when(mockAccountContext.list())
                .thenReturn(mockAccountListResponse);
        when(mockAccountProperties.getId())
                .thenReturn(mockAccountID);
        when(mockContext.account.get(any(AccountID.class)))
                .thenReturn(mockAccountGetResponse);
        when(this.mockAccount.getId())
                .thenReturn(mockAccountID);
    }

}
