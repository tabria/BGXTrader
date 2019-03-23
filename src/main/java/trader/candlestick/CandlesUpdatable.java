package trader.candlestick;

import java.util.List;

public interface CandlesUpdatable {

    List<Candlestick> getCandles();
    Candlestick getUpdatedCandle();
}
