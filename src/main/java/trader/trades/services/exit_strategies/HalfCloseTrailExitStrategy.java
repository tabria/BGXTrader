package trader.trades.services.exit_strategies;

import com.oanda.v20.Context;
import com.oanda.v20.account.Account;
import com.oanda.v20.instrument.CandlestickGranularity;
import com.oanda.v20.order.*;
import com.oanda.v20.primitives.DateTime;
import com.oanda.v20.trade.*;
import com.oanda.v20.transaction.TransactionID;
import trader.candles.CandlesUpdater;


import java.math.BigDecimal;


/**
 * This exit strategy will close part of the position when price hit first target point. After that it will trail position stop behind previous bar high(for short) or low(for long)
 */
public final class HalfCloseTrailExitStrategy implements ExitStrategy {

    private static final BigDecimal FIRST_TARGET_DISTANCE = BigDecimal.valueOf(0.0032);
    private static final BigDecimal PARTS_TO_CLOSE = BigDecimal.valueOf(2);

    private final BaseExitStrategy baseExitStrategy;
    private OrderCreateResponse halfTradeResponse;
    private TradeSetDependentOrdersResponse tradeSetDependentOrdersResponse;
    private BigDecimal exitBarHigh;
    private BigDecimal exitBarLow;

    /**
     * Constructor
     * @param context current context
     * @param candlestickGranularity time frame
     * @see Context
     * @see CandlestickGranularity
     */
    public HalfCloseTrailExitStrategy(Context context, CandlestickGranularity candlestickGranularity) {
        this.baseExitStrategy = new BaseExitStrategy(context, candlestickGranularity);
        this.exitBarHigh = null;
        this.exitBarLow = null;
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
        TradeID id = trade.getId();
        BigDecimal initialUnits = trade.getInitialUnits().bigDecimalValue();
        BigDecimal currentUnits = trade.getCurrentUnits().bigDecimalValue();
        BigDecimal tradeOpenPrice = trade.getPrice().bigDecimalValue();

        this.baseExitStrategy.updaterUpdateCandles(dateTime);

        //close half position
        this.closeHalfPosition(trade, ask, bid);
        //if 1st half is closed then trail the rest after prev bar extremes
        this.trailStopLoss(account, trade);
    }

    /**
     * Close half of the position if it is not already closed
     * @param trade current trade
     * @param ask current ask price
     * @param bid current bid price
     */
    private void closeHalfPosition(TradeSummary trade, BigDecimal ask, BigDecimal bid){
        TradeID id = trade.getId();
        BigDecimal initialUnits = trade.getInitialUnits().bigDecimalValue();
        BigDecimal currentUnits = trade.getCurrentUnits().bigDecimalValue();
        BigDecimal tradeOpenPrice = trade.getPrice().bigDecimalValue();

        //Check if first half is closed
        if (initialUnits.compareTo(currentUnits) != 0) {
            return;
        }

        BigDecimal firstTargetPrice = this.calculateFirstTargetPrice(currentUnits, tradeOpenPrice);

        if (isReadyToCloseHalf(currentUnits, firstTargetPrice, ask, bid)){
            this.halfTradeResponse = this.baseExitStrategy.partialTradeClose(currentUnits, PARTS_TO_CLOSE);
        }
    }

    /**
     * Check if half ot the trade can be closed
     * @param currentUnits trade current amount of units
     * @param firstTargetPrice price of the first target
     * @param ask ask price
     * @param bid bid price
     * @return {@link boolean} {@code true} if the half of the trade can be closed
     *                         {@code false} otherwise
     */
    private boolean isReadyToCloseHalf(BigDecimal currentUnits, BigDecimal firstTargetPrice, BigDecimal ask, BigDecimal bid){
        //for short trade
        boolean shortCondition = currentUnits.compareTo(BigDecimal.ZERO) < 0 && firstTargetPrice.compareTo(bid) >= 0;
        boolean longCondition = currentUnits.compareTo(BigDecimal.ZERO) > 0 && firstTargetPrice.compareTo(ask) <= 0;

        return shortCondition || longCondition;
    }

    /**
     * Calculate Initial Target's price
     * @param currentUnits units size
     * @param tradeOpenPrice open price of the trade
     * @return {@link BigDecimal} value of the price
     */
    private BigDecimal calculateFirstTargetPrice(BigDecimal currentUnits, BigDecimal tradeOpenPrice){
        if (currentUnits.compareTo(BigDecimal.ZERO) < 0){

            return tradeOpenPrice.subtract(FIRST_TARGET_DISTANCE).setScale(5, BigDecimal.ROUND_HALF_UP);
        } else  {

           return tradeOpenPrice.add(FIRST_TARGET_DISTANCE).setScale(5, BigDecimal.ROUND_HALF_UP);
        }
    }

