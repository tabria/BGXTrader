package trader.entity.point;

import java.math.BigDecimal;

public interface Point {
    BigDecimal getPrice();

    BigDecimal getTime();

    int hashCode();

    boolean equals(Object obj);

    String toString();
}
