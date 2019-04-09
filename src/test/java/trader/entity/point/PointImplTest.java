package trader.entity.point;

import org.junit.Before;
import org.junit.Test;
import trader.entity.point.Point;
import trader.entity.point.PointImpl;
import trader.exception.NullArgumentException;

import java.lang.reflect.Field;
import java.math.BigDecimal;

import static org.junit.Assert.*;

public class PointImplTest {


    private static final BigDecimal DEFAULT_PRICE_START = BigDecimal.valueOf(1.1722);


    private Point point;
    private BigDecimal startTime = BigDecimal.valueOf(10);

    @Before
    public void before() {

        this.point = new PointImpl(DEFAULT_PRICE_START, this.startTime);

    }

    @Test
    public void WhenCreateNewPointThenNewObject(){
        Point point2 = new PointImpl(DEFAULT_PRICE_START);

        assertNotEquals(this.point, point2);
    }

    @Test
    public void WhenCreateNewPointThenReturnCorrectPriceStart(){
        int result = this.point.getPrice().compareTo(DEFAULT_PRICE_START);

        assertEquals(0, result);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreatingNewPointFromNullPointThenException(){
        new PointImpl((Point) null);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreateNewPointWithNullStartPriceThenException(){
        new PointImpl((BigDecimal) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void WhenCreateNewPointWithNegativeStartPriceThenException(){
        new PointImpl(BigDecimal.valueOf(-1));
    }

    @Test (expected = NullArgumentException.class)
    public void WhenCreateNewPointWithNullStartTimeThenException()  {
        new PointImpl(DEFAULT_PRICE_START, null);

    }

    @Test (expected = IllegalArgumentException.class)
    public void WhenCreateNewPointWithStartTimeLessThanOneThenException(){
        new PointImpl(DEFAULT_PRICE_START, BigDecimal.valueOf(0));
    }

    @Test
    public void WhenCreateNewPointThenReturnCorrectTimeStart(){
        int result = this.point.getTime().compareTo(this.startTime);

        assertEquals(0, result);
    }

    @Test
    public void WhenCreateNewPointWithNoStartTimeThenReturnDefaultStartTime() throws NoSuchFieldException, IllegalAccessException {
        Point point = new PointImpl(DEFAULT_PRICE_START);
        Field field = point.getClass().getDeclaredField("DEFAULT_START_TIME");
        field.setAccessible(true);
        BigDecimal expected = (BigDecimal) field.get(point);
        BigDecimal actual = point.getTime();
        int result = expected.compareTo(actual);

        assertEquals(0, result);

    }

    @Test
    public void WhenCallEqualsOnSameObjectsThenReturnTrue(){
        Point newPoint = this.point;
        boolean equals = this.point.equals(newPoint);
        assertTrue(equals);
    }

    @Test
    public void WhenCallEqualsThenResultMustBeSymmetric(){
        Point newPoint = new PointImpl(DEFAULT_PRICE_START, this.startTime);
        boolean symmetricResult1 = this.point.equals(newPoint);
        boolean symmetricResult2 = newPoint.equals(this.point);

        assertNotSame(this.point, newPoint);
        assertTrue(symmetricResult1);
        assertTrue(symmetricResult2);
    }

    @Test
    public void WhenCallEqualsThenResultMustBeTransitive(){
        Point newPoint = new PointImpl(DEFAULT_PRICE_START, this.startTime);
        Point lastPoint = new PointImpl(DEFAULT_PRICE_START, this.startTime);
        boolean pointNewPointResult = this.point.equals(newPoint);
        boolean newPointLastPointResult = newPoint.equals(lastPoint);
        boolean pointLastPointResult = this.point.equals(lastPoint);

        assertNotSame(this.point, newPoint);
        assertNotSame(this.point, lastPoint);
        assertNotSame(newPoint, lastPoint);
        assertTrue(pointNewPointResult);
        assertTrue(newPointLastPointResult);
        assertTrue(pointLastPointResult);
    }

    @Test
    public void WhenCallEqualsThenResultMustBeConsistence(){
        Point newPoint = new PointImpl(DEFAULT_PRICE_START, this.startTime);

        assertNotSame(this.point, newPoint);
        for (int i = 0; i <10 ; i++) {
            boolean pointNewPointResult = this.point.equals(newPoint);
            assertTrue(pointNewPointResult);
        }
    }

    @Test
    public void WhenCallEqualsWithNullThenReturnFalse(){
        assertFalse(this.point.equals(null));
    }

    @Test
    public void WhenCallHashCodeMultipleTimesThenSameResultEachTime(){
        int expected = this.point.hashCode();
        for (int i = 0; i <100 ; i++) {
            int result = this.point.hashCode();
            assertEquals(expected, result);
        }
    }

    @Test
    public void WhenCallHashCodeOnEqualObjectsThenResultsMustBeSame(){

        Point newPoint = new PointImpl(DEFAULT_PRICE_START, this.startTime);

        assertNotSame(this.point, newPoint);

        boolean equalResult = this.point.equals(newPoint);
        assertTrue(equalResult);

        int pointHashCodeResult = this.point.hashCode();
        int newPointHashCodeResult = newPoint.hashCode();

        assertEquals(pointHashCodeResult, newPointHashCodeResult);

    }

    @Test
    public void WhenCallHashCodeOnNonEqualObjectsThenResultsMustBeDifferent(){

        Point newPoint = new PointImpl(DEFAULT_PRICE_START, BigDecimal.valueOf(10.1));

        assertNotSame(this.point, newPoint);

        boolean equalResult = this.point.equals(newPoint);
        assertFalse(equalResult);

        int pointHashCodeResult = this.point.hashCode();
        int newPointHashCodeResult = newPoint.hashCode();

        assertNotEquals(pointHashCodeResult, newPointHashCodeResult);
    }

    @Test
    public void WhenCallToStringThenCorrectOutput(){

        String expected = String.format("Point{price=%s, time=%s}", DEFAULT_PRICE_START, this.startTime);
        assertEquals(expected, point.toString());
    }

}