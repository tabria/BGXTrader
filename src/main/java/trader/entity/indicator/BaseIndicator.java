package trader.entity.indicator;

import trader.entity.candlestick.candle.CandleGranularity;
import trader.entity.candlestick.candle.CandlePriceType;
import trader.entity.candlestick.Candlestick;
import trader.exception.BadRequestException;
import trader.exception.IndicatorPeriodTooBigException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public abstract class BaseIndicator implements Indicator{
 //   protected final CandlesUpdatable candlesUpdater;


    protected long indicatorPeriod;
    protected CandlePriceType candlePriceType;
    protected CandleGranularity granularity;
    protected List<Candlestick> candlestickList;
    protected List<BigDecimal> indicatorValues;
    protected BigDecimal divisor;

    //////////////////////////// to remove ///////////////////////////////////////////
//    public BaseIndicator(long indicatorPeriod, CandlePriceType candlePriceType, CandlesUpdatable candlesUpdater) {
//        this.indicatorPeriod = indicatorPeriod;
//        this.candlePriceType = candlePriceType;
//        this.candlesUpdater = candlesUpdater;
//        this.indicatorValues = new ArrayList<>();
//        this.candlestickList = new ArrayList<>();
//    }

    //////////////////to remove -/////////////////////////////////


    public BaseIndicator(long indicatorPeriod, CandlePriceType candlePriceType, CandleGranularity granularity) {
        this.indicatorPeriod = indicatorPeriod;
        this.candlePriceType = candlePriceType;
        this.granularity = granularity;
//        this.candlesUpdater = null; //to be removed
        this.indicatorValues = new ArrayList<>();
        this.candlestickList = new ArrayList<>();
    }

    @Override
    public List<BigDecimal> getValues() {
        return Collections.unmodifiableList(indicatorValues);
    }

    @Override
    public CandleGranularity getGranularity(){
        return granularity;
    }

    @Override
    public abstract void updateIndicator();

    protected abstract void setDivisor();

    protected void verifyCalculationInput(List<Candlestick> candlestickList) {
        if(candlestickList.size() == 0)
            throw new BadRequestException();
        if(candlestickList.size() <= indicatorPeriod)
            throw new IndicatorPeriodTooBigException();
    }

    protected BigDecimal obtainPrice(Candlestick candle) {
        return candlePriceType.extractPrice(candle)
                        .setScale(5, BigDecimal.ROUND_HALF_UP);
    }
}
