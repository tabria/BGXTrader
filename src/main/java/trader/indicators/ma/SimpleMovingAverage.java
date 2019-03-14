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

    private BigDecimal divisor;

    SimpleMovingAverage(long candlesticksQuantity, CandlestickPriceType candlestickPriceType, CandlesUpdater updater) {
        super(candlestickPriceType, candlesticksQuantity, updater);
        this.setDivisor(candlesticksQuantity);
    }

    @Override
    public List<Point> getPoints() {
        return Collections.unmodifiableList(this.points);
    }

    @Override
    public List<BigDecimal> getValues() {
        return Collections.unmodifiableList(this.maValues);
    }

    @Override
    public void updateMovingAverage(DateTime dateTime, BigDecimal ask, BigDecimal bid) {

       boolean isUpdated =  this.candlesUpdater.updateCandles(dateTime);
       isUpdated = !isUpdated && this.maValues.size() == 0 ? true : isUpdated;
       if (isUpdated){
           this.setSMAValues();
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
        return "SimpleMovingAverage{" +
                "candlesticksQuantity=" + candlesticksQuantity +
                ", candlestickPriceType=" + candlestickPriceType.toString() +
                ", maValues=" + maValues.toString() +
                ", points=" + points.toString() +
                ", isTradeGenerated=" + isTradeGenerated +
                '}';
    }

    private void setSMAValues(){

        List<Candlestick> candlestickList = this.candlesUpdater.getCandles();
        this.maValues.clear();
        calculateSMAValue(candlestickList);
    }

    private void calculateSMAValue(List<Candlestick> candlestickList) {

        int count = 0;
        BigDecimal result = BigDecimal.ZERO;

        for (int i = candlestickList.size()-1; i >= 0 ; i--) {
            CandlestickData candle = candlestickList.get(i).getMid();
            result = result.add(getCandlePrice(candle)).setScale(5, BigDecimal.ROUND_HALF_UP);

            count++;
            if (count == this.candlesticksQuantity){
                addSMAValue(result);
                CandlestickData oldestCandle = candlestickList.get(i + count -1).getMid();
                result = result.subtract(getCandlePrice(oldestCandle)).setScale(5,BigDecimal.ROUND_HALF_UP);
                count--;
            }
        }
    }

    private void addSMAValue(BigDecimal result) {
        this.maValues.add(0,result.divide(this.divisor, 5, BigDecimal.ROUND_HALF_UP));
    }

    private BigDecimal getCandlePrice(CandlestickData candleMid) {
        return this.candlestickPriceType.extractPrice(candleMid);
    }

    private void setDivisor(long period){
        this.divisor = BigDecimal.valueOf(period);
    }

}
