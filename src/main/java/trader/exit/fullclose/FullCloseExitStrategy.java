package trader.exit.fullclose;

import trader.entity.price.Price;
import trader.entity.trade.BrokerTradeDetails;
import trader.exit.BaseExitStrategy;
import trader.exit.service.BreakEvenService;
import trader.exit.service.ClosePositionService;

import java.math.BigDecimal;

public final class FullCloseExitStrategy extends BaseExitStrategy {
    //original 25
    private static final BigDecimal TARGET_DISTANCE = BigDecimal.valueOf(0.0054);
    private static final int FIRST_TRADE = 0;
    private static final BigDecimal PARTS_TO_CLOSE = BigDecimal.valueOf(1);

    private BreakEvenService breakEvenService;
    private ClosePositionService closePositionService;

    public FullCloseExitStrategy() {
        super();
        breakEvenService = new BreakEvenService();
        closePositionService = new ClosePositionService();
    }

    public void execute(Price price) {
        updateCandlesService.updateCandles(brokerGateway, configuration);
        BrokerTradeDetails tradeDetails = brokerGateway.getTradeDetails(FIRST_TRADE);
        boolean isMovedToBreakEven = breakEvenService.moveToBreakEven(tradeDetails, price, brokerGateway);
        if(isMovedToBreakEven){

        }
        closePosition(price, tradeDetails);
    }

    @Override
    public String toString() {
        return "Exit strategy: FULL CLOSE";
    }

    private void closePosition(Price price, BrokerTradeDetails tradeDetails) {

        BigDecimal firstTargetPrice = getFirstTarget(tradeDetails);

        if(isAbleToSetStopLoss(tradeDetails.getCurrentUnits(), firstTargetPrice, price))
            closePositionService.closePosition(tradeDetails, brokerGateway, configuration, PARTS_TO_CLOSE);
    }

    private BigDecimal getFirstTarget(BrokerTradeDetails tradeDetails) {
        return isShortTrade(tradeDetails.getCurrentUnits()) ?
                subtract(tradeDetails.getOpenPrice(), TARGET_DISTANCE) :
                add(tradeDetails.getOpenPrice(), TARGET_DISTANCE);
    }

    private boolean isAbleToSetStopLoss(BigDecimal currentUnits, BigDecimal breakEvenPrice, Price price){
        boolean shortCondition =
                isShortTrade(currentUnits) && isAbove(breakEvenPrice, price.getAsk());
        boolean longCondition =
                !isShortTrade(currentUnits) && isBelow(breakEvenPrice, price.getBid());

        return shortCondition || longCondition;
    }
}
