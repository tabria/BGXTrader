package trader.exit.halfclosetrail;

import trader.entity.candlestick.Candlestick;
import trader.entity.trade.BrokerTradeDetails;
import trader.exit.BaseExitStrategy;
import trader.entity.price.Price;
import trader.exit.service.BreakEvenService;
import trader.exit.service.ClosePositionService;
import trader.exit.service.TrailStopLossService;

import java.math.BigDecimal;
import java.util.List;


/**
 * This exit strategy will close part of the position when price hit first target point. After that it will trail position's stop behind previous bar high(for short) or low(for long).
 * For short trade stop will be moved if last closed candlestick's close is below current low and last closed candlestick's high is also below current high.
 * For long trade stop will be moved if last closed candlestick's close is above current high and last closed candlestick's low is also above current low;
 */
public final class HalfCloseTrailExitStrategy extends BaseExitStrategy{

    private static final BigDecimal FIRST_TARGET_DISTANCE = BigDecimal.valueOf(0.0032);
    private static final BigDecimal PARTS_TO_CLOSE = BigDecimal.valueOf(2);
    private static final int FIRST_TRADE = 0;


    private BreakEvenService breakEvenService;
    private ClosePositionService closePositionService;
    private TrailStopLossService trailStopLossService;


    public HalfCloseTrailExitStrategy() {
        super();
        breakEvenService = new BreakEvenService();
        closePositionService = new ClosePositionService();
        trailStopLossService = new TrailStopLossService();
    }

    @Override
    public void execute(Price price) {
        updateCandlesService.updateCandles(brokerGateway, configuration);
        BrokerTradeDetails tradeDetails = brokerGateway.getTradeDetails(FIRST_TRADE);
        movePositionToBreakEven(price, tradeDetails);
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

            presenter.execute(trailStopLossService.toString());
        }
    }

    private void movePositionToBreakEven(Price price, BrokerTradeDetails tradeDetails) {
        boolean isMovedToBreakEven = breakEvenService.moveToBreakEven(tradeDetails, price, brokerGateway);
        if(isMovedToBreakEven){
            presenter.execute(breakEvenService.toString());
        }
    }

    private int isTradeSizeReduced(BrokerTradeDetails tradeDetails){
        BigDecimal currentUnits = tradeDetails.getCurrentUnits();
        BigDecimal initialUnits = tradeDetails.getInitialUnits();
        return initialUnits.compareTo(currentUnits);
    }

    private void closePositionFirstHalf(Price price, BrokerTradeDetails tradeDetails) {

        if (isTradeSizeReduced(tradeDetails) == 0){
            BigDecimal firstTargetPrice = getFirstTarget(tradeDetails);

            if(isAbleToSetStopLoss(tradeDetails.getCurrentUnits(), firstTargetPrice, price)) {
                closePositionService.closePosition(tradeDetails, brokerGateway, configuration, PARTS_TO_CLOSE);

                presenter.execute(PARTS_TO_CLOSE.toString(), closePositionService.toString(), firstTargetPrice.toString());
            }

        }
    }

    private BigDecimal getFirstTarget(BrokerTradeDetails tradeDetails) {
        return isShortTrade(tradeDetails.getCurrentUnits()) ?
                subtract(tradeDetails.getOpenPrice(), FIRST_TARGET_DISTANCE) :
                add(tradeDetails.getOpenPrice(), FIRST_TARGET_DISTANCE);
    }

    private boolean isAbleToSetStopLoss(BigDecimal currentUnits, BigDecimal breakEvenPrice, Price price){
        boolean shortCondition =
                isShortTrade(currentUnits) && isAbove(breakEvenPrice, price.getAsk());
        boolean longCondition =
                !isShortTrade(currentUnits) && isBelow(breakEvenPrice, price.getBid());

        return shortCondition || longCondition;
    }

}
