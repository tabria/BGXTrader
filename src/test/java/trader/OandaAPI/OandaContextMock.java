package trader.OandaAPI;

import com.oanda.v20.Context;
import com.oanda.v20.instrument.InstrumentContext;

import static org.mockito.Mockito.mock;

public class OandaContextMock {
    private Context context;

    public OandaContextMock(){
        this.context = mock(Context.class);
        this.context.instrument = mock(InstrumentContext.class);
    }

    public Context getContext() {
        return context;
    }
}
