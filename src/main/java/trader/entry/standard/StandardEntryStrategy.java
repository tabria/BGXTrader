package trader.entry.standard;

import trader.configuration.TradingStrategyConfiguration;
import trader.controller.TraderController;
import trader.entity.indicator.Indicator;
import trader.entity.trade.point.Point;
import trader.entity.trade.segment.LineSegment;
import trader.entity.trade.segment.LineSegmentImpl;
import trader.entity.trade.Trade;
import trader.entry.EntryStrategy;
import trader.entry.standard.service.IntersectionService;
import trader.entry.standard.service.TradeCalculationService;
import trader.exception.BadRequestException;
import trader.exception.NoSuchStrategyException;
import trader.entity.trade.point.PointImpl;
import trader.entity.trade.Direction;
import trader.exception.NullArgumentException;
import trader.responder.Response;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class StandardEntryStrategy implements EntryStrategy {

    private static final int INDICATORS_COUNT = 6;
    private static final int START_OFFSET = 3;
    private static final int END_OFFSET = 2;


    private TraderController<Trade> createTradeController;
    private TradeCalculationService tradeCalculationService;
    private TradingStrategyConfiguration configuration;

    private Indicator fastWMA;
    private Indicator middleWMA;
    private Indicator slowWMA;
    private Indicator dailySMA;
    private Indicator priceSMA;
    private Indicator rsi;
    private Direction direction;

    public StandardEntryStrategy() {
        direction = Direction.FLAT;
        tradeCalculationService = new TradeCalculationService();
    }

    public void setConfiguration(TradingStrategyConfiguration configuration) {
        if(configuration == null)
            throw new NullArgumentException();
        this.configuration = configuration;
        tradeCalculationService.setConfiguration(configuration);
    }

    @Override
    public void setCreateTradeController(TraderController<Trade> createTradeController) {
        if(createTradeController == null)
            throw new NullArgumentException();
        this.createTradeController = createTradeController;
    }

    @Override
    public Trade generateTrade(){
        validateConfigurationExistence();
        validateIndicatorExistence();
        validateCreateTradeControllerExistence();
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

    @Override
    public void setIndicators(List<Indicator> indicators) {
        validateInput(indicators);
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

    private void validateIndicatorExistence() {
        if(rsi == null || priceSMA == null || slowWMA == null ||
                fastWMA == null || dailySMA == null || middleWMA == null)
            throw new NullArgumentException();
    }

    private void validateConfigurationExistence() {
        if(configuration == null)
            throw new NullArgumentException();
    }

    private void validateCreateTradeControllerExistence() {
        if(createTradeController == null)
            throw new NullArgumentException();
    }

    private void validateInput(List<Indicator> indicators) {
        if(indicators == null || indicators.size() != INDICATORS_COUNT)
            throw new NoSuchStrategyException();
    }

    private Trade defaultTrade() {
        Response<Trade> tradeResponse = createTradeController.execute(new HashMap<>());
        return tradeResponse.getBody();
    }

    private Trade generateTradeAfterIntersection(LineSegment fastSegment, LineSegment middleSegment){

        Point intersectionPoint = IntersectionService.calculateIntersectionPoint(fastSegment, middleSegment);
        List<BigDecimal> dailyValues = this.dailySMA.getValues();

        if (isLongCross())
            //          System.out.println("New Long TradeImpl generated: " + intersectionPoint.getPrice());
            return getTrade(intersectionPoint, dailyValues);
        else if (isShortCross())
            //          System.out.println("New SHORT TradeImpl generated: " + intersectionPoint.getPrice());
            return getTrade(intersectionPoint, dailyValues);
        return defaultTrade();
    }

    private boolean isShortCross() {
        return direction.equals(Direction.DOWN) && !isAbove(this.fastWMA, this.slowWMA) && !isAbove(this.middleWMA, this.slowWMA);
    }

    private boolean isLongCross() {
        return direction.equals(Direction.UP) && isAbove(this.fastWMA, this.slowWMA) && isAbove(this.middleWMA, this.slowWMA);
    }

    private Trade getTrade(Point intersectionPoint, List<BigDecimal> dailyValues) {
        Map<String, Object> inputSettings = new HashMap<>();
        inputSettings.put("settings", getTradeSettings(intersectionPoint, dailyValues));
        Response<Trade> tradeResponse = createTradeController.execute(inputSettings);
        return tradeResponse.getBody();
    }

    private Map<String, String> getTradeSettings(Point intersectionPoint, List<BigDecimal> dailyValues) {
        BigDecimal entryPrice = tradeCalculationService.calculateEntryPrice(intersectionPoint, direction);
        BigDecimal stopLossPrice = tradeCalculationService.calculateStopLossPrice(intersectionPoint, direction);
        boolean tradable = tradeCalculationService.setTradable(intersectionPoint, direction, dailyValues.get(dailyValues.size() - 1), entryPrice);
        Map<String, String> settings = new HashMap<>();
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
            return checkValue.compareTo(configuration.getRsiFilter()) >= 0;
        }
        if (direction.equals(Direction.DOWN)){
            return checkValue.compareTo(configuration.getRsiFilter()) <= 0;
        }
        return false;
    }

    private LineSegment getLineSegment(Indicator indicator){
        List<BigDecimal> indicatorValues = indicator.getValues();
        BigDecimal pointAPrice = indicatorValues.get(indicatorValues.size()-START_OFFSET);
        BigDecimal pointBPrice = indicatorValues.get(indicatorValues.size()-END_OFFSET);
        Point pointA = new PointImpl(pointAPrice);
        Point pointB = new PointImpl(pointBPrice, BigDecimal.valueOf(2));

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
