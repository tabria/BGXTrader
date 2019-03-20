package trader.indicators;

//import com.oanda.v20.Context;
import com.oanda.v20.instrument.Candlestick;
import com.oanda.v20.instrument.CandlestickData;
import com.oanda.v20.primitives.DateTime;
import trader.candle.CandlesUpdater;
import trader.candle.CandlestickPriceType;
import trader.trades.entities.Point;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class BaseIndicator implements Indicator {

    //protected Context context;
    protected long candlesticksQuantity = 0L;
    protected boolean isTradeGenerated;


//    protected BaseIndicator(long candlesticksQuantity, CandlestickPriceType candlestickPriceType, CandlesUpdater candlesUpdater) {
//        this.candlestickPriceType = candlestickPriceType;
//        this.candlesticksQuantity = candlesticksQuantity;
//        this.candlesUpdater = candlesUpdater;
//        this.indicatorValues = new ArrayList<>();
//        this.points = new ArrayList<>();
//        this.isTradeGenerated = false;
//    }
/////////////////////////////////////////
    protected long indicatorPeriod;
    protected final CandlestickPriceType candlestickPriceType;
    protected final CandlesUpdater candlesUpdater;
    protected List<BigDecimal> indicatorValues;
    protected List<Point> points;
    protected BigDecimal divisor;

    public BaseIndicator(long indicatorPeriod, CandlestickPriceType candlestickPriceType, CandlesUpdater candlesUpdater) {
        this.indicatorPeriod = indicatorPeriod;
        this.candlestickPriceType = candlestickPriceType;
        this.candlesUpdater = candlesUpdater;
        this.indicatorValues = new ArrayList<>();
        this.points = new ArrayList<>();
    }


    //////////////////////////////////////////////////////////////////////////////////////////


    public List<Point> getPoints() {
        return Collections.unmodifiableList(points);
    }

    public List<BigDecimal> getValues() {
        return Collections.unmodifiableList(indicatorValues);
    }

    public abstract void updateIndicator();

    protected abstract void setDivisor();

//    protected boolean candlesUpdated(DateTime dateTime) {
//        boolean isUpdated =  this.candlesUpdater.updateCandles(dateTime);
//        return !isUpdated && this.indicatorValues.size() == 0 ? true : isUpdated;
//    }

    protected void fillPoints() {
        this.points.clear();
        int time = 1;
        int index = this.indicatorValues.size()-3;
        for (BigDecimal value :this.indicatorValues) {
            Point point = new Point.PointBuilder(this.indicatorValues.get(index))
                    .setTime(BigDecimal.valueOf(time++))
                    .build();
            this.points.add(point);
            if (++index >= this.indicatorValues.size()){
                break;
            }
        }
//        int index = indicatorValues.size()-3 >0 ? indicatorValues.size()-3 : 0;
//        for (int i = indicatorValues.size()-3; i < this.indicatorValues.size() ; i++) {
//            Point point = new Point.PointBuilder(this.indicatorValues.get(i))
//                    .setTime(BigDecimal.valueOf(time++))
//                    .build();
//
//            this.points.add(point);
//        }
    }

//    protected CandlestickData candlestickPriceData(List<Candlestick> candlestickList, int index) {
//        return candlestickList.get(index).getMid();
//    }
}
