package trader.entity.segment;

import trader.entity.point.PointImpl;

public interface LineSegment {
    PointImpl getPointA();

    PointImpl getPointB();

    boolean equals(Object obj);
}
