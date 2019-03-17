package trader.OandaAPI;

import com.oanda.v20.Context;
import com.oanda.v20.ExecuteException;
import com.oanda.v20.RequestException;
import com.oanda.v20.account.*;
import com.oanda.v20.instrument.Candlestick;
import com.oanda.v20.instrument.InstrumentCandlesRequest;
import com.oanda.v20.instrument.InstrumentCandlesResponse;
import com.oanda.v20.instrument.InstrumentContext;
import com.oanda.v20.primitives.AccountUnits;
import com.oanda.v20.primitives.DateTime;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OandaAPIMock {
    private Context mockContext;
    private InstrumentCandlesRequest mockRequest;
    private InstrumentCandlesResponse mockResponse;
    private AccountContext mockAccountContext;
    private AccountGetResponse mockAccountGetResponse;
    private Account mockAccount ;
    private AccountUnits mockAccountUnits;
    private AccountID mockAccountID;
    private DateTime mockDateTime;

    public OandaAPIMock() {
        mockContext = mock(Context.class);
        mockContext.instrument = mock(InstrumentContext.class);
        mockRequest = mock(InstrumentCandlesRequest.class);
        mockResponse = mock(InstrumentCandlesResponse.class);
        mockAccountContext = mock(AccountContext.class);
        mockAccountGetResponse = mock(AccountGetResponse.class);
        mockAccount = mock(Account.class);
        mockAccountUnits = mock(AccountUnits.class);
        mockAccountID = mock(AccountID.class);
        mockDateTime = mock(DateTime.class);

    }

    public Context getContext() {
        return mockContext;
    }

    public InstrumentCandlesRequest getMockRequest() {
        return mockRequest;
    }

    public InstrumentCandlesResponse getMockResponse() {
        return mockResponse;
    }

    public AccountContext getMockAccountContext() {
        return mockAccountContext;
    }

    public AccountGetResponse getMockAccountGetResponse() {
        return mockAccountGetResponse;
    }

    public Account getMockAccount() {
        return mockAccount;
    }

    public AccountUnits getMockAccountUnits() {
        return mockAccountUnits;
    }

    public AccountID getMockAccountID(){
        return mockAccountID;
    }

    public DateTime getMockDateTime(){
        return mockDateTime;
    }

    public void setMockRequestToCandles() throws ExecuteException, RequestException {
        when(mockContext.instrument.candles(mockRequest)).thenReturn(mockResponse);
    }

    public void setMockResponseToGetCandles(List<Candlestick> candlestickList){
        when(mockResponse.getCandles()).thenReturn(candlestickList);
    }

    public void setMockAccountUnitsDoubleValue(double newValue){
        when(mockAccountUnits.doubleValue()).thenReturn(newValue);
    }

    public void setMockAccountGetBalance(){
        when(mockAccount.getBalance()).thenReturn(mockAccountUnits);
    }

    public void setMockAccountContextGet(AccountID accountID){
        try {
            when(mockAccountContext.get(accountID)).thenReturn(mockAccountGetResponse);
        } catch (RequestException | ExecuteException e) {
            throw new RuntimeException();
        }
    }

    public void setMockAccountGetResponse(){
        when(mockAccountGetResponse.getAccount()).thenReturn(mockAccount);
    }




}
