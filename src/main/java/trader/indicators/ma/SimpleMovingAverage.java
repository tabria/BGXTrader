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

public final class SimpleMovingAverage extends BaseMovingAverage {

    SimpleMovingAverage(long candlesticksQuantity, CandlestickPriceType candlestickPriceType, CandlesUpdater updater) {
        super(candlestickPriceType, candlesticksQuantity, updater);
        setDivisor();
    }

    @Override
    public void updateMovingAverage(DateTime dateTime) {

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
                ", maValues=" + maValues.toString() +
                ", points=" + points.toString() +
                ", isTradeGenerated=" + isTradeGenerated +
                '}';
    }

    @Override
    protected void setDivisor(){
        divisor = BigDecimal.valueOf(candlesticksQuantity);
    }

    private void setSMAValues(){
        List<Candlestick> candlestickList = candlesUpdater.getCandles();
        maValues.clear();
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
        maValues.add(0,result.divide(divisor, SCALE, BigDecimal.ROUND_HALF_UP));
    }

    private int index(int count, int i) {
        return i+count -1;
    }

}
