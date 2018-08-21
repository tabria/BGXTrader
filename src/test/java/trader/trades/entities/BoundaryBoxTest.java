package trader.trades.entities;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BoundaryBoxTest {

    private BoundaryBox boundaryBox;
    private LineSegment mockLineSegment;
    private Point mockPointX;
    private Point mockPointY;


    @Before
    public void before(){

        this.mockPointX = mock(Point.class);
        when(this.mockPointX.getTime()).thenReturn(BigDecimal.valueOf(1));

        this.mockPointY = mock(Point.class);
        when(this.mockPointY.getTime()).thenReturn(BigDecimal.valueOf(3));


        this.mockLineSegment = mock(LineSegment.class);

    }


    @Test(expected = NullPointerException.class)
    public void WhenCreateNewBoundaryBoxWithNullThenException(){
        new BoundaryBox(null);
    }

    @Test(expected = NullPointerException.class)
    public void WhenCreateNewBoundaryBoxWithNullOffsetThenException(){
        new BoundaryBox(this.mockLineSegment, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void WhenCreateNewBoundaryBoxWithNegativeOffsetThenException(){
        new BoundaryBox(this.mockLineSegment, BigDecimal.valueOf(-1));
    }

    @Test
    public void WhenCreateNewBoundaryBoxWithCorrectOffsetThenCorrectResult() throws NoSuchFieldException, IllegalAccessException {

        BigDecimal priceA = BigDecimal.valueOf(1.16000);
        BigDecimal priceB = BigDecimal.valueOf(1.15900);

        when(this.mockPointX.getPrice()).thenReturn(priceA);
        when(this.mockPointY.getPrice()).thenReturn(priceB);
        when(this.mockLineSegment.getPointA()).thenReturn(this.mockPointX);
        when(this.mockLineSegment.getPointB()).thenReturn(this.mockPointY);

        BigDecimal expected = BigDecimal.valueOf(0.0004);
        BoundaryBox boundaryBox = new BoundaryBox(this.mockLineSegment, expected);

        Field field = boundaryBox.getClass().getDeclaredField("offset");
        field.setAccessible(true);
        BigDecimal result = (BigDecimal) field.get(boundaryBox);

        int compare = expected.compareTo(result);
        assertEquals(0, compare);
    }

    @Test(expected = IllegalArgumentException.class)
    public void WhenCreateNewBoundaryBoxWithOffsetBiggerThenMaxThenException()  {

        BigDecimal expected = BigDecimal.valueOf(10);
        BoundaryBox boundaryBox = new BoundaryBox(this.mockLineSegment, expected);
    }

    @Test
    public void WhenGetLineSegmentThenReturnCorrectResult(){

        BigDecimal priceA = BigDecimal.valueOf(1.16000);
        BigDecimal priceB = BigDecimal.valueOf(1.15900);

        when(this.mockPointX.getPrice()).thenReturn(priceA);
        when(this.mockPointY.getPrice()).thenReturn(priceB);
        when(this.mockLineSegment.getPointA()).thenReturn(this.mockPointX);
        when(this.mockLineSegment.getPointB()).thenReturn(this.mockPointY);

        BoundaryBox bb = new BoundaryBox(this.mockLineSegment);
        LineSegment result = bb.getLineSegment();

        assertNotSame(this.mockLineSegment, result);
        assertSame(this.mockPointX, this.mockLineSegment.getPointA());
        assertSame(this.mockPointY, this.mockLineSegment.getPointB());

    }

    @Test
    public void WhenCreateBoundaryBoxWithPointAPriceBiggerThanPointBPriceThenReturnCorrectResults() throws NoSuchFieldException, IllegalAccessException {

        BigDecimal priceA = BigDecimal.valueOf(1.16000);
        BigDecimal priceB = BigDecimal.valueOf(1.15900);

        when(this.mockPointX.getPrice()).thenReturn(priceA);
        when(this.mockPointY.getPrice()).thenReturn(priceB);
        when(this.mockLineSegment.getPointA()).thenReturn(this.mockPointX);
        when(this.mockLineSegment.getPointB()).thenReturn(this.mockPointY);

        BoundaryBox bb = new BoundaryBox(this.mockLineSegment);

        Field field = bb.getClass().getDeclaredField("DEFAULT_OFFSET");
        field.setAccessible(true);

        BigDecimal resultA = priceA.add((BigDecimal) field.get(bb)).setScale(5, BigDecimal.ROUND_HALF_UP);
        BigDecimal resultB = priceB.subtract((BigDecimal) field.get(bb)).setScale(5, BigDecimal.ROUND_HALF_UP);

        int compareAPrices = resultA.compareTo(bb.getPointA().getPrice());
        int compareBPrices = resultB.compareTo(bb.getPointB().getPrice());

        assertEquals(0, compareAPrices);
        assertEquals(0, compareBPrices);
    }

    @Test
    public void WhenCreateBoundaryBoxWithPointAPriceEqualThanPointBPriceThenReturnCorrectResults() throws NoSuchFieldException, IllegalAccessException {

        BigDecimal priceA = BigDecimal.valueOf(1.16000);
        BigDecimal priceB = BigDecimal.valueOf(1.16000);

        when(this.mockPointX.getPrice()).thenReturn(priceA);
        when(this.mockPointY.getPrice()).thenReturn(priceB);
        when(this.mockLineSegment.getPointA()).thenReturn(this.mockPointX);
        when(this.mockLineSegment.getPointB()).thenReturn(this.mockPointY);

        BoundaryBox bb = new BoundaryBox(this.mockLineSegment);

        Field field = bb.getClass().getDeclaredField("DEFAULT_OFFSET");
        field.setAccessible(true);

        BigDecimal resultA = priceA.add((BigDecimal) field.get(bb)).setScale(5, BigDecimal.ROUND_HALF_UP);
        BigDecimal resultB = priceB.subtract((BigDecimal) field.get(bb)).setScale(5, BigDecimal.ROUND_HALF_UP);

        int compareAPrices = resultA.compareTo(bb.getPointA().getPrice());
        int compareBPrices = resultB.compareTo(bb.getPointB().getPrice());

        assertEquals(0, compareAPrices);
        assertEquals(0, compareBPrices);
    }

    @Test
    public void WhenCreateBoundaryBoxWithPointAPriceLessThanPointBPriceThenReturnCorrectResults() throws NoSuchFieldException, IllegalAccessException {

        BigDecimal priceA = BigDecimal.valueOf(1.16000);
        BigDecimal priceB = BigDecimal.valueOf(1.16200);

        when(this.mockPointX.getPrice()).thenReturn(priceA);
        when(this.mockPointY.getPrice()).thenReturn(priceB);
        when(this.mockLineSegment.getPointA()).thenReturn(this.mockPointX);
        when(this.mockLineSegment.getPointB()).thenReturn(this.mockPointY);

        BoundaryBox bb = new BoundaryBox(this.mockLineSegment);

        Field field = bb.getClass().getDeclaredField("DEFAULT_OFFSET");
        field.setAccessible(true);

        BigDecimal resultA = priceA.subtract((BigDecimal) field.get(bb)).setScale(5, BigDecimal.ROUND_HALF_UP);
        BigDecimal resultB = priceB.add((BigDecimal) field.get(bb)).setScale(5, BigDecimal.ROUND_HALF_UP);

        int compareAPrices = resultA.compareTo(bb.getPointA().getPrice());
        int compareBPrices = resultB.compareTo(bb.getPointB().getPrice());

        assertEquals(0, compareAPrices);
        assertEquals(0, compareBPrices);
    }

    @Test
    public void TestToString(){
        BigDecimal priceA = BigDecimal.valueOf(1.16000);
        BigDecimal priceB = BigDecimal.valueOf(1.16000);

        when(this.mockPointX.getPrice()).thenReturn(priceA);
        when(this.mockPointX.toString()).thenReturn("Point{price=1.16040, time=1}");

        when(this.mockPointY.getPrice()).thenReturn(priceB);
        when(this.mockPointY.toString()).thenReturn("Point{price=1.15960, time=2}");

        when(this.mockLineSegment.getPointA()).thenReturn(this.mockPointX);
        when(this.mockLineSegment.getPointB()).thenReturn(this.mockPointY);

        this.boundaryBox = new BoundaryBox(this.mockLineSegment);
        String result = this.boundaryBox.toString();
//        String expected = String.format("WeightedMA{period=%d, appliedPrice=%s, maValues=[], points=[], isTradeGenerated=false}", this.period, this.mockAppliedPrice.toString());
        String expected = String.format("BoundaryBox{lineSegment=%s, offset=0.00040, pointA=%s, pointB=%s}",this.mockLineSegment.toString(), this.mockPointX.toString(), this.mockPointY.toString());
        assertEquals(expected, result);
    }
}
