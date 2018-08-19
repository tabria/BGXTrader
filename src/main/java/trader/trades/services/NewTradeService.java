package trader.trades.services;

import com.oanda.v20.Context;
import com.oanda.v20.ExecuteException;
import com.oanda.v20.RequestException;
import com.oanda.v20.account.Account;
import com.oanda.v20.order.*;
import com.oanda.v20.primitives.DateTime;
import com.oanda.v20.transaction.StopLossDetails;
import com.oanda.v20.transaction.TransactionID;
import trader.config.Config;
import trader.trades.entities.Trade;
import trader.trades.enums.Direction;
import trader.trades.generators.BGXTradeGenerator;

import java.math.BigDecimal;

public final class NewTradeService {

    private static final BigDecimal ONE_PIP = BigDecimal.valueOf(0.0001);
    private static final BigDecimal EUR_LEVERAGE = BigDecimal.valueOf(30);
    private static final BigDecimal PIP_MULTIPLIER = BigDecimal.valueOf(10_000);

    private Context context;
    private BGXTradeGenerator tradeGenerator;
    private OrderCreateResponse orderCreateResponse;


    /**
     * Constructor
     * @param tradeGenerator generator for new trades
     * @see BGXTradeGenerator
     */
    public NewTradeService(Context context, BGXTradeGenerator tradeGenerator){
        this.setContext(context);
        this.setTradeGenerator(tradeGenerator);
        this.orderCreateResponse = null;
    }

    /**
     * Sending of new trade order in OANDA platform
     * @param account current account
     * @param bid last bid price
     * @throws RuntimeException when oanda server do not accept order
     */
    public void sendNewTradeOrder(Account account, BigDecimal bid){

        //check if orderList have pending trades (market if touch trades, not stop losses)
        //if don't have open trades or pending market orders, then generate new trade
        //if trade is not yet generated
        if(account.getTrades().size() == 0 && !hasWaitingTrades(account)  && !this.tradeGenerator.isGenerated() ){

            Trade newTrade = this.tradeGenerator.generateTrade();

            if (newTrade.getTradable()){
                BigDecimal unitsSize = calculateUnitsSize(account, newTrade, bid);
                BigDecimal availableMargin = account.getMarginAvailable().bigDecimalValue();
                BigDecimal futureMargin = this.calculateTradeMargin(account, unitsSize);
                if (availableMargin.compareTo(futureMargin)>0 && unitsSize.compareTo(BigDecimal.ZERO)!=0){

                    //setting stop Loss for the new order
                    StopLossDetails stopLossDetails = new StopLossDetails()
                            .setPrice(newTrade.getStopLossPrice());

                    //check if trade direction is down(short) then units must be negative
                    if(newTrade.getDirection().equals(Direction.DOWN) ){
                        unitsSize = unitsSize.multiply(BigDecimal.valueOf(-1)).setScale(5, BigDecimal.ROUND_HALF_UP);
                    }

                    MarketIfTouchedOrderRequest marketIfTouchedOrderRequest = new MarketIfTouchedOrderRequest()
                            .setInstrument(Config.INSTRUMENT)
                            .setUnits(unitsSize)
                            .setStopLossOnFill(stopLossDetails)
                            .setPrice(newTrade.getEntryPrice());

                    //create order request
                    OrderCreateRequest request = new OrderCreateRequest(Config.ACCOUNTID)
                            .setOrder(marketIfTouchedOrderRequest);

                    try {
                        //get the response from the server
                        this.orderCreateResponse = context.order.create(request);


                        TransactionID id = this.orderCreateResponse.getOrderFillTransaction().getId();
                        DateTime time = this.orderCreateResponse.getOrderFillTransaction().getTime();
                        System.out.println("New Trade has been added with id: " +id.toString() + " and time: " +time.toString() );
                    } catch (RequestException | ExecuteException e){
                        throw new RuntimeException(e);

                    }
                }
            }
        }
    }

