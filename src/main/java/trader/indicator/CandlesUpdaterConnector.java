package trader.indicator;

import trader.candlestick.Candlestick;

import java.util.List;

public interface CandlesUpdaterConnector {

    List<Candlestick> getInitialCandles();

    Candlestick updateCandle();
}
