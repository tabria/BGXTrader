package trader.indicators.ma;

import com.oanda.v20.instrument.Candlestick;
import com.oanda.v20.primitives.DateTime;
import trader.candle.CandlesUpdater;
import trader.indicators.BaseIndicator;
import trader.candle.CandlestickPriceType;

import java.math.BigDecimal;
import java.util.List;

public final class SimpleMovingAverage extends BaseIndicator {

    SimpleMovingAverage(long indicatorPeriod, CandlestickPriceType candlestickPriceType, CandlesUpdater updater) {
        super(indicatorPeriod, candlestickPriceType, updater);
        setDivisor();
    }

    @Override
    public void updateIndicator(DateTime dateTime) {
       if (candlesUpdated(dateTime)){
           setSMAValues();
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
        return "SimpleMovingAverage{" +
                "candlesticksQuantity=" + candlesticksQuantity +
                ", candlestickPriceType=" + candlestickPriceType.toString() +
                ", indicatorValues=" + indicatorValues.toString() +
                ", points=" + points.toString() +
                ", isTradeGenerated=" + isTradeGenerated +
                '}';
    }

    @Override
    protected void setDivisor(){
        divisor = BigDecimal.valueOf(candlesticksQuantity);
    }

    private void setSMAValues(){
     //   List<Candlestick> candlestickList = candlesUpdater.getCandles();
        List<Candlestick> candlestickList = null;
        indicatorValues.clear();
        calculateSMAValue(candlestickList);
    }

    private void calculateSMAValue(List<Candlestick> candlestickList) {
        int countCandlesticks   = 0;
        BigDecimal smaValue = ZERO;
        for (int candleIndex = candlestickList.size()-1; candleIndex >= 0 ; candleIndex--) {
            smaValue = smaValue.add(candlestickPriceType
                    .extractPrice(candlestickPriceData(candlestickList, candleIndex)))
                    .setScale(SCALE, BigDecimal.ROUND_HALF_UP);

            countCandlesticks++;
            if (countCandlesticks == candlesticksQuantity){
                addSMAValue(smaValue);
                smaValue = smaValue.subtract(candlestickPriceType
                        .extractPrice(candlestickPriceData(candlestickList, index(countCandlesticks, candleIndex))))
                        .setScale(SCALE,BigDecimal.ROUND_HALF_UP);
                countCandlesticks--;
            }
        }
    }

    private void addSMAValue(BigDecimal result) {
        indicatorValues.add(0,result.divide(divisor, SCALE, BigDecimal.ROUND_HALF_UP));
    }

    private int index(int count, int i) {
        return i+count -1;
    }

}
