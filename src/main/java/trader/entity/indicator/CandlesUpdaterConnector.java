package trader.entity.indicator;

import trader.entity.candlestick.Candlestick;

import java.util.List;

public interface CandlesUpdaterConnector {

    List<Candlestick> getInitialCandles();

    Candlestick updateCandle();
}
