package trader.broker.connector.oanda;

import com.oanda.v20.Context;
import com.oanda.v20.ContextBuilder;
import com.oanda.v20.account.*;
import com.oanda.v20.instrument.InstrumentCandlesResponse;
import com.oanda.v20.order.*;
import com.oanda.v20.pricing.PricingGetResponse;
import trader.broker.connector.*;
import trader.entity.candlestick.Candlestick;
import trader.price.Price;
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
    private static final String NON_EXISTING_MARKET_IF_TOUCHED_ORDER_ID = "0";

    private Context context;
    private BrokerConnector connector;
    private OandaAccountValidator oandaAccountValidator ;
    private OandaRequestBuilder oandaRequestBuilder;
    private OandaResponseBuilder oandaResponseBuilder;
    private PriceTransformable oandaPriceTransformer;
    private CandlestickTransformable oandaCandlesTransformer;
    private HashMap<String, String> priceSettings;
    private HashMap<String, String> accountSettings;

    private OandaGateway(BrokerConnector connector){
        this.connector = connector;
        setContext();
        oandaAccountValidator = new OandaAccountValidator();
        oandaRequestBuilder = new OandaRequestBuilder();
        oandaResponseBuilder = new OandaResponseBuilder(context, connector.getUrl());
        oandaPriceTransformer = new OandaPriceTransformer();
        oandaCandlesTransformer = new OandaCandleTransformer();
        priceSettings = setAccount();
        accountSettings = setAccount();
    }

    @Override
    public Price getPrice(String instrument) {
        priceSettings.put(INSTRUMENT, instrument);
        Request<?> priceRequest = oandaRequestBuilder.build(PRICE, priceSettings);
        Response<PricingGetResponse> priceResponse = oandaResponseBuilder.buildResponse(PRICE, priceRequest);
        return oandaPriceTransformer.transformToPrice(priceResponse);
    }

    @Override
    public List<Candlestick> getCandles(HashMap<String, String> settings) {
        Request<?> candleRequest = oandaRequestBuilder.build(CANDLE, settings);
        Response<InstrumentCandlesResponse> candlesResponse = oandaResponseBuilder.buildResponse(CANDLE,candleRequest);
        return oandaCandlesTransformer.transformCandlesticks(candlesResponse);
    }

    @Override
    public String placeMarketIfTouchedOrder(HashMap<String, String> settings){
        settings.put(ACCOUNT_ID, getConnector().getAccountID());
        Request<?> marketIfTouchedOrderRequest = oandaRequestBuilder.build(CREATE_MARKET_IF_TOUCHED_ORDER, settings);
        Response<OrderCreateResponse> marketIfTouchedOrderResponse = oandaResponseBuilder.buildResponse(CREATE_MARKET_IF_TOUCHED_ORDER, marketIfTouchedOrderRequest);
        OrderCreateResponse orderResponse =marketIfTouchedOrderResponse.getResponseDataStructure();
        return orderResponse.getOrderCreateTransaction().getId().toString();
    }

    @Override
    public String getNotFilledOrderID(){
        List<Order> orders = getAccount().getOrders();
        for (Order order : orders){
            if (order.getType().equals(OrderType.MARKET_IF_TOUCHED))
                return order.getId().toString();
        }
        return NON_EXISTING_MARKET_IF_TOUCHED_ORDER_ID;
    }

    @Override
    public void validateConnector() {
        oandaAccountValidator.validateAccount(connector, context);
        oandaAccountValidator.validateAccountBalance(connector, context);
    }

    @Override
    public int totalOpenTradesSize() {
        Account account = getAccount();
        return account.getTrades().size();
    }

    @Override
    public int totalOpenOrdersSize() {
        Account account = getAccount();
        return account.getOrders().size();
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
    public BrokerConnector getConnector() {
        return connector;
    }

    Context getContext(){
        return context;
    }

    private Account getAccount(){
        Request<?> accountRequest = oandaRequestBuilder.build(ACCOUNT_ID, accountSettings);
        Response<Account> accountResponse = oandaResponseBuilder.buildResponse(ACCOUNT_ID, accountRequest);
        return accountResponse.getResponseDataStructure();
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



//    @Override
//    public List<Candlestick> getInitialCandles() {
//        return oandaCandlesResponse.getInitialCandles();
//    }
//
//    @Override
//    public Candlestick updateCandle(){
//        return oandaCandlesResponse.getUpdateCandle();
//    }
//
//    @Override
//    public List<Order> getOpenOrders() {
//        return null;
//    }
//
//
//    @Override
//    public List<TradeImpl> getOpenTrades() {
//        return null;
//    }


//    AccountID getAccountID(){
//        return oandaConfig.getAccountID();
//    }
//
//    String getToken(){
//        return oandaConfig.getToken();
//    }
//
//    String getUrl(){
//        return oandaConfig.getUrl();
//    }

//
//    private void setOandaValidator() {
//        oandaAccountValidator = new OandaAccountValidator();
//    }
//
//    private void setOandaPriceResponse() {
//        oandaResponseBuilder = new OandaResponseBuilder(this);
//    }
//
//    private void setOandaCandlesResponse() {
//        oandaCandlesResponse = new OandaCandlesResponse(this);
//    }
//
//
//    private void initialize() {
//        setContext();
//        setOandaValidator();
//        validateAccount();
//        setOandaPriceResponse();
//        setOandaCandlesResponse();
//    }

}
