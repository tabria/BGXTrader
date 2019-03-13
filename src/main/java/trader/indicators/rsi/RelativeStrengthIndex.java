package trader.indicators.rsi;

import com.oanda.v20.instrument.Candlestick;
import com.oanda.v20.primitives.DateTime;

import trader.candles.CandlesUpdater;
import trader.indicators.Indicator;
import trader.indicators.enums.CandlestickPriceType;
import trader.prices.PriceObservable;
import trader.trades.entities.Point;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class RelativeStrengthIndex implements Indicator {


    private static final BigDecimal RSI_MAX_VALUE = BigDecimal.valueOf(100);
    private static final BigDecimal RSI_MIDDLE_VALUE = BigDecimal.valueOf(50);

    private final long candlesticksQuantity;
    private final CandlestickPriceType candlestickPriceType;
    private List<BigDecimal> rsiValues;
    private final CandlesUpdater updater;
    private List<Point> points;
    private boolean isTradeGenerated;


    RelativeStrengthIndex(long candlesticksQuantity, CandlestickPriceType candlestickPriceType, CandlesUpdater candlesUpdater){
        this.candlesticksQuantity = candlesticksQuantity;
        this.candlestickPriceType = candlestickPriceType;
        this.updater = candlesUpdater;
        this.rsiValues = new ArrayList<>();
        this.points = new ArrayList<>();
        this.isTradeGenerated = false;
    }

    /**
     * Get the current values of the RSI
     * @return {@link List} unmodifiable list of {@link BigDecimal} values
     */
    @Override
    public List<BigDecimal> getValues() {
        return Collections.unmodifiableList(this.rsiValues);
    }

    /**
     * Update RSI
     * @param dateTime new dateTime from price polling
     * @see CandlesUpdater
     * @see PriceObservable
     */
    @Override
    public void updateMovingAverage(DateTime dateTime, BigDecimal ask, BigDecimal bid) {

        boolean isUpdated =  this.updater.updateCandles(dateTime);
        isUpdated = !isUpdated && this.rsiValues.size() == 0 ? true : isUpdated;
        if (isUpdated){
            this.setRSIValues();
            this.isTradeGenerated = false;
        }
    }

    /**
     * Get point for calculating intersections
     * @return {@link List<Point>}
     * @see Point
     */
    @Override
    public List<Point> getPoints() {
        return Collections.unmodifiableList(this.points);
    }

    /**
     * Getter for generated trade check
     * @return {@link boolean} {@code true} if trade is generated
     *                         {@code false} otherwise
     */
    @Override
    public boolean isTradeGenerated() {
        return this.isTradeGenerated;
    }

    /**
     * Setter for isTradeGenerated field
     * @param isGenerated boolean value for current trade
     */
    @Override
    public void setIsTradeGenerated(boolean isGenerated) {
        this.isTradeGenerated = isGenerated;
    }


    @Override
    public String toString() {
        return "RelativeStrengthIndex{" +
                "candlesticksQuantity=" + candlesticksQuantity +
                ", candlestickPriceType=" + candlestickPriceType.toString() +
                ", rsiValues=" + rsiValues.toString() +
                ", points=" + points.toString() +
                ", isTradeGenerated=" + isTradeGenerated +
                '}';
    }

    /**
     * Set calculated value
     */
    private void setRSIValues() {
        List<Candlestick> candlestickList = this.updater.getCandles();
        this.rsiValues = new ArrayList<>();
        calculateRSI(candlestickList);
    }

    /**
     * Calculate RSI value
     * @param candlestickList available candlesticks
     */
    private void calculateRSI(List<Candlestick> candlestickList) {
        BigDecimal frsGains = BigDecimal.ZERO;
        BigDecimal frsLosses = BigDecimal.ZERO;

        //calculate First RS from 1 to candlesticksQuantity candlesticks
        for (int i = 1; i <=this.candlesticksQuantity; i++) {
            BigDecimal currentPrice = this.candlestickPriceType.extractPrice(candlestickList.get(i).getMid());
            BigDecimal previousPrice =  this.candlestickPriceType.extractPrice(candlestickList.get(i-1).getMid());

            BigDecimal change = currentPrice.subtract(previousPrice).setScale(5, BigDecimal.ROUND_HALF_UP);

            if (change.compareTo(BigDecimal.ZERO) > 0){
                frsGains = frsGains.add(change).setScale(5, BigDecimal.ROUND_HALF_UP);
            } else if (change.compareTo(BigDecimal.ZERO) < 0)  {
                frsLosses = frsLosses.subtract(change).setScale(5, BigDecimal.ROUND_HALF_UP);
            }
        }

        BigDecimal averageGains = frsGains.divide(BigDecimal.valueOf(this.candlesticksQuantity), 5, BigDecimal.ROUND_HALF_UP);
        BigDecimal averageLosses = frsLosses.divide(BigDecimal.valueOf(this.candlesticksQuantity), 5, BigDecimal.ROUND_HALF_UP);

        //add RSI Value
        addRSIValue(averageGains, averageLosses);

        //main calculation
        for (int i = (int) this.candlesticksQuantity + 1; i < candlestickList.size() ; i++) {

            BigDecimal currentPrice = this.candlestickPriceType.extractPrice(candlestickList.get(i).getMid());
            BigDecimal previousPrice =  this.candlestickPriceType.extractPrice(candlestickList.get(i-1).getMid());

            BigDecimal change = currentPrice.subtract(previousPrice).setScale(5, BigDecimal.ROUND_HALF_UP);

            BigDecimal positiveChange = BigDecimal.ZERO;
            BigDecimal negativeChange = BigDecimal.ZERO;
            if (change.compareTo(BigDecimal.ZERO) > 0){
                positiveChange = change;
            } else {
                negativeChange = change.abs();
            }

            //(prevAverageGains * (candlesticksQuantity - 1) + (change > 0 ? change : 0))/candlesticksQuantity
            BigDecimal currentAverageGains = calculateAverage(averageGains, positiveChange);
            //(prevAverageLosses * (candlesticksQuantity - 1) + (change < 0 ? -change : 0))/candlesticksQuantity
            BigDecimal currentAverageLosses = calculateAverage(averageLosses, negativeChange);

            //set rsiValue
            addRSIValue(currentAverageGains, currentAverageLosses);

            averageGains = currentAverageGains;
            averageLosses = currentAverageLosses;
        }
    }

    /**
     * Calculate averages
     * @param prevAverage previous average
     * @param change difference between current and previous price
     * @return {@link BigDecimal} calculated average value
     */
    private BigDecimal calculateAverage(BigDecimal prevAverage, BigDecimal change){

        //(prevAverageGains * (candlesticksQuantity - 1) + (change > 0 ? change : 0))/candlesticksQuantity
        //(prevAverageLosses * (candlesticksQuantity - 1) + (change < 0 ? -change : 0))/candlesticksQuantity

        //(prevAverageGains * (candlesticksQuantity - 1)
        BigDecimal result = prevAverage.multiply(BigDecimal.valueOf(this.candlesticksQuantity - 1)).setScale(5, BigDecimal.ROUND_HALF_UP);
        //(prevAverageGains * (candlesticksQuantity - 1) + (change > 0 ? change : 0))
        result = result.add(change).setScale(5, BigDecimal.ROUND_HALF_UP);
        //(prevAverageGains * (candlesticksQuantity - 1) + (change > 0 ? change : 0))/candlesticksQuantity
        return result.divide(BigDecimal.valueOf(this.candlesticksQuantity), 5, BigDecimal.ROUND_HALF_UP);
    }

    private void addRSIValue(BigDecimal currentAverageGains, BigDecimal currentAverageLosses){
        //calculate rsiValue
        if (currentAverageLosses.compareTo(BigDecimal.ZERO) != 0){
            this.rsiValues.add(calculateRSIValue(currentAverageGains, currentAverageLosses));
        } else {
            if (currentAverageGains.compareTo(BigDecimal.ZERO) != 0){
                this.rsiValues.add(RSI_MAX_VALUE);
            } else {
                this.rsiValues.add(RSI_MIDDLE_VALUE);
            }
        }
    }

    /**
     * Calculate single rsi value
     * @param averageGains average gains
     * @param averageLosses average losses
     * @return {@link BigDecimal} calculated value
     */
    private BigDecimal calculateRSIValue(BigDecimal averageGains, BigDecimal averageLosses){

        //100 - (100/(1+frsAverageGains/frsAverageLosses))

        //AverageGains/AverageLosses
        BigDecimal averageResult = averageGains.divide(averageLosses, 5, BigDecimal.ROUND_HALF_UP);
        //(1+AverageGains/AverageLosses)
        BigDecimal divider = BigDecimal.ONE.add(averageResult).setScale(5,BigDecimal.ROUND_HALF_UP);
        //100/(1+AverageGains/AverageLosses)
        BigDecimal divisionResult = RSI_MAX_VALUE.divide(divider, 5, BigDecimal.ROUND_HALF_UP);

        //100 - (100/(1+AverageGains/AverageLosses))
        return RSI_MAX_VALUE.subtract(divisionResult).setScale(5, BigDecimal.ROUND_HALF_UP);
    }

    //TODO to be implemented for divergence
    private void fillPoints() {

    }
}
