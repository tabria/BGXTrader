package trader.indicators.ma;

import com.oanda.v20.instrument.Candlestick;
import com.oanda.v20.instrument.CandlestickData;
import com.oanda.v20.primitives.DateTime;
import trader.candles.CandlesUpdater;
import trader.indicators.enums.CandlestickPriceType;
import java.math.BigDecimal;
import java.util.List;

public final class WeightedMovingAverage extends BaseMovingAverage {

    WeightedMovingAverage(long candlesticksQuantity, CandlestickPriceType candlestickPriceType, CandlesUpdater updater) {
        super(candlestickPriceType, candlesticksQuantity, updater);
        setDivisor();
    }

    @Override
    public void updateMovingAverage(DateTime dateTime) {
        if (candlesUpdated(dateTime)){
            setWMAValues();
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
        return "WeightedMovingAverage{" +
                "candlesticksQuantity=" + candlesticksQuantity +
                ", candlestickPriceType=" + candlestickPriceType.toString() +
                ", maValues=" + maValues.toString() +
                ", points=" + points.toString() +
                ", isTradeGenerated=" + isTradeGenerated +
                '}';
    }

    @Override
    protected void setDivisor(){
        divisor = calculateDivisor();
    }

    private void setWMAValues(){
        List<Candlestick> candlestickList = candlesUpdater.getCandles();
        maValues.clear();

        for (int candleIndex = candlestickList.size()-1; candleIndex >= lastCandlestickIndex() ; candleIndex--) {
            BigDecimal result = calculateWMAValue(candlestickList, candleIndex);
            maValues.add(0, result);
        }
    }

    private long lastCandlestickIndex() {
        return candlesticksQuantity-1;
    }

    /**
     This method calculate Weighted Moving Average(WMA) based on the formula
     * {@code
     * WMA: (Period*Price) + (Period-1)*PrevPrice + ... + Price(Period-1)*1)/(Period*(Period + 1)/2)
     * }
     * @param candlestickList list of available candlesticks
     * @return {@link BigDecimal} current WMA value
     */
    private BigDecimal calculateWMAValue(List<Candlestick> candlestickList, int candleIndex){

        long period = candlesticksQuantity;
        BigDecimal wmaValue = ZERO;
        for (int i = candleIndex; i >= candleIndex - lastCandlestickIndex(); i--) {

            BigDecimal lastCandlePrice = candlestickPriceType
                    .extractPrice(candlestickPriceData(candlestickList, i));
            lastCandlePrice = lastCandlePrice
                    .multiply(BigDecimal.valueOf(period--))
                    .setScale(SCALE, BigDecimal.ROUND_HALF_UP);
            wmaValue = wmaValue
                    .add(lastCandlePrice)
                    .setScale(SCALE, BigDecimal.ROUND_HALF_UP);
        }
        if (candlestickList.size() > 0 && divisor.compareTo(ZERO) != 0)
            return wmaValue.divide(divisor, SCALE, BigDecimal.ROUND_HALF_UP);
        return wmaValue;
    }

    private BigDecimal calculateDivisor(){
        BigDecimal divider = BigDecimal.valueOf(candlesticksQuantity)
                .add(BigDecimal.ONE)
                .setScale(SCALE, BigDecimal.ROUND_HALF_UP);
        divider = divider
                .multiply(BigDecimal.valueOf(candlesticksQuantity))
                .setScale(SCALE,BigDecimal.ROUND_HALF_UP);
        return divider.divide(BigDecimal.valueOf(2), SCALE, BigDecimal.ROUND_HALF_UP);
    }

}
