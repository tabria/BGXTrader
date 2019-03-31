package trader.indicator.rsi;


import trader.indicator.CandlesUpdatable;
import trader.candlestick.candle.CandlePriceType;
import trader.candlestick.Candlestick;
import trader.indicator.BaseIndicator;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import static trader.strategy.bgxstrategy.configuration.StrategyConfig.*;

public final class RelativeStrengthIndex extends BaseIndicator {

    private static final BigDecimal RSI_MAX_VALUE = BigDecimal.valueOf(100);
    private static final BigDecimal RSI_MIDDLE_VALUE = BigDecimal.valueOf(50);
    private static final BigDecimal ZERO = BigDecimal.ZERO;

    private BigDecimal averageGains;
    private BigDecimal averageLosses;

    ////////////////////////remove///////////////////////////////////

    RelativeStrengthIndex(long indicatorPeriod, CandlePriceType candlePriceType, CandlesUpdatable candlesUpdater){
        super(indicatorPeriod, candlePriceType, candlesUpdater);
        averageGains = BigDecimal.ZERO;
        averageLosses = BigDecimal.ZERO;
        initiateRSIValues();
    }
    ////////////////////////remove/////////////////////////

    RelativeStrengthIndex(long indicatorPeriod, CandlePriceType candlePriceType, List<Candlestick> candlesList){
        super(indicatorPeriod, candlePriceType, candlesList);
        averageGains = BigDecimal.ZERO;
        averageLosses = BigDecimal.ZERO;
      //  initiateRSIValues();
    }


    @Override
    public List<BigDecimal> getValues() {
        return Collections.unmodifiableList(indicatorValues);
    }

    @Override
    public void updateIndicator() {
        Candlestick candlestick = candlesUpdater.getUpdatedCandle();
        List<Candlestick> candles = candlesUpdater.getCandles();
        Candlestick prevCandle = getCurrentCandle(candles, candles.size() - 1);
        insertRemainingRSIValues(candlestick, prevCandle);
    }

    @Override
    protected void setDivisor() {}

    @Override
    public String toString() {
        return "RelativeStrengthIndex{" +
                "period=" + indicatorPeriod +
                ", candlePriceType=" + candlePriceType.toString() +
                ", rsiValues=" + indicatorValues.toString() +
                '}';
    }

    private void initiateRSIValues() {
        List<Candlestick> candles = candlesUpdater.getCandles();
        setRSIValues(candles);
    }

    private void setRSIValues(List<Candlestick> candlestickList) {
        verifyCalculationInput(candlestickList);
        addIndicatorValues(candlestickList);
    }

    private void addIndicatorValues(List<Candlestick> candlestickList) {
        insertFirstRSIValue(candlestickList);
        for (int candleIndex = (int) this.indicatorPeriod + 1; candleIndex < candlestickList.size() ; candleIndex++) {
            Candlestick currentCandle = getCurrentCandle(candlestickList, candleIndex);
            Candlestick prevCandle = getPrevCandle(candlestickList, candleIndex);
            insertRemainingRSIValues(currentCandle, prevCandle);
        }
    }

    private void insertFirstRSIValue(List<Candlestick> candlestickList) {
        FirstRelativeStrength firstRelativeStrength = new FirstRelativeStrength(candlestickList).invoke();
        averageGains = firstRelativeStrength.getAverageGains();
        averageLosses = firstRelativeStrength.getAverageLosses();
        addRSIValue(averageGains, averageLosses);
    }

    private void insertRemainingRSIValues(Candlestick currentCandle, Candlestick prevCandle) {
        BigDecimal priceDifference = calculatePriceDifference(currentCandle, prevCandle);
        BigDecimal positiveChange = isPositive(priceDifference) ? priceDifference : ZERO;
        BigDecimal negativeChange = isPositive(priceDifference) ? ZERO : priceDifference.abs();
        averageGains = calculateAverage(averageGains, positiveChange);
        averageLosses = calculateAverage(averageLosses, negativeChange);
        addRSIValue(averageGains, averageLosses);
    }

