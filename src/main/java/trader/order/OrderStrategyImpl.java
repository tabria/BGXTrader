package trader.order;

import com.oanda.v20.account.Account;
import trader.broker.BrokerGateway;
import trader.configuration.TradingStrategyConfiguration;
import trader.entity.trade.Direction;
import trader.entity.trade.Trade;
import trader.exception.EmptyArgumentException;
import trader.exception.NullArgumentException;
import trader.price.Price;

import java.math.BigDecimal;

public class OrderStrategyImpl implements OrderStrategy {

    private static final BigDecimal ONE_PIP = BigDecimal.valueOf(0.0001);
    private static final BigDecimal EUR_LEVERAGE = BigDecimal.valueOf(30);
    private static final BigDecimal PIP_MULTIPLIER = BigDecimal.valueOf(10_000);


//price is bid
    public void placeTradeAsOrder(BrokerGateway brokerGateway, Price price, Trade trade){
        if(brokerGateway == null || price == null || trade == null)
            throw new NullArgumentException();

//        BigDecimal unitsSize = calculateUnitsSize(account, newTrade, bid);
        BigDecimal availableMargin = brokerGateway.getAvailableMargin();
//        BigDecimal tradeMargin = calculateTradeMargin(brokerGateway, unitsSize);
//        if (availableMargin.compareTo(futureMargin)>0 && unitsSize.compareTo(BigDecimal.ZERO)!=0){
//
//            //create order request
//            OrderCreateRequest request = this.createOrderRequest(unitsSize, newTrade);
//            try {
//                this.orderCreateResponse = this.context.order.create(request);
//                TransactionID id = this.orderCreateResponse.getOrderCreateTransaction().getId();
//                DateTime time = this.orderCreateResponse.getOrderCreateTransaction().getTime();
//                System.out.println("New TradeImpl has been added with id: " +id.toString() + " and time: " +time.toString() );
//            } catch (RequestException | ExecuteException e){
//                throw new RuntimeException(e);
//            }
//        }
    }

    public BigDecimal calculateUnitsSize(BrokerGateway brokerGateway, Price price, Trade trade, TradingStrategyConfiguration configuration) {
        if(brokerGateway == null || price == null || trade == null || configuration == null)
            throw new NullArgumentException();
        //(balance * risk)/(stopSize*pipValue)
        BigDecimal balance = brokerGateway.getBalance();
        BigDecimal pipValue = getPipValue(price);

        BigDecimal stopSize = calculateStopSize(trade);
        BigDecimal unitsSize = balance
                .multiply(configuration.getRiskPerTrade())
                .setScale(5, BigDecimal.ROUND_HALF_UP);
        BigDecimal divider = stopSize.multiply(pipValue).setScale(5, BigDecimal.ROUND_HALF_UP);
        //for short trades units must be negative number
        if(trade.getDirection().equals(Direction.DOWN) ) {
            divider = divider
                    .multiply(BigDecimal.valueOf(-1))
                    .setScale(5, BigDecimal.ROUND_HALF_UP);
        }
        if (divider.compareTo(BigDecimal.ZERO) != 0)
            return  unitsSize.divide(divider, 0, BigDecimal.ROUND_HALF_UP);
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getPipValue(Price price){
        if(price == null || price.getBid().compareTo(BigDecimal.ZERO) <= 0)
            throw new EmptyArgumentException();
        return ONE_PIP.divide(price.getBid(), 7, BigDecimal.ROUND_HALF_UP);//.multiply(LOT_SIZE).setScale(2, BigDecimal.ROUND_HALF_UP);

    }

    @Override
    public BigDecimal calculateStopSize(Trade trade){
        if(trade == null)
            throw new NullArgumentException();
        BigDecimal entryPrice = trade.getEntryPrice();
        BigDecimal stopPrice = trade.getStopLossPrice();

        BigDecimal stopSize = entryPrice.subtract(stopPrice).setScale(5, BigDecimal.ROUND_HALF_UP).abs();
        return stopSize.multiply(PIP_MULTIPLIER).setScale(5, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal calculateTradeMargin(BrokerGateway brokerGateway, BigDecimal unitsSize){
        if(brokerGateway == null || unitsSize == null)
            throw new NullArgumentException();
        String leverage = brokerGateway.getConnector().getLeverage();
        return  unitsSize.compareTo(BigDecimal.ZERO) !=0 ? unitsSize.divide(new BigDecimal(leverage), 5, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO;
        //brokerGateway.getMarginUsed();
     //   return null; //account.getMarginUsed().bigDecimalValue().add(tradeMargin).setScale(5, BigDecimal.ROUND_HALF_UP);

    }


}
