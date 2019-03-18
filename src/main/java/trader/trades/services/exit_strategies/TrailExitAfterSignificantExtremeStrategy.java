package trader.trades.services.exit_strategies;


import com.oanda.v20.Context;
import com.oanda.v20.account.Account;
import com.oanda.v20.primitives.DateTime;
import com.oanda.v20.trade.*;
import com.oanda.v20.transaction.TransactionID;
import trader.candle.CandlesUpdater;
import trader.config.Config;
import trader.candle.CandleGranularity;


import java.math.BigDecimal;

/**
 * This exit strategy will trail position stop after important extremes. When price hit first take profit, then stop will be set to a distance equal to half of the take profit size, and after that stop will be trailed.
 */

public final class TrailExitAfterSignificantExtremeStrategy implements ExitStrategy {

    private static final BigDecimal FIRST_TARGET_DISTANCE = BigDecimal.valueOf(0.0032);
    private static final BigDecimal FIRST_STOP_DISTANCE = FIRST_TARGET_DISTANCE.divide(BigDecimal.valueOf(2), 4, BigDecimal.ROUND_HALF_UP);

    private final BaseExitStrategy baseExitStrategy;
    private TradeSetDependentOrdersResponse tradeSetDependentOrdersResponse;
    private BigDecimal prevBarHigh;
    private BigDecimal prevBarLow;
    private BigDecimal lastSignificantHigh;
    private BigDecimal lastSignificantLow;

    /**
     * Constructor
     * @param context current context
     * @param candlestickGranularity current time frame
     * @see Context
     */
    public TrailExitAfterSignificantExtremeStrategy(Context context, CandleGranularity candlestickGranularity){
        this.baseExitStrategy = new BaseExitStrategy(context, candlestickGranularity);
        this.prevBarHigh = null;
        this.prevBarLow = null;
        this.lastSignificantHigh = null;
        this.lastSignificantLow = null;
    }


    /**
     * If trade hits first target point then trail
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
        BigDecimal tradeOpenPrice = trade.getPrice().bigDecimalValue();
        BigDecimal currentUnits = trade.getCurrentUnits().bigDecimalValue();
        int unitsSign = currentUnits.compareTo(BigDecimal.ZERO);

        this.baseExitStrategy.updaterUpdateCandles(dateTime);
        BigDecimal lastFullCandleHigh = this.baseExitStrategy.getLastFullCandleHigh();
        BigDecimal lastFullCandleLow = this.baseExitStrategy.getLastFullCandleLow();
        BigDecimal currentStopLossPrice = this.baseExitStrategy.getStopLossOrderPriceByID(account, trade.getStopLossOrderID());

        BigDecimal firstTargetPrice = priceDistance(unitsSign, tradeOpenPrice,  FIRST_TARGET_DISTANCE);
        BigDecimal firstStopPrice = priceDistance(unitsSign, tradeOpenPrice, FIRST_STOP_DISTANCE);

        //if utilities are null then set them to trade open price
        this.setUtilities(tradeOpenPrice, unitsSign, currentStopLossPrice, firstStopPrice);

        //if short trade and first target price is hit
        BigDecimal stopLossResultPrice = BigDecimal.ZERO;
        if(unitsSign < 0 && ( this.lastSignificantLow.compareTo(firstTargetPrice) <=0 || bid.compareTo(firstTargetPrice) <=0 )){

            this.setSignificantLow(lastFullCandleLow);
            stopLossResultPrice  =  modifyShortTradeStopLoss(id, currentStopLossPrice, ask, firstStopPrice);
            this.setSignificantHigh(lastFullCandleHigh);

        }
        //if long trade and first target price is hit
        else if(unitsSign > 0 && ( this.lastSignificantHigh.compareTo(firstTargetPrice) >=0 || ask.compareTo(firstTargetPrice) >=0 ) ) {

            this.setSignificantHigh(lastFullCandleHigh);
            stopLossResultPrice = modifyLongTradeStopLoss(id, currentStopLossPrice, ask, firstStopPrice);
            this.setSignificantLow(lastFullCandleLow);
        }

        //print new stopLoss price if changed
        if (stopLossResultPrice.compareTo(BigDecimal.ZERO) != 0){
            this.printTransaction(stopLossResultPrice);
        }

        this.prevBarHigh = lastFullCandleHigh;
        this.prevBarLow = lastFullCandleLow;
    }

    /**
     * Trailing stop loss for long trades.
     * @param id trade id
     * @param currentStopLossPrice current stop loss
     * @param bid current bid price
     * @param firstStopPrice first stop loss price, after hitting first target price
     * @return {@link BigDecimal} value of the new stop loss price for the trade. If no change will return zero
     */
    private BigDecimal modifyLongTradeStopLoss(TradeID id, BigDecimal currentStopLossPrice, BigDecimal bid, BigDecimal firstStopPrice) {

        BigDecimal newStopLossPrice = this.lastSignificantLow.subtract(Config.SPREAD);

        if (currentStopLossPrice.compareTo(this.lastSignificantLow) < 0 && bid.compareTo(this.lastSignificantHigh) >=0 &&
                currentStopLossPrice.compareTo(newStopLossPrice)!=0 && newStopLossPrice.compareTo(firstStopPrice) > 0){

            this.tradeSetDependentOrdersResponse = this.baseExitStrategy.changeStopLoss(id, newStopLossPrice);
            return newStopLossPrice;
        }
        //set stop to firstStopPrice
        else if(currentStopLossPrice.compareTo(this.lastSignificantLow) < 0 && this.lastSignificantLow.compareTo(firstStopPrice) <= 0
                && currentStopLossPrice.compareTo(firstStopPrice) !=0){

            this.tradeSetDependentOrdersResponse = this.baseExitStrategy.changeStopLoss(id, firstStopPrice);
            return  firstStopPrice;
        }

        return BigDecimal.ZERO;
    }

