package trader.candle;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public interface Candlestick {
//    CandlePriceType getPriceType();

    long getTimeFrame();

    boolean isComplete();

    long getVolume();

    BigDecimal getOpenPrice();

    BigDecimal getHighPrice();

    BigDecimal getLowPrice();

    BigDecimal getClosePrice();

    ZonedDateTime getDateTime();
}
