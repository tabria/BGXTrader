package trader.trades.services;


import trader.trades.entities.BoundaryBox;
import trader.trades.entities.LineSegment;
import trader.trades.entities.Point;

import java.math.BigDecimal;

/**
 * Class provide methods for checking intersections and touches of lineSegments
 * Non instantiable class
 */
public final class IntersectionService {

    /**
     * Non instantiable
     *
     */
    private IntersectionService(){

    }

    /**
     *
     * Check if boundary boxes do intersect. If one boundary box
     * touches the other, they do intersect.
     * Boundary box is the box around lineSegment.
     * @param a first boundary box
     * @param b second boundary box
     * @return <code>true</code> if they intersect,
     *         <code>false</code> otherwise.
     */
    public static boolean doBoundaryBoxesIntersect(BoundaryBox a, BoundaryBox b) {

        //Time intervals are irrelevant for the moment.They are left future use
        BigDecimal startPriceA = a.getPointA().getPrice();
    //    BigDecimal startTimeA = a.getPointA().getTime();
        BigDecimal endPriceA = a.getPointB().getPrice();
    //    BigDecimal endTimeA = a.getPointB().getTime();

        BigDecimal startPriceB = b.getPointA().getPrice();
    //    BigDecimal startTimeB = b.getPointA().getTime();
        BigDecimal endPriceB = b.getPointB().getPrice();
    //    BigDecimal endTimeB = b.getPointB().getTime();

    //    int compareStartTimeAEndTimeB = startTimeA.compareTo(endTimeB);
     //   int compareEndTimeAStartTimeB = endTimeA.compareTo(startTimeB);

        //change start and end point it that way that start point is always smaller than end point
        if (startPriceA.compareTo(endPriceA) > 0){
            startPriceA = endPriceB;
            endPriceA = a.getPointA().getPrice();
        }
        if (startPriceB.compareTo(endPriceB) > 0){
            startPriceB = endPriceB;
            endPriceB = b.getPointA().getPrice();
        }

        int compareStartPriceAEndPriceB = startPriceA.compareTo(endPriceB);
        int compareEndPriceAStartPriceB = endPriceA.compareTo(startPriceB);

        return compareStartPriceAEndPriceB <= 0 && compareEndPriceAStartPriceB >= 0;

       // return compareStartTimeAEndTimeB <= 0 && compareEndTimeAStartTimeB >= 0 &&
        //        compareStartPriceAEndPriceB <= 0 && compareEndPriceAStartPriceB >= 0;
    }

