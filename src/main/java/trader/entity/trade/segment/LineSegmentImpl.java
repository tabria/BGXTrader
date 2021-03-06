package trader.entity.trade.segment;

import trader.entity.trade.point.Point;
import trader.entity.trade.point.PointImpl;
import trader.exception.NullArgumentException;

public final class LineSegmentImpl implements LineSegment {

    private Point pointA;
    private Point pointB;

    public LineSegmentImpl(Point a, Point b) {
        this.setPointA(a);
        this.setPointB(b);
    }

    @Override
    public Point getPointA(){
        return new PointImpl(this.pointA);
    }

    @Override
    public Point getPointB(){
        return new PointImpl(this.pointB);
    }

    @Override
    public int hashCode() {
        int result = this.pointA.hashCode();
        result = 31*result + this.pointB.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if(!(obj instanceof LineSegmentImpl))
            return false;
        LineSegmentImpl newSegment = (LineSegmentImpl) obj;

        return newSegment.pointA.equals(this.pointA) &&
                newSegment.pointB.equals(this.pointB);
    }

    @Override
    public String toString() {
        return "LineSegment{" +
                "pointA=" + pointA.toString() +
                ", pointB=" + pointB.toString() +
                '}';
    }

    private void setPointA(Point a){
        if(a == null){
            throw new NullArgumentException();
        }
        this.pointA = a;
    }

    private void setPointB(Point b){
        if(b == null)
            throw new NullArgumentException();
        int result = this.pointA.getTime().compareTo(b.getTime());
        if (result >= 0 )
            throw new IllegalArgumentException();
        this.pointB = b;
    }
}
