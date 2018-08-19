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

        //Check if first half is closed
        if (initialUnits.compareTo(currentUnits) == 0) {

            //short trades
            if (currentUnits.compareTo(BigDecimal.ZERO) < 0){
                BigDecimal firstTargetPrice = tradeOpenPrice.subtract(FIRST_TARGET_DISTANCE).setScale(5, BigDecimal.ROUND_HALF_UP);
                if (firstTargetPrice.compareTo(bid) >= 0) {
                    this.halfTradeResponse = this.baseExitStrategy.partialTradeClose(currentUnits, PARTS_TO_CLOSE);
                    this.tradeSetDependentOrdersResponse = this.baseExitStrategy.changeStopLoss(id, tradeOpenPrice);

                    TransactionID lastTransactionID = this.tradeSetDependentOrdersResponse.getLastTransactionID();
                    System.out.println("Trade with id: "+ lastTransactionID +" closed half");
                }
            }
            //long trade
            else if (currentUnits.compareTo(BigDecimal.ZERO) > 0){
                BigDecimal firstTargetPrice = tradeOpenPrice.add(FIRST_TARGET_DISTANCE).setScale(5, BigDecimal.ROUND_HALF_UP);

                if (firstTargetPrice.compareTo(ask) <= 0) {
                    this.halfTradeResponse = this.baseExitStrategy.partialTradeClose(currentUnits, PARTS_TO_CLOSE);
                    this.tradeSetDependentOrdersResponse = this.baseExitStrategy.changeStopLoss(id, tradeOpenPrice);

                    TransactionID lastTransactionID = this.tradeSetDependentOrdersResponse.getLastTransactionID();
                    System.out.println("Trade with id: "+ lastTransactionID +" closed half");
                }
            }
        }
        //if 1st half is closed then trail the rest after prev bar extremes
        else {

            this.baseExitStrategy.updaterUpdateCandles(dateTime);

            BigDecimal lastFullCandleHigh = this.baseExitStrategy.getLastFullCandleHigh();
            BigDecimal lastFullCandleLow = this.baseExitStrategy.getLastFullCandleLow();
            BigDecimal lastFullCandleClose = this.baseExitStrategy.getLastFullCandleClose();

            BigDecimal stopLossPrice = this.baseExitStrategy.getStopLossOrderPriceByID(account, trade.getStopLossOrderID());

            //prev bar values
            if(this.exitBarHigh == null || this.exitBarLow == null){
                this.exitBarHigh = tradeOpenPrice;
                this.exitBarLow = tradeOpenPrice;
            }

            //check for short trade
            if(currentUnits.compareTo(BigDecimal.ZERO) < 0 && lastFullCandleClose.compareTo(this.exitBarLow) < 0
                    && lastFullCandleHigh.compareTo(this.exitBarHigh) < 0) {

                this.exitBarLow = lastFullCandleLow;
                this.exitBarHigh = lastFullCandleHigh;

                if (stopLossPrice.compareTo(BigDecimal.ZERO) == 0 || stopLossPrice.compareTo(this.exitBarHigh) > 0){
                    this.tradeSetDependentOrdersResponse = this.baseExitStrategy.changeStopLoss(id, this.exitBarHigh);

                    TransactionID lastTransactionID = this.tradeSetDependentOrdersResponse.getLastTransactionID();
                    System.out.println("Trade with id: " + lastTransactionID+" StopLoss moved to: " + this.exitBarHigh);
                }
            }
            //check for long trade
            else if(currentUnits.compareTo(BigDecimal.ZERO) > 0 &&lastFullCandleClose.compareTo(this.exitBarHigh) > 0
                    && lastFullCandleLow.compareTo(this.exitBarLow) > 0) {
                this.exitBarLow = lastFullCandleLow;
                this.exitBarHigh = lastFullCandleHigh;


                if (stopLossPrice.compareTo(BigDecimal.ZERO) == 0 || stopLossPrice.compareTo(this.exitBarLow) < 0){
                    this.tradeSetDependentOrdersResponse = this.baseExitStrategy.changeStopLoss(id, this.exitBarLow);

                    TransactionID lastTransactionID = this.tradeSetDependentOrdersResponse.getLastTransactionID();
                    System.out.println("Trade with id: " + lastTransactionID+" StopLoss moved to: " + this.exitBarLow);
                }
            }
        }
    }

}
