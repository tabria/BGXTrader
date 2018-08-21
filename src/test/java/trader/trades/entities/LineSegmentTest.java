package trader.trades.entities;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.booleanThat;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LineSegmentTest {

    private LineSegment lineSegment;
    private Point mockPointX;
    private Point mockPointY;


    @Before
    public void before() {

        this.mockPointX = mock(Point.class);
        this.mockPointY = mock(Point.class);

        when(this.mockPointX.getTime()).thenReturn(BigDecimal.valueOf(2));
        when(this.mockPointX.getPrice()).thenReturn(BigDecimal.valueOf(1.1678));
        when(this.mockPointY.getTime()).thenReturn(BigDecimal.valueOf(3));
        when(this.mockPointY.getPrice()).thenReturn(BigDecimal.valueOf(1.1677));

        this.lineSegment = new LineSegment(this.mockPointX, this.mockPointY);

    }


    @Test(expected = NullPointerException.class)
    public void WhenCreateListSegmentWithXNullThenException(){
        this.lineSegment = new LineSegment(null, this.mockPointY);

    }

    @Test(expected = NullPointerException.class)
    public void WhenCreateListSegmentWithYNullThenException(){
        this.lineSegment = new LineSegment(this.mockPointX, null);

    }

    @Test(expected = IllegalArgumentException.class)
    public void WhenCreateLineSegmentWithYTimeLessThanXTimeThenException(){
        when(this.mockPointX.getTime()).thenReturn(BigDecimal.valueOf(3));
        when(this.mockPointY.getTime()).thenReturn(BigDecimal.valueOf(2));

        this.lineSegment = new LineSegment(this.mockPointX, this.mockPointY);
    }

    @Test(expected = NullPointerException.class)
    public void WhenCreatingSegmentWithNullArgumentThenException(){
        new LineSegment(null);
    }

    @Test
    public void WhenCreatingSementFromPricesThenReturnCorrectResult(){
        BigDecimal priceA = BigDecimal.ONE;
        BigDecimal priceB = BigDecimal.TEN;
        LineSegment segment = new LineSegment(priceA, priceB);

        BigDecimal priceAResult = segment.getPointA().getPrice();
        int comparePriceA = priceA.compareTo(priceAResult);

        BigDecimal timeAResult = segment.getPointA().getTime();
        int compareTimeA = BigDecimal.ONE.compareTo(timeAResult);

        BigDecimal priceBResult = segment.getPointB().getPrice();
        int comparePriceB = priceB.compareTo(priceBResult);

        BigDecimal timeBResult = segment.getPointB().getTime();
        int compareTimeB = BigDecimal.valueOf(2).compareTo(timeBResult);

        assertEquals(0, comparePriceA);
        assertEquals(0, compareTimeA);

        assertEquals(0, comparePriceB);
        assertEquals(0, compareTimeB
        );

    }

    @Test
    public void WhenGetPointAThenReturnCorrectResult(){

        when(this.mockPointX.getTime()).thenReturn(BigDecimal.valueOf(2));
        when(this.mockPointX.getPrice()).thenReturn(BigDecimal.valueOf(1.1678));
        when(this.mockPointY.getTime()).thenReturn(BigDecimal.valueOf(3));
        when(this.mockPointY.getPrice()).thenReturn(BigDecimal.valueOf(1.1677));

        this.lineSegment = new LineSegment(this.mockPointX, this.mockPointY);
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

        this.lineSegment = new LineSegment(this.mockPointX, this.mockPointY);
        Point pointB = this.lineSegment.getPointB();

        int comparePrice = pointB.getPrice().compareTo(BigDecimal.valueOf(1.1677));
        int compareTime = pointB.getTime().compareTo(BigDecimal.valueOf(3));

        assertNotSame(this.mockPointY, pointB);
        assertEquals(0, comparePrice);
        assertEquals(0, compareTime);
    }

    //Reflexive
    @Test
    public void WhenCallEqualsOnSameObjectThenReturnsTrue(){
        assertTrue(this.lineSegment.equals(this.lineSegment));
    }

    //Symmetric
    @Test
    public void WhenCallEqualsOnEqualObjectsThenResultMustBeSymmetric(){

        LineSegment lineSegment2 = new LineSegment(this.mockPointX, this.mockPointY);

        assertNotSame(this.lineSegment, lineSegment2);

        boolean result1 = this.lineSegment.equals(lineSegment2);
        boolean result2 = lineSegment2.equals(this.lineSegment);

        assertTrue(result1);
        assertTrue(result2);
    }

    //Transitive
    @Test
    public void WhenCallEqualsOnEqualObjectsThenResultMustBeTransitive(){

        LineSegment lineSegment1 = new LineSegment(this.mockPointX, this.mockPointY);
        LineSegment lineSegment2 = new LineSegment(this.mockPointX, this.mockPointY);

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

    //Consistence
    @Test
    public void WhenCallEqualsOnEqualObjectsThenResultMustBeConsistent(){

        LineSegment lineSegment = new LineSegment(this.mockPointX, this.mockPointY);

        assertNotSame(this.lineSegment, lineSegment);

        for (int i = 0; i <100 ; i++) {
            boolean result = this.lineSegment.equals(lineSegment);
            assertTrue(result);
        }
    }

    //Null
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
        LineSegment newLineSegment = new LineSegment(this.mockPointX, this.mockPointY);

        assertNotSame(this.lineSegment, newLineSegment);

        boolean equalObjectsResult = this.lineSegment.equals(newLineSegment);

        assertTrue(equalObjectsResult);

        int result1 = this.lineSegment.hashCode();
        int result2 = newLineSegment.hashCode();

        assertEquals(result1, result2);
    }

    @Test
    public void WhenCallHashCodeOnNonEqualObjectsThenResultsMustBeDifferent(){
        Point newMockPoint = mock(Point.class);
        when(newMockPoint.getPrice()).thenReturn(BigDecimal.valueOf(1.1111));
        when(newMockPoint.getTime()).thenReturn(BigDecimal.valueOf(12));

        LineSegment newLineSegment = new LineSegment(this.mockPointX, newMockPoint);

        boolean equalObjectsResult = this.lineSegment.equals(newLineSegment);

        assertFalse(equalObjectsResult);

        int result1 = this.lineSegment.hashCode();
        int result2 = newLineSegment.hashCode();

        assertNotEquals(result1, result2);

    }

}