package trader.indicators.rsi;

import com.oanda.v20.instrument.Candlestick;
import com.oanda.v20.primitives.DateTime;
import trader.candle.CandlesUpdater;
import trader.indicators.BaseIndicator;
import trader.candle.CandlestickPriceType;
import trader.strategies.BGXStrategy.StrategyConfig;
import trader.trades.entities.Point;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static trader.strategies.BGXStrategy.StrategyConfig.*;

public final class RelativeStrengthIndex extends BaseIndicator {


    private static final BigDecimal RSI_MAX_VALUE = BigDecimal.valueOf(100);
    private static final BigDecimal RSI_MIDDLE_VALUE = BigDecimal.valueOf(50);


    RelativeStrengthIndex(long candlesticksQuantity, CandlestickPriceType candlestickPriceType, CandlesUpdater candlesUpdater){
        super(candlesticksQuantity, candlestickPriceType, candlesUpdater);
    }

    @Override
    public List<BigDecimal> getValues() {
        return Collections.unmodifiableList(indicatorValues);
    }

//    @Override
//    public List<Point> getPoints() {
//        return Collections.unmodifiableList(this.points);
//    }

    @Override
    public void updateIndicator() {
//        if (candlesUpdated(dateTime)){
//            setRSIValues();
//            fillPoints();
//            isTradeGenerated = false;
//        }
    }

    @Override
    protected void setDivisor() {

    }

    /**
     * Getter for generated trade check
     * @return {@link boolean} {@code true} if trade is generated
     *                         {@code false} otherwise
     */

    public boolean isTradeGenerated() {
        return isTradeGenerated;
    }

    /**
     * Setter for isTradeGenerated field
     * @param isGenerated boolean value for current trade
     */

    public void setIsTradeGenerated(boolean isGenerated) {
        isTradeGenerated = isGenerated;
    }


    @Override
    public String toString() {
        return "RelativeStrengthIndex{" +
                "candlesticksQuantity=" + candlesticksQuantity +
                ", candlestickPriceType=" + candlestickPriceType.toString() +
                ", rsiValues=" + indicatorValues.toString() +
 //               ", points=" + points.toString() +
                ", isTradeGenerated=" + isTradeGenerated +
                '}';
    }

    private void setRSIValues() {
      //  List<Candlestick> candlestickList = candlesUpdater.getCandles();
        List<Candlestick> candlestickList = null;
        indicatorValues = new ArrayList<>();
        fillIndicatorValues(candlestickList);
    }

    private void fillIndicatorValues(List<Candlestick> candlestickList) {

        FirstRelativeStrength firstRelativeStrength = new FirstRelativeStrength(candlestickList).invoke();
        BigDecimal averageGains = firstRelativeStrength.getAverageGains();
        BigDecimal averageLosses = firstRelativeStrength.getAverageLosses();
        addRSIValue(averageGains, averageLosses);

        for (int candleIndex = (int) this.candlesticksQuantity + 1; candleIndex < candlestickList.size() ; candleIndex++) {

            BigDecimal priceDifference = calculatePriceDifference(candlestickList, candleIndex);
            BigDecimal positiveChange = isPositive(priceDifference) ? priceDifference : ZERO;
            BigDecimal currentAverageGains = calculateAverage(averageGains, positiveChange);
            BigDecimal negativeChange = isPositive(priceDifference) ? ZERO : priceDifference.abs();
            BigDecimal currentAverageLosses = calculateAverage(averageLosses, negativeChange);
            addRSIValue(currentAverageGains, currentAverageLosses);
            averageGains = currentAverageGains;
            averageLosses = currentAverageLosses;
        }
    }

    private boolean isPositive(BigDecimal value) {
        return value.compareTo(ZERO) > 0;
    }

    private BigDecimal candlePrice(List<Candlestick> candlestickList, int candleIndex) {
 //       return candlestickPriceType.extractPrice(candlestickPriceData(candlestickList, candleIndex));
        return null;
    }

    private BigDecimal calculateAverage(BigDecimal currentAverage, BigDecimal change){
        return currentAverage
                .multiply(BigDecimal.valueOf(this.candlesticksQuantity - 1))
                .add(change)
                .divide(BigDecimal.valueOf(this.candlesticksQuantity), SCALE, BigDecimal.ROUND_HALF_UP);
    }

    private void addRSIValue(BigDecimal currentAverageGains, BigDecimal currentAverageLosses){
        if (isNotZero(currentAverageLosses)){
            indicatorValues.add(calculateRSIValue(currentAverageGains, currentAverageLosses));
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
        BigDecimal averageResult = averageGains.divide(averageLosses, SCALE, BigDecimal.ROUND_HALF_UP);
        BigDecimal divider = BigDecimal.ONE.add(averageResult).setScale(SCALE,BigDecimal.ROUND_HALF_UP);
        BigDecimal divisionResult = RSI_MAX_VALUE.divide(divider, SCALE, BigDecimal.ROUND_HALF_UP);

        return RSI_MAX_VALUE.subtract(divisionResult).setScale(SCALE, BigDecimal.ROUND_HALF_UP);
    }

    private BigDecimal calculatePriceDifference(List<Candlestick> candlestickList, int candleIndex) {
        BigDecimal currentPrice = candlePrice(candlestickList, candleIndex);
        BigDecimal previousPrice = candlePrice(candlestickList, candleIndex-1);
        return currentPrice.subtract(previousPrice).setScale(SCALE, BigDecimal.ROUND_HALF_UP);
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
            return gains.divide(BigDecimal.valueOf(candlesticksQuantity), SCALE, BigDecimal.ROUND_HALF_UP);
        }
        BigDecimal getAverageLosses(){
            return losses.divide(BigDecimal.valueOf(candlesticksQuantity), SCALE, BigDecimal.ROUND_HALF_UP);
        }
        FirstRelativeStrength invoke() {
            for (int candleIndex = 1; candleIndex <= candlesticksQuantity; candleIndex++) {
                BigDecimal priceDifference = calculatePriceDifference(candlesticks, candleIndex);
                gains = isPositive(priceDifference) ? gains
                                                         .add(priceDifference)
                                                         .setScale(SCALE, BigDecimal.ROUND_HALF_UP) : gains;
                losses = isNegative(priceDifference) ? losses
                                                          .subtract(priceDifference)
                                                          .setScale(SCALE, BigDecimal.ROUND_HALF_UP) : losses;
            }
            return this;
        }

        private boolean isNegative(BigDecimal value) {
            return value.compareTo(ZERO) < 0;
        }
    }
}
