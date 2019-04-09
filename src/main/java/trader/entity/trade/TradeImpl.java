package trader.entity.trade;

import trader.entity.point.Point;
import trader.exception.NegativeNumberException;

import java.math.BigDecimal;

/**
 * Class will create signal object. This object will be used as base for creating and placing Trades
 */

public final class TradeImpl implements Trade {

    private static final BigDecimal DEFAULT_SPREAD = BigDecimal.valueOf(0.0002);
    //unoptimized value 0.0025
    private static final BigDecimal DEFAULT_ENTRY_FILTER = BigDecimal.valueOf(0.0020);
    private static final BigDecimal DEFAULT_STOP_LOSS_FILTER = BigDecimal.valueOf(0.0005);
    private static final BigDecimal FIRST_TARGET = BigDecimal.valueOf(0.0050);

    private static final BigDecimal DEFAULT_PRICE = BigDecimal.valueOf(0.0001);

    private BigDecimal entryPrice;
    private BigDecimal stopLossPrice;
    private boolean tradable;
    private Direction direction;


    public TradeImpl() {
        direction = Direction.FLAT;
        tradable = false;
        entryPrice = DEFAULT_PRICE;
        stopLossPrice = DEFAULT_PRICE;
    }

    /**
     * Constructor for trade
     *
     * @param intersectionPoint point of intersect
     * @param direction trade direction
     * @param dailyOpenPrice daily open price
     */
    public TradeImpl(Point intersectionPoint, Direction direction, BigDecimal dailyOpenPrice) {
      //  this.direction = direction;
  //      this.setEntryPrice(intersectionPoint, direction);
   //     this.setStopLossPrice(intersectionPoint, direction);
    //    this.setTradable(intersectionPoint, direction, dailyOpenPrice);

    }

    @Override
    public Direction getDirection() {
        return this.direction;
    }

    @Override
    public void setDirection(String direction) {
        if (verifyInput(direction)) return;
        this.direction = Direction.valueOf(direction.toUpperCase());
    }

    @Override
    public boolean getTradable(){
        return this.tradable;
    }

    @Override
    public void setTradable(String tradable) {
        if (verifyInput(tradable)) return;
        this.tradable = Boolean.parseBoolean(tradable);
    }

    @Override
    public BigDecimal getEntryPrice(){
        return this.entryPrice;
    }

    @Override
    public void setEntryPrice(String entryPrice) {
        if (verifyInput(entryPrice)) return;
        checkForNegativeNumbers(entryPrice);
        this.entryPrice = new BigDecimal(entryPrice);
    }


    @Override
    public BigDecimal getStopLossPrice(){
        return this.stopLossPrice;
    }

    @Override
    public void setStopLossPrice(String stopLossPrice) {
        if (verifyInput(stopLossPrice)) return;
        checkForNegativeNumbers(stopLossPrice);
        this.stopLossPrice = new BigDecimal(stopLossPrice);
    }

    @Override
    public String toString() {
        return "TradeImpl{" +
                "entryPrice=" + entryPrice .toString() +
                ", stopLossPrice=" + stopLossPrice.toString() +
                ", tradable=" + tradable +
                ", direction=" + direction.toString() +
                '}';
    }

    private boolean verifyInput(String direction) {
        return direction == null || direction.trim().isEmpty();
    }

