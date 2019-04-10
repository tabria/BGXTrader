package trader.entity.candlestick;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public interface Candlestick {

    long getTimeFrame();

    boolean isComplete();

    long getVolume();

    BigDecimal getOpenPrice();

    BigDecimal getHighPrice();

    BigDecimal getLowPrice();

    BigDecimal getClosePrice();

    ZonedDateTime getDateTime();
}
