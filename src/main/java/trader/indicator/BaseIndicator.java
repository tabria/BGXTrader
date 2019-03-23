package trader.indicator;


import trader.candlestick.CandlesUpdatable;
import trader.candlestick.candle.CandlePriceType;
import trader.candlestick.Candlestick;
import trader.exception.BadRequestException;
import trader.exception.IndicatorPeriodTooBigException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static trader.strategy.BGXStrategy.StrategyConfig.SCALE;

public abstract class BaseIndicator implements Indicator {

    protected long indicatorPeriod;
    protected final CandlePriceType candlePriceType;
    protected final CandlesUpdatable candlesUpdater;
    protected List<BigDecimal> indicatorValues;
    protected BigDecimal divisor;

    public BaseIndicator(long indicatorPeriod, CandlePriceType candlePriceType, CandlesUpdatable candlesUpdater) {
        this.indicatorPeriod = indicatorPeriod;
        this.candlePriceType = candlePriceType;
        this.candlesUpdater = candlesUpdater;
        this.indicatorValues = new ArrayList<>();
    }

    public List<BigDecimal> getValues() {
        return Collections.unmodifiableList(indicatorValues);
    }

    public abstract void updateIndicator();

    protected abstract void setDivisor();

    protected void verifyCalculationInput(List<Candlestick> candlestickList) {
        if(candlestickList.size() == 0)
            throw new BadRequestException();
        if(candlestickList.size() <= indicatorPeriod)
            throw new IndicatorPeriodTooBigException();
    }

    protected BigDecimal obtainPrice(Candlestick candle) {
        return candlePriceType.extractPrice(candle)
                        .setScale(SCALE, BigDecimal.ROUND_HALF_UP);
    }
}
