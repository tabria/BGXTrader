package trader.indicators.ma;

import com.oanda.v20.Context;
import com.oanda.v20.primitives.DateTime;
import trader.candles.CandlesUpdater;
import trader.indicators.Indicator;
import trader.indicators.enums.CandlestickPriceType;
import trader.trades.entities.Point;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseMovingAverage implements Indicator {

    protected Context context;
    protected final long candlesticksQuantity;
    protected final CandlestickPriceType candlestickPriceType;
    protected final CandlesUpdater candlesUpdater;
    protected List<BigDecimal> maValues;
    protected List<Point> points;
    protected boolean isTradeGenerated;

    public BaseMovingAverage(CandlestickPriceType candlestickPriceType, long candlesticksQuantity, CandlesUpdater candlesUpdater) {
        this.candlestickPriceType = candlestickPriceType;
        this.candlesticksQuantity = candlesticksQuantity;
        this.candlesUpdater = candlesUpdater;
        this.maValues = new ArrayList<>();
        this.points = new ArrayList<>();
        this.isTradeGenerated = false;
    }

    public abstract List<Point> getPoints();
    public abstract List<BigDecimal> getValues();
    public abstract void updateMovingAverage(DateTime dateTime, BigDecimal ask, BigDecimal bid);
}
