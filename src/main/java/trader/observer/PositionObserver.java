package trader.observer;

import trader.broker.BrokerGateway;
import trader.configuration.TradingStrategyConfiguration;
import trader.entity.trade.Direction;
import trader.entity.trade.Trade;
import trader.entry.EntryStrategy;
import trader.exception.NullArgumentException;
import trader.order.OrderStrategy;
import trader.entity.price.Price;

import java.math.BigDecimal;

public class PositionObserver extends BaseObserver {

    private static final BigDecimal TRADABLE_THRESHOLD = BigDecimal.valueOf(0.0020);
    private TradingStrategyConfiguration configuration;
    private EntryStrategy entryStrategy;
    private OrderStrategy orderStrategy;

    public PositionObserver(BrokerGateway brokerGateway, EntryStrategy entryStrategy, OrderStrategy orderStrategy, TradingStrategyConfiguration configuration){
        super(brokerGateway);
        if(entryStrategy == null || orderStrategy == null || configuration == null)
            throw new NullArgumentException();
        this.entryStrategy = entryStrategy;
        this.orderStrategy = orderStrategy;
        this.configuration = configuration;
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
        if(brokerGateway.totalOpenTradesSize() == 0 && brokerGateway.totalOpenOrdersSize() == 0){
            Trade newTrade = entryStrategy.generateTrade();
            setTradableForThreshold(price, newTrade);
            if(isTradable(newTrade))
               orderStrategy.placeTradeAsOrder(brokerGateway, price, newTrade, configuration);
        } else if(brokerGateway.totalOpenOrdersSize() > 0)
            orderStrategy.closeUnfilledOrders(brokerGateway, price);
    }

    private boolean isTradable(Trade newTrade) {
        return newTrade.getTradable() && !newTrade.getDirection().equals(Direction.FLAT);
    }

    private void setTradableForThreshold(Price price, Trade newTrade) {
        if(isTradable(newTrade)) {
            BigDecimal delta = getEntryPriceAndPriceDelta(newTrade, price);
            if (delta.compareTo(TRADABLE_THRESHOLD) > 0)
                newTrade.setTradable("false");
        }
    }

    private BigDecimal getEntryPriceAndPriceDelta(Trade newTrade, Price price) {
        BigDecimal currentPrice = newTrade.getDirection().equals(Direction.UP) ? price.getAsk() : price.getBid();
        BigDecimal entryPrice = newTrade.getEntryPrice();
        return entryPrice.subtract(currentPrice).abs();
    }

//    BrokerGateway getBrokerGateway() {
//        return brokerGateway;
//    }

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
