package trader.indicators;

import com.oanda.v20.primitives.DateTime;
import trader.trades.entities.Point;

import java.math.BigDecimal;
import java.util.List;

public interface Indicator {

    int SCALE = 5;
    BigDecimal ZERO = BigDecimal.ZERO;

    List<BigDecimal> getValues();
    void updateIndicator();
    List<Point> getPoints();
//    boolean isTradeGenerated();
//    void setIsTradeGenerated(boolean isGenerated);

}