    /**
     * Set trade generator
     * @param tradeGenerator trade generator object
     * @throws NullPointerException when tradeGenerator is null
     * @see BGXTradeGenerator
     */
    private void setTradeGenerator(BGXTradeGenerator tradeGenerator){
        if (tradeGenerator == null){
            throw  new NullPointerException("Trade Generator must not be null");
        }
        this.tradeGenerator = tradeGenerator;
    }

    /**
     * Check if there are waiting trades to be opened
     * @param account current account
     * @return {@link boolean} {@code true} if there are waiting orders
     *                         {@code false} otherwise
     */
    private boolean hasWaitingTrades(Account account) {
        for (Order order: account.getOrders()) {
            if (order.getType().equals(OrderType.MARKET_IF_TOUCHED)){
                return true;
            }
        }
        return false;
    }

    /**
     * Calculate units size. Each trade must risk (RISK*100)% amount from the account. With Oanda min unit size is 1.
     * @param account current account
     * @param newTrade generated trade
     * @param bid last bid price
     * @return {@link BigDecimal} unit size. If units size are less then 1 will return 0
     */
    private BigDecimal calculateUnitsSize(Account account, Trade newTrade, BigDecimal bid) {
        //(balance * risk)/(stopSize*pipValue)
        BigDecimal balance = account.getBalance().bigDecimalValue();
        BigDecimal pipValue = this.pipValue(bid);

        BigDecimal stopSize = calculateStopSize(newTrade);
        BigDecimal divider = stopSize.multiply(pipValue).setScale(5, BigDecimal.ROUND_HALF_UP);

        BigDecimal unitsSize = balance.multiply(Config.RISK_PER_TRADE).setScale(5, BigDecimal.ROUND_HALF_UP);
        if (divider.compareTo(BigDecimal.ZERO) > 0){
            unitsSize =  unitsSize.divide(divider, 0, BigDecimal.ROUND_HALF_UP);
            return unitsSize.compareTo(BigDecimal.ZERO) < 1 ? BigDecimal.ZERO : unitsSize;
        }

        return BigDecimal.ZERO;
    }

    /**
     * Calculate Pip value
     * @param bid bid price
     * @return {@link BigDecimal} pip value
     */
    private BigDecimal pipValue(BigDecimal bid){

        return ONE_PIP.divide(bid, 7, BigDecimal.ROUND_HALF_UP);//.multiply(LOT_SIZE).setScale(2, BigDecimal.ROUND_HALF_UP);

    }

    /**
     * Calculate future margin used. Current margin used + future trade margin used
     * @param account active account
     * @param unitsSize trade's unit size
     * @return {@link BigDecimal} trade margin
     * @see Account
     */
    private BigDecimal calculateTradeMargin(Account account, BigDecimal unitsSize){

        BigDecimal tradeMargin =  unitsSize.compareTo(BigDecimal.ZERO) !=0 ? unitsSize.divide(EUR_LEVERAGE, 5, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO;

        return account.getMarginUsed().bigDecimalValue().add(tradeMargin).setScale(5, BigDecimal.ROUND_HALF_UP);

    }

    /**
     * Calculate stop size in pips.
     * @param newTrade current trade
     * @return {@link BigDecimal} stop size
     */
    private BigDecimal calculateStopSize(Trade newTrade){
        BigDecimal entryPrice = newTrade.getEntryPrice();
        BigDecimal stopPrice = newTrade.getStopLossPrice();

        BigDecimal stopSize = entryPrice.subtract(stopPrice).setScale(5, BigDecimal.ROUND_HALF_UP).abs();
        return stopSize.multiply(PIP_MULTIPLIER).setScale(5, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Setter for Context
     * @param context current context
     */
    private void setContext(Context context){
        if (context == null){
            throw new NullPointerException("Context is null");
        }
        this.context = context;
    }


}
