package trader.trade.entitie;

import org.junit.Before;
import org.junit.Test;
import trader.entity.point.PointImpl;
import trader.exception.NullArgumentException;

import java.lang.reflect.Field;
import java.math.BigDecimal;

import static org.junit.Assert.*;

public class PointImplTest {


    private static final BigDecimal DEFAULT_PRICE_START = BigDecimal.valueOf(1.1722);


    private PointImpl point;
    private BigDecimal startTime = BigDecimal.valueOf(10);

    @Before
    public void before() {

        this.point = new PointImpl.PointBuilder(DEFAULT_PRICE_START)
                .setTime(this.startTime)
                .build();

    }

    @Test
    public void WhenCreateNewPointThenNewObject(){
        PointImpl point2 = new PointImpl.PointBuilder(DEFAULT_PRICE_START).build();

        assertNotEquals(this.point, point2);
    }

    @Test
    public void WhenCreateNewPointThenReturnCorrectPriceStart(){
        int result = this.point.getPrice().compareTo(DEFAULT_PRICE_START);

        assertEquals(0, result);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreatingNewPointFromNullPointThenException(){
        new PointImpl(null);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreateNewPointWithNullStartPriceThenException(){
        PointImpl point = new PointImpl.PointBuilder(null).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void WhenCreateNewPointWithNegativeStartPriceThenException(){
        PointImpl point = new PointImpl.PointBuilder(BigDecimal.valueOf(-1)).build();
    }

    @Test (expected = NullArgumentException.class)
    public void WhenCreateNewPointWithNullStartTimeThenException()  {

        PointImpl point = new PointImpl.PointBuilder(DEFAULT_PRICE_START)
                .setTime(null)
                .build();

    }

    @Test (expected = IllegalArgumentException.class)
    public void WhenCreateNewPointWithStartTimeLessThanOneThenException(){
        PointImpl point = new PointImpl.PointBuilder(DEFAULT_PRICE_START)
                .setTime(BigDecimal.valueOf(0))
                .build();
    }

    @Test
    public void WhenCreateNewPointThenReturnCorrectTimeStart(){

        int result = this.point.getTime().compareTo(this.startTime);

        assertEquals(0, result);
    }



    @Test
    public void WhenCreateNewPointWithNoStartTimeThenReturnDefaultStartTime() throws NoSuchFieldException, IllegalAccessException {

        PointImpl point = new PointImpl.PointBuilder(DEFAULT_PRICE_START).build();
        Field field = point.getClass().getDeclaredField("DEFAULT_START_TIME");
        field.setAccessible(true);
        BigDecimal expected = (BigDecimal) field.get(point);

        BigDecimal actual = point.getTime();
        int result = expected.compareTo(actual);

        assertEquals(0, result);

    }

    //Reflexive
    @Test
    public void WhenCallEqualsOnSameObjectsThenReturnTrue(){
        PointImpl newPoint = this.point;
        boolean equals = this.point.equals(newPoint);
        assertTrue(equals);
    }

    //Symmetric
    @Test
    public void WhenCallEqualsThenResultMustBeSymmetric(){
        PointImpl newPoint = new PointImpl.PointBuilder(DEFAULT_PRICE_START)
                .setTime(this.startTime)
                .build();

        boolean symmetricResult1 = this.point.equals(newPoint);
        boolean symmetricResult2 = newPoint.equals(this.point);

        assertNotSame(this.point, newPoint);
        assertTrue(symmetricResult1);
        assertTrue(symmetricResult2);
    }

    //Transitive
    @Test
    public void WhenCallEqualsThenResultMustBeTransitive(){
        PointImpl newPoint = new PointImpl.PointBuilder(DEFAULT_PRICE_START)
                .setTime(this.startTime)
                .build();
        PointImpl lastPoint = new PointImpl.PointBuilder(DEFAULT_PRICE_START)
                .setTime(this.startTime)
                .build();
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

    //Consistence
    @Test
    public void WhenCallEqualsThenResultMustBeConsistence(){
        PointImpl newPoint = new PointImpl.PointBuilder(DEFAULT_PRICE_START)
                .setTime(this.startTime)
                .build();

        assertNotSame(this.point, newPoint);

        for (int i = 0; i <10 ; i++) {
            boolean pointNewPointResult = this.point.equals(newPoint);
            assertTrue(pointNewPointResult);
        }
    }

    //Null
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

        PointImpl newPoint = new PointImpl.PointBuilder(DEFAULT_PRICE_START)
                .setTime(this.startTime)
                .build();

        assertNotSame(this.point, newPoint);

        boolean equalResult = this.point.equals(newPoint);
        assertTrue(equalResult);

        int pointHashCodeResult = this.point.hashCode();
        int newPointHashCodeResult = newPoint.hashCode();

        assertEquals(pointHashCodeResult, newPointHashCodeResult);

    }

    @Test
    public void WhenCallHashCodeOnNonEqualObjectsThenResultsMustBeDifferent(){

        PointImpl newPoint = new PointImpl.PointBuilder(DEFAULT_PRICE_START)
                .setTime(BigDecimal.valueOf(10.1))
                .build();

        assertNotSame(this.point, newPoint);

        boolean equalResult = this.point.equals(newPoint);
        assertFalse(equalResult);

        int pointHashCodeResult = this.point.hashCode();
        int newPointHashCodeResult = newPoint.hashCode();

        assertNotEquals(pointHashCodeResult, newPointHashCodeResult);

    }

}