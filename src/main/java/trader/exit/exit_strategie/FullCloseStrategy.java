package trader.exit.exit_strategie;

import com.oanda.v20.Context;
import com.oanda.v20.account.Account;
import com.oanda.v20.order.OrderCreateResponse;
import com.oanda.v20.primitives.DateTime;
import com.oanda.v20.trade.TradeID;
import com.oanda.v20.trade.TradeSetDependentOrdersResponse;
import com.oanda.v20.trade.TradeSummary;
import com.oanda.v20.transaction.TransactionID;
import trader.indicator.updater.CandlesUpdater;
import trader.candlestick.candle.CandleGranularity;
import trader.exit.ExitStrategy;

import java.math.BigDecimal;

public final class FullCloseStrategy implements ExitStrategy {
    //original 25
    private static final BigDecimal TARGET_DISTANCE = BigDecimal.valueOf(0.0054);
    private static final BigDecimal PARTS_TO_CLOSE = BigDecimal.valueOf(1);
    private static final BigDecimal BREAK_EVEN_DISTANCE = BigDecimal.valueOf(0.0025);

    private final BaseExitStrategy baseExitStrategy;
    private OrderCreateResponse halfTradeResponse;
    private TradeSetDependentOrdersResponse tradeSetDependentOrdersResponse;

    /**
     * Constructor
     * @param context current context
     * @param candlestickGranularity time frame
     * @see Context
     */
    public FullCloseStrategy(Context context, CandleGranularity candlestickGranularity) {
        this.baseExitStrategy = new BaseExitStrategy(context, candlestickGranularity);
    }

    /**
     * If trade hits first target point close half, else trail after prev bar high
     * @param account current account
     * @param ask current ask price
     * @param bid current bid price
     * @param dateTime current dateTime
     * @see CandlesUpdater
     */
    @Override
    public void execute(Account account, BigDecimal ask, BigDecimal bid, DateTime dateTime) {

        TradeSummary trade = this.baseExitStrategy.getTrade(account);

        this.baseExitStrategy.updaterUpdateCandles(dateTime);

        this.moveToBreakEven(account, trade, ask, bid);

        this.closePosition(trade, ask, bid);

    }

    /**
     * Move stop loss to break even when price move X amount of pips in trade's direction
     * @param trade current trade
     * @param ask current ask price
     * @param bid current bid price
     */
    private void moveToBreakEven(Account account, TradeSummary trade, BigDecimal ask, BigDecimal bid){
        BigDecimal tradeOpenPrice = trade.getPrice().bigDecimalValue();
        BigDecimal stopLossPrice = this.baseExitStrategy.getStopLossOrderPriceByID(account, trade.getStopLossOrderID());
        BigDecimal currentUnits = trade.getCurrentUnits().bigDecimalValue();

        if (currentUnits.compareTo(BigDecimal.ZERO) > 0 && stopLossPrice.compareTo(tradeOpenPrice) >= 0){
            return;
        }

        if (currentUnits.compareTo(BigDecimal.ZERO) < 0 && stopLossPrice.compareTo(tradeOpenPrice) <= 0){
            return;
        }

        BigDecimal breakEvenPrice = this.calculateTargetPrice(currentUnits, tradeOpenPrice, BREAK_EVEN_DISTANCE);
        if (isFilterPassed(currentUnits, breakEvenPrice, ask, bid)){
            TradeID id = trade.getId();

            this.tradeSetDependentOrdersResponse = this.baseExitStrategy.changeStopLoss(id, tradeOpenPrice);
        }
    }

    /**
     * Close position if it is not already closed
     * @param trade current trade
     * @param ask current ask price
     * @param bid current bid price
     */
    private void closePosition(TradeSummary trade, BigDecimal ask, BigDecimal bid){

        BigDecimal initialUnits = trade.getInitialUnits().bigDecimalValue();
        BigDecimal currentUnits = trade.getCurrentUnits().bigDecimalValue();
        BigDecimal tradeOpenPrice = trade.getPrice().bigDecimalValue();

        //Check if first half is closed
        if (initialUnits.compareTo(currentUnits) != 0) {
            return;
        }

        BigDecimal targetPrice = this.calculateTargetPrice(currentUnits, tradeOpenPrice, TARGET_DISTANCE);

        if (isFilterPassed(currentUnits, targetPrice, ask, bid)){
            this.halfTradeResponse = this.baseExitStrategy.partialTradeClose(currentUnits, PARTS_TO_CLOSE);
        }
    }


    /**
     * Calculate Initial target price
     * @param currentUnits units size
     * @param tradeOpenPrice open price of the trade
     * @return {@link BigDecimal} value of the price
     */
    private BigDecimal calculateTargetPrice(BigDecimal currentUnits, BigDecimal tradeOpenPrice, BigDecimal distance){
        if (currentUnits.compareTo(BigDecimal.ZERO) < 0){

            return tradeOpenPrice.subtract(distance).setScale(5, BigDecimal.ROUND_HALF_UP);
        } else  {

            return tradeOpenPrice.add(distance).setScale(5, BigDecimal.ROUND_HALF_UP);
        }
    }

    /**
     * Check if trade can be close
     * @param currentUnits trade current amount of units
     * @param targetPrice price of the  target
     * @param ask ask price
     * @param bid bid price
     * @return {@link boolean} {@code true} if the half of the trade can be closed
     *                         {@code false} otherwise
     */
    private boolean isFilterPassed(BigDecimal currentUnits, BigDecimal targetPrice, BigDecimal ask, BigDecimal bid){
        //for short trade
        boolean shortCondition = currentUnits.compareTo(BigDecimal.ZERO) < 0 && targetPrice.compareTo(ask) >= 0;
        boolean longCondition = currentUnits.compareTo(BigDecimal.ZERO) > 0 && targetPrice.compareTo(bid) <= 0;

        return shortCondition || longCondition;
    }

    /**
     * Printing id of the closed transaction
     */
    private void printInformation(BigDecimal newStopLoss){
        TransactionID lastTransactionID = this.tradeSetDependentOrdersResponse.getLastTransactionID();
        System.out.println("Trade with id: " + lastTransactionID+" StopLoss moved to: " + newStopLoss);
    }
}
