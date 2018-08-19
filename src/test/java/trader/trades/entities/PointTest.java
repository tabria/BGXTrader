package trader.trades.entities;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;

import static org.junit.Assert.*;

public class PointTest {


    private static final BigDecimal DEFAULT_PRICE_START = BigDecimal.valueOf(1.1722);


    private Point point;
    private BigDecimal startTime = BigDecimal.valueOf(10);

    @Before
    public void before() throws Exception {

        this.point = new Point.PointBuilder(DEFAULT_PRICE_START)
                .setTime(this.startTime)
                .build();

    }

    @Test
    public void WhenCreateNewPointThenNewObject(){
        Point point2 = new Point.PointBuilder(DEFAULT_PRICE_START).build();

        assertNotEquals(this.point, point2);
    }

    @Test
    public void WhenCreateNewPointThenReturnCorrectPriceStart(){
        int result = this.point.getPrice().compareTo(DEFAULT_PRICE_START);

        assertEquals(0, result);
    }

    @Test(expected = NullPointerException.class)
    public void WhenCreatingNewPointFromNullPointThenException(){
        new Point(null);
    }

    @Test(expected = NullPointerException.class)
    public void WhenCreateNewPointWithNullStartPriceThenException(){
        Point point = new Point.PointBuilder(null).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void WhenCreateNewPointWithNegativeStartPriceThenException(){
        Point point = new Point.PointBuilder(BigDecimal.valueOf(-1)).build();
    }

    @Test (expected = NullPointerException.class)
    public void WhenCreateNewPointWithNullStartTimeThenException() throws NoSuchFieldException, IllegalAccessException {

        Point point = new Point.PointBuilder(DEFAULT_PRICE_START)
                .setTime(null)
                .build();

    }

    @Test (expected = IllegalArgumentException.class)
    public void WhenCreateNewPointWithStartTimeLessThanOneThenException(){
        Point point = new Point.PointBuilder(DEFAULT_PRICE_START)
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

        Point point = new Point.PointBuilder(DEFAULT_PRICE_START).build();
        Field field = point.getClass().getDeclaredField("DEFAULT_START_TIME");
        field.setAccessible(true);
        BigDecimal expected = (BigDecimal) field.get(point);

        BigDecimal actual = point.getTime();
        int result = expected.compareTo(actual);

        assertEquals(0, result);

    }
}