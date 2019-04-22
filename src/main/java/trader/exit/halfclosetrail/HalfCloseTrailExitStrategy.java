package trader.exit.halfclosetrail;

import trader.broker.BrokerGateway;
import trader.configuration.TradingStrategyConfiguration;
import trader.entity.candlestick.Candlestick;
import trader.entity.trade.BrokerTradeDetails;
import trader.exception.NullArgumentException;
import trader.exit.ExitStrategy;
import trader.entity.price.Price;
import trader.exit.service.BreakEvenService;
import trader.exit.service.ClosePositionService;
import trader.exit.service.TrailStopLossService;
import trader.exit.service.UpdateCandlesService;

import java.math.BigDecimal;
import java.util.List;


/**
 * This exit strategy will close part of the position when price hit first target point. After that it will trail position's stop behind previous bar high(for short) or low(for long).
 * For short trade stop will be moved if last closed candlestick's close is below current low and last closed candlestick's high is also below current high.
 * For long trade stop will be moved if last closed candlestick's close is above current high and last closed candlestick's low is also above current low;
 */
public final class HalfCloseTrailExitStrategy implements ExitStrategy {

    private static final BigDecimal FIRST_TARGET_DISTANCE = BigDecimal.valueOf(0.0032);
    private static final BigDecimal PARTS_TO_CLOSE = BigDecimal.valueOf(2);
    private static final int FIRST_TRADE = 0;


    private UpdateCandlesService updateCandlesService;
    private BreakEvenService breakEvenService;
    private ClosePositionService closePositionService;
    private TrailStopLossService trailStopLossService;
    private TradingStrategyConfiguration configuration;
    private BrokerGateway brokerGateway;


    public HalfCloseTrailExitStrategy() {
        updateCandlesService = new UpdateCandlesService();
        breakEvenService = new BreakEvenService();
        closePositionService = new ClosePositionService();
        trailStopLossService = new TrailStopLossService();
    }

    public void setConfiguration(TradingStrategyConfiguration configuration) {
        if(configuration == null)
            throw new NullArgumentException();
        this.configuration = configuration;
    }

    public void setBrokerGateway(BrokerGateway brokerGateway) {
        if(brokerGateway == null)
            throw new NullArgumentException();
        this.brokerGateway = brokerGateway;
    }

    @Override
    public void execute(Price price) {
        updateCandlesService.updateCandles(brokerGateway, configuration);
        BrokerTradeDetails tradeDetails = brokerGateway.getTradeDetails(FIRST_TRADE);
        breakEvenService.moveToBreakEven(tradeDetails, price, brokerGateway);
        closePositionFirstHalf(price, tradeDetails);
        trailPosition(tradeDetails);
    }

    @Override
    public String toString() {
        return "Exit strategy: HALF CLOSE, TRAIL";
    }

    private void trailPosition(BrokerTradeDetails tradeDetails) {
        if (isTradeSizeReduced(tradeDetails) != 0){
            List<Candlestick> candlesticks = updateCandlesService.getCandlesticks();
            Candlestick candlestick = candlesticks.get(candlesticks.size()-2);
            trailStopLossService.trailStopLoss(tradeDetails, candlestick, brokerGateway);
        }
    }

    private int isTradeSizeReduced(BrokerTradeDetails tradeDetails){
        BigDecimal currentUnits = tradeDetails.getCurrentUnits();
        BigDecimal initialUnits = tradeDetails.getInitialUnits();
        return initialUnits.compareTo(currentUnits);
    }

    private void closePositionFirstHalf(Price price, BrokerTradeDetails tradeDetails) {

        if (isTradeSizeReduced(tradeDetails) == 0){
            BigDecimal firstTargetPrice = getFirstTarget(tradeDetails, FIRST_TARGET_DISTANCE);

            if(isAbleToSetStopLoss(tradeDetails.getCurrentUnits(), firstTargetPrice, price))
                closePositionService.closePosition(tradeDetails, brokerGateway, configuration, PARTS_TO_CLOSE);
        }
    }

    private BigDecimal getFirstTarget(BrokerTradeDetails tradeDetails, BigDecimal firstTargetDistance) {
        return isShortTrade(tradeDetails.getCurrentUnits()) ?
                subtract(tradeDetails.getOpenPrice(), firstTargetDistance) :
                add(tradeDetails.getOpenPrice(), firstTargetDistance);
    }

    private boolean isAbleToSetStopLoss(BigDecimal currentUnits, BigDecimal breakEvenPrice, Price price){
        boolean shortCondition =
                isShortTrade(currentUnits) && isAbove(breakEvenPrice, price.getAsk());
        boolean longCondition =
                !isShortTrade(currentUnits) && isBelow(breakEvenPrice, price.getBid());

        return shortCondition || longCondition;
    }

    private boolean isAbove(BigDecimal priceA, BigDecimal priceB) {
        return priceA.compareTo(priceB) >= 0;
    }

    private boolean isBelow(BigDecimal priceA, BigDecimal priceB) {
        return priceA.compareTo(priceB) <= 0;
    }

    private boolean isShortTrade(BigDecimal currentUnits) {
        return currentUnits.compareTo(BigDecimal.ZERO) < 0;
    }

    private BigDecimal subtract(BigDecimal NumberA, BigDecimal NumberB) {
        return NumberA.subtract(NumberB)
                .setScale(5, BigDecimal.ROUND_HALF_UP);
    }

    private BigDecimal add(BigDecimal NumberA, BigDecimal NumberB) {
        return NumberA.add(NumberB)
                .setScale(5, BigDecimal.ROUND_HALF_UP);
    }
}
