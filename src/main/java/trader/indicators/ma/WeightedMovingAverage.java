package trader.indicators.ma;

import trader.candle.CandlesUpdater;
import trader.candle.Candlestick;
import trader.indicators.BaseIndicator;
import trader.candle.CandlestickPriceType;
import java.math.BigDecimal;
import java.util.List;

import static trader.strategies.BGXStrategy.StrategyConfig.SCALE;

public final class WeightedMovingAverage extends BaseIndicator {

    WeightedMovingAverage(long indicatorPeriod, CandlestickPriceType candlestickPriceType, CandlesUpdater updater) {
        super(indicatorPeriod, candlestickPriceType, updater);
        setDivisor();
        initiateWMAValues();
    }

    @Override
    public void updateIndicator() {
        candlesUpdater.getUpdateCandle();
        List<Candlestick> candles = candlesUpdater.getCandles();
        indicatorValues.add(calculateWMAValue(candles, candles.size() - 1));
    }

    @Override
    public String toString() {
        return "WeightedMovingAverage{" +
                "period=" + indicatorPeriod +
                ", candlestickPriceType=" + candlestickPriceType.toString() +
                ", indicatorValues=" + indicatorValues.toString() +
                '}';
    }

    @Override
    protected void setDivisor(){
        divisor = BigDecimal.valueOf(indicatorPeriod)
                .add(BigDecimal.ONE)
                .divide(BigDecimal.valueOf(2), SCALE, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(indicatorPeriod)).setScale(SCALE, BigDecimal.ROUND_HALF_UP);
    }

    private void initiateWMAValues() {
        List<Candlestick> candles = candlesUpdater.getCandles();
        setWMAValues(candles);
    }

    private void setWMAValues(List<Candlestick> candlestickList){
        verifyCalculationInput(candlestickList);
        for (int candleIndex = candlestickList.size()-1; candleIndex >= lastCandlestickIndex() ; candleIndex--)
            indicatorValues.add(0, calculateWMAValue(candlestickList, candleIndex));
    }

    private long lastCandlestickIndex() {
        return indicatorPeriod-1;
    }

    /**
     This method calculate Weighted Moving Average(WMA) based on the formula
     * {@code
     * WMA: (Period*Price) + (Period-1)*PrevPrice + ... + Price(Period-1)*1)/(Period*(Period + 1)/2)
     * }
     * @param candlestickList list of available candlesticks
     * @return {@link BigDecimal} current WMA value
     */
    private BigDecimal calculateWMAValue(List<Candlestick> candlestickList, int candleIndex){
        long wmaPeriod = indicatorPeriod;
        BigDecimal wmaValue = BigDecimal.ZERO;
        for (int i = candleIndex; i >= candleIndex - lastCandlestickIndex(); i--) {
            wmaValue = obtainPrice(candlestickList.get(i))
                    .multiply(BigDecimal.valueOf(wmaPeriod--))
                    .add(wmaValue)
                    .setScale(SCALE, BigDecimal.ROUND_HALF_UP);
        }
        if (candlestickList.size() > 0 && divisor.compareTo(BigDecimal.ZERO) != 0)
            return wmaValue.divide(divisor, SCALE, BigDecimal.ROUND_HALF_UP);
        return wmaValue;
    }
}