    /**
     * Trail stop loss after previous bar extreme
     * @param account current account
     * @param trade current trade
     */
    private void trailStopLoss(Account account, TradeSummary trade){

        BigDecimal currentUnits = trade.getCurrentUnits().bigDecimalValue();
        BigDecimal initialUnits = trade.getInitialUnits().bigDecimalValue();

        //Check if first half is closed
        if (initialUnits.compareTo(currentUnits) == 0) {
            return;
        }

        BigDecimal lastFullCandleHigh = this.baseExitStrategy.getLastFullCandleHigh();
        BigDecimal lastFullCandleLow = this.baseExitStrategy.getLastFullCandleLow();
        BigDecimal lastFullCandleClose = this.baseExitStrategy.getLastFullCandleClose();

        //initial prev bar values
        this.initialSetExitBar(trade);

        boolean readyToTrailStopLoss = this.isReadyToTrailStopLoss(currentUnits, lastFullCandleClose, lastFullCandleHigh, lastFullCandleLow);

        this.updateExitBarHigh(readyToTrailStopLoss, lastFullCandleHigh);
        this.updateExitBarLow(readyToTrailStopLoss, lastFullCandleLow);

        if (this.isReadyToSendTrailOrder(account, trade) && readyToTrailStopLoss){
            BigDecimal newStopLossPrice = this.setNewStopLoss(currentUnits);
            this.tradeSetDependentOrdersResponse = this.baseExitStrategy.changeStopLoss(trade.getId(), newStopLossPrice);

            this.printInformation(newStopLossPrice);
        }
    }

    /**
     * Set value to exit bar's extremes. If they are {@code null} then set values to trade open price
     * @param trade current trade
     */
    private void initialSetExitBar(TradeSummary trade){
        if(this.exitBarHigh == null || this.exitBarLow == null){
            BigDecimal tradeOpenPrice = trade.getPrice().bigDecimalValue();
            this.exitBarHigh = tradeOpenPrice;
            this.exitBarLow = tradeOpenPrice;
        }
    }

    /**
     * Check if stop loss can be trailed
     * @param currentUnits current size of the trade
     * @param lastFullCandleClose last full candle close
     * @param lastFullCandleHigh last full candle high
     * @param lastFullCandleLow last full candle low
     * @return {@link boolean} {@code true} if stop can be trailed
     *                         {@code false} otherwise
     */
    private boolean isReadyToTrailStopLoss(BigDecimal currentUnits, BigDecimal lastFullCandleClose,
                                           BigDecimal lastFullCandleHigh, BigDecimal lastFullCandleLow){

        boolean shortCondition = currentUnits.compareTo(BigDecimal.ZERO) < 0 && lastFullCandleClose.compareTo(this.exitBarLow) < 0
                && lastFullCandleHigh.compareTo(this.exitBarHigh) < 0;

        boolean longCondition = currentUnits.compareTo(BigDecimal.ZERO) > 0 &&lastFullCandleClose.compareTo(this.exitBarHigh) > 0
                && lastFullCandleLow.compareTo(this.exitBarLow) > 0;

        return shortCondition || longCondition;
    }

    /**
     * Update exit bar low
     * @param isReadyToTrailStopLoss will update bar if this is true
     * @param lastFullCandleLow last full candle low
     */
    private void updateExitBarLow(boolean isReadyToTrailStopLoss, BigDecimal lastFullCandleLow){
        this.exitBarLow = isReadyToTrailStopLoss ? lastFullCandleLow : this.exitBarLow;
    }

    /**
     * Update exit bat high
     * @param isReadyToTrailStopLoss will update if this is true
     * @param lastFullCandleHigh last full candle high
     */
    private void updateExitBarHigh(boolean isReadyToTrailStopLoss, BigDecimal lastFullCandleHigh){
        this.exitBarHigh = isReadyToTrailStopLoss ? lastFullCandleHigh : this.exitBarHigh;
    }

    /**
     * Check if new stopLoss order can be sent
     * @param account current account
     * @param trade current trade
     * @return {@link boolean} {@code true} if stopLoss can be moved
     *                         {@code false} otherwise
     */
    private boolean isReadyToSendTrailOrder(Account account, TradeSummary trade){

        BigDecimal stopLossPrice = this.baseExitStrategy.getStopLossOrderPriceByID(account, trade.getStopLossOrderID());

        boolean shortCondition = stopLossPrice.compareTo(BigDecimal.ZERO) == 0 || stopLossPrice.compareTo(this.exitBarHigh) > 0;
        boolean longCondition = stopLossPrice.compareTo(BigDecimal.ZERO) == 0 || stopLossPrice.compareTo(this.exitBarLow) < 0;

        return shortCondition || longCondition;
    }

    /**
     * Set value of the new stopLoss
     * @param unitsSize current unit size
     * @return {@link BigDecimal} price value of the new stop
     */
    private BigDecimal setNewStopLoss(BigDecimal unitsSize){
        if (unitsSize.compareTo(BigDecimal.ZERO) < 0){
            return this.exitBarHigh;
        }
        return this.exitBarLow;
    }









    /**
     * Printing id of the closed transaction
     */
    private void printInformation(BigDecimal newStopLoss){
        TransactionID lastTransactionID = this.tradeSetDependentOrdersResponse.getLastTransactionID();
        System.out.println("Trade with id: " + lastTransactionID+" StopLoss moved to: " + newStopLoss);
    }




}
