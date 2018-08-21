package trader.trades.entities;

import java.math.BigDecimal;


/**
 * Point class
 */
public final class Point {

    private static final BigDecimal DEFAULT_START_TIME = BigDecimal.ONE;

    /**
     * time is intervals between prices
     */
    private final BigDecimal price ;
    private final BigDecimal time;

    /**
     * Private constructor
     * @param builder current builder object
     */
    private Point(PointBuilder builder){
        this.price = builder.price;
        this.time = builder.time;

    }

    /**
     * Copy Constructor
     * @param point get current point
     * @throws NullPointerException when argument is null
     */
    public Point(Point point){
        if (point == null){
            throw new NullPointerException("Point must not be null");
        }

        this.price = point.getPrice();
        this.time = point.getTime();
    }

    public static class PointBuilder{

        private  BigDecimal price ;
        private  BigDecimal time;

        /**
         * Builder constructor
         * @param price point price
         */
        public PointBuilder(BigDecimal price){
            this.setPrice(price);
            this.time = DEFAULT_START_TIME;
        }

        /**
         * Set point time. Time is in intervals (1, 2, 3 ...)
         * @param time time interval
         * @return {@link PointBuilder} object
         * @throws NullPointerException when time is null
         * @throws IllegalArgumentException when time is less than default
         */
        public PointBuilder setTime(BigDecimal time){
            if (time == null){
                throw new NullPointerException("Time must not be null");
            }
            int result = time.compareTo(DEFAULT_START_TIME);
            if (result < 0){
                throw  new IllegalArgumentException("Time must be bigger or equal to: " + DEFAULT_START_TIME);
            }
            this.time = time;
            return this;
        }

        /**
         * Build method
         * @return {@link Point} object
         */
        public Point build(){
            return new Point(this);
        }

        /**
         * Set price of the point
         * @param price point price
         * @throws NullPointerException when price is null
         * @throws IllegalArgumentException  when price is not positive
         */
        private void setPrice(BigDecimal price){
            if (price == null){
                throw new NullPointerException("Price must not be null");
            }
            int result = price.compareTo(BigDecimal.ZERO);
            if (result <= 0 ){
                throw new IllegalArgumentException("Price must be bigger than Zero");
            }
            this.price = price;
        }

    }

    /**
     * Getter for the price
     * @return {@link BigDecimal} point price
     */
    public BigDecimal getPrice() {
        return this.price;
    }

    /**
     * Getter for time
     * @return {@link BigDecimal} point time interval
     */
    public BigDecimal getTime(){
        return this.time;
    }

    @Override
    public int hashCode() {
        int result = this.price.hashCode();
        result = 31*result + this.time.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this){
            return true;
        }
        if(!(obj instanceof Point)){
            return false;
        }
        Point newPoint = (Point) obj;

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
