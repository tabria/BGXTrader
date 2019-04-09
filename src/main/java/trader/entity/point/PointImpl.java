package trader.entity.point;

import trader.exception.NullArgumentException;

import java.math.BigDecimal;

public class PointImpl implements Point {

    private static final BigDecimal DEFAULT_START_TIME = BigDecimal.ONE;

    private BigDecimal price ;
    private BigDecimal time;

    public PointImpl(BigDecimal price){
        setPrice(price);
        this.time = DEFAULT_START_TIME;
    }

    public PointImpl(BigDecimal price, BigDecimal time){
        setPrice(price);
        setTime(time);
    }

    public PointImpl(Point point){
        if (point == null)
            throw new NullArgumentException();
        this.price = point.getPrice();
        this.time = point.getTime();
    }

    @Override
    public BigDecimal getPrice() {
        return this.price;
    }

    @Override
    public BigDecimal getTime(){
        return this.time;
    }

    @Override
    public void setPrice(BigDecimal price){
        if (price == null)
            throw new NullArgumentException();
        int result = price.compareTo(BigDecimal.ZERO);
        if (result <= 0 )
            throw new IllegalArgumentException();
        this.price = price;
    }

    @Override
    public void setTime(BigDecimal time){
        if (time == null)
            throw new NullArgumentException();
        int result = time.compareTo(DEFAULT_START_TIME);
        if (result < 0)
            throw  new IllegalArgumentException();
        this.time = time;
    }

    @Override
    public int hashCode() {
        int result = this.price.hashCode();
        result = 31*result + this.time.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if(!(obj instanceof PointImpl))
            return false;
        PointImpl newPoint = (PointImpl) obj;

        return newPoint.price.compareTo(this.price)==0
                && newPoint.time.compareTo(this.time)==0;
    }

    @Override
    public String toString() {
        return "Point{" +
                "price=" + price.toString() +
                ", time=" + time.toString() +
                '}';
    }
}
