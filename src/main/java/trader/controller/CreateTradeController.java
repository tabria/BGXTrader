package trader.controller;


import trader.configuration.TradingStrategyConfiguration;
import trader.exception.NullArgumentException;
import trader.requestor.Request;
import trader.requestor.RequestBuilder;
import trader.requestor.UseCase;
import trader.requestor.UseCaseFactory;
import trader.responder.Response;

import java.util.HashMap;

public class CreateTradeController<T> implements TraderController<T> {

    private RequestBuilder requestBuilder;
    private UseCaseFactory useCaseFactory;
    private TradingStrategyConfiguration configuration;


    public CreateTradeController(RequestBuilder requestBuilder, UseCaseFactory useCaseFactory, TradingStrategyConfiguration configuration) {
        if(requestBuilder == null || configuration == null || useCaseFactory == null)
            throw new NullArgumentException();
        this.requestBuilder = requestBuilder;
        this.configuration = configuration;
        this.useCaseFactory = useCaseFactory;
    }

    @Override
    public Response<T> execute(HashMap<String, String> settings) {
        String controllerName = this.getClass().getSimpleName();
        UseCase useCase = useCaseFactory.make(controllerName);
        Request<?> request = requestBuilder.build(controllerName, settings);
        return useCase.execute(request);
    }



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
}
