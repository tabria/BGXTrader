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
 * Simple Moving Average (SMA)
 */

public final class SimpleMA implements Indicator {

    private final long period;
    private final CandlestickPriceType candlestickPriceType;
    private final CandlesUpdater updater;
    private List<BigDecimal> maValues;
    private BigDecimal divisor;
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
    SimpleMA(long period, CandlestickPriceType candlestickPriceType, CandlesUpdater updater) {
        this.period = period;
        this.candlestickPriceType = candlestickPriceType;
        this.updater = updater;
        this.setDivisor(this.period);
        this.maValues = new ArrayList<>();
        this.points = new ArrayList<>();
        this.isTradeGenerated = false;
    }
    /**
     * Get the current SMA values
     * @return {@link List} calculated value for the Simple Moving Average
     */
    @Override
    public List<BigDecimal> getValues() {
        return Collections.unmodifiableList(this.maValues);
    }

    /**
     * Update sma values
     * @param dateTime the dateTime from the last price poll request
     * @see DateTime
     * @see CandlesUpdater
     */
    @Override
    public void update(DateTime dateTime, BigDecimal ask, BigDecimal bid) {

       boolean isUpdated =  this.updater.updateCandles(dateTime);
        isUpdated = !isUpdated && this.maValues.size() == 0 ? true : isUpdated;
       if (isUpdated){
           this.setSMAValues();
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
        return "SimpleMA{" +
                "period=" + period +
                ", candlestickPriceType=" + candlestickPriceType.toString() +
                ", maValues=" + maValues.toString() +
                ", points=" + points.toString() +
                ", isTradeGenerated=" + isTradeGenerated +
                '}';
    }

    /**
     * Set calculated values
     */
    private void setSMAValues(){

        List<Candlestick> candlestickList = this.updater.getCandles();
        this.maValues.clear();
        calculateSMAValue(candlestickList);
    }

    /**
     * Calculate Simple Moving Average values
     * @param candlestickList list of available candlesticks
     */
    private void calculateSMAValue(List<Candlestick> candlestickList) {

        int count = 0;

        BigDecimal result = BigDecimal.ZERO;

        for (int i = candlestickList.size()-1; i >= 0 ; i--) {

            //getting Open, Close, High, Low prices for each candle
            CandlestickData candleMid = candlestickList.get(i).getMid();

            //add the price based on the candlestickPriceType enum
            result = result.add(this.candlestickPriceType.extractPrice(candleMid)).setScale(5, BigDecimal.ROUND_HALF_UP);

            count++;
            if (count == this.period){

                //the values are calculated from the newest to oldest, so the add is always at 0 index
                this.maValues.add(0,result.divide(this.divisor, 5, BigDecimal.ROUND_HALF_UP));

                //getting the oldest candle participating in current result
                candleMid = candlestickList.get(i + count -1).getMid();

                //removing oldest candle's price from the result
                result = result.subtract(this.candlestickPriceType.extractPrice(candleMid))
                        .setScale(5,BigDecimal.ROUND_HALF_UP);
                count--;
            }
        }
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

    private void setDivisor(long period){
        this.divisor = BigDecimal.valueOf(period);
    }

}
