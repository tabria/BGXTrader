package trader.indicators.ma;

import com.oanda.v20.instrument.Candlestick;
import com.oanda.v20.instrument.CandlestickData;
import com.oanda.v20.primitives.DateTime;
import trader.candles.CandlesUpdater;
import trader.indicators.Indicator;
import trader.indicators.enums.CandlestickPriceType;
import trader.trades.entities.Point;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Weighted Moving Average (WMA)
 */
public final class WeightedMovingAverage implements Indicator {

    private final long period;
    private final CandlestickPriceType candlestickPriceType;
    private final CandlesUpdater updater;
    private List<BigDecimal> maValues;
    private List<Point> points;
    private boolean isTradeGenerated;


    /**
     * Constructor
     *
     * @param period sma period
     * @param candlestickPriceType what price to get from the candlestick to calculate sma
     * @param updater update candlestick collection
     * @see CandlestickPriceType
     * @see CandlesUpdater
     */
    WeightedMovingAverage(long period, CandlestickPriceType candlestickPriceType, CandlesUpdater updater) {
        this.period = period;
        this.candlestickPriceType = candlestickPriceType;
        this.updater = updater;
        this.maValues = new ArrayList<>();
        this.points = new ArrayList<>();
        this.isTradeGenerated = false;
    }

    /**
     * Get the current WMA values
     * @return {@link List} calculated values for the Weighted Moving Average
     */
    @Override
    public List<BigDecimal> getValues() {
        return Collections.unmodifiableList(this.maValues);
    }

    /**
     * recalculate the maValue, when the new candle appear
     * @param dateTime the dateTime from the last price poll request
     * @see DateTime
     * @see CandlesUpdater
     */
    @Override
    public void update(DateTime dateTime, BigDecimal ask, BigDecimal bid) {

        boolean isUpdated =  this.updater.updateCandles(dateTime);
        //cannot be simplified because will break update for first update or for every update after first
        isUpdated = !isUpdated && this.maValues.size() == 0 ? true : isUpdated;
        if (isUpdated){
            this.setWMAValues();
            fillPoints();
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
        return "WeightedMovingAverage{" +
                "candlesticksQuantity=" + period +
                ", candlestickPriceType=" + candlestickPriceType.toString() +
                ", maValues=" + maValues.toString() +
                ", points=" + points.toString() +
                ", isTradeGenerated=" + isTradeGenerated +
                '}';
    }

    /**
     * Set Weighted Moving Average values
     */
    private void setWMAValues(){

        List<Candlestick> candlestickList = this.updater.getCandles();
        this.maValues.clear();

        for (int i = candlestickList.size()-1; i >= this.period - 1 ; i--) {
            long endIndex = i - (this.period - 1);
            BigDecimal result = calculateWMAValue(candlestickList, i, endIndex );
            this.maValues.add(0, result);
        }
    }

    /**
     This method calculate Weighted Moving Average(WMA) based on this formula
     * {@code
     * WMA: (Period*Price) + (Period-1)*PrevPrice + ...Price(Period-1)*1)/(Period*(Period + 1)/2)
     * }
     * @param candlestickList list of available candlesticks
     * @return {@link BigDecimal} current WMA value
     */
    private BigDecimal calculateWMAValue(List<Candlestick> candlestickList, int startIndex, long endIndex){

        //calculate divider
        BigDecimal divider = calculateDivider(this.period);

        long period = this.period;

        BigDecimal result = BigDecimal.ZERO;
        for (int i = startIndex; i >= endIndex ; i--) {

            CandlestickData candleMid = candlestickList.get(i).getMid();

            BigDecimal lastPrice = this.candlestickPriceType.extractPrice(candleMid);
            lastPrice = lastPrice.multiply(BigDecimal.valueOf(period)).setScale(5, BigDecimal.ROUND_HALF_UP);

            result = result.add(lastPrice).setScale(5, BigDecimal.ROUND_HALF_UP);
            period--;

        }

        if (candlestickList.size() > 0 && divider.compareTo(BigDecimal.ZERO) != 0){
            result = result.divide(divider, 5, BigDecimal.ROUND_HALF_UP);
        }
        return result;

    }

    /**
     * Calculate divider
     *
     * @param period current period for the WMA
     * @return {@link BigDecimal} divider for calculating WMA value
     */
    private BigDecimal calculateDivider(long period){
        //Period + 1
        BigDecimal divider = BigDecimal.valueOf(period).add(BigDecimal.ONE).setScale(5, BigDecimal.ROUND_HALF_UP);
        //Period * (Period + 1)
        divider = divider.multiply(BigDecimal.valueOf(period)).setScale(5,BigDecimal.ROUND_HALF_UP);
        //(Period *(Period + 1)) / 2
        divider = divider.divide(BigDecimal.valueOf(2), 5, BigDecimal.ROUND_HALF_UP);
        return divider;
    }


    /**
     * Fill points for signal checking
     */
    private void fillPoints() {
        this.points.clear();
        int time = 1;
        for (int i = this.maValues.size()-4; i < this.maValues.size() -1 ; i++) {
            Point point = new Point.PointBuilder(this.maValues.get(i))
                    .setTime(BigDecimal.valueOf(time++))
                    .build();

            this.points.add(point);
        }

    }

}
