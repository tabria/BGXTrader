package trader.entity.segment;


import trader.entity.point.Point;

public interface LineSegment {
    Point getPointA();

    Point getPointB();

    boolean equals(Object obj);
}
