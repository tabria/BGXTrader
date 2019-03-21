package trader.trade.entitie;


import java.math.BigDecimal;

/**
 * Segment class. This class create a line from 2 points
 */
public final class LineSegment {

    private Point pointA;
    private Point pointB;

    /**
     * Segment constructor
     * @param a point
     * @param b point
     * @see Point
     */
    public LineSegment(Point a, Point b) {
        this.setPointA(a);
        this.setPointB(b);
    }

    /**
     * Segment constructor. Create segment base on price. Time intervals will be 1 for point a and 2 for point b
     * @param priceA price of point A
     * @param priceB price of point B
     * @see Point
     */
    public LineSegment(BigDecimal priceA, BigDecimal priceB) {
        this.setPointA(priceA);
        this.setPointB(priceB);
    }

    /**
     * Copy Constructor
     * @param lineSegment current line segment
     * @throws NullPointerException when argument is null
     */
    public LineSegment(LineSegment lineSegment){
        if (lineSegment == null){
            throw new NullPointerException("Segment must not be null");
        }
        this.setPointA(lineSegment.getPointA());
        this.setPointB(lineSegment.getPointB());
    }

    /**
     * Getter for the point A
     * @return new {@link Point}
     */
    public Point getPointA(){
        return new Point(this.pointA);
    }

    /**
     * Getter for the point B
     * @return new {@link Point}
     */
    public Point getPointB(){
        return new Point(this.pointB);
    }

    @Override
    public int hashCode() {
        int result = this.pointA.hashCode();
        result = 31*result + this.pointB.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this){
            return true;
        }
        if(!(obj instanceof LineSegment)){
            return false;
        }

        LineSegment newSegment = (LineSegment) obj;

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

    /**
     * Setter for point A
     * @param a point
     * @throws NullPointerException when point is null
     */
    private void setPointA(Point a){
        if(a == null){
            throw new NullPointerException("Point A must not be null");
        }
        this.pointA = a;
    }

    /**
     * Setter for point A
     * @param priceA start price
     */
    private void setPointA(BigDecimal priceA){
        this.pointA = new Point.PointBuilder(priceA).build();
    }

    /**
     * Setter for point B
     * @param b point B
     * @throws NullPointerException if point b is null
     * @throws IllegalArgumentException if time interval of b is less or equal to the time interval of point a
     */
    private void setPointB(Point b){
        if(b == null){
            throw new NullPointerException("Point B must not be null");
        }
        int result = this.pointA.getTime().compareTo(b.getTime());
        if (result >= 0 ){
            throw new IllegalArgumentException("PointB time must be bigger than PointA time");
        }
        this.pointB = b;
    }

    /**
     * Setter for point B
     * @param priceB price for the end point
     */
    private void setPointB(BigDecimal priceB){
        this.pointB = new Point.PointBuilder(priceB).setTime(BigDecimal.valueOf(2)).build();
    }
}
