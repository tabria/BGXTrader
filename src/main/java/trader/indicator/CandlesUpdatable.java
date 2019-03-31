package trader.indicator;

import trader.candlestick.Candlestick;

import java.util.List;

public interface CandlesUpdatable {

    List<Candlestick> getCandles();
    Candlestick getUpdatedCandle();
}
