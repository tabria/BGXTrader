package trader.entity.price;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public interface Price {

    BigDecimal getAsk();
    BigDecimal getBid();
    ZonedDateTime getDateTime();
    boolean isTradable();
    BigDecimal getAvailableUnits();
}
