package trader.indicators.ma;

import trader.candle.CandlesUpdater;
import trader.candle.Candlestick;
import trader.indicators.BaseIndicator;
import trader.candle.CandlestickPriceType;
import trader.strategies.BGXStrategy.StrategyConfig;

import java.math.BigDecimal;
import java.util.List;

import static trader.strategies.BGXStrategy.StrategyConfig.*;

public final class SimpleMovingAverage extends BaseIndicator {

    SimpleMovingAverage(long indicatorPeriod, CandlestickPriceType candlestickPriceType, CandlesUpdater updater) {
        super(indicatorPeriod, candlestickPriceType, updater);
        setDivisor();
        initiateSMAValues();
    }
    
    @Override
    public void updateIndicator() {
        candlesUpdater.getUpdateCandle();
        List<Candlestick> candles = candlesUpdater.getCandles();
        BigDecimal smaValue = BigDecimal.ZERO;
        for (int i = candles.size()-1; i >indicatorPeriod ; i--) {
            smaValue = smaValue.add(obtainPrice(candles.get(i)));
        }
        indicatorValues.add(smaValue.divide(divisor, SCALE, BigDecimal.ROUND_HALF_UP));
    }
    
    @Override
    public String toString() {
        return "SimpleMovingAverage{" +
                "period=" + indicatorPeriod +
                ", candlestickPriceType=" + candlestickPriceType.toString() +
                ", indicatorValues=" + indicatorValues.toString() +
                '}';
    }

    @Override
    protected void setDivisor(){
        super.divisor = BigDecimal.valueOf(indicatorPeriod);
    }

    private void calculateSMAValue(List<Candlestick> candlestickList) {
        int removePriceIndex =0;
        int periodIndex = 0;
        BigDecimal commonPrice = BigDecimal.ZERO;
        for (Candlestick Candle : candlestickList) {
            commonPrice = commonPrice.add(obtainPrice(Candle));
            if (periodIndex < indicatorPeriod) {
                periodIndex++;
            }
            if (periodIndex == indicatorPeriod) {
                indicatorValues.add(calculatedSMAValue(commonPrice));
                commonPrice = commonPrice.subtract(obtainPrice(candlestickList.get(removePriceIndex++)));
            }
        }
    }

    private void initiateSMAValues() {
        List<Candlestick> candles = candlesUpdater.getCandles();
        calculateSMAValue(candles);
    }

    private BigDecimal calculatedSMAValue(BigDecimal commonPrice) {
        return commonPrice.divide(divisor, SCALE, BigDecimal.ROUND_HALF_UP);
    }

    private BigDecimal obtainPrice(Candlestick candle) {
        return candlestickPriceType.extractPrice(candle)
                        .setScale(SCALE, BigDecimal.ROUND_HALF_UP);
    }

}
