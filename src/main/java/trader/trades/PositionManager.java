package trader.trades;

import com.oanda.v20.Context;
import com.oanda.v20.ExecuteException;
import com.oanda.v20.RequestException;
import com.oanda.v20.account.Account;
import com.oanda.v20.primitives.DateTime;
import trader.config.Config;
import trader.connectors.Connection;
import trader.core.Observer;
import trader.trades.services.NewTradeService;
import trader.trades.services.OrderService;
import trader.trades.services.exit_strategies.ExitStrategy;


import java.math.BigDecimal;


/**
 * Manage open trades, waiting orders
 */
public final class PositionManager implements Observer {


    private Context context;
    private NewTradeService newTradeService;
    private ExitStrategy exitStrategy;
    private OrderService orderService;


    /**
     * Constructor for PositionManager
     * @param context current context
     * @param newTradeService new trade service
     * @param exitStrategy exit strategy
     * @param orderService order service
     * @see NewTradeService
     * @see ExitStrategy
     * @see OrderService
     */
    public PositionManager(Context context, NewTradeService newTradeService, ExitStrategy exitStrategy,
                           OrderService orderService) {
        this.setContext(context);
        this.setNewTradeService(newTradeService);
        this.setExitStrategy(exitStrategy);
        this.setOrderService(orderService);

    }

    /**
     * Update current positions
     * @param dateTime last price dateTime
     * @param ask last ask price
     * @param bid last bid price
     * @see DateTime
     */
    @Override
    public void updateObserver(DateTime dateTime, BigDecimal ask, BigDecimal bid) {

        try {
            Account account = this.context.account.get(Config.ACCOUNTID).getAccount();
            if (account.getTrades().size() > 0){
                // Exit Strategy
                this.exitStrategy.execute(account, ask, bid, dateTime);

            } else if (account.getOrders().size() > 0) {
                // Manage waiting order
                this.orderService.closeUnfilledOrder(account, ask, bid);

            } else {
                // Send new trade
                this.newTradeService.sendNewTradeOrder(account, bid);
            }

        } catch(ExecuteException ee){
            Connection.waitToConnect(Config.URL);
        } catch (RequestException re ) {
            if (re.getMessage().equalsIgnoreCase("Service unavailable, please try again later.")){
                Connection.waitToConnect(Config.URL);
            } else {
                throw new RuntimeException(re);
            }
        }
    }

    /**
     * Setter for context
     * @param context current context
     * @throws NullPointerException when context is null
     */
    private void setContext(Context context){
        if (context == null){
            throw new NullPointerException("Context is null");
        }
        this.context = context;
    }

    /**
     * Setter for NewTradeService
     * @param newTradeService object
     * @throws NullPointerException when newTradeService is null
     * @see NewTradeService
     */
    private void setNewTradeService(NewTradeService newTradeService) {
        if (newTradeService == null){
            throw new NullPointerException("NewTradeService is null");
        }
        this.newTradeService = newTradeService;
    }

    /**
     * Setter for exitStrategy
     * @param exitStrategy object
     * @throws NullPointerException when halfCloseTrailExitStrategy is null
     * @see ExitStrategy
     */
    private void setExitStrategy(ExitStrategy exitStrategy) {
        if (exitStrategy == null){
            throw new NullPointerException("ExitStrategy is null");
        }
        this.exitStrategy = exitStrategy;
    }

    /**
     * Setter for OrderService
     * @param orderService object
     * @throws NullPointerException when orderService is null
     * @see OrderService ;
     */
    private void setOrderService(OrderService orderService) {
        if(orderService == null){
            throw  new NullPointerException("OrderService is null");
        }
        this.orderService = orderService;
    }


}
