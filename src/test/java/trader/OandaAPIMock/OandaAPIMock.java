package trader.OandaAPIMock;

import com.oanda.v20.Context;
import com.oanda.v20.ExecuteException;
import com.oanda.v20.RequestException;
import com.oanda.v20.instrument.Candlestick;
import com.oanda.v20.instrument.InstrumentCandlesRequest;
import com.oanda.v20.instrument.InstrumentCandlesResponse;
import com.oanda.v20.instrument.InstrumentContext;
import com.oanda.v20.primitives.DateTime;
import trader.CommonTestClassMembers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OandaAPIMock {

    Context mockContext;
    DateTime mockDateTime;

    public OandaAPIMock() {
        mockContext = mock(Context.class);
        mockDateTime = mock(DateTime.class);
    }

    public Context getContext() {
        return mockContext;
    }

    public void setMockContext(Context mockContext) {
        this.mockContext = mockContext;
    }

    public void setMockDateTime(DateTime mockDateTime) {
        this.mockDateTime = mockDateTime;
    }

    public DateTime getMockDateTime(){
        return mockDateTime;
    }






}
