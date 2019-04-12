package trader.order.standard;

import trader.broker.BrokerGateway;
import trader.configuration.TradingStrategyConfiguration;
import trader.entity.order.Order;
import trader.entity.order.enums.OrderType;
import trader.entity.trade.Direction;
import trader.entity.trade.Trade;
import trader.exception.EmptyArgumentException;
import trader.exception.NullArgumentException;
import trader.order.OrderStrategy;
import trader.price.Price;

import java.math.BigDecimal;
import java.util.HashMap;

public class StandardOrderStrategy implements OrderStrategy {

    private static final BigDecimal ONE_PIP = BigDecimal.valueOf(0.0001);
    private static final BigDecimal PIP_MULTIPLIER = BigDecimal.valueOf(10_000);
    private static final BigDecimal STOP_LOSS_OFFSET = BigDecimal.valueOf(0.0005);
    private static final String TRADE_STOP_LOSS_PRICE = "tradeStopLossPrice";
    private static final String TRADE_ENTRY_PRICE = "tradeEntryPrice";
    private static final String INSTRUMENT = "instrument";
    private static final String UNITS_SIZE = "unitsSize";

    private String lastOrderTransactionID;

    public StandardOrderStrategy() {
        lastOrderTransactionID = null;
    }


    @Override
    public void closeUnfilledOrders(BrokerGateway brokerGateway, Price price) {
        Order order = brokerGateway.getOrder(OrderType.MARKET_IF_TOUCHED);
        if ( parseOrderID(order) > 0){
           BigDecimal delta = calculateStopLossAndPriceDelta(order, price);
           if(delta != null && delta.compareTo(STOP_LOSS_OFFSET) > 0){
               brokerGateway.cancelOrder(order.getId());
//               TransactionID id = this.cancelOrderResponse.getOrderCancelTransaction().getId();
//               DateTime time = this.cancelOrderResponse.getOrderCancelTransaction().getTime();
//
//               System.out.println("Order canceled id: "+id.toString()+" time: "+time);
           }
       }
    }

    @Override
    public void placeTradeAsOrder(BrokerGateway brokerGateway, Price price, Trade trade, TradingStrategyConfiguration configuration){
        if(brokerGateway == null || price == null || trade == null || configuration == null)
            throw new NullArgumentException();

        BigDecimal unitsSize = calculateUnitsSize(brokerGateway, price, trade, configuration);
        BigDecimal availableMargin = brokerGateway.getAvailableMargin();
        BigDecimal tradeMargin = calculateTradeMargin(brokerGateway, unitsSize);
        BigDecimal futureMargin = brokerGateway.getMarginUsed().add(tradeMargin).setScale(5, BigDecimal.ROUND_HALF_UP);
        if (availableMargin.compareTo(futureMargin)>0 && isNotZero(unitsSize)){
            HashMap<String, String> settings = gatherOrderSettings(trade, configuration, unitsSize);
            lastOrderTransactionID = brokerGateway.placeMarketIfTouchedOrder(settings);
        }
    }

    @Override
    public BigDecimal calculateUnitsSize(BrokerGateway brokerGateway, Price price, Trade trade, TradingStrategyConfiguration configuration) {
        if(brokerGateway == null || price == null || trade == null || configuration == null)
            throw new NullArgumentException();
        //(balance * risk)/(stopSize*pipValue)
        BigDecimal unitsSize = multiply(brokerGateway.getBalance(), configuration.getRiskPerTrade());
        BigDecimal divider = multiply(calculateStopSize(trade), getPipValue(price));
        //for short trades units must be negative number
        if(isShort(trade))
            divider = multiply(divider, BigDecimal.valueOf(-1));
        if(isNotZero(divider))
            return divide(unitsSize, divider, 0);
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getPipValue(Price price){
        if(price == null || price.getBid().compareTo(BigDecimal.ZERO) <= 0)
            throw new EmptyArgumentException();
        return divide(ONE_PIP, price.getBid(), 7);//.multiply(LOT_SIZE).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    @Override
    public BigDecimal calculateStopSize(Trade trade){
        if(trade == null)
            throw new NullArgumentException();
        return  multiply(
                subtract(trade.getEntryPrice(), trade.getStopLossPrice()),
                PIP_MULTIPLIER).abs();
    }

    @Override
    public BigDecimal calculateTradeMargin(BrokerGateway brokerGateway, BigDecimal unitsSize){
        if(brokerGateway == null || unitsSize == null)
            throw new NullArgumentException();
        String leverage = brokerGateway.getConnector().getLeverage();
        return  isNotZero(unitsSize) ? divide(unitsSize, new BigDecimal(leverage), 5) : BigDecimal.ZERO;
    }

    private BigDecimal calculateStopLossAndPriceDelta(Order order, Price price){
        BigDecimal delta = null;
        if(order.getUnits().compareTo(BigDecimal.ZERO) < 0)
            delta = subtract(price.getAsk(), order.getStopLossPrice());
        else if(order.getUnits().compareTo(BigDecimal.ZERO) > 0)
            delta = subtract(order.getStopLossPrice(), price.getBid());
        return delta;
    }

    private int parseOrderID(Order order) {
        if(order != null)
            return Integer.parseInt(order.getId());
        return -1;
    }

    private HashMap<String, String> gatherOrderSettings(Trade trade, TradingStrategyConfiguration configuration, BigDecimal unitsSize) {
        HashMap<String, String> settings = new HashMap<>();
        settings.put(TRADE_STOP_LOSS_PRICE, trade.getStopLossPrice().toString());
        settings.put(TRADE_ENTRY_PRICE, trade.getEntryPrice().toString());
        settings.put(INSTRUMENT, configuration.getInstrument());
        settings.put(UNITS_SIZE, unitsSize.toString());
        return settings;
    }

    private BigDecimal subtract(BigDecimal valueA, BigDecimal valueB) {
        return valueA.subtract(valueB).setScale(5, BigDecimal.ROUND_HALF_UP);
    }

    private boolean isShort(Trade trade) {
        return trade.getDirection().equals(Direction.DOWN);
    }

    private BigDecimal divide(BigDecimal unitsSize, BigDecimal divider, int scale) {
        return unitsSize
                .divide(divider, scale, BigDecimal.ROUND_HALF_UP);
    }

    private BigDecimal multiply(BigDecimal numberA, BigDecimal numberB) {
        return (numberA)
                .multiply(numberB)
                .setScale(5, BigDecimal.ROUND_HALF_UP);
    }

    private boolean isNotZero(BigDecimal number) {
        return number.compareTo(BigDecimal.ZERO) !=0;
    }


}
