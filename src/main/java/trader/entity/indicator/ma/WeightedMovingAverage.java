package trader.entity.indicator.ma;

import trader.entity.candlestick.candle.CandleGranularity;
import trader.entity.candlestick.candle.CandlePriceType;
import trader.entity.candlestick.Candlestick;
import trader.entity.indicator.BaseIndicator;

import java.math.BigDecimal;
import java.util.List;

public final class WeightedMovingAverage extends BaseIndicator {

    WeightedMovingAverage(long indicatorPeriod, CandlePriceType candlePriceType, CandleGranularity granularity, String position) {
        super(indicatorPeriod, candlePriceType, granularity, position);
        setDivisor();
    }


    @Override
    public void updateIndicator(List<Candlestick> candles) {
        if(indicatorValues.size() == 0){
            candlestickList.addAll(candles);
            setWMAValues(candles);
        } else {
            candlestickList.add(candles.get(candles.size()-1));
            indicatorValues.add(calculateWMAValue(candlestickList, candlestickList.size() - 1));
        }
    }

    @Override
    public String toString() {
        return "WeightedMovingAverage{" +
                "period=" + indicatorPeriod +
                ", candlePriceType=" + candlePriceType.toString() +
                ", granularity=" + granularity.toString() +
                ", indicatorValues=" + indicatorValues.toString() +
                '}';
    }

    @Override
    protected void setDivisor(){
        divisor = BigDecimal.valueOf(indicatorPeriod)
                .add(BigDecimal.ONE)
                .divide(BigDecimal.valueOf(2), 5, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(indicatorPeriod)).setScale(5, BigDecimal.ROUND_HALF_UP);
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
     * WMA: (Period*PriceImpl) + (Period-1)*PrevPrice + ... + PriceImpl(Period-1)*1)/(Period*(Period + 1)/2)
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
                    .setScale(5, BigDecimal.ROUND_HALF_UP);
        }
        if (candlestickList.size() > 0 && divisor.compareTo(BigDecimal.ZERO) != 0)
            return wmaValue.divide(divisor, 5, BigDecimal.ROUND_HALF_UP);
        return wmaValue;
    }
}