    /**
     * Trailing stop loss for long trades.
     * @param id trade id
     * @param currentStopLossPrice current stop loss
     * @param ask current ask price
     * @param firstStopPrice first stop loss price, after hitting first target price
     * @return {@link BigDecimal} value of the new stop loss price for the trade. If no change will return zero
     */
    private BigDecimal modifyShortTradeStopLoss(TradeID id, BigDecimal currentStopLossPrice, BigDecimal ask,  BigDecimal firstStopPrice) {

        BigDecimal newStopLossPrice = this.lastSignificantHigh.add(Config.SPREAD);
        if (currentStopLossPrice.compareTo(this.lastSignificantHigh) > 0 && ask.compareTo(this.lastSignificantLow) <= 0
                && currentStopLossPrice.compareTo(newStopLossPrice) !=0 && newStopLossPrice.compareTo(firstStopPrice) < 0){

            this.tradeSetDependentOrdersResponse = this.baseExitStrategy.changeStopLoss(id, newStopLossPrice);
            return  newStopLossPrice;
        }
        //set stop to firstStopPrice
        else if(currentStopLossPrice.compareTo(this.lastSignificantHigh) > 0 && this.lastSignificantHigh.compareTo(firstStopPrice) >=0
                && currentStopLossPrice.compareTo(firstStopPrice) != 0){

            this.tradeSetDependentOrdersResponse = this.baseExitStrategy.changeStopLoss(id, firstStopPrice);
            return firstStopPrice;
        }
        return BigDecimal.ZERO;
    }

    /**
     * Set significant Low
     * @param lastFullCandleLow last full candle low
     */
    private void setSignificantLow(BigDecimal lastFullCandleLow){

        this.lastSignificantLow = lastFullCandleLow.compareTo(this.prevBarLow) < 0 ? lastFullCandleLow : this.lastSignificantLow;
    }

    /**
     * Set significant high
     * @param lastFullCandleHigh last full candle high
     */
    private void setSignificantHigh(BigDecimal lastFullCandleHigh){

        this.lastSignificantHigh = lastFullCandleHigh.compareTo(this.prevBarHigh) > 0 ? lastFullCandleHigh : this.lastSignificantHigh;
    }

    /**
     * Calculate price form given start price and distance for positive and negative units
     * @param unitsSign units sign (minus for short, plus for long)
     * @param startPrice current start price
     * @param distance distance
     * @return {@link BigDecimal} calculated price
     */
    private BigDecimal priceDistance(int unitsSign, BigDecimal startPrice, BigDecimal distance) {
        BigDecimal price;
        if(unitsSign < 0){
            price = startPrice.subtract(distance).subtract(Config.SPREAD).setScale(5, BigDecimal.ROUND_HALF_UP);
        } else {
            price = startPrice.add(distance).add(Config.SPREAD).setScale(5, BigDecimal.ROUND_HALF_UP);
        }
        return price;
    }

    /**
     * Setting default values to utility variables. If the stop is not moved to the first stop price and beyond, then default values
     * @param tradeOpenPrice trade open price
     * @param unitsSign sign of the trade's units (negative for short, positive for long)
     * @param currentStopLossPrice trade's stop loss price
     * @param firstStopPrice When stop is moved for the first time, this will be the price.
     */
    private void setUtilities(BigDecimal tradeOpenPrice, int unitsSign, BigDecimal currentStopLossPrice, BigDecimal firstStopPrice){

        boolean resetUtilities = false;
        if(unitsSign < 0 && currentStopLossPrice.compareTo(firstStopPrice) > 0){
            resetUtilities = true;
        } else if (unitsSign > 0 && currentStopLossPrice.compareTo(firstStopPrice) < 0) {
            resetUtilities = true;
        }

        boolean nullConditions = this.prevBarLow == null || this.prevBarHigh == null || this.lastSignificantLow == null || this.lastSignificantHigh == null;
        if( nullConditions || resetUtilities ){
            this.prevBarHigh = tradeOpenPrice;
            this.prevBarLow = tradeOpenPrice;
            this.lastSignificantHigh = tradeOpenPrice;
            this.lastSignificantLow = tradeOpenPrice;
        }
    }

    /**
     * Print transaction if and stop price
     * @param stopPrice new stop price value
     */
    private void printTransaction(BigDecimal stopPrice){
        TransactionID lastTransactionID = this.tradeSetDependentOrdersResponse.getLastTransactionID();
        System.out.println("Trade with id: " + lastTransactionID+" StopLoss moved to: " + stopPrice);
    }

}
