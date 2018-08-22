package trader.trades.services.exit_strategies;


import com.oanda.v20.Context;
import com.oanda.v20.account.Account;
import com.oanda.v20.instrument.CandlestickGranularity;
import com.oanda.v20.order.OrderID;
import com.oanda.v20.primitives.DateTime;
import com.oanda.v20.trade.*;
import com.oanda.v20.transaction.TransactionID;
import trader.candles.CandlesUpdater;
import trader.config.Config;


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
     * @see CandlestickGranularity
     */
    public TrailExitAfterSignificantExtremeStrategy(Context context, CandlestickGranularity candlestickGranularity){
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

        //if utilities are null then set them to trade open price
        this.setUtilities(tradeOpenPrice);

        this.baseExitStrategy.updaterUpdateCandles(dateTime);
        BigDecimal lastFullCandleHigh = this.baseExitStrategy.getLastFullCandleHigh();
        BigDecimal lastFullCandleLow = this.baseExitStrategy.getLastFullCandleLow();
        BigDecimal currentStopLossPrice = this.baseExitStrategy.getStopLossOrderPriceByID(account, trade.getStopLossOrderID());

        BigDecimal firstTargetPrice = priceDistance(unitsSign, tradeOpenPrice,  FIRST_TARGET_DISTANCE);
        BigDecimal firstStopPrice = priceDistance(unitsSign, tradeOpenPrice, FIRST_STOP_DISTANCE);

        //if short trade and first target price is hit
        if(unitsSign < 0 && (this.lastSignificantLow.compareTo(firstTargetPrice) <=0 || bid.compareTo(firstTargetPrice) <=0)){

            //saving last significant low
            this.setSignificantLow(unitsSign, lastFullCandleLow, bid);
            //trailing stop after significant high if new significant high is lower than current stop
            if (currentStopLossPrice.compareTo(this.lastSignificantHigh) > 0 && this.lastSignificantHigh.compareTo(firstStopPrice) < 0 ){

                BigDecimal newStopLossPrice = this.lastSignificantHigh.add(Config.SPREAD);
                this.tradeSetDependentOrdersResponse = this.baseExitStrategy.changeStopLoss(id, newStopLossPrice);
                this.printTransaction(newStopLossPrice);
            }
            //set stop to firstStopPrice
            else if(currentStopLossPrice.compareTo(this.lastSignificantHigh) > 0 && this.lastSignificantHigh.compareTo(firstStopPrice) >=0){

                this.tradeSetDependentOrdersResponse = this.baseExitStrategy.changeStopLoss(id, firstStopPrice);
                this.printTransaction(firstStopPrice);
            }

            this.setSignificantHigh(unitsSign, lastFullCandleHigh, ask);

        }
        //if long trade and first target price is hit
        else if(unitsSign > 0 &&(this.lastSignificantHigh.compareTo(firstTargetPrice) >=0 || ask.compareTo(firstTargetPrice) >=0) ) {

            //saving last significant high
            this.setSignificantHigh(unitsSign, lastFullCandleHigh, ask);
            //trailing stop after significant low if new significant low is higher than current stop
            if (currentStopLossPrice.compareTo(this.lastSignificantLow) < 0 && this.lastSignificantLow.compareTo(firstStopPrice) > 0){

                BigDecimal newStopLossPrice = this.lastSignificantLow.subtract(Config.SPREAD);
                this.tradeSetDependentOrdersResponse = this.baseExitStrategy.changeStopLoss(id, newStopLossPrice);
                this.printTransaction(newStopLossPrice);
            }
            //set stop to firstStopPrice
            else if(currentStopLossPrice.compareTo(this.lastSignificantLow) < 0 && this.lastSignificantLow.compareTo(firstStopPrice) <= 0){

                this.tradeSetDependentOrdersResponse = this.baseExitStrategy.changeStopLoss(id, firstStopPrice);
                this.printTransaction(firstStopPrice);
            }

            this.setSignificantLow(unitsSign, lastFullCandleLow, bid);
        }

        this.prevBarHigh = lastFullCandleHigh;
        this.prevBarLow = lastFullCandleLow;
    }

    /**
     * Set significant Low
     * @param unitsSign units sign. Negative for short trade, positive for long
     * @param lastFullCandleLow last full candle low
     * @param bid current bid price
     */
    private void setSignificantLow(int unitsSign, BigDecimal lastFullCandleLow, BigDecimal bid){
        //set if long trade
        if (unitsSign > 0 && lastFullCandleLow.compareTo(this.prevBarLow) < 0){
            this.lastSignificantLow = lastFullCandleLow;
        }
        //set if short trade
        else if (unitsSign < 0 && (bid.compareTo(this.lastSignificantLow) < 0)){
            this.lastSignificantLow = bid;
        }
    }

    /**
     * Set significant high
     * @param unitsSign units sign. Negative for short trade, positive for long
     * @param lastFullCandleHigh last full candle high
     * @param ask current ask price
     */
    private void setSignificantHigh(int unitsSign, BigDecimal lastFullCandleHigh, BigDecimal ask){
        //set if long trade
        if (ask.compareTo(this.lastSignificantHigh) > 0) {
            this.lastSignificantHigh = ask;
        }
        //set if short trade
        else if (unitsSign < 0 && lastFullCandleHigh.compareTo(this.prevBarHigh) > 0){
            this.lastSignificantHigh = lastFullCandleHigh;
        }
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
     * Setting default values to utility variables
     * @param tradeOpenPrice trade open price
     */
    private void setUtilities(BigDecimal tradeOpenPrice){
        if (this.prevBarLow == null || this.prevBarHigh == null || this.lastSignificantLow == null || this.lastSignificantHigh == null){
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
