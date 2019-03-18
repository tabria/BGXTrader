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
    protected final long candlesticksQuantity;
    protected final CandlestickPriceType candlestickPriceType;
    protected final CandlesUpdater candlesUpdater;
    protected List<BigDecimal> indicatorValues;
    protected List<Point> points;
    protected boolean isTradeGenerated;
    protected BigDecimal divisor;

    protected BaseIndicator(long candlesticksQuantity, CandlestickPriceType candlestickPriceType, CandlesUpdater candlesUpdater) {
        this.candlestickPriceType = candlestickPriceType;
        this.candlesticksQuantity = candlesticksQuantity;
        this.candlesUpdater = candlesUpdater;
        this.indicatorValues = new ArrayList<>();
        this.points = new ArrayList<>();
        this.isTradeGenerated = false;
    }



    public List<Point> getPoints() {
        return Collections.unmodifiableList(points);
    }

    public List<BigDecimal> getValues() {
        return Collections.unmodifiableList(indicatorValues);
    }

    public abstract void updateIndicator(DateTime dateTime);

    protected abstract void setDivisor();

    protected boolean candlesUpdated(DateTime dateTime) {
        boolean isUpdated =  this.candlesUpdater.updateCandles(dateTime);
        return !isUpdated && this.indicatorValues.size() == 0 ? true : isUpdated;
    }

    protected void fillPoints() {
        this.points.clear();
        int time = 1;
        for (int i = this.indicatorValues.size()-4; i < this.indicatorValues.size() -1 ; i++) {
            Point point = new Point.PointBuilder(this.indicatorValues.get(i))
                    .setTime(BigDecimal.valueOf(time++))
                    .build();

            this.points.add(point);
        }
    }

    protected CandlestickData candlestickPriceData(List<Candlestick> candlestickList, int index) {
        return candlestickList.get(index).getMid();
    }
}
