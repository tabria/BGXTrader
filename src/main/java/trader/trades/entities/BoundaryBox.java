package trader.trades.entities;


import java.math.BigDecimal;

/**
 * This class supply methods for boundary boxes
 * Boundary box is the box around line segment. Boundary box have offset. This offset is applied only on price, not on time.
 * Offset is the difference between line segment point A and boundary box point A, and line segment point B and boundary box point B
 *
 */
public final class BoundaryBox {

    private static final BigDecimal DEFAULT_OFFSET = BigDecimal.valueOf(0.0004);
    private static final BigDecimal MAX_OFFSET_VALUE = BigDecimal.valueOf(0.0010);

    private LineSegment lineSegment;
    private BigDecimal offset;
    private Point pointA;
    private Point pointB;

    /**
     * Constructor
     * @param lineSegment current LineSegment
     */
    public BoundaryBox(LineSegment lineSegment){
        this(lineSegment, DEFAULT_OFFSET);
    }

    /**
     * Constructor
     * @param lineSegment current LineSegment
     * @param offset current offset for the boundary box
     */
    public BoundaryBox(LineSegment lineSegment, BigDecimal offset){
        this.setOffset(offset);
        this.setLineSegment(lineSegment);
        this.setPoints(offset);
    }

    /**
     * Getter for line segment
     * @return new {@link LineSegment} object
     */
    public LineSegment getLineSegment(){
        return new LineSegment(this.lineSegment);
    }

    /**
     * Getter for point A
     * @return new {@link Point} object
     */
    public Point getPointA(){
        return new Point(this.pointA);
    }

    /**
     * Getter for point B
     * @return new {@link Point} object
     */
    public Point getPointB(){
        return new Point(this.pointB);
    }

    @Override
    public String toString() {
        return "BoundaryBox{" +
                "lineSegment=" + lineSegment.toString() +
                ", offset=" + offset.toString() +
                ", pointA=" + pointA.toString() +
                ", pointB=" + pointB.toString() +
                '}';
    }

    /**
     * Setter for offset
     * @param offset offset value
     * @throws NullPointerException when offset is null
     * @throws IllegalArgumentException when offset is less than ZERO
     * @throws IllegalArgumentException when offset is bigger than MAX_OFFSET_VALUE
     */
    private void setOffset(BigDecimal offset) {
        if (offset == null){
            throw new NullPointerException("Offset must not be null");
        }
        if (offset.compareTo(BigDecimal.ZERO)<0){
            throw new IllegalArgumentException("Offset must be positive number");
        }
        if(offset.compareTo(MAX_OFFSET_VALUE) > 0 ){
            throw new IllegalArgumentException("Offset must be below " + MAX_OFFSET_VALUE);
        }
        this.offset = offset;
    }

    /**
     * Setter for lineSegment
     * @param lineSegment current lineSegment
     * @throws NullPointerException when line segment is null
     */
    private void setLineSegment(LineSegment lineSegment){
        if (lineSegment ==null){
            throw new NullPointerException("Line segment must not be null");
        }
        this.lineSegment = lineSegment;
    }

    /**
     * Setter of offset. Offset is the difference between line segment point A and boundary box point A, and line segment point B and boundary box point B
     * @param offsetValue offset
     */
    private void setPoints(BigDecimal offsetValue){
        BigDecimal segmentPricePointA = this.lineSegment.getPointA().getPrice();
        BigDecimal segmentPricePointB = this.lineSegment.getPointB().getPrice();

        BigDecimal delta = segmentPricePointA.subtract(segmentPricePointB).setScale(5, BigDecimal.ROUND_HALF_UP);

        int result = delta.compareTo(BigDecimal.ZERO);
        BigDecimal boundaryBoxPointAPrice;
        BigDecimal boundaryBoxPointBPrice;
        if(result < 0){
            boundaryBoxPointAPrice = segmentPricePointA.subtract(offsetValue)
                                                                  .setScale(5, BigDecimal.ROUND_HALF_UP);
            boundaryBoxPointBPrice = segmentPricePointB.add(offsetValue)
                                                                  .setScale(5, BigDecimal.ROUND_HALF_UP);
        } else {
            boundaryBoxPointAPrice = segmentPricePointA.add(offsetValue)
                    .setScale(5, BigDecimal.ROUND_HALF_UP);
            boundaryBoxPointBPrice = segmentPricePointB.subtract(offsetValue)
                    .setScale(5, BigDecimal.ROUND_HALF_UP);
        }

        //this.pointA = new Point.PointBuilder(boundaryBoxPointAPrice).setTime(this.lineSegment.getPointA().getTime()).build();
        //this.pointB = new Point.PointBuilder(boundaryBoxPointBPrice).setTime(this.lineSegment.getPointB().getTime()).build();

        this.pointA = new Point.PointBuilder(boundaryBoxPointAPrice).setTime(BigDecimal.ONE).build();
        this.pointB = new Point.PointBuilder(boundaryBoxPointBPrice).setTime(BigDecimal.valueOf(2)).build();
    }


}
