package trader.entity.indicator.ma;


import trader.entity.candlestick.candle.CandleGranularity;
import trader.entity.candlestick.candle.CandlePriceType;
import trader.entity.candlestick.Candlestick;
import trader.entity.indicator.BaseIndicator;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

public final class ExponentialMovingAverage extends BaseIndicator {

    private static final BigDecimal SMOOTH_FACTOR_CONSTANT = BigDecimal.valueOf(2);

    private BigDecimal smoothFactor;
    private BigDecimal smoothMultiplier;

    ExponentialMovingAverage(long indicatorPeriod, CandlePriceType candlePriceType, CandleGranularity granularity, String position) {
        super(indicatorPeriod, candlePriceType, granularity, position);
        setDivisor();
        setSmoothFactor();
        setSmoothMultiplier();

    }

    @Override
    public void updateIndicator(List<Candlestick> candles) {
        if(indicatorValues.size() == 0){
            candlestickList.addAll(candles);
            setEMAValues(candles);
        } else {
            Candlestick lastIndicatorCandle = getLastCandlestick(candlestickList, candlestickList.size() - 1);
            if(isTimeToUpdate(candles.get(candles.size()-1), lastIndicatorCandle)) {
                Candlestick candlestick = candles.get(candles.size() - 1);
                candlestickList.add(candlestick);
                indicatorValues.add(currentPriceSmoothed(candlestick).add(previousEMASmoothed()));
            }
        }
    }

    @Override
    public String toString() {
        return "ExponentialMovingAverage{" +
                "period=" + indicatorPeriod +
                ", candlePriceType=" + candlePriceType.toString() +
                ", granularity=" + granularity.toString() +
                ", indicatorValues=" + indicatorValues.toString() +
                '}';
    }

    @Override
    protected void setDivisor(){
        divisor = BigDecimal.valueOf(indicatorPeriod);
    }

    private boolean isTimeToUpdate(Candlestick candlestick, Candlestick prevCandle) {
        ZonedDateTime nextUpdateTime = prevCandle.getDateTime().plusSeconds(granularity.toSeconds());
        return candlestick.getDateTime().compareTo(nextUpdateTime) >0;
    }

    private Candlestick getLastCandlestick(List<Candlestick> candlestickList, int candleIndex) {
        return candlestickList.get(candleIndex);
    }

    private void setSmoothFactor(){
        smoothFactor = SMOOTH_FACTOR_CONSTANT.divide(
                BigDecimal.valueOf(indicatorPeriod + 1L), 5, BigDecimal.ROUND_HALF_UP);
    }

    private void setSmoothMultiplier(){
        smoothMultiplier = BigDecimal.ONE.subtract(smoothFactor)
                .setScale(5, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Calculation Formula:
     * {@code
     *
     * Initial SMA: sum(PriceImpl) / indicatorPeriod  -> this is the start point of the EMA
     * EMA = PriceImpl(t) * k + EMA(y) * (1 – k)
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
        this.indicatorValues.add(smaValue.divide(divisor, 5, BigDecimal.ROUND_HALF_UP));
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
                .setScale(5, BigDecimal.ROUND_HALF_UP);
    }

    private BigDecimal previousEMASmoothed() {
        int lastIndex = indicatorValues.size()-1;
        return indicatorValues.get(lastIndex)
                .multiply(smoothMultiplier)
                .setScale(5, BigDecimal.ROUND_HALF_UP);
    }
}
