package trader.entity.trade.segment;

import org.junit.Before;
import org.junit.Test;
import trader.entity.trade.point.Point;
import trader.entity.trade.point.PointImpl;
import trader.exception.NullArgumentException;

import java.math.BigDecimal;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LineSegmentImplTest {

    private LineSegmentImpl lineSegment;
    private PointImpl mockPointX;
    private PointImpl mockPointY;

    @Before
    public void before() {

        this.mockPointX = mock(PointImpl.class);
        this.mockPointY = mock(PointImpl.class);

        when(this.mockPointX.getTime()).thenReturn(BigDecimal.valueOf(2));
        when(this.mockPointX.getPrice()).thenReturn(BigDecimal.valueOf(1.1678));
        when(this.mockPointY.getTime()).thenReturn(BigDecimal.valueOf(3));
        when(this.mockPointY.getPrice()).thenReturn(BigDecimal.valueOf(1.1677));

        this.lineSegment = new LineSegmentImpl(this.mockPointX, this.mockPointY);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreateListSegmentWithXNullThenException(){
        this.lineSegment = new LineSegmentImpl(null, this.mockPointY);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreateListSegmentWithYNullThenException(){
        this.lineSegment = new LineSegmentImpl(this.mockPointX, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void WhenCreateLineSegmentWithYTimeLessThanXTimeThenException(){
        when(this.mockPointX.getTime()).thenReturn(BigDecimal.valueOf(3));
        when(this.mockPointY.getTime()).thenReturn(BigDecimal.valueOf(2));
        this.lineSegment = new LineSegmentImpl(this.mockPointX, this.mockPointY);
    }

    @Test
    public void WhenGetPointAThenReturnCorrectResult(){

        when(this.mockPointX.getTime()).thenReturn(BigDecimal.valueOf(2));
        when(this.mockPointX.getPrice()).thenReturn(BigDecimal.valueOf(1.1678));
        when(this.mockPointY.getTime()).thenReturn(BigDecimal.valueOf(3));
        when(this.mockPointY.getPrice()).thenReturn(BigDecimal.valueOf(1.1677));
        this.lineSegment = new LineSegmentImpl(this.mockPointX, this.mockPointY);
         Point pointA = this.lineSegment.getPointA();
        int comparePrice = pointA.getPrice().compareTo(BigDecimal.valueOf(1.1678));
        int compareTime = pointA.getTime().compareTo(BigDecimal.valueOf(2));

         assertNotSame(this.mockPointX, pointA);
         assertEquals("Prices are not same",0, comparePrice);
         assertEquals("Times are not same",0, compareTime);
    }

    @Test
    public void WhenGetPointBThenReturnCorrectResult(){

        when(this.mockPointX.getTime()).thenReturn(BigDecimal.valueOf(2));
        when(this.mockPointX.getPrice()).thenReturn(BigDecimal.valueOf(1.1678));
        when(this.mockPointY.getTime()).thenReturn(BigDecimal.valueOf(3));
        when(this.mockPointY.getPrice()).thenReturn(BigDecimal.valueOf(1.1677));

        this.lineSegment = new LineSegmentImpl(this.mockPointX, this.mockPointY);
        Point pointB = this.lineSegment.getPointB();

        int comparePrice = pointB.getPrice().compareTo(BigDecimal.valueOf(1.1677));
        int compareTime = pointB.getTime().compareTo(BigDecimal.valueOf(3));

        assertNotSame(this.mockPointY, pointB);
        assertEquals(0, comparePrice);
        assertEquals(0, compareTime);
    }

    @Test
    public void WhenCallEqualsOnSameObjectThenReturnsTrue(){
        LineSegmentImpl newSegment = this.lineSegment;
        boolean result = this.lineSegment.equals(newSegment);
        assertTrue(result);
    }

    @Test
    public void WhenCallEqualsOnEqualObjectsThenResultMustBeSymmetric(){
        LineSegmentImpl lineSegment2 = new LineSegmentImpl(this.mockPointX, this.mockPointY);
        assertNotSame(this.lineSegment, lineSegment2);
        boolean result1 = this.lineSegment.equals(lineSegment2);
        boolean result2 = lineSegment2.equals(this.lineSegment);

        assertTrue(result1);
        assertTrue(result2);
    }

    @Test
    public void WhenCallEqualsOnEqualObjectsThenResultMustBeTransitive(){
        LineSegmentImpl lineSegment1 = new LineSegmentImpl(this.mockPointX, this.mockPointY);
        LineSegmentImpl lineSegment2 = new LineSegmentImpl(this.mockPointX, this.mockPointY);

        assertNotSame(this.lineSegment, lineSegment1);
        assertNotSame(this.lineSegment, lineSegment2);
        assertNotSame(lineSegment1, lineSegment2);

        boolean result1 = this.lineSegment.equals(lineSegment1);
        boolean result2 = lineSegment1.equals(lineSegment2);
        boolean result3 = this.lineSegment.equals(lineSegment2);

        assertTrue(result1);
        assertTrue(result2);
        assertTrue(result3);

    }

    @Test
    public void WhenCallEqualsOnEqualObjectsThenResultMustBeConsistent(){

        LineSegmentImpl lineSegment = new LineSegmentImpl(this.mockPointX, this.mockPointY);
        assertNotSame(this.lineSegment, lineSegment);
        for (int i = 0; i <100 ; i++) {
            boolean result = this.lineSegment.equals(lineSegment);
            assertTrue(result);
        }
    }

    @Test
    public void WhenCallEqualsWithNullThenReturnFalse(){
        assertFalse(this.lineSegment.equals(null));
    }

    @Test
    public void WhenCallHashCodeMultipleTimeThenReturnsSameResult(){
        int expected = this.lineSegment.hashCode();
        for (int i = 0; i <100 ; i++) {
            int result = this.lineSegment.hashCode();
            assertEquals(expected, result);
        }
    }

    @Test
    public void WhenCallHashCodeOnEqualObjectsThenResultMustBeSame(){
        LineSegmentImpl newLineSegment = new LineSegmentImpl(this.mockPointX, this.mockPointY);
        assertNotSame(this.lineSegment, newLineSegment);

        boolean equalObjectsResult = this.lineSegment.equals(newLineSegment);
        assertTrue(equalObjectsResult);

        int result1 = this.lineSegment.hashCode();
        int result2 = newLineSegment.hashCode();
        assertEquals(result1, result2);
    }

    @Test
    public void WhenCallHashCodeOnNonEqualObjectsThenResultsMustBeDifferent(){
        PointImpl newMockPoint = mock(PointImpl.class);
        when(newMockPoint.getPrice()).thenReturn(BigDecimal.valueOf(1.1111));
        when(newMockPoint.getTime()).thenReturn(BigDecimal.valueOf(12));
        LineSegmentImpl newLineSegment = new LineSegmentImpl(this.mockPointX, newMockPoint);

        boolean equalObjectsResult = this.lineSegment.equals(newLineSegment);
        assertFalse(equalObjectsResult);

        int result1 = this.lineSegment.hashCode();
        int result2 = newLineSegment.hashCode();
        assertNotEquals(result1, result2);
    }

    @Test
    public void WhenCallToString_CorrectResult(){
        String expected = String.format("LineSegment{pointA=%s, pointB=%s}", mockPointX.toString(), mockPointY.toString());
        assertEquals(expected, lineSegment.toString());
    }

}