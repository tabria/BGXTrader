package trader.order;

import com.oanda.v20.Context;
import com.oanda.v20.ExecuteException;
import com.oanda.v20.RequestException;
import com.oanda.v20.account.Account;
import com.oanda.v20.order.*;
import com.oanda.v20.order.Order;
import com.oanda.v20.primitives.DateTime;
import com.oanda.v20.transaction.StopLossDetails;
import com.oanda.v20.transaction.TransactionID;
import trader.config.Config;
import trader.entity.trade.Trade;
import trader.entity.trade.Direction;
import trader.entry.StandardEntryStrategy;

import java.math.BigDecimal;


public final class NewTradeService {

    private static final BigDecimal ONE_PIP = BigDecimal.valueOf(0.0001);
    private static final BigDecimal EUR_LEVERAGE = BigDecimal.valueOf(30);
    private static final BigDecimal PIP_MULTIPLIER = BigDecimal.valueOf(10_000);

    private Context context;
    private StandardEntryStrategy tradeGenerator;
    private OrderCreateResponse orderCreateResponse;


    public NewTradeService(Context context, StandardEntryStrategy tradeGenerator){
        this.setContext(context);
        this.setTradeGenerator(tradeGenerator);
        this.orderCreateResponse = null;
    }

    public void sendNewTradeOrder(Account account, BigDecimal bid){

        //check if orderList have pending trade (market if touch trade, not stop losses)
        //if don't have open trade or pending market orders, then generate new trade
        //if trade is not yet generated

//        if(account.getTrades().size() != 0 || hasWaitingTrades(account) || this.tradeGenerator.isGenerated() ){
//            return;
//        }

        Trade newTrade = this.tradeGenerator.generateTrade();

        if (!newTrade.getTradable()) {
            return;
        }

        BigDecimal unitsSize = calculateUnitsSize(account, newTrade, bid);
        BigDecimal availableMargin = account.getMarginAvailable().bigDecimalValue();
        BigDecimal futureMargin = this.calculateTradeMargin(account, unitsSize);
        if (availableMargin.compareTo(futureMargin)>0 && unitsSize.compareTo(BigDecimal.ZERO)!=0){

            //create order request
            OrderCreateRequest request = this.createOrderRequest(unitsSize, newTrade);
            try {
                this.orderCreateResponse = this.context.order.create(request);
                TransactionID id = this.orderCreateResponse.getOrderCreateTransaction().getId();
                DateTime time = this.orderCreateResponse.getOrderCreateTransaction().getTime();
                System.out.println("New TradeImpl has been added with id: " +id.toString() + " and time: " +time.toString() );
            } catch (RequestException | ExecuteException e){
                throw new RuntimeException(e);
            }
        }
    }

    private OrderCreateRequest createOrderRequest(BigDecimal unitsSize, Trade newTrade){

        //setting stop Loss for the new order
        StopLossDetails stopLossDetails = new StopLossDetails()
                .setPrice(newTrade.getStopLossPrice());

        MarketIfTouchedOrderRequest marketIfTouchedOrderRequest = new MarketIfTouchedOrderRequest()
                .setInstrument(Config.INSTRUMENT)
                .setUnits(unitsSize)
                .setStopLossOnFill(stopLossDetails)
                .setPrice(newTrade.getEntryPrice());

        return new OrderCreateRequest(Config.ACCOUNTID).setOrder(marketIfTouchedOrderRequest);
    }


    private void setTradeGenerator(StandardEntryStrategy tradeGenerator){
        if (tradeGenerator == null){
            throw  new NullPointerException("TradeImpl Generator must not be null");
        }
        this.tradeGenerator = tradeGenerator;
    }


    private boolean hasWaitingTrades(Account account) {
        for (Order order: account.getOrders()) {
            if (order.getType().equals(OrderType.MARKET_IF_TOUCHED)){
                return true;
            }
        }
        return false;
    }


    private BigDecimal calculateUnitsSize(Account account, Trade newTrade, BigDecimal bid) {
        //(balance * risk)/(stopSize*pipValue)
        BigDecimal balance = account.getBalance().bigDecimalValue();
        BigDecimal pipValue = this.pipValue(bid);

        BigDecimal stopSize = calculateStopSize(newTrade);
        BigDecimal divider = stopSize.multiply(pipValue).setScale(5, BigDecimal.ROUND_HALF_UP);

        //if direction is down the trade must be short, so the units must be negative number
        if(newTrade.getDirection().equals(Direction.DOWN) ){
            divider = divider.multiply(BigDecimal.valueOf(-1)).setScale(5, BigDecimal.ROUND_HALF_UP);
        }

        BigDecimal unitsSize = balance.multiply(Config.RISK_PER_TRADE).setScale(5, BigDecimal.ROUND_HALF_UP);
        if (divider.compareTo(BigDecimal.ZERO) != 0){
            return  unitsSize.divide(divider, 0, BigDecimal.ROUND_HALF_UP);
        }

        return BigDecimal.ZERO;
    }


    private BigDecimal pipValue(BigDecimal bid){

        return ONE_PIP.divide(bid, 7, BigDecimal.ROUND_HALF_UP);//.multiply(LOT_SIZE).setScale(2, BigDecimal.ROUND_HALF_UP);

    }


    private BigDecimal calculateTradeMargin(Account account, BigDecimal unitsSize){

        BigDecimal tradeMargin =  unitsSize.compareTo(BigDecimal.ZERO) !=0 ? unitsSize.divide(EUR_LEVERAGE, 5, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO;

        return account.getMarginUsed().bigDecimalValue().add(tradeMargin).setScale(5, BigDecimal.ROUND_HALF_UP);

    }


    private BigDecimal calculateStopSize(Trade newTrade){
        BigDecimal entryPrice = newTrade.getEntryPrice();
        BigDecimal stopPrice = newTrade.getStopLossPrice();

        BigDecimal stopSize = entryPrice.subtract(stopPrice).setScale(5, BigDecimal.ROUND_HALF_UP).abs();
        return stopSize.multiply(PIP_MULTIPLIER).setScale(5, BigDecimal.ROUND_HALF_UP);
    }


    private void setContext(Context context){
        if (context == null){
            throw new NullPointerException("Context is null");
        }
        this.context = context;
    }
}
