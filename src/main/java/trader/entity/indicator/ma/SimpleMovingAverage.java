package trader.entity.indicator.ma;

import trader.entity.candlestick.candle.CandleGranularity;
import trader.entity.indicator.CandlesUpdatable;
import trader.entity.candlestick.candle.CandlePriceType;
import trader.entity.candlestick.Candlestick;
import trader.entity.indicator.BaseIndicator;

import java.math.BigDecimal;
import java.util.List;

import static trader.strategy.bgxstrategy.configuration.StrategyConfig.*;

public final class SimpleMovingAverage extends BaseIndicator {

    ////////////////////////////remove/////////////////////////
//    SimpleMovingAverage(long indicatorPeriod, CandlePriceType candlePriceType, CandlesUpdatable updater) {
//        super(indicatorPeriod, candlePriceType, updater);
//        setDivisor();
//        initiateSMAValues();
//    }
///////////////////////////////////remove//////////////////////////
    SimpleMovingAverage(long indicatorPeriod, CandlePriceType candlePriceType, CandleGranularity granularity) {
        super(indicatorPeriod, candlePriceType, granularity);
        setDivisor();
     //   initiateSMAValues();
    }


    @Override
    public void updateIndicator() {
//        candlesUpdater.getUpdatedCandle();
//        List<Candlestick> candles = candlesUpdater.getCandles();
//        BigDecimal smaValue = BigDecimal.ZERO;
//        for (int i = candles.size()-1; i >indicatorPeriod ; i--) {
//            smaValue = smaValue.add(obtainPrice(candles.get(i)));
//        }
//        indicatorValues.add(smaValue.divide(divisor, SCALE, BigDecimal.ROUND_HALF_UP));
    }
    
    @Override
    public String toString() {
        return "SimpleMovingAverage{" +
                "period=" + indicatorPeriod +
                ", candlePriceType=" + candlePriceType.toString() +
                ", granularity=" + granularity.toString() +
                ", indicatorValues=" + indicatorValues.toString() +
                '}';
    }

    @Override
    protected void setDivisor(){
        super.divisor = BigDecimal.valueOf(indicatorPeriod);
    }

//    private void initiateSMAValues() {
//        List<Candlestick> candles = candlesUpdater.getCandles();
//        calculateSMAValue(candles);
//    }

    private void calculateSMAValue(List<Candlestick> candlestickList) {
        verifyCalculationInput(candlestickList);
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

    private BigDecimal calculatedSMAValue(BigDecimal commonPrice) {
        return commonPrice.divide(divisor, SCALE, BigDecimal.ROUND_HALF_UP);
    }
}