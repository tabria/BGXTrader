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

public final class ExponentialMA implements Indicator {

    private static final BigDecimal MULTIPLIER_CONSTANT = BigDecimal.valueOf(2);

    private final long period;
    private final CandlestickPriceType candlestickPriceType;
    private final CandlesUpdater updater;
    private List<BigDecimal> maValues;
    private List<Point> points;
    private BigDecimal multiplier;
    private BigDecimal multiplierCorrected;
    private BigDecimal divisor;
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
    ExponentialMA(long period, CandlestickPriceType candlestickPriceType, CandlesUpdater updater) {
        this.period = period;
        this.candlestickPriceType = candlestickPriceType;
        this.updater = updater;
        this.setDivisor(this.period);
        this.setMultiplier();
        this.setMultiplierCorrected();
        this.maValues = new ArrayList<>();
        this.points = new ArrayList<>();
        this.isTradeGenerated = false;

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
     * Get the EMA values
     * @return {@link List} calculated value for the Exponential Moving Average
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
        isUpdated = !isUpdated && this.maValues.size() == 0 ? true : isUpdated;
        if (isUpdated){
            this.setEMAValues();
            fillPoints();
            this.isTradeGenerated = false;
        }
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
        return "ExponentialMA{" +
                "candlesticksQuantity=" + period +
                ", candlestickPriceType=" + candlestickPriceType.toString() +
                ", maValues=" + maValues.toString() +
                ", points=" + points.toString() +
                ", isTradeGenerated=" + isTradeGenerated +
                '}';
    }

    /**
     * Get fill points for signal checking
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

    /**
     * Set Moving Average values
     */
    private void setEMAValues(){

        List<Candlestick> candlestickList = this.updater.getCandles();
        this.maValues.clear();
        calculateEMAValue(candlestickList);
    }

    /**
     * This method calculate Exponential Moving Average(EMA) based on the formula:
     * {@code
     * Initial SMA take extra (period) prices from the first price for the period.
     * If we have 10 period EMA, the quantity of available prices must be 20 -> 10 for the Initial SMA and 10 for EMA
     * Initial SMA: sum(Price) / period  -> this is the start point of the EMA
     *
     * Multiplier: (2 / (Time periods + 1) )
     * EMA: (Price * Multiplier) + (Previous EMA * (1 - Multiplier))
     * }
     *
     * @param candlestickList available candlesticks
     * @see Candlestick
     */
    private void calculateEMAValue(List<Candlestick> candlestickList){
        BigDecimal firstPrice = calculateSMAValue(candlestickList);
        this.maValues.add(firstPrice);
        int maValuesIndex = 0;
        for (int i = (int) this.period; i < candlestickList.size() ; i++) {
            CandlestickData candleMid = candlestickList.get(i).getMid();
            BigDecimal lastPriceCorrected = this.candlestickPriceType
                    .extractPrice(candleMid)
                    .multiply(this.multiplier)
                    .setScale(5, BigDecimal.ROUND_HALF_UP);

            //prevEMA
            BigDecimal previousEMA = this.maValues.get(maValuesIndex++);

            //prevEma * (1-multiplier)
            BigDecimal subResult = previousEMA.multiply(this.multiplierCorrected)
                    .setScale(5, BigDecimal.ROUND_HALF_UP);

            //return current period EMA value
            BigDecimal result = lastPriceCorrected.add(subResult)
                    .setScale(5, BigDecimal.ROUND_HALF_UP);
            this.maValues.add(result);
        }
    }

    /**
     * set multiplier
     */
    private void setMultiplier(){
        // calculate multiplier (2 / period + 1)
        this.multiplier = MULTIPLIER_CONSTANT.divide(
                BigDecimal.valueOf(this.period + 1),
                5,
                BigDecimal.ROUND_HALF_UP);
    }

    private void setMultiplierCorrected(){
        //(1-Multiplier)
        this.multiplierCorrected = BigDecimal.ONE
                .subtract(this.multiplier)
                .setScale(5, BigDecimal.ROUND_HALF_UP);
    }
    /**
     * Calculate the SMA
     * @param candlestickList list of available candlesticks
     * @return {@link BigDecimal} sma value for the chosen period and candlestickPriceType
     * @see Candlestick
     */
    private BigDecimal calculateSMAValue(List<Candlestick> candlestickList){
        BigDecimal result = BigDecimal.ZERO;

        for (int i = 0; i <= this.period-1 ; i++) {
            CandlestickData candleMid = candlestickList.get(i).getMid();
            //add the price based on the candlestickPriceType enum
            result = result.add(this.candlestickPriceType.extractPrice(candleMid))
                    .setScale(5, BigDecimal.ROUND_HALF_UP);
        }
        return result.divide(this.divisor, 5, BigDecimal.ROUND_HALF_UP);
    }

    private void setDivisor(long period){
        this.divisor = BigDecimal.valueOf(period);
    }
}
