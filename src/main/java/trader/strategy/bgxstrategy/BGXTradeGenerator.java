package trader.strategy.bgxstrategy;


import trader.connector.BaseConnector;
import trader.indicator.Indicator;
import trader.indicator.ma.MovingAverageBuilder;
import trader.indicator.rsi.RSIBuilder;
import trader.trade.entitie.LineSegment;
import trader.trade.entitie.Point;
import trader.trade.entitie.Trade;
import trader.trade.enums.Direction;
import trader.trade.service.IntersectionService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import static trader.strategy.bgxstrategy.configuration.StrategyConfig.*;


/**
 * Class is supplying methods for generating trade based on BunnyGirl Cross System.
 * Signals are generating on MA crosses.
 *
 * There are extra filters preventing from entering bad trade.
 *
 */


public final class BGXTradeGenerator {

//    private static final BigDecimal OFFSET_FAST_WMA = BigDecimal.valueOf(0.0005);
//    private static final BigDecimal OFFSET_MIDDLE_WMA = BigDecimal.valueOf(0.0005);
//    private static final BigDecimal OFFSET_SLOW_WMA = BigDecimal.valueOf(0.0005);
//    private static final BigDecimal OFFSET_PRICE_SMA = BigDecimal.valueOf(0.0005);
    private static final BigDecimal RSI_FILTER = BigDecimal.valueOf(50);


    private final Indicator fastWMA;
    private final Indicator middleWMA;
    private final Indicator slowWMA;
    private final Indicator dailySMA;
    private final Indicator priceSma;
    private final Indicator rsi;
    private Direction direction;
    private Trade defaultTrade;


    public BGXTradeGenerator(Indicator fastWMA, Indicator middleWMA, Indicator slowWMA,
                             Indicator priceClose,Indicator dailySMA, Indicator rsi) {
        this.fastWMA = fastWMA;
        this.middleWMA = middleWMA;
        this.slowWMA = slowWMA;
        this.priceSma = priceClose;
        this.dailySMA = dailySMA;
        this.rsi = rsi;
        this.setDefaultTrade();
    }

    public BGXTradeGenerator(BaseConnector baseConnector) {
//        fastWMA = new MovingAverageBuilder(baseConnector).build(FAST_WMA_SETTINGS);
//        middleWMA = new MovingAverageBuilder(baseConnector).build(MIDDLE_WMA_SETTINGS);
//        priceSma = new MovingAverageBuilder(baseConnector).build( PRICE_SMA_SETTINGS);
//        dailySMA = new MovingAverageBuilder(baseConnector).build(DAILY_SMA_SETTINGS);
//        slowWMA = new MovingAverageBuilder(baseConnector).build(SLOW_WMA_SETTINGS);
 //       rsi = new RSIBuilder(baseConnector).build(RSI_SETTINGS);
        fastWMA = new MovingAverageBuilder().build(new HashMap<>());
        middleWMA = new MovingAverageBuilder().build(new HashMap<>());
        priceSma = new MovingAverageBuilder().build( new HashMap<>());
        dailySMA = new MovingAverageBuilder().build(new HashMap<>());
        slowWMA = new MovingAverageBuilder().build(new HashMap<>());
        rsi = new RSIBuilder().build(new HashMap<>());
        direction = Direction.FLAT;

    }



    /**
     * Check if trade is generated
     * @return {@link boolean} {@code true} if trade is already generated
     *                          {@code false} otherwise
     */
    public boolean isGenerated(){
        if (this.fastWMA.getValues().size() == 0 && this.middleWMA.getValues().size() == 0 &&
                this.slowWMA.getValues().size() == 0 && this.rsi.getValues().size() == 0){
            return false;
        }
//        return this.fastWMA.isTradeGenerated() && this.middleWMA.isTradeGenerated()
//                && this.slowWMA.isTradeGenerated() && this.rsi.isTradeGenerated();
        return true;
    }

