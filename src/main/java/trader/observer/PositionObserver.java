package trader.observer;

import trader.broker.BrokerGateway;
import trader.controller.TraderController;
import trader.entity.trade.Trade;
import trader.entry.EntryStrategy;
import trader.exception.NullArgumentException;
import trader.price.Price;

public class PositionObserver extends BaseObserver {

    private TraderController<Trade> createTradeController;
    private EntryStrategy entryStrategy;

    public PositionObserver(BrokerGateway brokerGateway, TraderController<Trade> addTradeController){
        super(brokerGateway);
        if(addTradeController == null)
            throw new NullArgumentException();
        this. createTradeController = addTradeController;
    }

//    private Context context;
//    private NewTradeService newTradeService;
//    private ExitStrategy exitStrategy;
//    private OrderService orderService;
//
//
//    /**
//     * Constructor for PositionManager
//     * @param context current context
//     * @param newTradeService new trade service
//     * @param exitStrategy exit strategy
//     * @param orderService order service
//     * @see NewTradeService
//     * @see ExitStrategy
//     * @see OrderService
//     */
//    public PositionManager(Context context, NewTradeService newTradeService, ExitStrategy exitStrategy,
//                           OrderService orderService) {
//        this.setContext(context);
//        this.setNewTradeService(newTradeService);
//        this.setExitStrategy(exitStrategy);
//        this.setOrderService(orderService);
//
//    }
//
    @Override
    public void updateObserver(Price price) {
        if(brokerGateway.totalOpenTradesSize() == 0 &&
                brokerGateway.totalOpenOrdersSize() == 0){
            //addTradeController.execute(price);
        }
    }

    BrokerGateway getBrokerGateway() {
        return brokerGateway;
    }

    //    /**
//     * Update current positions
//     * @param dateTime last price dateTime
//     * @param ask last ask price
//     * @param bid last bid price
//     * @see DateTime
//     */
//    @Override
//    public void updateObserver(DateTime dateTime, BigDecimal ask, BigDecimal bid) {
//
//        try {
//            Account account = this.context.account.get(Config.ACCOUNTID).getAccount();
//            if (account.getTrades().size() > 0){
//                // Exit Strategy
//                this.exitStrategy.execute(account, ask, bid, dateTime);
//
//            } else if (account.getOrders().size() > 0) {
//                // Manage waiting order
//                this.orderService.closeUnfilledOrder(account, ask, bid);
//
//            } else {
//                // Send new trade
//                this.newTradeService.sendNewTradeOrder(account, bid);
//            }
//
//        } catch (RequestException | ExecuteException e) {
//            throw new RuntimeException(e);
//        }
//    }

//
//    /**
//     * Setter for NewTradeService
//     * @param newTradeService object
//     * @throws NullPointerException when newTradeService is null
//     * @see NewTradeService
//     */
//    private void setNewTradeService(NewTradeService newTradeService) {
//        if (newTradeService == null){
//            throw new NullPointerException("NewTradeService is null");
//        }
//        this.newTradeService = newTradeService;
//    }
//
//    /**
//     * Setter for exitStrategy
//     * @param exitStrategy object
//     * @throws NullPointerException when halfCloseTrailExitStrategy is null
//     * @see ExitStrategy
//     */
//    private void setExitStrategy(ExitStrategy exitStrategy) {
//        if (exitStrategy == null){
//            throw new NullPointerException("ExitStrategy is null");
//        }
//        this.exitStrategy = exitStrategy;
//    }
//
//    /**
//     * Setter for OrderService
//     * @param orderService object
//     * @throws NullPointerException when orderService is null
//     * @see OrderService ;
//     */
//    private void setOrderService(OrderService orderService) {
//        if(orderService == null){
//            throw  new NullPointerException("OrderService is null");
//        }
//        this.orderService = orderService;
//    }
//

}
