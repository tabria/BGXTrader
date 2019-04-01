package trader.entity.indicator;

import trader.entity.candlestick.Candlestick;

import java.util.List;

public interface CandlesUpdatable {

    List<Candlestick> getCandles();
    Candlestick getUpdatedCandle();
}