    /**
     * Generate trade based on intersections and bounces
     * @return {@link Trade} object
     * @see Trade
     */
    public Trade generateTrade(){
//////setISTradeGenerated
        setIsTradeGenerated();
        //        this.fastWMA.setIsTradeGenerated(true);
//        this.middleWMA.setIsTradeGenerated(true);
//        this.slowWMA.setIsTradeGenerated(true);
//        this.dailySMA.setIsTradeGenerated(true);
//        this.priceSma.setIsTradeGenerated(true);
//        this.rsi.setIsTradeGenerated(true);
/////////////////////////////////////////////////////////////

        LineSegment fastWMALineSegment = getLineSegment(this.fastWMA);
        LineSegment middleWMALineSegment = getLineSegment(this.middleWMA);
        LineSegment slowWMALineSegment = getLineSegment(this.slowWMA);
        LineSegment priceSMALineSegment = getLineSegment(this.priceSma);


        //signal from intersection between fastWMA and middleWMA
//        boolean isLineSegmentIntersecting = IntersectionService.doLineSegmentsIntersect(fastWMALineSegment, middleWMALineSegment);
//        Direction direction = this.getIntersectionDirection(fastWMALineSegment);
//        if(isLineSegmentIntersecting && isRSITradable(direction)){
//            System.out.println("New WMA crossover");
//            return this.generateTradeAfterIntersection(fastWMALineSegment, middleWMALineSegment, direction);
//        }
//
////
////        //signal from intersection between priceSMA and middleWMA
////        boolean isLineSegmentIntersecting = IntersectionService.doLineSegmentsIntersect(priceSMALineSegment, middleWMALineSegment);
////         direction = this.getIntersectionDirection(priceSMALineSegment);
////        if(isLineSegmentIntersecting && isRSITradable(direction)){
////            System.out.println("New priceSMA and middleWMA crossover");
////            return this.generateTradeAfterIntersection(priceSMALineSegment, middleWMALineSegment);
////        }
////
////        //signal from intersection between priceSMA and slowWMA
////        isLineSegmentIntersecting = IntersectionService.doLineSegmentsIntersect(priceSMALineSegment, slowWMALineSegment);
////        direction = this.getIntersectionDirection(priceSMALineSegment);
////        if(isLineSegmentIntersecting && isRSITradable(direction)){
////            System.out.println("New priceSMA and slowWMA crossover");
////            return this.generateTradeAfterIntersection(priceSMALineSegment, middleWMALineSegment);
////        }


        if(isCrossover(fastWMALineSegment, middleWMALineSegment)){
             return this.generateTradeAfterIntersection(fastWMALineSegment, middleWMALineSegment);
        }
        if(isCrossover(priceSMALineSegment, middleWMALineSegment)){
            return this.generateTradeAfterIntersection(priceSMALineSegment, middleWMALineSegment);
        }
        if(isCrossover(priceSMALineSegment, slowWMALineSegment)){
            return this.generateTradeAfterIntersection(priceSMALineSegment, middleWMALineSegment);
        }


        return this.defaultTrade;

    }


    /**
     * Set default trade
     */
    private void setDefaultTrade() {
        this.defaultTrade = new Trade(new Point.PointBuilder(BigDecimal.ONE).build(), Direction.FLAT, BigDecimal.ONE);
    }

    /**
     * Set indicator tradeGenerated field to true
     */
    private void setIsTradeGenerated() {
//        this.fastWMA.setIsTradeGenerated(true);
//        this.middleWMA.setIsTradeGenerated(true);
//        this.slowWMA.setIsTradeGenerated(true);
//        this.dailySMA.setIsTradeGenerated(true);
//        this.priceSma.setIsTradeGenerated(true);
//        this.rsi.setIsTradeGenerated(true);
    }

    /**
     * Generate trade after intersection
     * @param fastWMALineSegment fast WMA (5 period)
     * @param middleWMALineSegment middle WMA (20 period)
     * @return {@link Trade} object
     */
    private Trade generateTradeAfterIntersection(LineSegment fastWMALineSegment, LineSegment middleWMALineSegment){

        Point intersectionPoint = IntersectionService.calculateIntersectionPoint(fastWMALineSegment, middleWMALineSegment);
        List<BigDecimal> dailyValues = this.dailySMA.getValues();

        if (direction.equals(Direction.UP) && isAbove(this.fastWMA, this.slowWMA) && isAbove(this.middleWMA, this.slowWMA)){

            System.out.println("New LONG Trade generated: " + intersectionPoint.getPrice());
            return new Trade(intersectionPoint, direction, dailyValues.get(dailyValues.size()-1));

        } else if (direction.equals(Direction.DOWN) && !isAbove(this.fastWMA, this.slowWMA) && !isAbove(this.middleWMA, this.slowWMA)){

            System.out.println("New SHORT Trade generated: " + intersectionPoint.getPrice());
            return new Trade(intersectionPoint, direction, dailyValues.get(dailyValues.size()-1));
        }
        return this.defaultTrade;
    }
//////////////////to be removed //////////////////////////////
    /**
     * Check if the value before last value rsi value is on or above 50
     * @return {@link boolean} {@code true} if it is on 50 or above
     *                         {@code false} otherwise
     */
    private boolean isRSITradable(Direction direction) {
        List<BigDecimal> rsiValues = this.rsi.getValues();
        BigDecimal checkValue = rsiValues.get(rsiValues.size() - 2);
        if (direction.equals(Direction.UP)){
            return checkValue.compareTo(RSI_FILTER) >= 0;
        }
        if (direction.equals(Direction.DOWN)){
            return checkValue.compareTo(RSI_FILTER) <= 0;
        }
        return false;
    }
////////////////////////////////////////////////////////////////

