package trader.entry.standard.service;


import trader.entity.trade.point.Point;
import trader.entity.trade.point.PointImpl;
import trader.entity.trade.segment.LineSegment;
import trader.exception.NullArgumentException;

import java.math.BigDecimal;

public final class IntersectionService {

    private IntersectionService(){ }

    /**
     * Check if 2 lineSegments(lines) have intersection. Method is based on Franklin Antonio's "Faster Line Segment IntersectionService" topic "in Graphics Gems III" book (http://www.graphicsgems.org/)
     */
    public static boolean doLineSegmentsIntersect(LineSegment segmentA, LineSegment segmentB){
        if (segmentA == null || segmentB == null)
            throw new NullArgumentException();
        Point pointA1 = segmentA.getPointB();
        Point pointA2 = segmentA.getPointA();

        Point pointB1 = segmentB.getPointB();
        Point pointB2 = segmentB.getPointA();

        //check if end point of segmentA lies on top of end point of segmentB. This may be bounce or intersection => return false
        if (pointA1.getPrice().compareTo(pointB1.getPrice()) == 0)
            return false;

        //check if start point of segmentA lies on top of the start point of segmentB. It is not real intersection, but the signal generated will be same as if it was real intersection.
        if(pointA2.getPrice().compareTo(pointB2.getPrice()) == 0)
            return true;

        BigDecimal alpha = calculateParts(
                subtractPrice(pointB2, pointB1),
                subtractTime(pointB1, pointA1),
                subtractTime(pointB2, pointB1),
                subtractPrice(pointB1, pointA1));

        BigDecimal beta = calculateParts(
                subtractTime(pointA1, pointA2),
                subtractPrice(pointB1, pointA1),
                subtractPrice(pointA1, pointA2),
                subtractTime(pointB1, pointA1));

        BigDecimal denominator = calculateParts(
                subtractPrice(pointA1, pointA2),
                subtractTime(pointB2, pointB1),
                subtractTime(pointA1, pointA2),
                subtractPrice(pointB2, pointB1));

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

    private static BigDecimal calculateParts(BigDecimal subtractResultA, BigDecimal subtractResultB, BigDecimal subtractResultC, BigDecimal subtractResultD) {
        BigDecimal multiplyA = subtractResultA
                .multiply(subtractResultB)
                .setScale(5, BigDecimal.ROUND_HALF_UP);
        BigDecimal multiplyB = subtractResultC
                .multiply(subtractResultD)
                .setScale(5, BigDecimal.ROUND_HALF_UP);
        return multiplyA
                .subtract(multiplyB)
                .setScale(5, BigDecimal.ROUND_HALF_UP);
    }

    private static BigDecimal subtractPrice(Point pointA1, Point pointA2) {
        return pointA2.getPrice().subtract(pointA1.getPrice())
                    .setScale(5, BigDecimal.ROUND_HALF_UP);
    }

    private static BigDecimal subtractTime(Point pointA1, Point pointA2) {
        return pointA2.getTime().subtract(pointA1.getTime())
                    .setScale(5, BigDecimal.ROUND_HALF_UP);
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
     * @return {@link Point } intersection point
     */
    public static Point calculateIntersectionPoint(LineSegment segmentA, LineSegment segmentB){
        if (segmentA == null || segmentB == null)
            throw new NullArgumentException();

        Point pointA1 = segmentA.getPointB();
        Point pointA2 = segmentA.getPointA();
        Point pointB1 = segmentB.getPointB();
        Point pointB2 = segmentB.getPointA();

        //calculating Slope
        BigDecimal slopeSegmentA = IntersectionService
                .calculateSlope(pointA1.getTime(), pointA1.getPrice(), pointA2.getTime(), pointA2.getPrice());
        BigDecimal slopeSegmentB = IntersectionService
                .calculateSlope(pointB1.getTime(), pointB1.getPrice(), pointB2.getTime(),  pointB2.getPrice());
        //calculating b for y = m*x + b
        BigDecimal bForSegmentA = IntersectionService
                .calculatingB(slopeSegmentA, pointA2.getTime(), pointA2.getPrice());
        BigDecimal bForSegmentB = IntersectionService
                .calculatingB(slopeSegmentB, pointB2.getTime(), pointB2.getPrice());
        //calculating x for the intersection point x = (b2-b1)/(m1-m2)
        //b2-b1
        BigDecimal yInterceptDifference = bForSegmentB
                .subtract(bForSegmentA)
                .setScale(5, BigDecimal.ROUND_HALF_UP);
        //m1 - m2
        BigDecimal slopeDifference = slopeSegmentA
                .subtract(slopeSegmentB)
                .setScale(5, BigDecimal.ROUND_HALF_UP);

        BigDecimal xIntersectPoint;
        if (slopeDifference.compareTo(BigDecimal.ZERO) == 0)
            xIntersectPoint = BigDecimal.ZERO;
        else
            xIntersectPoint = yInterceptDifference.divide(slopeDifference, 5, BigDecimal.ROUND_HALF_UP);

        //calculate y = m1*((b2-b1)/(m1-m2)) + b1
        BigDecimal m1x = slopeSegmentA
                .multiply(xIntersectPoint)
                .setScale(5, BigDecimal.ROUND_HALF_UP);
        BigDecimal yIntersectPoint = m1x
                .add(bForSegmentA)
                .setScale(5, BigDecimal.ROUND_HALF_UP);

        //Time is irrelevant. So the x. Set default time of 1 for the intersection point.
        return new PointImpl(yIntersectPoint);
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
        if(xx1.compareTo(BigDecimal.ZERO) == 0)
            return BigDecimal.ZERO;
        else
            //m = (y-y1)/(x-x1)
            return yy1.divide(xx1,5, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Calculate b for the slope-intersection format(y = mx + b)
     * From slope formula m = (y-y1)/(x-x1) => y-y1=m*(x-x1).
     * X1 and y1 are known values - from the segment end point => after substitution we can convert this formula to slope intersection format y = m*x + b. Then b = y1 - m*x1
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
