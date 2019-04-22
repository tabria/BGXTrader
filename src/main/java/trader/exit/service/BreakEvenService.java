package trader.exit.service;

import trader.broker.BrokerGateway;
import trader.entity.price.Price;
import trader.entity.trade.BrokerTradeDetails;

import java.math.BigDecimal;

public class BreakEvenService {

    private static final BigDecimal BREAK_EVEN_DISTANCE = BigDecimal.valueOf(0.0025);


    public boolean moveToBreakEven(BrokerTradeDetails tradeDetails, Price price, BrokerGateway brokerGateway) {
        BigDecimal stopLossPrice = tradeDetails.getStopLossPrice();
        BigDecimal tradeOpenPrice = tradeDetails.getOpenPrice();
        BigDecimal currentUnits = tradeDetails.getCurrentUnits();

        if (!isShortTrade(currentUnits) && isAbove(stopLossPrice, tradeOpenPrice))
            return false;
        if (isShortTrade(currentUnits) && isBelow(stopLossPrice, tradeOpenPrice))
            return false;

        if(isAbleToSetStopLoss(currentUnits, getBreakEvenPrice(tradeDetails), price)) {
            brokerGateway.setTradeStopLossPrice(tradeDetails.getTradeID(), tradeOpenPrice.toString());
            return true;
        }
        return  false;
    }

    @Override
    public String toString() {
        return "position to break even";
    }

    private BigDecimal getBreakEvenPrice(BrokerTradeDetails tradeDetails) {
        return isShortTrade(tradeDetails.getCurrentUnits()) ?
                subtract(tradeDetails.getOpenPrice(), BREAK_EVEN_DISTANCE) :
                add(tradeDetails.getOpenPrice(), BREAK_EVEN_DISTANCE);
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
