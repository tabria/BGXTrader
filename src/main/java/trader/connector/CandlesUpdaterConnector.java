package trader.connector;

import trader.candlestick.Candlestick;

import java.util.List;

public interface CandlesUpdaterConnector {

    List<Candlestick> getInitialCandles();

    Candlestick getUpdateCandle();
}
