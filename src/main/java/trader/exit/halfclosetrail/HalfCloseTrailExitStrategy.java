//package trader.exit.halfclosetrail;
//
//import com.oanda.v20.Context;
//import com.oanda.v20.account.Account;
//import com.oanda.v20.order.*;
//import com.oanda.v20.primitives.DateTime;
//import com.oanda.v20.trade.*;
//import com.oanda.v20.transaction.TransactionID;
//import trader.broker.BrokerGateway;
//import trader.configuration.TradingStrategyConfiguration;
//import trader.entity.candlestick.Candlestick;
//import trader.entity.indicator.updater.CandlesUpdater;
//import trader.entity.candlestick.candle.CandleGranularity;
//import trader.exception.NullArgumentException;
//import trader.exit.service.ServiceExitStrategy;
//import trader.exit.ExitStrategy;
//import trader.price.Price;
//
//
//import java.math.BigDecimal;
//import java.util.HashMap;
//import java.util.List;
//
//
///**
// * This exit strategy will close part of the position when price hit first target point. After that it will trail position's stop behind previous bar high(for short) or low(for long).
// * For short trade stop will be moved if last closed candlestick's close is below current low and last closed candlestick's high is also below current high.
// * For long trade stop will be moved if last closed candlestick's close is above current high and last closed candlestick's low is also above current low;
// */
//public final class HalfCloseTrailExitStrategy implements ExitStrategy {
//
//    private static final BigDecimal FIRST_TARGET_DISTANCE = BigDecimal.valueOf(0.0032);
//    private static final BigDecimal PARTS_TO_CLOSE = BigDecimal.valueOf(2);
//    private static final BigDecimal BREAK_EVEN_DISTANCE = BigDecimal.valueOf(0.0025);
//
//
//    private List<Candlestick> candlesticks;
//    private TradingStrategyConfiguration configuration;
//    private BrokerGateway brokerGateway;
//    private HashMap<String, String> settings;
//
// //   private ServiceExitStrategy serviceExitStrategy;
//    private OrderCreateResponse halfTradeResponse;
//    private TradeSetDependentOrdersResponse tradeSetDependentOrdersResponse;
//    private BigDecimal exitBarHigh;
//    private BigDecimal exitBarLow;
//
//
//    public HalfCloseTrailExitStrategy() {
////        this.serviceExitStrategy = new ServiceExitStrategy();
//        this.exitBarHigh = null;
//        this.exitBarLow = null;
//        settings = new HashMap<>();
//        initializeRequestSettings();
//    }
//
//
//    public void updateCandles(Price price) {
//        if (price == null)
//            throw new NullArgumentException();
//        setUpdateQuantityInSettings();
//        List<Candlestick> candles = brokerGateway.getCandles(settings);
//        if(candlesticks.size() >0)
//            candlesticks.add(candles.get(candles.size()-1));
//        else
//            candlesticks.addAll(candles);
//    }
//
//    private void setUpdateQuantityInSettings() {
//        if(candlesticks.size() != 0)
//            settings.put("quantity", String.valueOf(configuration.getUpdateCandlesQuantity()));
//    }
//
//    private void initializeRequestSettings(){
//        settings.put("instrument", configuration.getInstrument());
//        settings.put("quantity", String.valueOf(configuration.getInitialCandlesQuantity()));
//        settings.put("granularity", configuration.getExitGranularity().toString());
//    }
//
//
//    @Override
//    public void execute(Price price) {
//
//        if (account == null){
//            throw new NullPointerException("account is null");
//        }
//      //  TradeSummary trade = account.getTrades().get(TRADE_INDEX);
//
//     //   this.candlesUpdater.updateCandles(dateTime);
//
//        this.moveToBreakEven(account, trade, ask, bid);
//
//        BigDecimal stopLossPrice = BigDecimal.ZERO;
//        BigDecimal tradeOpenPrice = trade.getPrice().bigDecimalValue();
//        OrderID id = trade.getStopLossOrderID();
//        if (account == null || id == null){
//            throw new NullPointerException("account or id is null");
//        }
//        for (Order order:account.getOrders()) {
//            if (order.getId().equals(id) && order.getType().equals(OrderType.STOP_LOSS)){
//                StopLossOrder stopLossOrder = (StopLossOrder) order;
//                stopLossPrice = stopLossOrder.getPrice().bigDecimalValue();
//            }
//        }
// //       BigDecimal stopLossPrice = this.serviceExitStrategy.getStopLossOrderPriceByID(account, trade.getStopLossOrderID());
//        BigDecimal currentUnits = trade.getCurrentUnits().bigDecimalValue();
//
//        if (currentUnits.compareTo(BigDecimal.ZERO) > 0 && stopLossPrice.compareTo(tradeOpenPrice) >= 0){
//            return;
//        }
//
//        if (currentUnits.compareTo(BigDecimal.ZERO) < 0 && stopLossPrice.compareTo(tradeOpenPrice) <= 0){
//            return;
//        }
//
//        BigDecimal breakEvenPrice = this.calculateTargetPrice(currentUnits, tradeOpenPrice, BREAK_EVEN_DISTANCE);
//        if (isFilterPassed(currentUnits, breakEvenPrice, ask, bid)){
//            TradeID id = trade.getId();
//
//            this.tradeSetDependentOrdersResponse = this.serviceExitStrategy.changeStopLoss(id, tradeOpenPrice);
//        }
//
//
//        this.closeHalfPosition(trade, ask, bid);
//
//        //if 1st half is closed then trail the rest after prev bar extremes
//        this.trailStopLoss(account, trade);
//    }
//
//    /**
//     * If trade hits first target point close half, else trail after prev bar high
//     * @param account current account
//     * @param ask current ask price
//     * @param bid current bid price
//     * @param dateTime current dateTime
//     * @see CandlesUpdater
//     */
//    @Override
//    public void execute(Account account, BigDecimal ask, BigDecimal bid, DateTime dateTime) {
//
//        TradeSummary trade = this.serviceExitStrategy.getTrade(account);
//
//        this.serviceExitStrategy.updaterUpdateCandles(dateTime);
//
//        this.moveToBreakEven(account, trade, ask, bid);
//
//        this.closeHalfPosition(trade, ask, bid);
//
//        //if 1st half is closed then trail the rest after prev bar extremes
//        this.trailStopLoss(account, trade);
//    }
//
//    /**
//     * Move stop loss to break even when price move X amount of pips in trade's direction
//      * @param trade current trade
//     * @param ask current ask price
//     * @param bid current bid price
//     */
//    private void moveToBreakEven(Account account, TradeSummary trade, BigDecimal ask, BigDecimal bid){
//        BigDecimal tradeOpenPrice = trade.getPrice().bigDecimalValue();
//        BigDecimal stopLossPrice = this.serviceExitStrategy.getStopLossOrderPriceByID(account, trade.getStopLossOrderID());
//        BigDecimal currentUnits = trade.getCurrentUnits().bigDecimalValue();
//
//        if (currentUnits.compareTo(BigDecimal.ZERO) > 0 && stopLossPrice.compareTo(tradeOpenPrice) >= 0){
//            return;
//        }
//
//        if (currentUnits.compareTo(BigDecimal.ZERO) < 0 && stopLossPrice.compareTo(tradeOpenPrice) <= 0){
//            return;
//        }
//
//        BigDecimal breakEvenPrice = this.calculateTargetPrice(currentUnits, tradeOpenPrice, BREAK_EVEN_DISTANCE);
//        if (isFilterPassed(currentUnits, breakEvenPrice, ask, bid)){
//            TradeID id = trade.getId();
//
//            this.tradeSetDependentOrdersResponse = this.serviceExitStrategy.changeStopLoss(id, tradeOpenPrice);
//        }
//    }
//
//    /**
//     * Close half of the position if it is not already closed
//     * @param trade current trade
//     * @param ask current ask price
//     * @param bid current bid price
//     */
//    private void closeHalfPosition(TradeSummary trade, BigDecimal ask, BigDecimal bid){
//        TradeID id = trade.getId();
//        BigDecimal initialUnits = trade.getInitialUnits().bigDecimalValue();
//        BigDecimal currentUnits = trade.getCurrentUnits().bigDecimalValue();
//        BigDecimal tradeOpenPrice = trade.getPrice().bigDecimalValue();
//
//        //Check if first half is closed
//        if (initialUnits.compareTo(currentUnits) != 0) {
//            return;
//        }
//
//        BigDecimal firstTargetPrice = this.calculateTargetPrice(currentUnits, tradeOpenPrice, FIRST_TARGET_DISTANCE);
//
//        if (isFilterPassed(currentUnits, firstTargetPrice, ask, bid)){
//            this.halfTradeResponse = this.serviceExitStrategy.partialTradeClose(currentUnits, PARTS_TO_CLOSE);
//        }
//    }
//
//    /**
//     * Check if half of the trade can be closed
//     * @param currentUnits trade current amount of units
//     * @param firstTargetPrice price of the first target
//     * @param ask ask price
//     * @param bid bid price
//     * @return {@link boolean} {@code true} if the half of the trade can be closed
//     *                         {@code false} otherwise
//     */
//    private boolean isFilterPassed(BigDecimal currentUnits, BigDecimal firstTargetPrice, BigDecimal ask, BigDecimal bid){
//        //for short trade
//        boolean shortCondition = currentUnits.compareTo(BigDecimal.ZERO) < 0 && firstTargetPrice.compareTo(ask) >= 0;
//        boolean longCondition = currentUnits.compareTo(BigDecimal.ZERO) > 0 && firstTargetPrice.compareTo(bid) <= 0;
//
//        return shortCondition || longCondition;
//    }
//
//    /**
//     * Calculate Initial Target's price
//     * @param currentUnits units size
//     * @param tradeOpenPrice open price of the trade
//     * @return {@link BigDecimal} value of the price
//     */
//    private BigDecimal calculateTargetPrice(BigDecimal currentUnits, BigDecimal tradeOpenPrice, BigDecimal distance){
//        if (currentUnits.compareTo(BigDecimal.ZERO) < 0){
//
//            return tradeOpenPrice.subtract(distance).setScale(5, BigDecimal.ROUND_HALF_UP);
//        } else  {
//
//           return tradeOpenPrice.add(distance).setScale(5, BigDecimal.ROUND_HALF_UP);
//        }
//    }
//
//    /**
//     * Trail stop loss after previous bar extreme
//     * @param account current account
//     * @param trade current trade
//     */
//    private void trailStopLoss(Account account, TradeSummary trade){
//
//        BigDecimal currentUnits = trade.getCurrentUnits().bigDecimalValue();
//        BigDecimal initialUnits = trade.getInitialUnits().bigDecimalValue();
//
//        //Check if first half is closed
//        if (initialUnits.compareTo(currentUnits) == 0) {
//            return;
//        }
//
//        BigDecimal lastFullCandleHigh = this.serviceExitStrategy.getLastFullCandleHigh();
//        BigDecimal lastFullCandleLow = this.serviceExitStrategy.getLastFullCandleLow();
//        BigDecimal lastFullCandleClose = this.serviceExitStrategy.getLastFullCandleClose();
//
//        //initial prev bar values
//        this.initialSetExitBar(trade);
//
//        boolean isReadyToTrailStopLoss = this.isReadyToTrailStopLoss(currentUnits, lastFullCandleClose, lastFullCandleHigh, lastFullCandleLow);
//
//
//        this.exitBarLow = updateExitBarComponent(this.exitBarLow, lastFullCandleLow, currentUnits);
//        this.exitBarHigh = updateExitBarComponent(this.exitBarHigh, lastFullCandleHigh, currentUnits);
//        //this.updateExitBarHigh(isReadyToTrailStopLoss, lastFullCandleHigh);
//       // this.updateExitBarLow(isReadyToTrailStopLoss, lastFullCandleLow, currentUnits);
//
//        if (this.isReadyToSendTrailOrder(account, trade, currentUnits) && isReadyToTrailStopLoss){
//            BigDecimal newStopLossPrice = this.setNewStopLoss(currentUnits);
//            this.tradeSetDependentOrdersResponse = this.serviceExitStrategy.changeStopLoss(trade.getId(), newStopLossPrice);
//
//            this.printInformation(newStopLossPrice);
//        }
//    }
//
//    /**
//     * Set value to exit bar's extremes. If they are {@code null} then set values to trade open price
//     * @param trade current trade
//     */
//    private void initialSetExitBar(TradeSummary trade){
//        if(this.exitBarHigh == null || this.exitBarLow == null){
//            BigDecimal tradeOpenPrice = trade.getPrice().bigDecimalValue();
//            this.exitBarHigh = tradeOpenPrice;
//            this.exitBarLow = tradeOpenPrice;
//        }
//    }
//
//    /**
//     * Check if stop loss can be trailed.
//     * For short trade stop will be trailed if candlestick close is lower than current low and candlestick high is also lower than current high.
//     * For long trade stop will be trailed if candlestick close is higher than current high and candlestick low is also higher than current low
//     * @param currentUnits current size of the trade
//     * @param lastFullCandleClose last full candlestick close
//     * @param lastFullCandleHigh last full candlestick high
//     * @param lastFullCandleLow last full candlestick low
//     * @return {@link boolean} {@code true} if stop can be trailed
//     *                         {@code false} otherwise
//     */
//    private boolean isReadyToTrailStopLoss(BigDecimal currentUnits, BigDecimal lastFullCandleClose,
//                                           BigDecimal lastFullCandleHigh, BigDecimal lastFullCandleLow){
//
//        boolean shortCondition = currentUnits.compareTo(BigDecimal.ZERO) < 0 && lastFullCandleClose.compareTo(this.exitBarLow) < 0
//                && lastFullCandleHigh.compareTo(this.exitBarHigh) < 0;
//
//        boolean longCondition = currentUnits.compareTo(BigDecimal.ZERO) > 0 && lastFullCandleClose.compareTo(this.exitBarHigh) > 0
//                && lastFullCandleLow.compareTo(this.exitBarLow) > 0;
//
//        return shortCondition || longCondition;
//    }
//
//    /**
//     * Update exit bar components - low or high
//     * @param exitBarComponent exit bar component - exitBarHigh or exitBarLow
//     * @param lastFullCandleLow last full candlestick low
//     * @param currentUnits trade's units
//     */
//    private BigDecimal updateExitBarComponent(BigDecimal exitBarComponent, BigDecimal lastFullCandleLow, BigDecimal currentUnits){
//        if(currentUnits.compareTo(BigDecimal.ZERO) < 0){
//            exitBarComponent = lastFullCandleLow.compareTo(exitBarComponent) < 0 ? lastFullCandleLow : exitBarComponent;
//        } else {
//            exitBarComponent = lastFullCandleLow.compareTo(exitBarComponent) > 0 ? lastFullCandleLow : exitBarComponent;
//        }
//        return exitBarComponent;
//    }
//
//    /**
//     * Check if new stopLoss order can be sent
//     * @param account current account
//     * @param trade current trade
//     * @return {@link boolean} {@code true} if stopLoss can be moved
//     *                         {@code false} otherwise
//     */
//    private boolean isReadyToSendTrailOrder(Account account, TradeSummary trade, BigDecimal currentUnits){
//
//        BigDecimal stopLossPrice = this.serviceExitStrategy.getStopLossOrderPriceByID(account, trade.getStopLossOrderID());
//
//        boolean shortCondition = (currentUnits.compareTo(BigDecimal.ZERO) < 0) && ( stopLossPrice.compareTo(BigDecimal.ZERO) == 0 || stopLossPrice.compareTo(this.exitBarHigh) > 0);
//        boolean longCondition = (currentUnits.compareTo(BigDecimal.ZERO) > 0) && ( stopLossPrice.compareTo(BigDecimal.ZERO) == 0 || stopLossPrice.compareTo(this.exitBarLow) < 0);
//
//        return shortCondition || longCondition;
//    }
//
//    /**
//     * Set value of the new stopLoss
//     * @param unitsSize current unit size
//     * @return {@link BigDecimal} price value of the new stop
//     */
//    private BigDecimal setNewStopLoss(BigDecimal unitsSize){
//        if (unitsSize.compareTo(BigDecimal.ZERO) < 0){
//            return this.exitBarHigh;
//        }
//        return this.exitBarLow;
//    }
//
//    /**
//     * Printing id of the closed transaction
//     */
//    private void printInformation(BigDecimal newStopLoss){
//        TransactionID lastTransactionID = this.tradeSetDependentOrdersResponse.getLastTransactionID();
//        System.out.println("TradeImpl with id: " + lastTransactionID+" StopLoss moved to: " + newStopLoss);
//    }
//}
