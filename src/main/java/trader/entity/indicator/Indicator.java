package trader.entity.indicator;

import trader.entity.candlestick.Candlestick;
import trader.entity.candlestick.candle.CandleGranularity;

import java.math.BigDecimal;
import java.util.List;

public interface Indicator {

    List<BigDecimal> getValues();

    CandleGranularity getGranularity();

    void updateIndicator(List<Candlestick> candles);

}
