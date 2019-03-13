package trader.indicators;

import com.oanda.v20.primitives.DateTime;
import trader.trades.entities.Point;

import java.math.BigDecimal;
import java.util.List;

public interface Indicator {

    List<BigDecimal> getValues();
    void updateMovingAverage(DateTime dateTime, BigDecimal ask, BigDecimal bid);
    List<Point> getPoints();
    boolean isTradeGenerated();
    void setIsTradeGenerated(boolean isGenerated);

}
