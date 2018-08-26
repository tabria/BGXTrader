package trader.trades.entities;

import trader.trades.enums.Direction;

import java.math.BigDecimal;

/**
 * Class will create signal object. This object will be used as base for creating and placing Trades
 */

public final class Trade {

    private static final BigDecimal DEFAULT_SPREAD = BigDecimal.valueOf(0.0002);
    private static final BigDecimal DEFAULT_FILTER = BigDecimal.valueOf(0.0025);
    private static final BigDecimal DEFAULT_STOP_LOSS_FILTER = BigDecimal.valueOf(0.0005);
    private static final BigDecimal FIRST_TARGET = BigDecimal.valueOf(0.0050);
    
    private BigDecimal entryPrice;
    private BigDecimal stopLossPrice;
    private boolean tradable;
    private Direction direction;

    /**
     * Constructor for trade
     *
     * @param intersectionPoint point of intersect
     * @param direction trade direction
     * @param dailyOpenPrice daily open price
     */
    public Trade(Point intersectionPoint, Direction direction, BigDecimal dailyOpenPrice) {
        this.direction = direction;
        this.setEntryPrice(intersectionPoint, direction);
        this.setStopLossPrice(intersectionPoint, direction);
        this.setTradable(intersectionPoint, direction, dailyOpenPrice);

    }

    public Direction getDirection() {
        return this.direction;
    }
    /**
     * Getter for tradable flag
     * @return {@link boolean} if {@code false} then signal will be skipped
     */
    public boolean getTradable(){
        return this.tradable;
    }

    /**
     * Getter for trade entry price
     * @return {@link BigDecimal} entry price value
     */
    public BigDecimal getEntryPrice(){
        return this.entryPrice;
    }

    /**
     * Getter for stop loss
     * @return {@link BigDecimal} stop loss price value
     */
    public BigDecimal getStopLossPrice(){
        return this.stopLossPrice;
    }

    @Override
    public String toString() {
        return "Trade{" +
                "entryPrice=" + entryPrice .toString() +
                ", stopLossPrice=" + stopLossPrice.toString() +
                ", tradable=" + tradable +
                ", direction=" + direction.toString() +
                '}';
    }

    /**
     * Setter for tradability of the trade
     *
     * @param intersectionPoint point of intersect
     * @param direction trade direction
     * @param dailyOpenPrice daily open price
     * @throws NullPointerException when some of the arguments is null
     */
    private void setTradable(Point intersectionPoint, Direction direction, BigDecimal dailyOpenPrice) {

        if (intersectionPoint == null || direction == null || dailyOpenPrice == null){
            throw new NullPointerException("Arguments mut not be null");
        }

        BigDecimal intersectionPrice = intersectionPoint.getPrice();
        int compareIntersectionPriceDailyOpen = intersectionPrice.compareTo(dailyOpenPrice);
        int compareEntryPriceDailyOpen = this.entryPrice.compareTo(dailyOpenPrice);

        this.tradable = false;
        if (direction.equals(Direction.DOWN)){
            this.setTradableDownDirection(compareIntersectionPriceDailyOpen, compareEntryPriceDailyOpen, dailyOpenPrice);

        } else if(direction.equals(Direction.UP)) {
            this.setTradableUpDirection(compareIntersectionPriceDailyOpen, compareEntryPriceDailyOpen, dailyOpenPrice);
        }
    }

    /**
     * Setter for entry price
     *
     * @param intersectionPoint point of intersect
     * @param direction trade direction
     * @throws NullPointerException when some of the arguments is null
     */
    private void setEntryPrice(Point intersectionPoint, Direction direction) {
        if (intersectionPoint == null || direction == null){
            throw new NullPointerException("Arguments must not be null");
        }
        if (direction.equals(Direction.DOWN)){
            this.entryPrice = intersectionPoint.getPrice().subtract(DEFAULT_FILTER).setScale(5, BigDecimal.ROUND_HALF_UP);
        } else {
            this.entryPrice = intersectionPoint.getPrice().add(DEFAULT_FILTER).setScale(5, BigDecimal.ROUND_HALF_UP);
            this.entryPrice = this.entryPrice.add(DEFAULT_SPREAD).setScale(5, BigDecimal.ROUND_HALF_UP);
        }
    }

    /**
     * Setter for stop loss price
     * @param intersectionPoint point of intersect
     * @param direction trade direction
     * @throws NullPointerException when some of the arguments is null
     */
    private void setStopLossPrice(Point intersectionPoint, Direction direction) {
        if (intersectionPoint == null || direction == null){
            throw new NullPointerException("Arguments must not be null");
        }
        if (direction.equals(Direction.DOWN)){
            this.stopLossPrice = intersectionPoint.getPrice().add(DEFAULT_SPREAD).setScale(5, BigDecimal.ROUND_HALF_UP);
            this.stopLossPrice = this.stopLossPrice.add(DEFAULT_STOP_LOSS_FILTER).setScale(5, BigDecimal.ROUND_HALF_UP);
        } else {
            this.stopLossPrice = intersectionPoint.getPrice().subtract(DEFAULT_STOP_LOSS_FILTER).setScale(5, BigDecimal.ROUND_HALF_UP);
        }
    }

    /**
     * Setting tradable for down move
     * @param compareIntersectionPriceDailyOpen comparision result between intersection price and daily open
     * @param compareEntryPriceDailyOpen comparision result between entry price and daily open
     * @param dailyOpenPrice daily open price
     */
    private void setTradableDownDirection(int compareIntersectionPriceDailyOpen, int compareEntryPriceDailyOpen, BigDecimal dailyOpenPrice){
        if (compareIntersectionPriceDailyOpen > 0 && compareEntryPriceDailyOpen > 0){
            BigDecimal delta = this.entryPrice.subtract(dailyOpenPrice).setScale(5, BigDecimal.ROUND_HALF_UP);
            this.tradable = delta.compareTo(FIRST_TARGET) >= 0;
        }else if( (compareIntersectionPriceDailyOpen >= 0 && compareEntryPriceDailyOpen <= 0) ||
                (compareIntersectionPriceDailyOpen < 0 && compareEntryPriceDailyOpen < 0)) {
            this.tradable = true;
        }
    }

    /**
     * Setting tradable for up move
     * @param compareIntersectionPriceDailyOpen comparision result between intersection price and daily open
     * @param compareEntryPriceDailyOpen comparision result between entry price and daily open
     * @param dailyOpenPrice daily open price
     */
    private void setTradableUpDirection(int compareIntersectionPriceDailyOpen, int compareEntryPriceDailyOpen, BigDecimal dailyOpenPrice){
        if(compareIntersectionPriceDailyOpen < 0 && compareEntryPriceDailyOpen < 0) {
            BigDecimal delta = dailyOpenPrice.subtract(this.entryPrice).setScale(5, BigDecimal.ROUND_HALF_UP);
            this.tradable = delta.compareTo(FIRST_TARGET) >= 0;
        } else if( (compareIntersectionPriceDailyOpen <=0 && compareEntryPriceDailyOpen >= 0) ||
                (compareIntersectionPriceDailyOpen > 0 && compareEntryPriceDailyOpen > 0) ){
            this.tradable = true;
        }
    }
}
