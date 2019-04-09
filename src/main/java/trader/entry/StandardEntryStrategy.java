package trader.entry;

import trader.controller.TraderController;
import trader.entity.indicator.Indicator;
import trader.entity.point.Point;
import trader.entity.segment.LineSegment;
import trader.entity.segment.LineSegmentImpl;
import trader.entity.trade.Trade;
import trader.entry.service.IntersectionService;
import trader.entry.service.TradeCalculationService;
import trader.exception.BadRequestException;
import trader.exception.NoSuchStrategyException;
import trader.entity.point.PointImpl;
import trader.entity.trade.Direction;
import trader.responder.Response;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

public final class StandardEntryStrategy {

    private static final BigDecimal RSI_FILTER = BigDecimal.valueOf(50);

    private static final int INDICATORS_COUNT = 6;
    private static final int START_OFFSET = 3;
    private static final int END_OFFSET = 2;


    private TraderController<Trade> createTradeController;
    private TradeCalculationService tradeCalculationService;
    private Indicator fastWMA;
    private Indicator middleWMA;
    private Indicator slowWMA;
    private Indicator dailySMA;
    private Indicator priceSMA;
    private Indicator rsi;
    private Direction direction;

    public StandardEntryStrategy(List<Indicator> indicators, TraderController<Trade> createTradeController) {
        validateInput(indicators, createTradeController);
        direction = Direction.FLAT;
        setIndicators(indicators);
        this.createTradeController = createTradeController;
        tradeCalculationService = new TradeCalculationService();

    }

    public Trade generateTrade(){
        LineSegment fastWMALineSegment = getLineSegment(this.fastWMA);
        LineSegment middleWMALineSegment = getLineSegment(this.middleWMA);
        LineSegment slowWMALineSegment = getLineSegment(this.slowWMA);
        LineSegment priceSMALineSegment = getLineSegment(this.priceSMA);

        if(isCrossover(fastWMALineSegment, middleWMALineSegment))
            return this.generateTradeAfterIntersection(fastWMALineSegment, middleWMALineSegment);
        if(isCrossover(priceSMALineSegment, middleWMALineSegment))
            return this.generateTradeAfterIntersection(priceSMALineSegment, middleWMALineSegment);
        if(isCrossover(priceSMALineSegment, slowWMALineSegment))
            return this.generateTradeAfterIntersection(priceSMALineSegment, middleWMALineSegment);

        return defaultTrade();
    }

    private void validateInput(List<Indicator> indicators, TraderController<Trade> createTradeController) {
        if(indicators == null || indicators.size() != INDICATORS_COUNT ||
                createTradeController == null)
            throw new NoSuchStrategyException();
    }

    private Trade defaultTrade() {
        Response<Trade> tradeResponse = createTradeController.execute(new HashMap<>());
        return tradeResponse.getResponseDataStructure();
    }

    private void setIndicators(List<Indicator> indicators) {
        for (Indicator indicator:indicators) {
            String position = indicator.getPosition();
            switch (position){
                case "rsi": rsi = indicator;
                break;
                case "price": priceSMA = indicator;
                break;
                case "slow": slowWMA = indicator;
                break;
                case "fast": fastWMA = indicator;
                break;
                case "daily": dailySMA = indicator;
                break;
                case "middle": middleWMA = indicator;
                break;
                default:
                    throw new BadRequestException();
            }
        }
    }

    private Trade generateTradeAfterIntersection(LineSegment fastSegment, LineSegment middleSegment){

        Point intersectionPoint = IntersectionService.calculateIntersectionPoint(fastSegment, middleSegment);
        List<BigDecimal> dailyValues = this.dailySMA.getValues();

        if (direction.equals(Direction.UP) && isAbove(this.fastWMA, this.slowWMA) && isAbove(this.middleWMA, this.slowWMA)){
            //          System.out.println("New Long TradeImpl generated: " + intersectionPoint.getPrice());
            return getTrade(intersectionPoint, dailyValues);

        } else if (direction.equals(Direction.DOWN) && !isAbove(this.fastWMA, this.slowWMA) && !isAbove(this.middleWMA, this.slowWMA)){
            //          System.out.println("New SHORT TradeImpl generated: " + intersectionPoint.getPrice());
            return getTrade(intersectionPoint, dailyValues);
        }
        return defaultTrade();
    }

    private Trade getTrade(Point intersectionPoint, List<BigDecimal> dailyValues) {
        HashMap<String, String> settings = getTradeSettings(intersectionPoint, dailyValues);
        Response<Trade> tradeResponse = createTradeController.execute(settings);
        return tradeResponse.getResponseDataStructure();
    }

    private HashMap<String, String> getTradeSettings(Point intersectionPoint, List<BigDecimal> dailyValues) {
        BigDecimal entryPrice = tradeCalculationService.calculateEntryPrice(intersectionPoint, direction);
        BigDecimal stopLossPrice = tradeCalculationService.calculateStopLossPrice(intersectionPoint, direction);
        boolean tradable = tradeCalculationService.setTradable(intersectionPoint, direction, dailyValues.get(dailyValues.size() - 1), entryPrice);
        HashMap<String, String> settings = new HashMap<>();
        settings.put("entryPrice", entryPrice.toString());
        settings.put("stopLossPrice", stopLossPrice.toString());
        settings.put("tradable", String.valueOf(tradable));
        return settings;
    }

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

    private LineSegment getLineSegment(Indicator indicator){
        List<BigDecimal> indicatorValues = indicator.getValues();
        BigDecimal pointAPrice = indicatorValues.get(indicatorValues.size()-START_OFFSET);
        BigDecimal pointBPrice = indicatorValues.get(indicatorValues.size()-END_OFFSET);
        Point pointA = new PointImpl.PointBuilder(pointAPrice).build();
        Point pointB = new PointImpl.PointBuilder(pointBPrice).setTime(BigDecimal.valueOf(2)).build();

        return new LineSegmentImpl(pointA, pointB);
    }

    private boolean isAbove(Indicator indicatorA, Indicator indicatorB ){

        int compareStartPrices = comparePrices(indicatorA.getValues(), indicatorB.getValues(), START_OFFSET);
        int compareEndPrices = comparePrices(indicatorA.getValues(), indicatorB.getValues(), END_OFFSET);

        return compareStartPrices >= 0 && compareEndPrices >0;
    }

    private int comparePrices(List<BigDecimal> indicatorAValues, List<BigDecimal> indicatorBValues, int offset){
        BigDecimal pointAStartPrice = indicatorAValues.get(indicatorAValues.size()- offset);
        BigDecimal pointBStartPrice = indicatorBValues.get(indicatorBValues.size()- offset);
        return pointAStartPrice.compareTo(pointBStartPrice);
    }

    private Direction getIntersectionDirection(LineSegment segmentA){
        direction = Direction.FLAT;
        BigDecimal segmentAStartPrice = segmentA.getPointA().getPrice();
        BigDecimal segmentAEndPrice = segmentA.getPointB().getPrice();
        BigDecimal delta = segmentAStartPrice.subtract(segmentAEndPrice)
                .setScale(5, BigDecimal.ROUND_HALF_UP);
        if (delta.compareTo(BigDecimal.ZERO) < 0) {
            direction = Direction.UP;
        } else if (delta.compareTo(BigDecimal.ZERO) > 0){
            direction = Direction.DOWN;
        }
        return direction;
    }
}
