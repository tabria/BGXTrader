package trader.indicators;

import com.oanda.v20.Context;
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

public abstract class BaseIndicator implements Indicator {

    protected Context context;
    protected final long candlesticksQuantity;
    protected final CandlestickPriceType candlestickPriceType;
    protected final CandlesUpdater candlesUpdater;
    protected List<BigDecimal> maValues;
    protected List<Point> points;
    protected boolean isTradeGenerated;
    protected BigDecimal divisor;

    protected BaseIndicator(CandlestickPriceType candlestickPriceType, long candlesticksQuantity, CandlesUpdater candlesUpdater) {
        this.candlestickPriceType = candlestickPriceType;
        this.candlesticksQuantity = candlesticksQuantity;
        this.candlesUpdater = candlesUpdater;
        this.maValues = new ArrayList<>();
        this.points = new ArrayList<>();
        this.isTradeGenerated = false;
    }

    public List<Point> getPoints() {
        return Collections.unmodifiableList(points);
    }

    public List<BigDecimal> getValues() {
        return Collections.unmodifiableList(maValues);
    }

    public abstract void updateMovingAverage(DateTime dateTime);

    protected abstract void setDivisor();

    protected boolean candlesUpdated(DateTime dateTime) {
        boolean isUpdated =  this.candlesUpdater.updateCandles(dateTime);
        return !isUpdated && this.maValues.size() == 0 ? true : isUpdated;
    }

    protected void fillPoints() {
        this.points.clear();
        int time = 1;
        for (int i = this.maValues.size()-4; i < this.maValues.size() -1 ; i++) {
            Point point = new Point.PointBuilder(this.maValues.get(i))
                    .setTime(BigDecimal.valueOf(time++))
                    .build();

            this.points.add(point);
        }
    }

    protected CandlestickData candlestickPriceData(List<Candlestick> candlestickList, int index) {
        return candlestickList.get(index).getMid();
    }
}
