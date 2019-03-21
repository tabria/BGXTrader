package trader.price;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public interface Pricing {

    BigDecimal getAsk();
    BigDecimal getBid();
    ZonedDateTime getDateTime();
    boolean isTradable();
    BigDecimal getAvailableUnits();
}