    private boolean isCrossover(LineSegment fastWMALineSegment, LineSegment middleWMALineSegment) {
        boolean isLineSegmentIntersecting = IntersectionService.doLineSegmentsIntersect(fastWMALineSegment, middleWMALineSegment);
        return isLineSegmentIntersecting && isRSITradable(fastWMALineSegment);
    }

    private boolean isRSITradable(LineSegment lineSegment) {
        direction = this.getIntersectionDirection(lineSegment);
        List<BigDecimal> rsiValues = this.rsi.getValues();
        BigDecimal checkValue = rsiValues.get(rsiValues.size() - 2);
        if (direction.equals(Direction.UP)){
            return checkValue.compareTo(RSI_FILTER) >= 0;
        }
        if (direction.equals(Direction.DOWN)){
            return checkValue.compareTo(RSI_FILTER) <= 0;
        }
        return false;
    }




    /**
     * Get the line segment from indicator.Line segment consist of the values from the last 2 finished candlesticks
     * @param indicator indicator
     * @return {@link LineSegment}
     * @see Indicator
     * @see LineSegment
     */
    private LineSegment getLineSegment(Indicator indicator){

        List<BigDecimal> indicatorValues = indicator.getValues();

        BigDecimal pointAPrice = indicatorValues.get(indicatorValues.size()-3);
        BigDecimal pointBPrice = indicatorValues.get(indicatorValues.size()-2);

        Point pointA = new Point.PointBuilder(pointAPrice).build();
        Point pointB = new Point.PointBuilder(pointBPrice).setTime(BigDecimal.valueOf(2)).build();

        return new LineSegment(pointA, pointB);
    }

    /**
     * Determine which indicator is above
     * @param indicatorA first indicator
     * @param indicatorB second indicator
     * @return {@link boolean} {@code true} if A above B
     *                          {@code false} otherwise
     */
    private boolean isAbove(Indicator indicatorA, Indicator indicatorB ){

        List<BigDecimal> indicatorAValues = indicatorA.getValues();
        BigDecimal pointAStartPrice = indicatorAValues.get(indicatorAValues.size()-3);
        BigDecimal pointAEndPrice = indicatorAValues.get(indicatorAValues.size()-2);

        List<BigDecimal> indicatorBValues = indicatorB.getValues();
        BigDecimal pointBStartPrice = indicatorBValues.get(indicatorBValues.size()-3);
        BigDecimal pointBEndPrice = indicatorBValues.get(indicatorBValues.size()-2);

        int compareStartPrices = pointAStartPrice.compareTo(pointBStartPrice);
        int compareEndPrices = pointAEndPrice.compareTo(pointBEndPrice);

        return compareStartPrices >= 0 && compareEndPrices >0;
    }

    /**
     * Getting direction of intersection
     *
     * @param segmentA first line segment
     * @return {@link Direction} object
     * @see LineSegment
     * @see Direction
     */
    private Direction getIntersectionDirection(LineSegment segmentA){
        direction = Direction.FLAT;
        BigDecimal segmentAStartPrice = segmentA.getPointA().getPrice();
        BigDecimal segmentAEndPrice = segmentA.getPointB().getPrice();

        BigDecimal delta = segmentAStartPrice.subtract(segmentAEndPrice).setScale(5, BigDecimal.ROUND_HALF_UP);
        if (delta.compareTo(BigDecimal.ZERO) < 0) {
            direction = Direction.UP;
        } else if (delta.compareTo(BigDecimal.ZERO) > 0){
            direction = Direction.DOWN;
        }
        return direction;
    }
}