    /**
     * Check if 2 lineSegments(lines) have intersection. Method is based on Franklin Antonio's "Faster Line Segment IntersectionService" topic "in Graphics Gems III" book (http://www.graphicsgems.org/)
     * @param segmentA first lineSegment
     * @param segmentB second lineSegment
     * @return {@code true} if there is intersection, {@code false} otherwise
     * @throws NullPointerException if segmentA or segmentB is null;
     */
    public static boolean doLineSegmentsIntersect(LineSegment segmentA, LineSegment segmentB){
        if (segmentA == null || segmentB == null){
            throw new NullPointerException("Segments must not be null");
        }
        //points from first segment
        Point pointA1 = segmentA.getPointB();
        BigDecimal pointA1x1 = pointA1.getTime();
        BigDecimal pointA1y1 = pointA1.getPrice();

        Point pointA2 = segmentA.getPointA();
        BigDecimal pointA2x2 = pointA2.getTime();
        BigDecimal pointA2y2 = pointA2.getPrice();

        //points from second segment
        Point pointB1 = segmentB.getPointB();
        BigDecimal pointB1x3 = pointB1.getTime();
        BigDecimal pointB1y3 = pointB1.getPrice();

        Point pointB2 = segmentB.getPointA();
        BigDecimal pointB2x4 = pointB2.getTime();
        BigDecimal pointB2y4 = pointB2.getPrice();

        //check if end point of segmentA lies on top of end point of segmentB. This may be bounce or intersection => return false
        if (pointA1y1.compareTo(pointB1y3) == 0){
            return false;
        }

        //check if start point of segmentA lies on top ot start point of segmentB. It is not real intersection, but the signal generated will be same as if it was real intersection
        if(pointA2y2.compareTo(pointB2y4) == 0){
            return true;
        }


        //ax = x2 - x1
        BigDecimal ax = pointA2x2.subtract(pointA1x1).setScale(5, BigDecimal.ROUND_HALF_UP);
        //ay = y2 - y1
        BigDecimal ay = pointA2y2.subtract(pointA1y1).setScale(5, BigDecimal.ROUND_HALF_UP);
        //bx = x3 - x4
        BigDecimal bx = pointB1x3.subtract(pointB2x4).setScale(5, BigDecimal.ROUND_HALF_UP);
        //by = y3 - y4
        BigDecimal by = pointB1y3.subtract(pointB2y4).setScale(5, BigDecimal.ROUND_HALF_UP);
        //cx = x1 - x3
        BigDecimal cx = pointA1x1.subtract(pointB1x3).setScale(5, BigDecimal.ROUND_HALF_UP);
        //cy = y1 -y3
        BigDecimal cy = pointA1y1.subtract(pointB1y3).setScale(5, BigDecimal.ROUND_HALF_UP);

        //alpha = by*cx - bx*cy
        BigDecimal bycx = by.multiply(cx).setScale(5, BigDecimal.ROUND_HALF_UP);
        BigDecimal bxcy = bx.multiply(cy).setScale(5, BigDecimal.ROUND_HALF_UP);
        BigDecimal alpha = bycx.subtract(bxcy).setScale(5, BigDecimal.ROUND_HALF_UP);

        //beta = ax*cy - ay*cx
        BigDecimal axcy = ax.multiply(cy).setScale(5, BigDecimal.ROUND_HALF_UP);
        BigDecimal aycx = ay.multiply(cx).setScale(5, BigDecimal.ROUND_HALF_UP);
        BigDecimal beta = axcy.subtract(aycx).setScale(5, BigDecimal.ROUND_HALF_UP);

        //denominator = ay*bx - ax*by
        BigDecimal aybx = ay.multiply(bx).setScale(5, BigDecimal.ROUND_HALF_UP);
        BigDecimal axby = ax.multiply(by).setScale(5, BigDecimal.ROUND_HALF_UP);
        BigDecimal denominator = aybx.subtract(axby).setScale(5, BigDecimal.ROUND_HALF_UP);



        int denominatorZeroCompare = denominator.compareTo(BigDecimal.ZERO);

        if(denominatorZeroCompare > 0){
            if (alpha.compareTo(BigDecimal.ZERO) < 0 || alpha.compareTo(denominator) > 0 ||
                    beta.compareTo(BigDecimal.ZERO) < 0 || beta.compareTo(denominator) > 0){
                return false;
            }
        }else if (denominatorZeroCompare < 0){
            if (alpha.compareTo(BigDecimal.ZERO) > 0 || alpha.compareTo(denominator) < 0 ||
                    beta.compareTo(BigDecimal.ZERO) > 0 || beta.compareTo(denominator) < 0){
                return false;
            }
        }

         // This code check if lines are on top of each other. And if this is so. it will return true. For now the system needs only real intersections. If after collinearity, no intersection occurs, then the trade direction is different from the one after the intersection. The code is here for future use


        if (denominatorZeroCompare == 0){
//            // The lines are parallel.
//            // Check if they're collinear.
//
//            //y3LessY1 = y3 -y1
//            BigDecimal y3LessY1 = pointB1y3.subtract(pointA1y1).setScale(5, BigDecimal.ROUND_HALF_UP);
//            //collinearityTestForP3 = x1*(y2-y3) + x2*(y3LessY1) + x3*(y1-y2);   // see http://mathworld.wolfram.com/Collinear.html
//            //y2 -y3
//            BigDecimal y2y3 = pointA2y2.subtract(pointB1y3).setScale(5, BigDecimal.ROUND_HALF_UP);
//            //x1*(y2 - y3)
//            BigDecimal x1y2y3 = pointA1x1.multiply(y2y3).setScale(5, BigDecimal.ROUND_HALF_UP);
//            //x2*(y3LessY1)
//            BigDecimal x2y3LessY1 = pointA2x2.multiply(y3LessY1).setScale(5, BigDecimal.ROUND_HALF_UP);
//            //x1*(y2-y3) + x2*(y3LessY1)
//            BigDecimal collinearityTest = x1y2y3.add(x2y3LessY1).setScale(5, BigDecimal.ROUND_HALF_UP);
//            //y1-y2
//            BigDecimal y1y2 = pointA1y1.subtract(pointA2y2).setScale(5, BigDecimal.ROUND_HALF_UP);
//            //x3*(y1-y2)
//            BigDecimal x3y1y2 = pointB1x3.multiply(y1y2).setScale(5, BigDecimal.ROUND_HALF_UP);
//
//            //x1*(y2-y3) + x2*(y3LessY1) + x3*(y1-y2)
//            collinearityTest = collinearityTest.add(x3y1y2).setScale(5, BigDecimal.ROUND_HALF_UP);
//
//            // If p3 is collinear with p1 and p2 then p4 will also be collinear, since p1-p2 is parallel with p3-p4
//            if (collinearityTest.compareTo(BigDecimal.ZERO) == 0){
//                // The lines are collinear. Now check if they overlap.
//                if (pointA1x1.compareTo(pointB1x3) >= 0 &&  pointA1x1.compareTo(pointB2x4) <= 0 ||
//                        pointA1x1.compareTo(pointB1x3) <= 0 && pointA1x1.compareTo(pointB2x4) >= 0 ||
//                        pointA2x2.compareTo(pointB1x3) >= 0 && pointA2x2.compareTo(pointB2x4) <= 0 ||
//                        pointA2x2.compareTo(pointB1x3) <= 0 && pointA2x2.compareTo(pointB2x4) >= 0 ||
//                        pointB1x3.compareTo(pointA1x1) >= 0 && pointB1x3.compareTo(pointA2x2) <= 0 ||
//                        pointB1x3.compareTo(pointA1x1) <= 0 && pointB1x3.compareTo(pointA2x2) >= 0){
//                    if (pointA1y1.compareTo(pointB1y3) >= 0 && pointA1y1.compareTo(pointB2y4) <= 0 ||
//                            pointA1y1.compareTo(pointB1y3) <= 0 && pointA1y1.compareTo(pointB2y4) >= 0 ||
//                            pointA2y2.compareTo(pointB1y3) >= 0 && pointA2y2.compareTo(pointB2y4) <= 0 ||
//                            pointA2y2.compareTo(pointB1y3) <= 0 && pointA2y2.compareTo(pointB2y4) >= 0 ||
//                            pointB1y3.compareTo(pointA1y1) >= 0 && pointB1y3.compareTo(pointA2y2) <= 0 ||
//                            pointB1y3.compareTo(pointA1y1) <= 0 && pointB1y3.compareTo(pointA2y2) >= 0){
//                        return true;
//                    }
//                }
//            }
            return false;
        }

        return true;

    }

