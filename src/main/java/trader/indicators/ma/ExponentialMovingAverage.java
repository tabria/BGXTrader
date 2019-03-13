package trader.indicators.ma;

import com.oanda.v20.instrument.Candlestick;
import com.oanda.v20.instrument.CandlestickData;
import com.oanda.v20.primitives.DateTime;
import trader.candles.CandlesUpdater;
import trader.indicators.enums.CandlestickPriceType;
import trader.trades.entities.Point;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public final class ExponentialMovingAverage extends BaseMovingAverage {

    private static final BigDecimal MULTIPLIER_CONSTANT = BigDecimal.valueOf(2);

    private BigDecimal multiplier;
    private BigDecimal multiplierCorrected;
    private BigDecimal divisor;

    ExponentialMovingAverage(long candlesticksQuantity, CandlestickPriceType candlestickPriceType, CandlesUpdater updater) {
        super(candlestickPriceType, candlesticksQuantity, updater);
        this.setDivisor(this.candlesticksQuantity);
        this.setMultiplier();
        this.setMultiplierCorrected();

    }

    @Override
    public List<Point> getPoints() {
        return Collections.unmodifiableList(points);
    }

    @Override
    public List<BigDecimal> getValues() {
        return Collections.unmodifiableList(maValues);
    }

    @Override
    public void updateMovingAverage(DateTime dateTime, BigDecimal ask, BigDecimal bid) {

        boolean isUpdated =  this.candlesUpdater.updateCandles(dateTime);
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
        return "ExponentialMovingAverage{" +
                "candlesticksQuantity=" + candlesticksQuantity +
                ", candlestickPriceType=" + candlestickPriceType.toString() +
                ", maValues=" + maValues.toString() +
                ", points=" + points.toString() +
                ", isTradeGenerated=" + isTradeGenerated +
                '}';
    }

    private void setMultiplier(){
        this.multiplier = MULTIPLIER_CONSTANT.divide(
                BigDecimal.valueOf(this.candlesticksQuantity + 1),
                5,
                BigDecimal.ROUND_HALF_UP);
    }

    private void setMultiplierCorrected(){
        this.multiplierCorrected = BigDecimal.ONE
                .subtract(this.multiplier)
                .setScale(5, BigDecimal.ROUND_HALF_UP);
    }

    private void setDivisor(long period){
        this.divisor = BigDecimal.valueOf(period);
    }

    private void setEMAValues(){
        List<Candlestick> candlestickList = this.candlesUpdater.getCandles();
        this.maValues.clear();
        calculateEMAValue(candlestickList);
    }

    /**
     * This method calculate Exponential Moving Average(EMA) based on the formula:
     * {@code
     * If we have 10 candlesticksQuantity EMA, the quantity of available prices must be 20 -> 10 for the Initial SMA and 10 for EMA
     * Initial SMA: sum(Price) / candlesticksQuantity  -> this is the start point of the EMA
     *
     * Multiplier: (2 / (Time periods + 1) )
     * EMA: (Price * Multiplier) + (Previous EMA * (1 - Multiplier))
     * }
     *
     * @param candlestickList available candlesticks
     * @see Candlestick
     */
    private void calculateEMAValue(List<Candlestick> candlestickList){
        addStartValue(candlestickList);
        addOtherValues(candlestickList);
    }

    private void addStartValue(List<Candlestick> candlestickList) {
        this.maValues.add(calculateSMAValue(candlestickList));
    }

        private BigDecimal calculateSMAValue(List<Candlestick> candlestickList){
            BigDecimal result = BigDecimal.ZERO;
            for (int i = 0; i <= this.candlesticksQuantity -1 ; i++) {
                CandlestickData candleMid = candlestickList.get(i).getMid();
                result = result.add(getCandlePrice(candleMid)).setScale(5, BigDecimal.ROUND_HALF_UP);
            }
            return result.divide(this.divisor, 5, BigDecimal.ROUND_HALF_UP);
        }

            private BigDecimal getCandlePrice(CandlestickData candleMid) {
                return this.candlestickPriceType.extractPrice(candleMid);
            }

    private void addOtherValues(List<Candlestick> candlestickList) {
        int maValuesIndex = 0;
        for (int index = (int) this.candlesticksQuantity; index < candlestickList.size() ; index++) {
            BigDecimal correctedPrice = calculateCorrectedPrice(candlestickList.get(index));
            BigDecimal previousEMA = this.maValues.get(maValuesIndex++);
            BigDecimal result = calculateFinalEMAValue(correctedPrice, previousEMA);
            this.maValues.add(result);
        }
    }

        private BigDecimal calculateCorrectedPrice(Candlestick candlestick) {
            return this.candlestickPriceType.extractPrice(candlestick.getMid())
                    .multiply(this.multiplier)
                    .setScale(5, BigDecimal.ROUND_HALF_UP);
        }

        private BigDecimal calculateFinalEMAValue(BigDecimal correctedPrice, BigDecimal previousEMA) {
            BigDecimal result = previousEMA.multiply(this.multiplierCorrected)
                    .setScale(5, BigDecimal.ROUND_HALF_UP);
            result = correctedPrice.add(result)
                    .setScale(5, BigDecimal.ROUND_HALF_UP);
            return result;
        }

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
