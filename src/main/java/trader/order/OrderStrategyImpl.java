package trader.order;

import trader.broker.BrokerGateway;
import trader.configuration.TradingStrategyConfiguration;
import trader.entity.trade.Direction;
import trader.entity.trade.Trade;
import trader.exception.EmptyArgumentException;
import trader.exception.NullArgumentException;
import trader.price.Price;

import java.math.BigDecimal;
import java.util.HashMap;

public class OrderStrategyImpl implements OrderStrategy {

    private static final BigDecimal ONE_PIP = BigDecimal.valueOf(0.0001);
    private static final BigDecimal PIP_MULTIPLIER = BigDecimal.valueOf(10_000);
    private static final String TRADE_STOP_LOSS_PRICE = "tradeStopLossPrice";
    private static final String TRADE_ENTRY_PRICE = "tradeEntryPrice";
    private static final String INSTRUMENT = "instrument";
    private static final String UNITS_SIZE = "unitsSize";

    private String lastOrderTransactionID;

    public OrderStrategyImpl() {
        lastOrderTransactionID = null;
    }

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

    public BigDecimal calculateTradeMargin(BrokerGateway brokerGateway, BigDecimal unitsSize){
        if(brokerGateway == null || unitsSize == null)
            throw new NullArgumentException();
        String leverage = brokerGateway.getConnector().getLeverage();
        return  isNotZero(unitsSize) ? divide(unitsSize, new BigDecimal(leverage), 5) : BigDecimal.ZERO;
    }

    private HashMap<String, String> gatherOrderSettings(Trade trade, TradingStrategyConfiguration configuration, BigDecimal unitsSize) {
        HashMap<String, String> settings = new HashMap<>();
        settings.put(TRADE_STOP_LOSS_PRICE, trade.getStopLossPrice().toString());
        settings.put(TRADE_ENTRY_PRICE, trade.getEntryPrice().toString());
        settings.put(INSTRUMENT, configuration.getInstrument());
        settings.put(UNITS_SIZE, unitsSize.toString());
        return settings;
    }

    private BigDecimal subtract(BigDecimal entryPrice, BigDecimal stopPrice) {
        return entryPrice.subtract(stopPrice);
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
