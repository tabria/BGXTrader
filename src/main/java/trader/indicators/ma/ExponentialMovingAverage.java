package trader.indicators.ma;

import com.oanda.v20.instrument.Candlestick;
import com.oanda.v20.primitives.DateTime;
import trader.candles.CandlesUpdater;
import trader.indicators.BaseIndicator;
import trader.indicators.enums.CandlestickPriceType;

import java.math.BigDecimal;
import java.util.List;

public final class ExponentialMovingAverage extends BaseIndicator {

    private static final BigDecimal MULTIPLIER_CONSTANT = BigDecimal.valueOf(2);

    private BigDecimal multiplier;
    private BigDecimal multiplierCorrected;

    ExponentialMovingAverage(long candlesticksQuantity, CandlestickPriceType candlestickPriceType, CandlesUpdater updater) {
        super(candlestickPriceType, candlesticksQuantity, updater);
        setDivisor();
        setMultiplier();
        setMultiplierCorrected();

    }

    @Override
    public void updateMovingAverage(DateTime dateTime) {
        if (candlesUpdated(dateTime)){
            setEMAValues();
            fillPoints();
            isTradeGenerated = false;
        }
    }

    /**
     * Getter for generated trade check
     * @return {@link boolean} {@code true} if trade is generated
     *                         {@code false} otherwise
     */
    @Override
    public boolean isTradeGenerated() {
        return isTradeGenerated;
    }

    /**
     * Setter for isTradeGenerated field
     * @param isGenerated boolean value for current trade
     */
    @Override
    public void setIsTradeGenerated(boolean isGenerated) {
        isTradeGenerated = isGenerated;
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
        multiplier = MULTIPLIER_CONSTANT.divide(
                BigDecimal.valueOf(candlesticksQuantity + 1), SCALE, BigDecimal.ROUND_HALF_UP);
    }

    private void setMultiplierCorrected(){
        multiplierCorrected = BigDecimal.ONE.subtract(multiplier)
                .setScale(SCALE, BigDecimal.ROUND_HALF_UP);
    }

    @Override
    protected void setDivisor(){
        divisor = BigDecimal.valueOf(candlesticksQuantity);
    }

    private void setEMAValues(){
        List<Candlestick> candlestickList = candlesUpdater.getCandles();
        maValues.clear();
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
            BigDecimal smaValue = ZERO;
            for (int candleIndex = 0; candleIndex <= candlesticksQuantity -1 ; candleIndex++) {
                smaValue = smaValue.add(candlestickPriceType
                        .extractPrice(candlestickPriceData(candlestickList, candleIndex)))
                        .setScale(SCALE, BigDecimal.ROUND_HALF_UP);
            }
            return smaValue.divide(divisor, SCALE, BigDecimal.ROUND_HALF_UP);
        }

    private void addOtherValues(List<Candlestick> candlestickList) {
        int maValuesIndex = 0;
        for (int index = (int) candlesticksQuantity; index < candlestickList.size() ; index++) {
            BigDecimal correctedPrice = candlestickPriceType
                    .extractPrice(candlestickPriceData(candlestickList, index))
                    .multiply(multiplier)
                    .setScale(SCALE, BigDecimal.ROUND_HALF_UP);
            BigDecimal previousEMA = maValues.get(maValuesIndex++);
            BigDecimal emaValue = calculateFinalEMAValue(correctedPrice, previousEMA);
            maValues.add(emaValue);
        }
    }

        private BigDecimal calculateFinalEMAValue(BigDecimal correctedPrice, BigDecimal previousEMA) {
            BigDecimal result = previousEMA
                    .multiply(multiplierCorrected)
                    .setScale(SCALE, BigDecimal.ROUND_HALF_UP);
            return correctedPrice
                    .add(result)
                    .setScale(SCALE, BigDecimal.ROUND_HALF_UP);
        }

}
