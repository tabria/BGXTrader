package trader.order;

import com.oanda.v20.Context;
import com.oanda.v20.ExecuteException;
import com.oanda.v20.RequestException;
import com.oanda.v20.account.Account;
import com.oanda.v20.account.AccountID;
import com.oanda.v20.order.*;
import com.oanda.v20.order.Order;
import com.oanda.v20.primitives.DateTime;
import com.oanda.v20.transaction.TransactionID;
import trader.connector.ApiConnector;
import trader.connector.BaseConnector;

import java.math.BigDecimal;

/**
 * Manage waiting orders
 */

public final class OrderService implements OrderStrategy {

    private static final BigDecimal STOP_LOSS_OFFSET = BigDecimal.valueOf(0.0005);

    private Context context;
    private OrderCancelResponse cancelOrderResponse;

    public OrderService(Context context){
        this.setContext(context);
    }


    private ApiConnector apiConnector;

    public OrderService(ApiConnector connector) {
        apiConnector = connector;
    }

    public void submitNewOrder(){

    }

    public void closeUnfilledOrder(){

    }

    /**
     * Closing unfilled orders. For short orders if ask is STOP_LOSS_OFFSET  above stopLossPrice and for long orders if bid is STOP_LOSS_OFFSET below stopLossPrice
     * @param account current account
     * @param ask last ask price
     * @param bid last bid price
     */
    public void closeUnfilledOrder(Account account, BigDecimal ask, BigDecimal bid){

        MarketIfTouchedOrder notFilledOrder = this.getMarketIfTouchedOrder(account);

        if (notFilledOrder == null) {
            return;
        }

        BigDecimal stopLossPrice = notFilledOrder.getStopLossOnFill().getPrice().bigDecimalValue();
        BigDecimal units = notFilledOrder.getUnits().bigDecimalValue();

        BigDecimal delta = null;
        if(units.compareTo(BigDecimal.ZERO) < 0) {
            delta = ask.subtract(stopLossPrice).setScale(5, BigDecimal.ROUND_HALF_UP);
        } else if(units.compareTo(BigDecimal.ZERO) > 0){
            delta = stopLossPrice.subtract(bid).setScale(5, BigDecimal.ROUND_HALF_UP);

        }
        if(delta != null && delta.compareTo(STOP_LOSS_OFFSET) > 0){
            this.cancelOrder(account.getId(), notFilledOrder.getId());

            TransactionID id = this.cancelOrderResponse.getOrderCancelTransaction().getId();
            DateTime time = this.cancelOrderResponse.getOrderCancelTransaction().getTime();

            System.out.println("Order canceled id: "+id.toString()+" time: "+time);
        }
    }

    /**
     * Getting first not filled MarketIfTouchOrder from orders list
     * @param account current account
     * @return {@link MarketIfTouchedOrder} object. If there is not such order then the returned object will be {@code null}
     */
    private MarketIfTouchedOrder getMarketIfTouchedOrder(Account account){
        MarketIfTouchedOrder notFilledOrder = null;

        for (Order order:account.getOrders()) {
            if (order.getType().equals(OrderType.MARKET_IF_TOUCHED)){
                notFilledOrder = (MarketIfTouchedOrder) order;
                break;
            }
        }
        return notFilledOrder;
    }

    /**
     * Cancel current order
     * @param accountID account id
     * @param orderID order id
     *
     */
    private void cancelOrder(AccountID accountID, OrderID orderID){
        OrderSpecifier orderSpecifier = new OrderSpecifier(orderID);
        try {
            this.cancelOrderResponse = this.context.order.cancel(accountID, orderSpecifier);
        } catch (RequestException | ExecuteException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Setter for context
     * @param context current context
     * @see Context;
     */
    private void setContext(Context context) {
        if (context == null){
            throw new NullPointerException("Context must not be null");
        }

        this.context = context;
    }

}
