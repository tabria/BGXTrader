package trader.entry.service;

import org.junit.Before;
import org.junit.Test;
import trader.entity.trade.point.Point;
import trader.entity.trade.point.PointImpl;
import trader.entity.trade.segment.LineSegment;
import trader.entity.trade.segment.LineSegmentImpl;
import trader.exception.NullArgumentException;

import java.math.BigDecimal;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class IntersectionServiceTest {

    private Point mockAPointA;
    private Point mockAPointB;
    private Point mockBPointA;
    private Point mockBPointB;
    private LineSegment mockSegmentA;
    private LineSegment mockSegmentB;

    @Before
    public void before(){

        this.mockSegmentA = mock(LineSegmentImpl.class);
        this.mockSegmentB = mock(LineSegmentImpl.class);
        this.mockAPointA = mock(PointImpl.class);
        this.mockAPointB = mock(PointImpl.class);
        this.mockBPointA = mock(PointImpl.class);
        this.mockBPointB = mock(PointImpl.class);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallDoLineSegmentIntersectWithNullSegmentAThenException() {
        IntersectionService.doLineSegmentsIntersect(null, this.mockSegmentB);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallLineSegmentIntersectWithNullSegmentBThenException() {
        IntersectionService.doLineSegmentsIntersect(this.mockSegmentA, null);
    }

    @Test
    public void WhenFastWMAIntersectMiddleWMAFromBelowDoLineSegmentsIntersectReturnsTrue(){
        //fast ma must intersect middle ma
        setSegmentA(BigDecimal.valueOf(1.23439), BigDecimal.valueOf(1), BigDecimal.valueOf(1.23307), BigDecimal.valueOf(2));
        setSegmentB(BigDecimal.valueOf(1.23358), BigDecimal.valueOf(1), BigDecimal.valueOf(1.23342), BigDecimal.valueOf(2));
        boolean result = IntersectionService.doLineSegmentsIntersect(this.mockSegmentA, this.mockSegmentB);

        assertTrue(result);
    }

    @Test
    public void WhenFastWMAIntersectMiddleWMAFromAboveDoLineSegmentsIntersectReturnsTrue(){

        //fast wma must intersect middle wma
        setSegmentA(BigDecimal.valueOf(1.23979), BigDecimal.valueOf(1), BigDecimal.valueOf(1.23519), BigDecimal.valueOf(2));
        setSegmentB(BigDecimal.valueOf(1.23730), BigDecimal.valueOf(1), BigDecimal.valueOf(1.23636), BigDecimal.valueOf(2));

        boolean result = IntersectionService.doLineSegmentsIntersect(this.mockSegmentA, this.mockSegmentB);

        assertTrue(result);
    }

    @Test
    public void WhenFastWMADoNotIntersectMiddleWMADoLineSegmentsIntersectReturnsFalse(){

        //fast wma must not intersect middle wma
        setSegmentA(BigDecimal.valueOf(1.22709), BigDecimal.valueOf(1), BigDecimal.valueOf(1.22927), BigDecimal.valueOf(2));
        setSegmentB(BigDecimal.valueOf(1.23424), BigDecimal.valueOf(1), BigDecimal.valueOf(1.23441), BigDecimal.valueOf(2));

        boolean result = IntersectionService.doLineSegmentsIntersect(this.mockSegmentA, this.mockSegmentB);

        assertFalse(result);
    }
 //If check for collinearity is active this test must be active too
//    @Test
//    public void WhenFastWMALiesOnTopOfMiddleWMADoLineSegmentsIntersectReturnsTrue(){
//
//        //fast wma must not intersect middle wma
//        setSegmentA(BigDecimal.valueOf(1.22709), BigDecimal.valueOf(1), BigDecimal.valueOf(1.22927), BigDecimal.valueOf(2));
//        setSegmentB(BigDecimal.valueOf(1.22709), BigDecimal.valueOf(1), BigDecimal.valueOf(1.22927), BigDecimal.valueOf(2));
//
//        boolean result = IntersectionService.doLineSegmentsIntersect(this.mockSegmentA, this.mockSegmentB);
//
//        assertTrue(result);
//
//    }

    //If check for collinearity is not active this test must be active
    @Test
    public void WhenFastWMALiesOnTopOfMiddleWMADoLineSegmentsIntersectReturnsFalse(){

        //fast wma must not intersect middle wma
        setSegmentA(BigDecimal.valueOf(1.22709), BigDecimal.valueOf(1), BigDecimal.valueOf(1.22927), BigDecimal.valueOf(2));
        setSegmentB(BigDecimal.valueOf(1.22709), BigDecimal.valueOf(1), BigDecimal.valueOf(1.22927), BigDecimal.valueOf(2));

        boolean result = IntersectionService.doLineSegmentsIntersect(this.mockSegmentA, this.mockSegmentB);

        assertFalse(result);
    }

    @Test
    public void WhenFirstPointOfFastWMALiesOnTopOfFirstPointOFMiddleWMADoLineSegmentsIntersectReturnsTrue(){

        //fast wma must not intersect middle wma
        setSegmentA(BigDecimal.valueOf(1.22709), BigDecimal.valueOf(1), BigDecimal.valueOf(1.22977), BigDecimal.valueOf(2));
        setSegmentB(BigDecimal.valueOf(1.22709), BigDecimal.valueOf(1), BigDecimal.valueOf(1.22927), BigDecimal.valueOf(2));

        boolean result = IntersectionService.doLineSegmentsIntersect(this.mockSegmentA, this.mockSegmentB);

        assertTrue(result);
    }

    @Test
    public void WhenLastPointOfFastWMALiesOnTopOfLastPointOFMiddleWMADoLineSegmentsIntersectReturnsFalse(){

        //fast wma must not intersect middle wma
        setSegmentA(BigDecimal.valueOf(1.22719), BigDecimal.valueOf(1), BigDecimal.valueOf(1.22927), BigDecimal.valueOf(2));
        setSegmentB(BigDecimal.valueOf(1.22709), BigDecimal.valueOf(1), BigDecimal.valueOf(1.22927), BigDecimal.valueOf(2));

        boolean result = IntersectionService.doLineSegmentsIntersect(this.mockSegmentA, this.mockSegmentB);

        assertFalse(result);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCalculateIntersectionPointWithNullSegmentAThenException(){
        IntersectionService.calculateIntersectionPoint(null, this.mockSegmentB);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCalculateIntersectionPointWithNullSegmentBThenException(){
        IntersectionService.calculateIntersectionPoint(this.mockSegmentA, null);
    }

    @Test
    public void WhenCalculateIntersectionPointThenReturnCorrectResults(){

        //fast ma must intersect middle ma
        setSegmentA(BigDecimal.valueOf(1.22889), BigDecimal.valueOf(1), BigDecimal.valueOf(1.23339), BigDecimal.valueOf(2));
        setSegmentB(BigDecimal.valueOf(1.23119), BigDecimal.valueOf(1), BigDecimal.valueOf(1.23196), BigDecimal.valueOf(2));

//        setSegmentA(BigDecimal.valueOf(1.22537), BigDecimal.valueOf(1), BigDecimal.valueOf(1.22622), BigDecimal.valueOf(2));
//        setSegmentB(BigDecimal.valueOf(1.22606), BigDecimal.valueOf(1), BigDecimal.valueOf(1.22615), BigDecimal.valueOf(2));

        Point resultPoint = IntersectionService.calculateIntersectionPoint(this.mockSegmentA, this.mockSegmentB);

        BigDecimal resultPrice = resultPoint.getPrice();

        int compare = BigDecimal.valueOf(1.23166).compareTo(resultPrice);

        assertEquals(0, compare);

    }

    @Test
    public void WhenCalculateIntersection(){

        //fast wma must not intersect middle wma
        setSegmentA(BigDecimal.valueOf(1.22700), BigDecimal.valueOf(1), BigDecimal.valueOf(1.22827), BigDecimal.valueOf(2));
        setSegmentB(BigDecimal.valueOf(1.220), BigDecimal.valueOf(1), BigDecimal.valueOf(1.2200), BigDecimal.valueOf(2));

        boolean result = IntersectionService.doLineSegmentsIntersect(this.mockSegmentA, this.mockSegmentB);

        setSegmentB(BigDecimal.valueOf(1.2290), BigDecimal.valueOf(1), BigDecimal.valueOf(1.2290), BigDecimal.valueOf(2));

        boolean result2 = IntersectionService.doLineSegmentsIntersect(this.mockSegmentA, this.mockSegmentB);

        assertFalse(result);
    }

    private void setMockAPoints(BigDecimal APriceA, BigDecimal ATimeA,
                                BigDecimal APriceB, BigDecimal ATimeB){

        when(this.mockAPointA.getPrice()).thenReturn(APriceA);
        when(this.mockAPointA.getTime()).thenReturn(ATimeA);
        when(this.mockAPointB.getPrice()).thenReturn(APriceB);
        when(this.mockAPointB.getTime()).thenReturn(ATimeB);
    }

    private void setMockBPoints(BigDecimal BPriceA, BigDecimal BTimeA,
                           BigDecimal BPriceB, BigDecimal BTimeB){

        when(this.mockBPointA.getPrice()).thenReturn(BPriceA);
        when(this.mockBPointA.getTime()).thenReturn(BTimeA);
        when(this.mockBPointB.getPrice()).thenReturn(BPriceB);
        when(this.mockBPointB.getTime()).thenReturn(BTimeB);
    }


    private void setSegmentA(BigDecimal APriceA, BigDecimal ATimeA,
                             BigDecimal APriceB, BigDecimal ATimeB){

        setMockAPoints(APriceA, ATimeA, APriceB, ATimeB);

        when(this.mockSegmentA.getPointA()).thenReturn(this.mockAPointA);
        when(this.mockSegmentA.getPointB()).thenReturn(this.mockAPointB);

    }

    private void setSegmentB(BigDecimal BPriceA, BigDecimal BTimeA,
                                 BigDecimal BPriceB, BigDecimal BTimeB){

        setMockBPoints(BPriceA, BTimeA, BPriceB, BTimeB);

        when(this.mockSegmentB.getPointA()).thenReturn(this.mockBPointA);
        when(this.mockSegmentB.getPointB()).thenReturn(this.mockBPointB);

    }
}