    private void checkForNegativeNumbers(String entryPrice) {
        BigDecimal price = new BigDecimal(entryPrice);
        if(price.compareTo(BigDecimal.ZERO)<=0)
            throw new NegativeNumberException();
    }

//    /**
//     * Setter for tradability of the trade
//     *
//     * @param intersectionPoint point of intersect
//     * @param direction trade direction
//     * @param dailyOpenPrice daily open price
//     * @throws NullPointerException when some of the arguments is null
//     */
//    private void setTradable(Point intersectionPoint, Direction direction, BigDecimal dailyOpenPrice) {
//
//        if (intersectionPoint == null || direction == null || dailyOpenPrice == null){
//            throw new NullPointerException("Arguments mut not be null");
//        }
//
//        BigDecimal intersectionPrice = intersectionPoint.getPrice();
//        int compareIntersectionPriceDailyOpen = intersectionPrice.compareTo(dailyOpenPrice);
//        int compareEntryPriceDailyOpen = this.entryPrice.compareTo(dailyOpenPrice);
//
//        this.tradable = false;
//        if (direction.equals(Direction.DOWN)){
//            this.setTradableDownDirection(compareIntersectionPriceDailyOpen, compareEntryPriceDailyOpen, dailyOpenPrice);
//
//        } else if(direction.equals(Direction.UP)) {
//            this.setTradableUpDirection(compareIntersectionPriceDailyOpen, compareEntryPriceDailyOpen, dailyOpenPrice);
//        }
//    }
//
//    /**
//     * Setter for entry price
//     *
//     * @param intersectionPoint point of intersect
//     * @param direction trade direction
//     * @throws NullPointerException when some of the arguments is null
//     */
//    private void setEntryPrice(Point intersectionPoint, Direction direction) {
//        if (intersectionPoint == null || direction == null){
//            throw new NullPointerException("Arguments must not be null");
//        }
//        if (direction.equals(Direction.DOWN)){
//            this.entryPrice = intersectionPoint.getPrice().subtract(DEFAULT_ENTRY_FILTER).setScale(5, BigDecimal.ROUND_HALF_UP);
//        } else {
//            this.entryPrice = intersectionPoint.getPrice().add(DEFAULT_ENTRY_FILTER).setScale(5, BigDecimal.ROUND_HALF_UP);
//            this.entryPrice = this.entryPrice.add(DEFAULT_SPREAD).setScale(5, BigDecimal.ROUND_HALF_UP);
//        }
//    }
//
//    /**
//     * Setter for stop loss price
//     * @param intersectionPoint point of intersect
//     * @param direction trade direction
//     * @throws NullPointerException when some of the arguments is null
//     */
//    private void setStopLossPrice(Point intersectionPoint, Direction direction) {
//        if (intersectionPoint == null || direction == null){
//            throw new NullPointerException("Arguments must not be null");
//        }
//        if (direction.equals(Direction.DOWN)){
//            this.stopLossPrice = intersectionPoint.getPrice().add(DEFAULT_SPREAD).setScale(5, BigDecimal.ROUND_HALF_UP);
//            this.stopLossPrice = this.stopLossPrice.add(DEFAULT_STOP_LOSS_FILTER).setScale(5, BigDecimal.ROUND_HALF_UP);
//        } else {
//            this.stopLossPrice = intersectionPoint.getPrice().subtract(DEFAULT_STOP_LOSS_FILTER).setScale(5, BigDecimal.ROUND_HALF_UP);
//        }
//    }
//
//    /**
//     * Setting tradable for down move
//     * @param compareIntersectionPriceDailyOpen comparision result between intersection price and daily open
//     * @param compareEntryPriceDailyOpen comparision result between entry price and daily open
//     * @param dailyOpenPrice daily open price
//     */
//    private void setTradableDownDirection(int compareIntersectionPriceDailyOpen, int compareEntryPriceDailyOpen, BigDecimal dailyOpenPrice){
//        if (compareIntersectionPriceDailyOpen > 0 && compareEntryPriceDailyOpen > 0){
//            BigDecimal delta = this.entryPrice.subtract(dailyOpenPrice).setScale(5, BigDecimal.ROUND_HALF_UP);
//            this.tradable = delta.compareTo(FIRST_TARGET) >= 0;
//        }else if( (compareIntersectionPriceDailyOpen >= 0 && compareEntryPriceDailyOpen <= 0) ||
//                (compareIntersectionPriceDailyOpen < 0 && compareEntryPriceDailyOpen < 0)) {
//            this.tradable = true;
//        }
//    }
//
//    /**
//     * Setting tradable for up move
//     * @param compareIntersectionPriceDailyOpen comparision result between intersection price and daily open
//     * @param compareEntryPriceDailyOpen comparision result between entry price and daily open
//     * @param dailyOpenPrice daily open price
//     */
//    private void setTradableUpDirection(int compareIntersectionPriceDailyOpen, int compareEntryPriceDailyOpen, BigDecimal dailyOpenPrice){
//        if(compareIntersectionPriceDailyOpen < 0 && compareEntryPriceDailyOpen < 0) {
//            BigDecimal delta = dailyOpenPrice.subtract(this.entryPrice).setScale(5, BigDecimal.ROUND_HALF_UP);
//            this.tradable = delta.compareTo(FIRST_TARGET) >= 0;
//        } else if( (compareIntersectionPriceDailyOpen <=0 && compareEntryPriceDailyOpen >= 0) ||
//                (compareIntersectionPriceDailyOpen > 0 && compareEntryPriceDailyOpen > 0) ){
//            this.tradable = true;
//        }
//    }
}
