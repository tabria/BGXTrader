package trader.entity.trade.segment;


import trader.entity.trade.point.Point;

public interface LineSegment {
    Point getPointA();

    Point getPointB();

    boolean equals(Object obj);
}
