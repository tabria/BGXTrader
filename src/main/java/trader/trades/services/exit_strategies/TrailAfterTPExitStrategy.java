package trader.trades.services.exit_strategies;


import com.oanda.v20.Context;
import com.oanda.v20.account.Account;
import com.oanda.v20.instrument.CandlestickGranularity;
import com.oanda.v20.primitives.DateTime;
import com.oanda.v20.trade.*;
import com.oanda.v20.transaction.TransactionID;
import trader.candles.CandlesUpdater;
import trader.config.Config;


import java.math.BigDecimal;

/**
 * This exit strategy will trail position stop after important extremes. When price hit first take profit, then stop will be set to a distance equal to half of the take profit size, and after that stop will be trailed.
 */

public final class TrailAfterTPExitStrategy implements ExitStrategy {

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
    public TrailAfterTPExitStrategy(Context context, CandlestickGranularity candlestickGranularity){
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

        //if utilities are null then set then to trade open price
        if (this.prevBarLow == null || this.prevBarHigh == null || this.lastSignificantLow == null || this.lastSignificantHigh == null){
            this.prevBarHigh = tradeOpenPrice;
            this.prevBarLow = tradeOpenPrice;
            this.lastSignificantHigh = tradeOpenPrice;
            this.lastSignificantLow = tradeOpenPrice;
        }

        BigDecimal currentUnits = trade.getCurrentUnits().bigDecimalValue();

        this.baseExitStrategy.updaterUpdateCandles(dateTime);

        BigDecimal lastFullCandleHigh = this.baseExitStrategy.getLastFullCandleHigh();
        BigDecimal lastFullCandleLow = this.baseExitStrategy.getLastFullCandleLow();

        BigDecimal currentStopLossPrice = this.baseExitStrategy.getStopLossOrderPriceByID(account, trade.getStopLossOrderID());

        int unitsSign = currentUnits.compareTo(BigDecimal.ZERO);

        BigDecimal firstTargetPrice = priceDistance(unitsSign, tradeOpenPrice,  FIRST_TARGET_DISTANCE);
        BigDecimal firstStopPrice = priceDistance(unitsSign, tradeOpenPrice, FIRST_STOP_DISTANCE);


        //if short trade
        if(unitsSign < 0){
            //saving last significant low
            if (bid.compareTo(this.lastSignificantLow) < 0){
                this.lastSignificantLow = bid;
                //check if firstStopPrice is hit
                if (this.lastSignificantLow.compareTo(firstStopPrice) <=0){
                    //trailing stop after significant high if new significant high is lower than current stop
                    if (currentStopLossPrice.compareTo(this.lastSignificantHigh) > 0 && this.lastSignificantHigh.compareTo(firstStopPrice) < 0 ){
                        BigDecimal newStopLossPrice = this.lastSignificantHigh.add(Config.SPREAD);
                        this.tradeSetDependentOrdersResponse = this.baseExitStrategy.changeStopLoss(id, newStopLossPrice);

                        TransactionID lastTransactionID = this.tradeSetDependentOrdersResponse.getLastTransactionID();
                        System.out.println("Trade with id: " + lastTransactionID+" StopLoss moved to: " + newStopLossPrice);
                    }
                    //set stop to firstStopPrice
                    else if(currentStopLossPrice.compareTo(this.lastSignificantHigh) > 0 && this.lastSignificantHigh.compareTo(firstStopPrice) >=0){
                        this.tradeSetDependentOrdersResponse = this.baseExitStrategy.changeStopLoss(id, firstStopPrice);

                        TransactionID lastTransactionID = this.tradeSetDependentOrdersResponse.getLastTransactionID();
                        System.out.println("Trade with id: " + lastTransactionID+" StopLoss moved to: " + firstStopPrice);
                    }
                }
            }
            if (lastFullCandleHigh.compareTo(this.prevBarHigh) > 0){
                this.lastSignificantHigh = lastFullCandleHigh;
            }
        }
        //if long trade
        else if(unitsSign > 0) {
            //saving last significant high
            if (ask.compareTo(this.lastSignificantHigh) > 0){
                this.lastSignificantHigh = ask;
                //check if firstStopPrice is hit
                if (this.lastSignificantHigh.compareTo(firstTargetPrice) >=0) {
                    //trailing stop after significant low if new significant low is higher than current stop
                    if (currentStopLossPrice.compareTo(this.lastSignificantLow) < 0 && this.lastSignificantLow.compareTo(firstStopPrice) > 0){
                        BigDecimal newStopLossPrice = this.lastSignificantLow.subtract(Config.SPREAD);
                        this.tradeSetDependentOrdersResponse = this.baseExitStrategy.changeStopLoss(id, newStopLossPrice);

                        TransactionID lastTransactionID = this.tradeSetDependentOrdersResponse.getLastTransactionID();
                        System.out.println("Trade with id: " + lastTransactionID+" StopLoss moved to: " + newStopLossPrice);
                    }
                    //set stop to firstStopPrice
                    else if(currentStopLossPrice.compareTo(this.lastSignificantLow) < 0 && this.lastSignificantLow.compareTo(firstStopPrice) <= 0){
                        this.tradeSetDependentOrdersResponse = this.baseExitStrategy.changeStopLoss(id, firstStopPrice);

                        TransactionID lastTransactionID = this.tradeSetDependentOrdersResponse.getLastTransactionID();
                        System.out.println("Trade with id: " + lastTransactionID+" StopLoss moved to: " + firstStopPrice);
                    }
                }
            }
            if (lastFullCandleLow.compareTo(this.prevBarLow) < 0){
                this.lastSignificantLow = lastFullCandleLow;
            }
        }

        this.prevBarHigh = lastFullCandleHigh;
        this.prevBarLow = lastFullCandleLow;
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
     * @param firstStopPrice stop price after hitting first target
     */
    private void setUtilities(BigDecimal firstStopPrice){
        this.prevBarHigh = firstStopPrice;
        this.prevBarLow = firstStopPrice;
        this.lastSignificantHigh = firstStopPrice;
        this.lastSignificantLow = firstStopPrice;
    }

}
