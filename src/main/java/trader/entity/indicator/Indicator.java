package trader.entity.indicator;

import java.math.BigDecimal;
import java.util.List;

public interface Indicator {

    List<BigDecimal> getValues();
    void updateIndicator();

}
