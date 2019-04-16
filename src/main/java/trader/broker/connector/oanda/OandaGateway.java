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
import trader.requestor.Request;
import trader.responder.Response;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;


public class OandaGateway extends BaseGateway {

    private static final String ACCOUNT_ID = "accountID";
    private static final String INSTRUMENT = "instrument";
    private static final String PRICE = "price";
    private static final String CANDLE = "candle";
    private static final String CREATE_MARKET_IF_TOUCHED_ORDER = "createMarketIfTouchedOrder";
    private static final String TRADE_ID = "tradeID";
    private static final String ORDER_ID = "orderID";
    private static final String CANCEL_ORDER = "cancelOrder";
    private static final String SET_STOP_LOSS_PRICE = "setStopLossPrice";
    private static final String CREATE_MARKET_ORDER = "createMarketOrder";

    private Context context;
    private BrokerConnector connector;
    private OandaAccountValidator oandaAccountValidator ;
    private OandaRequestBuilder oandaRequestBuilder;
    private OandaResponseBuilder oandaResponseBuilder;
    private Transformable oandaTransformer;
    private HashMap<String, String> priceSettings;
    private HashMap<String, String> accountSettings;

    private OandaGateway(BrokerConnector connector){
        this.connector = connector;
        setContext();
        oandaAccountValidator = new OandaAccountValidator();
        oandaRequestBuilder = new OandaRequestBuilder();
        oandaResponseBuilder = new OandaResponseBuilder(context, connector.getUrl());
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
        return getAccount().getOrders().size();
    }

    @Override
    public String placeMarketIfTouchedOrder(HashMap<String, String> settings){
        return placeOrder(settings, CREATE_MARKET_IF_TOUCHED_ORDER);
    }

    @Override
    public String placeMarketOrder(HashMap<String, String> settings){
        return placeOrder(settings, CREATE_MARKET_ORDER);
    }

    @Override
    public trader.entity.order.Order getOrder(trader.entity.order.enums.OrderType type){
        List<Order> orders = getAccount().getOrders();
        for (Order order : orders){
            if (order.getType().toString().equals(type.toString())) {
                MarketIfTouchedOrder orderToTransform = (MarketIfTouchedOrder) order;
                return oandaTransformer.transformOrder(orderToTransform);
            }
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
    public String cancelOrder(String orderID) {
        validateStringInput(orderID);
        HashMap<String, String> settings = setAccount();
        settings.put(ORDER_ID, orderID);
        Request<?> orderCancelRequest = oandaRequestBuilder.build(CANCEL_ORDER, settings);
        Response<OrderCancelResponse> cancelOrderResponse = oandaResponseBuilder.buildResponse("orderSpecifier",orderCancelRequest);
        OrderCancelResponse responseDataStructure = cancelOrderResponse.getBody();
        return responseDataStructure.getLastTransactionID().toString();
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

    private String placeOrder(HashMap<String, String> settings, String createMarketOrder) {
        settings.put(ACCOUNT_ID, getConnector().getAccountID());
        Request<?> marketOrderRequest = oandaRequestBuilder.build(createMarketOrder, settings);
        Response<OrderCreateResponse> marketOrderResponse = oandaResponseBuilder.buildResponse(createMarketOrder, marketOrderRequest);
        OrderCreateResponse orderResponse = marketOrderResponse.getBody();
        return orderResponse.getOrderCreateTransaction().getId().toString();
    }
}
