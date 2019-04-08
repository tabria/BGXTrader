package trader.entity.indicator;

import trader.entity.candlestick.candle.CandleGranularity;
import trader.entity.candlestick.candle.CandlePriceType;
import trader.entity.candlestick.Candlestick;
import trader.exception.BadRequestException;
import trader.exception.IndicatorPeriodTooBigException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class BaseIndicator implements Indicator{

    protected long indicatorPeriod;
    protected CandlePriceType candlePriceType;
    protected CandleGranularity granularity;
    protected String position;
    protected List<Candlestick> candlestickList;
    protected List<BigDecimal> indicatorValues;
    protected BigDecimal divisor;


    public BaseIndicator(long indicatorPeriod, CandlePriceType candlePriceType, CandleGranularity granularity, String position) {
        this.indicatorPeriod = indicatorPeriod;
        this.candlePriceType = candlePriceType;
        this.granularity = granularity;
        this.position = position;
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
    public String getPosition(){return position;}

    @Override
    public abstract void updateIndicator(List<Candlestick> candles);

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
