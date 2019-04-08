package trader.entry;

import trader.entity.indicator.Indicator;
import trader.entity.segment.LineSegment;
import trader.entity.segment.LineSegmentImpl;
import trader.entity.trade.TradeImpl;
import trader.entry.service.IntersectionService;
import trader.exception.BadRequestException;
import trader.exception.NoSuchStrategyException;
import trader.entity.point.PointImpl;
import trader.entity.trade.Direction;
import java.math.BigDecimal;
import java.util.List;

public final class StandardEntryStrategy {

    private static final BigDecimal RSI_FILTER = BigDecimal.valueOf(50);

    private static final int INDICATORS_COUNT = 6;
    private static final int START_OFFSET = 3;
    private static final int END_OFFSET = 2;

    private Indicator fastWMA;
    private Indicator middleWMA;
    private Indicator slowWMA;
    private Indicator dailySMA;
    private Indicator priceSMA;
    private Indicator rsi;
    private Direction direction;

    public StandardEntryStrategy(List<Indicator> indicators) {
        validateInput(indicators);
        direction = Direction.FLAT;
        setIndicators(indicators);
    }

    public TradeImpl generateTrade(){
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

        return null;
    }

    private void validateInput(List<Indicator> indicators) {
        if(indicators == null || indicators.size() != INDICATORS_COUNT)
            throw new NoSuchStrategyException();
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
//////////////////////////////////////

    public StandardEntryStrategy(Indicator fastWMA, Indicator middleWMA, Indicator slowWMA,
                                 Indicator priceClose, Indicator dailySMA, Indicator rsi) {
        this.fastWMA = fastWMA;
        this.middleWMA = middleWMA;
        this.slowWMA = slowWMA;
        this.priceSMA = priceClose;
        this.dailySMA = dailySMA;
        this.rsi = rsi;
    }
////////////////////////////////////////////////////////////////////////


    private TradeImpl generateTradeAfterIntersection(LineSegment fastSegment, LineSegment middleSegment){

        PointImpl intersectionPoint = IntersectionService.calculateIntersectionPoint(fastSegment, middleSegment);
        List<BigDecimal> dailyValues = this.dailySMA.getValues();

        if (direction.equals(Direction.UP) && isAbove(this.fastWMA, this.slowWMA) && isAbove(this.middleWMA, this.slowWMA)){

            System.out.println("New LONG TradeImpl generated: " + intersectionPoint.getPrice());
            return new TradeImpl(intersectionPoint, direction, dailyValues.get(dailyValues.size()-1));

        } else if (direction.equals(Direction.DOWN) && !isAbove(this.fastWMA, this.slowWMA) && !isAbove(this.middleWMA, this.slowWMA)){

            System.out.println("New SHORT TradeImpl generated: " + intersectionPoint.getPrice());
            return new TradeImpl(intersectionPoint, direction, dailyValues.get(dailyValues.size()-1));
        }
        return null;
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
        PointImpl pointA = new PointImpl.PointBuilder(pointAPrice).build();
        PointImpl pointB = new PointImpl.PointBuilder(pointBPrice).setTime(BigDecimal.valueOf(2)).build();

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
