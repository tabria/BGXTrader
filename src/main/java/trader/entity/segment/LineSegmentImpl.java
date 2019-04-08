package trader.entity.segment;

import trader.entity.point.PointImpl;
import trader.exception.NullArgumentException;

import java.math.BigDecimal;

public final class LineSegmentImpl implements LineSegment {

    private PointImpl pointA;
    private PointImpl pointB;

    public LineSegmentImpl(PointImpl a, PointImpl b) {
        this.setPointA(a);
        this.setPointB(b);
    }

    @Override
    public PointImpl getPointA(){
        return new PointImpl(this.pointA);
    }

    @Override
    public PointImpl getPointB(){
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
        return "LineSegmentImpl{" +
                "pointA=" + pointA.toString() +
                ", pointB=" + pointB.toString() +
                '}';
    }

    private void setPointA(PointImpl a){
        if(a == null){
            throw new NullArgumentException();
        }
        this.pointA = a;
    }

    private void setPointA(BigDecimal priceA){
        this.pointA = new PointImpl.PointBuilder(priceA).build();
    }

    private void setPointB(PointImpl b){
        if(b == null)
            throw new NullArgumentException();
        int result = this.pointA.getTime().compareTo(b.getTime());
        if (result >= 0 )
            throw new IllegalArgumentException();
        this.pointB = b;
    }

    private void setPointB(BigDecimal priceB){
        this.pointB = new PointImpl.PointBuilder(priceB).setTime(BigDecimal.valueOf(2)).build();
    }
}
