package trader.candles;

import com.oanda.v20.instrument.Candlestick;
import com.oanda.v20.instrument.CandlestickGranularity;
import org.junit.Before;
import org.junit.Test;
import trader.OandaAPI.OandaAPIMock;

import java.util.List;

public class CandlesUpdaterTest {
    private OandaAPIMock oandaAPIMock;
    private CandlesUpdater candlesUpdater;

    @Before
    public void setUp() throws Exception {
        oandaAPIMock = new OandaAPIMock();
        oandaAPIMock.setMockRequestToCandles();


        candlesUpdater = new CandlesUpdater(oandaAPIMock.getContext(), oandaAPIMock.getMockRequest(), CandlestickGranularity.M30);

    }

    @Test
    public void getCandles(){
        List<Candlestick> candles = candlesUpdater.getCandles();
        String a ="";
    }

}
