package trader.indicator.ma;


import trader.candlestick.CandlesUpdatable;
import trader.candlestick.candle.CandlePriceType;
import trader.candlestick.Candlestick;
import trader.indicator.BaseIndicator;

import java.math.BigDecimal;
import java.util.List;
import static trader.strategy.BGXStrategy.configuration.StrategyConfig.*;

public final class ExponentialMovingAverage extends BaseIndicator {

    private static final BigDecimal SMOOTH_FACTOR_CONSTANT = BigDecimal.valueOf(2);

    private BigDecimal smoothFactor;
    private BigDecimal smoothMultiplier;

    ExponentialMovingAverage(long indicatorPeriod, CandlePriceType candlePriceType, CandlesUpdatable updater) {
        super(indicatorPeriod, candlePriceType, updater);
        setDivisor();
        setSmoothFactor();
        setSmoothMultiplier();
        initiateEMAValues();
    }

    @Override
    public void updateIndicator() {
        Candlestick candlestick = candlesUpdater.getUpdatedCandle();
        indicatorValues.add(currentPriceSmoothed(candlestick).add(previousEMASmoothed()));
    }

    @Override
    public String toString() {
        return "ExponentialMovingAverage{" +
                "period=" + indicatorPeriod +
                ", candlePriceType=" + candlePriceType.toString() +
                ", indicatorValues=" + indicatorValues.toString() +
                '}';
    }

    @Override
    protected void setDivisor(){
        divisor = BigDecimal.valueOf(indicatorPeriod);
    }

    private void setSmoothFactor(){
        smoothFactor = SMOOTH_FACTOR_CONSTANT.divide(
                BigDecimal.valueOf(indicatorPeriod + 1L), SCALE, BigDecimal.ROUND_HALF_UP);
    }

    private void setSmoothMultiplier(){
        smoothMultiplier = BigDecimal.ONE.subtract(smoothFactor)
                .setScale(SCALE, BigDecimal.ROUND_HALF_UP);
    }

    private void initiateEMAValues() {
        List<Candlestick> candles = candlesUpdater.getCandles();
        setEMAValues(candles);
    }

    /**
     * Calculation Formula:
     * {@code
     *
     * Initial SMA: sum(Price) / indicatorPeriod  -> this is the start point of the EMA
     * EMA = Price(t) * k + EMA(y) * (1 – k)
     * t = current, y = yesterday, N = indicator period , k = 2/(N+1) smoothFactor
     *
     * }
     */
    private void setEMAValues(List<Candlestick> candlestickList){
        verifyCalculationInput(candlestickList);
        setSMAValue(candlestickList);
        setRemainingValues(candlestickList);
    }

    private void setSMAValue(List<Candlestick> candlestickList) {
        BigDecimal smaValue = BigDecimal.ZERO;
        for (int candleIndex = 0; candleIndex <= indicatorPeriod -1 ; candleIndex++) {
            smaValue = smaValue.add(obtainPrice(candlestickList.get(candleIndex)));
        }
        this.indicatorValues.add(smaValue.divide(divisor, SCALE, BigDecimal.ROUND_HALF_UP));
    }

    private void setRemainingValues(List<Candlestick> candlestickList) {
        for (int index = (int) indicatorPeriod; index < candlestickList.size() ; index++) {
            Candlestick candlestick = candlestickList.get(index);
            indicatorValues.add(currentPriceSmoothed(candlestick).add(previousEMASmoothed()));
        }
    }

    private BigDecimal currentPriceSmoothed(Candlestick candlestick) {
        return obtainPrice(candlestick)
                .multiply(smoothFactor)
                .setScale(SCALE, BigDecimal.ROUND_HALF_UP);
    }

    private BigDecimal previousEMASmoothed() {
        int lastIndex = indicatorValues.size()-1;
        return indicatorValues.get(lastIndex)
                .multiply(smoothMultiplier)
                .setScale(SCALE, BigDecimal.ROUND_HALF_UP);
    }
}
