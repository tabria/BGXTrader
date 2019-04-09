package trader.entity.point;

import java.math.BigDecimal;

public interface Point {
    BigDecimal getPrice();

    BigDecimal getTime();

    void setPrice(BigDecimal price);

    void setTime(BigDecimal time);

    int hashCode();

    boolean equals(Object obj);

    String toString();
}
