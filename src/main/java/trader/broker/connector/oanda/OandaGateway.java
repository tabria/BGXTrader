package trader.broker.connector.oanda;

import com.oanda.v20.Context;
import com.oanda.v20.ContextBuilder;
import com.oanda.v20.account.*;
import com.oanda.v20.instrument.InstrumentCandlesResponse;
import com.oanda.v20.order.*;
import com.oanda.v20.pricing.PricingGetResponse;
import com.oanda.v20.trade.TradeSetDependentOrdersResponse;
import trader.broker.connector.*;
import trader.broker.connector.oanda.transformer.*;
import trader.entity.candlestick.Candlestick;
import trader.entity.price.Price;
import trader.entity.trade.BrokerTradeDetails;
import trader.exception.BadRequestException;
import trader.exception.EmptyArgumentException;
import trader.exception.NullArgumentException;
import trader.presenter.Presenter;
import trader.requestor.Request;
import trader.responder.Response;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class OandaGateway extends BaseGateway {

    private static final String ACCOUNT_ID = "accountID";
    private static final String INSTRUMENT = "instrument";
    private static final String PRICE = "price";
    private static final String CANDLE = "candle";
    private static final String TRADE_ID = "tradeID";
    private static final String ORDER_ID = "orderID";
    private static final String CANCEL_ORDER = "cancelOrder";
    private static final String SET_STOP_LOSS_PRICE = "setStopLossPrice";

    private Context context;
    private BrokerConnector connector;
    private Presenter presenter;
    private OandaAccountValidator oandaAccountValidator ;
    private OandaRequestBuilder oandaRequestBuilder;
    private OandaResponseBuilder oandaResponseBuilder;
    private Transformable oandaTransformer;
    private HashMap<String, String> priceSettings;
    private HashMap<String, String> accountSettings;

    private OandaGateway(BrokerConnector connector, Presenter presenter){
        this.connector = connector;
        this.presenter = presenter;
        setContext();
        oandaAccountValidator = new OandaAccountValidator();
        oandaRequestBuilder = new OandaRequestBuilder();
        oandaResponseBuilder = new OandaResponseBuilder(context, connector.getUrl(), presenter);
        oandaTransformer = new OandaTransformer();
        priceSettings = setAccount();
        accountSettings = setAccount();
    }

    @Override
    public BrokerConnector getConnector() {
        return connector;
    }

    @Override
    public BigDecimal getMarginUsed(){
        return getAccount().getMarginUsed().bigDecimalValue();
    }

    @Override
    public BigDecimal getAvailableMargin() {
        return getAccount().getMarginAvailable().bigDecimalValue();
    }

    @Override
    public BigDecimal getBalance() {
        return getAccount().getBalance().bigDecimalValue();
    }

    @Override
    public void validateConnector() {
        oandaAccountValidator.validateAccount(connector, context);
        oandaAccountValidator.validateAccountBalance(connector, context);
    }

    @Override
    public Price getPrice(String instrument) {
        priceSettings.put(INSTRUMENT, instrument);
        Request<?> priceRequest = oandaRequestBuilder.build(PRICE, priceSettings);
        Response<PricingGetResponse> priceResponse = oandaResponseBuilder.buildResponse(PRICE, priceRequest);
        return oandaTransformer.transformToPrice(priceResponse);
    }

    @Override
    public List<Candlestick> getCandles(HashMap<String, String> settings) {
        Request<?> candleRequest = oandaRequestBuilder.build(CANDLE, settings);
        Response<InstrumentCandlesResponse> candlesResponse = oandaResponseBuilder.buildResponse(CANDLE,candleRequest);
        return  oandaTransformer.transformCandlesticks(candlesResponse);
    }

    @Override
    public int totalOpenTradesSize() {
        return getAccount().getTrades().size();
    }

    @Override
    public int totalOpenOrdersSize() {
        return getOrders().size();
    }

    @Override
    public String placeOrder(Map<String, String> settings, String orderType) {
        settings.put(ACCOUNT_ID, getConnector().getAccountID());
        Request<?> marketOrderRequest = oandaRequestBuilder.build(orderType, settings);
        Response<OrderCreateResponse> marketOrderResponse = oandaResponseBuilder.buildResponse(orderType, marketOrderRequest);
        OrderCreateResponse orderResponse = marketOrderResponse.getBody();
        return orderResponse.getOrderCreateTransaction().getId().toString();
    }

    @Override
    public String cancelOrder(String orderID) {
        validateStringInput(orderID);
        HashMap<String, String> settings = setAccount();
        settings.put(ORDER_ID, orderID);
        Request<?> orderCancelRequest = oandaRequestBuilder.build("orderSpecifier", settings);
        Response<OrderCancelResponse> cancelOrderResponse = oandaResponseBuilder.buildResponse("orderSpecifier",orderCancelRequest);
        OrderCancelResponse responseDataStructure = cancelOrderResponse.getBody();
        return responseDataStructure.getLastTransactionID().toString();
    }


    @Override
    public trader.entity.order.Order getOrder(trader.entity.order.enums.OrderType type){
        for (Order order : getOrders()){
            if (order.getType().toString().equals(type.toString()))
                return oandaTransformer.transformOrder(order);
        }
        return null;
    }

    @Override
    public BrokerTradeDetails getTradeDetails(int index){
        try{
            return oandaTransformer.transformTradeSummary(
                    getAccount().getTrades().get(index),
                    getAccount().getOrders()
            );
        } catch(Exception e){
            throw new BadRequestException();
        }
    }

    @Override
    public String setTradeStopLossPrice(String tradeID, String stopLossPrice){
        validateStringInput(tradeID);
        validateStringInput(stopLossPrice);
        HashMap<String, String> settings = setAccount();
        settings.put(TRADE_ID, tradeID);
        settings.put(PRICE, stopLossPrice);
        Request<?> tradeSetDependentOrderRequest = oandaRequestBuilder.build(SET_STOP_LOSS_PRICE, settings);
        Response<TradeSetDependentOrdersResponse> tradeSetDependentOrdersResponse = oandaResponseBuilder.buildResponse(SET_STOP_LOSS_PRICE, tradeSetDependentOrderRequest);

        TradeSetDependentOrdersResponse responseDataStructure = tradeSetDependentOrdersResponse.getBody();
        return responseDataStructure.getLastTransactionID().toString();
    }

    @Override
    public BigDecimal getTradeStopLossPrice(String tradeID){
        validateStringInput(tradeID);
        for (Order order : getOrders()) {
            if (order.getType().equals(OrderType.STOP_LOSS) && order.getId().toString().equals(tradeID) ) {
                StopLossOrder stopLossOrder = (StopLossOrder) order;
                return stopLossOrder.getPrice().bigDecimalValue();
            }
        }
        return BigDecimal.ZERO;
    }

    @Override
    public String toString() {
        return "Gateway: OANDA";
    }

    Context getContext(){
        return context;
    }

    private void validateStringInput(String tradeID) {
        if(tradeID == null)
            throw new NullArgumentException();
        if(tradeID.trim().isEmpty())
            throw new EmptyArgumentException();
    }

    private Account getAccount(){
        Request<?> accountRequest = oandaRequestBuilder.build(ACCOUNT_ID, accountSettings);
        Response<Account> accountResponse = oandaResponseBuilder.buildResponse(ACCOUNT_ID, accountRequest);
        return accountResponse.getBody();
    }

    private void setContext(){
        context = new ContextBuilder(connector.getUrl())
                .setToken(connector.getToken())
                .setApplication("Context")
                .build();
    }

    private HashMap<String,String> setAccount() {
        HashMap<String, String> settings = new HashMap<>();
        settings.put(ACCOUNT_ID, connector.getAccountID());
        return settings;
    }

    private List<Order> getOrders(){
        return  getAccount().getOrders();
    }
}