    private Candlestick getPrevCandle(List<Candlestick> candlestickList, int candleIndex) {
        return candlestickList.get(candleIndex-1);
    }

    private Candlestick getCurrentCandle(List<Candlestick> candlestickList, int candleIndex) {
        return candlestickList.get(candleIndex);
    }

    private boolean isPositive(BigDecimal value) {
        return value.compareTo(ZERO) > 0;
    }

    private BigDecimal calculateAverage(BigDecimal currentAverage, BigDecimal change){
        return currentAverage
                .multiply(BigDecimal.valueOf(this.indicatorPeriod - 1))
                .add(change)
                .divide(BigDecimal.valueOf(this.indicatorPeriod), SCALE, BigDecimal.ROUND_HALF_UP);
    }

    private void addRSIValue(BigDecimal currentAverageGains, BigDecimal currentAverageLosses){
        if (isNotZero(currentAverageLosses)){
            indicatorValues
                    .add(calculateRSIValue(currentAverageGains, currentAverageLosses));
            return;
        }
        if (isNotZero(currentAverageGains)) {
            indicatorValues.add(RSI_MAX_VALUE);
            return;
        }
        indicatorValues.add(RSI_MIDDLE_VALUE);
    }

    private boolean isNotZero(BigDecimal currentAverages) {
        return currentAverages.compareTo(ZERO) != 0;
    }

    private BigDecimal calculateRSIValue(BigDecimal averageGains, BigDecimal averageLosses){
        BigDecimal averageResult = averageGains
                .divide(averageLosses, SCALE, BigDecimal.ROUND_HALF_UP);
        BigDecimal divider = BigDecimal.ONE
                .add(averageResult).setScale(SCALE,BigDecimal.ROUND_HALF_UP);
        BigDecimal divisionResult = RSI_MAX_VALUE
                .divide(divider, SCALE, BigDecimal.ROUND_HALF_UP);
        return RSI_MAX_VALUE.subtract(divisionResult)
                .setScale(SCALE, BigDecimal.ROUND_HALF_UP);
    }

    private BigDecimal calculatePriceDifference(Candlestick currentCandle, Candlestick prevCandle) {
        return  obtainPrice(currentCandle)
                .subtract(obtainPrice(prevCandle))
                .setScale(SCALE, BigDecimal.ROUND_HALF_UP);
    }

    private class FirstRelativeStrength {
        private List<Candlestick> candlesticks;
        private BigDecimal gains;
        private BigDecimal losses;

        FirstRelativeStrength(List<Candlestick> candlestickList) {
            candlesticks = candlestickList;
            gains = ZERO;
            losses =ZERO;
        }
        BigDecimal getAverageGains(){
            return gains
                    .divide(BigDecimal.valueOf(indicatorPeriod), SCALE, BigDecimal.ROUND_HALF_UP);
        }
        BigDecimal getAverageLosses(){
            return losses
                    .divide(BigDecimal.valueOf(indicatorPeriod), SCALE, BigDecimal.ROUND_HALF_UP);
        }
        FirstRelativeStrength invoke() {
            for (int candleIndex = 1; candleIndex <= indicatorPeriod; candleIndex++) {
                BigDecimal priceDifference =
                        calculatePriceDifference(getCurrentCandle(candlesticks, candleIndex), getPrevCandle(candlesticks, candleIndex));
                gains = isPositive(priceDifference) ? gains.add(priceDifference).setScale(SCALE, BigDecimal.ROUND_HALF_UP) : gains;
                losses = isNegative(priceDifference) ? losses.subtract(priceDifference).setScale(SCALE, BigDecimal.ROUND_HALF_UP) : losses;
            }
            return this;
        }

        private boolean isNegative(BigDecimal value) {
            return value.compareTo(ZERO) < 0;
        }
    }
}
