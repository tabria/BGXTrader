package trader.entity.indicator.ma;

import trader.entity.candlestick.candle.CandleGranularity;
import trader.entity.candlestick.candle.CandlePriceType;
import trader.entity.candlestick.Candlestick;
import trader.entity.indicator.BaseIndicator;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;


public final class SimpleMovingAverage extends BaseIndicator {

    SimpleMovingAverage(long indicatorPeriod, CandlePriceType candlePriceType, CandleGranularity granularity, String position) {
        super(indicatorPeriod, candlePriceType, granularity, position);
        setDivisor();
    }


    @Override
    public void updateIndicator(List<Candlestick> candles) {
        if(indicatorValues.size() == 0){
            candlestickList.addAll(candles);
            calculateSMAValue(candles);
        } else {
            Candlestick lastIndicatorCandle = getLastCandlestick(candlestickList, candlestickList.size() - 1);
            if(isTimeToUpdate(candles.get(candles.size()-1), lastIndicatorCandle)) {
                candlestickList.addAll(candles);
                BigDecimal smaValue = BigDecimal.ZERO;
                for (int i = candlestickList.size() - 1; i >= candlestickList.size() - indicatorPeriod; i--) {
                    smaValue = smaValue.add(obtainPrice(candlestickList.get(i)));
                }
                indicatorValues.add(smaValue.divide(divisor, 5, BigDecimal.ROUND_HALF_UP));
            }
        }
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

    private boolean isTimeToUpdate(Candlestick candlestick, Candlestick prevCandle) {
        ZonedDateTime nextUpdateTime = prevCandle.getDateTime().plusSeconds(granularity.toSeconds());
        return candlestick.getDateTime().compareTo(nextUpdateTime) >=0;
    }

    private Candlestick getLastCandlestick(List<Candlestick> candlestickList, int candleIndex) {
        return candlestickList.get(candleIndex);
    }

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
        return commonPrice.divide(divisor, 5, BigDecimal.ROUND_HALF_UP);
    }
}