    /**
     * Calculate intersection point coordinates.First will calculate the slope m, then x and finally y.
     * The line is represented in slope-intercept format {@code y=m*x +b}
     * slope formula: {@code m=(y-y1)/(x-x1)}
     * x formula: {@code (b2 - b1)/(m1 - m2)}
     * y formula: {@code m*x + b1)
     *
     * @param segmentA
     * @param segmentB
     * @return {@link Point} intersection point
     */
    public static Point calculateIntersectionPoint(LineSegment segmentA, LineSegment segmentB){
        if (segmentA == null || segmentB == null){
            throw new NullPointerException("Segments must not be null");
        }

        //points from first segment
        Point pointA1 = segmentA.getPointB();
        BigDecimal pointA1x1 = pointA1.getTime();
        BigDecimal pointA1y1 = pointA1.getPrice();

        Point pointA2 = segmentA.getPointA();
        BigDecimal pointA2x2 = pointA2.getTime();
        BigDecimal pointA2y2 = pointA2.getPrice();

        //points from second segment
        Point pointB1 = segmentB.getPointB();
        BigDecimal pointB1x3 = pointB1.getTime();
        BigDecimal pointB1y3 = pointB1.getPrice();

        Point pointB2 = segmentB.getPointA();
        BigDecimal pointB2x4 = pointB2.getTime();
        BigDecimal pointB2y4 = pointB2.getPrice();


        //calculating Slope
        BigDecimal slopeSegmentA = IntersectionService.calculateSlope(pointA1x1, pointA1y1, pointA2x2, pointA2y2);
        BigDecimal slopeSegmentB = IntersectionService.calculateSlope(pointB1x3, pointB1y3, pointB2x4, pointB2y4);

        //calculating b for y = m*x + b
        BigDecimal bSegmentA = IntersectionService.calculatingB(slopeSegmentA, pointA2x2, pointA2y2);
        BigDecimal bSegmentB = IntersectionService.calculatingB(slopeSegmentB, pointB2x4, pointB2y4);

        //calculating x for the intersection point x = (b2-b1)/(m1-m2)
        //b2-b1
        BigDecimal b2b1 = bSegmentB.subtract(bSegmentA).setScale(5, BigDecimal.ROUND_HALF_UP);
        //m1 - m2
        BigDecimal m1m2 = slopeSegmentA.subtract(slopeSegmentB).setScale(5, BigDecimal.ROUND_HALF_UP);
        BigDecimal xIntersectPoint;
        if (m1m2.compareTo(BigDecimal.ZERO) == 0){
            xIntersectPoint = BigDecimal.ZERO;
        } else {
            xIntersectPoint = b2b1.divide(m1m2, 5, BigDecimal.ROUND_HALF_UP);
        }
        //calculate y = m1*((b2-b1)/(m1-m2)) + b1
        BigDecimal m1x = slopeSegmentA.multiply(xIntersectPoint).setScale(5, BigDecimal.ROUND_HALF_UP);
        BigDecimal yIntersectPoint = m1x.add(bSegmentA).setScale(5, BigDecimal.ROUND_HALF_UP);

        //Time is irrelevant. So the x. Set default time of 1 for the intersection point.
        return new Point.PointBuilder(yIntersectPoint).build();

    }

