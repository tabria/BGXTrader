package trader.controller;

import trader.broker.BrokerGateway;
import trader.configuration.TradingStrategyConfiguration;
import trader.exception.NullArgumentException;
import trader.responder.Response;

import java.util.Map;

public class AddTradeController<T> implements TraderController<T> {

    private BrokerGateway brokerGateway;
    private TradingStrategyConfiguration configuration;

    public AddTradeController(BrokerGateway brokerGateway, TradingStrategyConfiguration configuration) {
        if(brokerGateway == null || configuration == null)
            throw new NullArgumentException();
        this.brokerGateway = brokerGateway;
        this.configuration = configuration;
    }

    @Override
    public Response<T> execute(Map<String, Object> settings) {


//        TradeImpl newTrade = this.tradeGenerator.generateTrade();
//
//        if (!newTrade.getTradable()) {
//            return;
//        }
//
//        BigDecimal unitsSize = calculateUnitsSize(account, newTrade, bid);
//        BigDecimal availableMargin = account.getMarginAvailable().bigDecimalValue();
//        BigDecimal futureMargin = this.calculateTradeMargin(account, unitsSize);
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

        return null;
    }
}
