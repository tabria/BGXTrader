package trader.exit.exit_strategie;

import com.oanda.v20.Context;
import com.oanda.v20.account.Account;
import com.oanda.v20.primitives.DateTime;
import com.oanda.v20.trade.TradeSetDependentOrdersResponse;
import com.oanda.v20.trade.TradeSummary;
import com.oanda.v20.transaction.TransactionID;
import trader.candlestick.updater.CandlesUpdater;
import trader.candlestick.candle.CandleGranularity;
import trader.exit.ExitStrategy;

import java.math.BigDecimal;

public class TrailExitStrategy implements ExitStrategy {

        private final BaseExitStrategy baseExitStrategy;
        private TradeSetDependentOrdersResponse tradeSetDependentOrdersResponse;
        private BigDecimal exitBarHigh;
        private BigDecimal exitBarLow;

        /**
         * Constructor
         * @param context current context
         * @param candlestickGranularity time frame
         * @see Context
         */
        public TrailExitStrategy(Context context, CandleGranularity candlestickGranularity) {
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

            this.baseExitStrategy.updaterUpdateCandles(dateTime);

            //if 1st half is closed then trail the rest after prev bar extremes
            this.trailStopLoss(account, trade);
        }

        /**
         * Trail stop loss after previous bar extreme
         * @param account current account
         * @param trade current trade
         */
        private void trailStopLoss(Account account, TradeSummary trade){

            BigDecimal currentUnits = trade.getCurrentUnits().bigDecimalValue();

            BigDecimal lastFullCandleHigh = this.baseExitStrategy.getLastFullCandleHigh();
            BigDecimal lastFullCandleLow = this.baseExitStrategy.getLastFullCandleLow();
            BigDecimal lastFullCandleClose = this.baseExitStrategy.getLastFullCandleClose();

            //initial prev bar values
            this.initialSetExitBar(trade);

            boolean isReadyToTrailStopLoss = this.isReadyToTrailStopLoss(currentUnits, lastFullCandleClose, lastFullCandleHigh, lastFullCandleLow);


            this.exitBarLow = updateExitBarComponent(this.exitBarLow, lastFullCandleLow, currentUnits);
            this.exitBarHigh = updateExitBarComponent(this.exitBarHigh, lastFullCandleHigh, currentUnits);

            if (this.isReadyToSendTrailOrder(account, trade, currentUnits) && isReadyToTrailStopLoss){
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
         * Check if stop loss can be trailed.
         * For short trade stop will be trailed if candlestick close is lower than current low and candlestick high is also lower than current high.
         * For long trade stop will be trailed if candlestick close is higher than current high and candlestick low is also higher than current low
         * @param currentUnits current size of the trade
         * @param lastFullCandleClose last full candlestick close
         * @param lastFullCandleHigh last full candlestick high
         * @param lastFullCandleLow last full candlestick low
         * @return {@link boolean} {@code true} if stop can be trailed
         *                         {@code false} otherwise
         */
        private boolean isReadyToTrailStopLoss(BigDecimal currentUnits, BigDecimal lastFullCandleClose,
                                               BigDecimal lastFullCandleHigh, BigDecimal lastFullCandleLow){

            boolean shortCondition = currentUnits.compareTo(BigDecimal.ZERO) < 0 && lastFullCandleClose.compareTo(this.exitBarLow) < 0
                    && lastFullCandleHigh.compareTo(this.exitBarHigh) < 0;

            boolean longCondition = currentUnits.compareTo(BigDecimal.ZERO) > 0 && lastFullCandleClose.compareTo(this.exitBarHigh) > 0
                    && lastFullCandleLow.compareTo(this.exitBarLow) > 0;

            return shortCondition || longCondition;
        }

        /**
         * Update exit bar components - low or high
         * @param exitBarComponent exit bar component - exitBarHigh or exitBarLow
         * @param lastFullCandleLow last full candlestick low
         * @param currentUnits trade's units
         */
        private BigDecimal updateExitBarComponent(BigDecimal exitBarComponent, BigDecimal lastFullCandleLow, BigDecimal currentUnits){
            if(currentUnits.compareTo(BigDecimal.ZERO) < 0){
                exitBarComponent = lastFullCandleLow.compareTo(exitBarComponent) < 0 ? lastFullCandleLow : exitBarComponent;
            } else {
                exitBarComponent = lastFullCandleLow.compareTo(exitBarComponent) > 0 ? lastFullCandleLow : exitBarComponent;
            }
            return exitBarComponent;
        }

        /**
         * Check if new stopLoss order can be sent
         * @param account current account
         * @param trade current trade
         * @return {@link boolean} {@code true} if stopLoss can be moved
         *                         {@code false} otherwise
         */
        private boolean isReadyToSendTrailOrder(Account account, TradeSummary trade, BigDecimal currentUnits){

            BigDecimal stopLossPrice = this.baseExitStrategy.getStopLossOrderPriceByID(account, trade.getStopLossOrderID());

            boolean shortCondition = (currentUnits.compareTo(BigDecimal.ZERO) < 0) && ( stopLossPrice.compareTo(BigDecimal.ZERO) == 0 || stopLossPrice.compareTo(this.exitBarHigh) > 0);
            boolean longCondition = (currentUnits.compareTo(BigDecimal.ZERO) > 0) && ( stopLossPrice.compareTo(BigDecimal.ZERO) == 0 || stopLossPrice.compareTo(this.exitBarLow) < 0);

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
