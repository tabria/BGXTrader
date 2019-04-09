package trader.interactor;

import trader.requestor.Request;
import trader.requestor.UseCase;
import trader.responder.Response;

import java.math.BigDecimal;
import java.util.HashMap;

public class CreateTradeUseCase implements UseCase {



    @Override
    public <T, E> Response<E> execute(Request<T> request, HashMap<String, Object> settings) {
        return null;
    }

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
