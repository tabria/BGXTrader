//package trader.exit.service;
//
//import com.oanda.v20.Context;
//import com.oanda.v20.ExecuteException;
//import com.oanda.v20.RequestException;
//import com.oanda.v20.account.Account;
//import com.oanda.v20.instrument.Candlestick;
//import com.oanda.v20.instrument.CandlestickGranularity;
//import com.oanda.v20.instrument.InstrumentCandlesRequest;
//import com.oanda.v20.order.*;
//import com.oanda.v20.primitives.DateTime;
//import com.oanda.v20.trade.*;
//import com.oanda.v20.transaction.StopLossDetails;
//import trader.entity.indicator.updater.CandlesUpdater;
//import trader.config.Config;
//import trader.entity.candlestick.candle.CandleGranularity;
//import trader.exit.ExitStrategy;
//
//import java.math.BigDecimal;
//import java.util.List;
//
//public class ServiceExitStrategy {
//
//    private static final long NUMBER_OF_CANDLES = 2L;
//    private static final int TRADE_INDEX = 0;
//
//    private final Context context;
//  //  private final CandlesUpdater candlesUpdater;
//
//
//    public ServiceExitStrategy() {
//        this.context = null;
//        this.candlesUpdater = null;
//    }
//
//    public ServiceExitStrategy(Context context, CandleGranularity candlestickGranularity) {
//        this.context = this.setContext(context);
//        this.candlesUpdater = new CandlesUpdater(this.context, this.createCandleRequest(candlestickGranularity), candlestickGranularity);
//    }
//
//
//    public static ExitStrategy createInstance(){
//        return null;
//    }
//
//    /**
//     * Get last completed candlestick High.
//     * Index for the candlestick is calculated with this formula: listSize - numberOfCandles
//     * @return {@link BigDecimal} high value
//     * @throws IndexOutOfBoundsException if candlestick count is not equal to required
//     */
//    BigDecimal getLastFullCandleHigh(){
//
//        int index = this.getLastFinishedCandleIndex();
//
//        //return this.candlesUpdater.getCandles().get(index).getMid().getH().bigDecimalValue();
//        return null;
//    }
//
//    /**
//     * Get last completed candlestick Low
//     * * Index for the candlestick is calculated with this formula: listSize - numberOfCandles
//     * @return {@link BigDecimal} low value
//     */
//    BigDecimal getLastFullCandleLow(){
//
//        int index = this.getLastFinishedCandleIndex();
//
//       // return this.candlesUpdater.getCandles().get(index).getMid().getL().bigDecimalValue();
//        return null;
//    }
//
//    /**
//     * Get last completed candlestick Close
//     * @return {@link BigDecimal} close value
//     */
//    BigDecimal getLastFullCandleClose(){
//
//        int index = this.getLastFinishedCandleIndex();
//       // return this.candlesUpdater.getCandles().get(index).getMid().getC().bigDecimalValue();
//        return null;
//    }
//
//    /**
//     * Call updateCandles method in candlesUpdater
//     * * Index for the candlestick is calculated with this formula: listSize - numberOfCandles
//     * @param dateTime of the last price
//     * @return {@link boolean} {@code true} if updateIndicator successful
//     *                        {@code false} otherwise
//     * @see CandlesUpdater
//     */
//    boolean updaterUpdateCandles(DateTime dateTime){
//       return this.candlesUpdater.updateCandles(dateTime);
//    }
//
//    /**
//     * Change stop loss
//     * @param id trade if
//     * @param newStopPrice new price for the stop
//     * @throws NullPointerException when id or newStopPrice is null
//     */
//    TradeSetDependentOrdersResponse changeStopLoss(TradeID id, BigDecimal newStopPrice){
//        if (id == null || newStopPrice == null){
//            throw new NullPointerException("Id or newStopPrice is null");
//        }
//
//        TradeSpecifier tradeSpecifier = new TradeSpecifier(id);
//        StopLossDetails stopLossDetails = new StopLossDetails().setPrice(newStopPrice);
//
//        TradeSetDependentOrdersRequest tradeSetDependentOrdersRequest = new TradeSetDependentOrdersRequest(Config.ACCOUNTID, tradeSpecifier)
//                                                                                .setStopLoss(stopLossDetails);
//        try {
//            return this.context.trade.setDependentOrders(tradeSetDependentOrdersRequest);
//        } catch (RequestException | ExecuteException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    /**
//     * Get stop loss order price
//     * @param account current account
//     * @param id order id
//     * @return {@link BigDecimal} stop loss price
//     * @throws NullPointerException if account or id is null
//     */
//    BigDecimal getStopLossOrderPriceByID(Account account, OrderID id){
//        if (account == null || id == null){
//            throw new NullPointerException("account or id is null");
//        }
//        BigDecimal stopLossPrice = BigDecimal.ZERO;
//        for (Order order:account.getOrders()) {
//            if (order.getId().equals(id) && order.getType().equals(OrderType.STOP_LOSS)){
//                StopLossOrder stopLossOrder = (StopLossOrder) order;
//                stopLossPrice = stopLossOrder.getPrice().bigDecimalValue();
//            }
//        }
//        return stopLossPrice;
//    }
//
//    /**
//     * Method to close half of the open trade
//     * @param currentUnits trade's current units size
//     * @param  parts how much parts of the trade to close - example 2 means half position
//     * @return {@link OrderCreateResponse} response after executing the request
//     * @throws NullPointerException when currentUnits or parts is null
//     * @throws IllegalArgumentException when parts is less than 1
//     * @see OrderCreateResponse;
//     */
//    OrderCreateResponse partialTradeClose(BigDecimal currentUnits, BigDecimal parts){
//        if(currentUnits == null || parts == null){
//            throw new NullPointerException("currentUnits or parts is null");
//        }
//        if (parts.compareTo(BigDecimal.ONE)< 0){
//            throw new IllegalArgumentException("parts is less than 1");
//        }
//        //multiply with -1 to reverse units size. This will open trade with opposite direction to the current trade
//        BigDecimal unitsToClose = currentUnits.divide(parts, 0, BigDecimal.ROUND_HALF_UP)
//                .multiply(BigDecimal.valueOf(-1)).setScale(0, BigDecimal.ROUND_HALF_UP);
//
//        MarketOrderRequest marketOrderRequest = new MarketOrderRequest()
//                .setInstrument(Config.INSTRUMENT)
//                .setCurrentUnits(unitsToClose);
//        OrderCreateRequest halfTradeRequest = new OrderCreateRequest(Config.ACCOUNTID).setOrder(marketOrderRequest);
//        try {
//            return this.context.order.create(halfTradeRequest);
//        } catch (RequestException | ExecuteException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    /**
//     * Getting current active trade. TRADE_INDEX is zero because the robot cannot operate with more than one trade
//     * @param account current account
//     * @return {@link TradeSummary} current tradeSummary object
//     * @throws NullPointerException when account is null
//     * @see TradeSummary
//     */
//    TradeSummary getTrade(Account account){
//        if (account == null){
//            throw new NullPointerException("account is null");
//        }
//        return account.getTrades().get(TRADE_INDEX);
//    }
//
//    private int getLastFinishedCandleIndex(){
//      //  List<Candlestick> candles = this.candlesUpdater.getCandles();
//        List<Candlestick> candles = null;
//        if (candles.size()!= NUMBER_OF_CANDLES){
//            throw new IndexOutOfBoundsException("Candles count is: " +candles.size()+" required count: "+NUMBER_OF_CANDLES);
//        }
//        //index for last finished candlestick
//        return (int) (candles.size() - NUMBER_OF_CANDLES);
//    }
//
//    /**
//     * Setter for Context
//     * @param context current context
//     * @return {@link Context} current context
//     * @throws NullPointerException when context is null
//     * @see Context
//     */
//    private Context setContext(Context context){
//        if (context == null){
//            throw new NullPointerException("Context is null");
//        }
//        return context;
//    }
//
//    /**
//     * Create candlestick request object
//     * @param candlestickGranularity current time frame
//     * @return {@link InstrumentCandlesRequest} object
//     * @throws NullPointerException if candlestickGranularity is null
//     * @see InstrumentCandlesRequest
//     */
//    private InstrumentCandlesRequest createCandleRequest(CandleGranularity candlestickGranularity){
//        if (candlestickGranularity == null){
//            throw  new NullPointerException("CandleGranularity must not be null");
//        }
//        return new InstrumentCandlesRequest(Config.INSTRUMENT)
//                .setCount(NUMBER_OF_CANDLES)
//                .setGranularity(CandlestickGranularity.valueOf(candlestickGranularity.toString()))
//                .setSmooth(false);
//    }
//}