    /**
     * Calculating slope m = (y-y1)/(x-x1)
     * @param x - time intervals of the start point
     * @param y - price of the start point
     * @param x1 - time intervals of the end point
     * @param y1 - price of the end point
     * @return {@link BigDecimal} slope
     */
    private static BigDecimal calculateSlope(BigDecimal x, BigDecimal y, BigDecimal x1, BigDecimal y1){
        //calculating Slope m = (y-y1)/(x-x1)
        //y-y1
        BigDecimal yy1 = y.subtract(y1).setScale(5, BigDecimal.ROUND_HALF_UP);
        //x-x1
        BigDecimal xx1 = x.subtract(x1).setScale(5, BigDecimal.ROUND_HALF_UP);
        if(xx1.compareTo(BigDecimal.ZERO) == 0){

            return BigDecimal.ZERO;
        } else {
            //m = (y-y1)/(x-x1)
            return yy1.divide(xx1,5, BigDecimal.ROUND_HALF_UP);
        }
    }

    /**
     * Calculate b for the slope-intersection format(y = mx + b)
     * From slope formula m = (y-y1)/(x-x1) => y-y1=m*(x-x1).  X1 and y1 are known values - from the segment end point => after substitution we can convert this formula to slope intersection format y = m*x + b. Then b = y1 - m*x1
     * @param m slope of the segment
     * @param x1 time interval of the end point
     * @param y1 price of the end point
     * @return {@link BigDecimal} b value
     */
    private static BigDecimal calculatingB(BigDecimal m, BigDecimal x1, BigDecimal y1){
        //b = y1 - m*x1
        //m*x1
        BigDecimal mx1 = m.multiply(x1).setScale(5, BigDecimal.ROUND_HALF_UP);
        //b = y1 - m*x1
        return y1.subtract(mx1).setScale(5, BigDecimal.ROUND_HALF_UP);
    }

}
