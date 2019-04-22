package trader.broker.connector.oanda;

import com.oanda.v20.account.AccountID;
import com.oanda.v20.instrument.CandlestickGranularity;
import com.oanda.v20.instrument.InstrumentCandlesRequest;
import com.oanda.v20.order.*;
import com.oanda.v20.pricing.PricingGetRequest;
import com.oanda.v20.primitives.InstrumentName;
import com.oanda.v20.trade.TradeSetDependentOrdersRequest;
import com.oanda.v20.trade.TradeSpecifier;
import com.oanda.v20.transaction.StopLossDetails;
import trader.controller.enums.SettingsFieldNames;
import trader.entity.candlestick.candle.CandleGranularity;
import trader.exception.*;
import trader.interactor.RequestImpl;
import trader.requestor.Request;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class OandaRequestBuilder {

    private static final String ACCOUNT_ID = "accountID";
    private static final String INSTRUMENT = SettingsFieldNames.INSTRUMENT.toString();
    private static final String QUANTITY = SettingsFieldNames.QUANTITY.toString();
    private static final String GRANULARITY = SettingsFieldNames.GRANULARITY.toString();
    private static final String UNITS_SIZE = "unitsSize";
    private static final String TRADE_ENTRY_PRICE = "tradeEntryPrice";
    private static final String TRADE_STOP_LOSS_PRICE = "tradeStopLossPrice";
    private static final String ORDER_ID = "orderID";
    private static final String TRADE_ID = "tradeID";

    public Request<?> build(String requestType, Map<String, String> settings) {
        initialInputValidation(requestType, settings);
        if(requestType.trim().equalsIgnoreCase("price"))
            return buildPricingRequest(settings);
        if(requestType.trim().equalsIgnoreCase("candle"))
            return buildCandlesRequest(settings);
        if(requestType.trim().equalsIgnoreCase(ACCOUNT_ID))
            return buildAccountIDRequest(settings);
        if(requestType.trim().equalsIgnoreCase("createMarketIfTouchedOrder"))
            return buildCreateMarketIfTouchedOrderRequest(settings);
        if(requestType.trim().equalsIgnoreCase("marketOrder"))
            return buildCreateMarketOrderRequest(settings);
        if(requestType.trim().equalsIgnoreCase("orderSpecifier"))
            return buildOrderSpecifierRequest(settings);
        if(requestType.trim().equalsIgnoreCase("setStopLossPrice"))
            return buildSetStopLossPriceRequest(settings);

        throw new NoSuchDataStructureException();
    }

    private Request<PricingGetRequest> buildPricingRequest(Map<String, String> settings) {
        List<String> instruments = new ArrayList<>();
        instruments.add(settings.get(INSTRUMENT));
        AccountID accountId = new AccountID(settings.get(ACCOUNT_ID));
        Request<PricingGetRequest> request = new RequestImpl<>();
        request.setBody(new PricingGetRequest(accountId, instruments));
        return request;
    }

    private Request<InstrumentCandlesRequest> buildCandlesRequest(Map<String, String> settings){
        Request<InstrumentCandlesRequest> request = new RequestImpl<>();
        validateGranularity(settings);
        request.setBody(
                new InstrumentCandlesRequest(new InstrumentName(settings.get(INSTRUMENT)))
                        .setCount(parseQuantity(settings))
                        .setGranularity(extractGranularity(settings.get(GRANULARITY).toUpperCase()))
                        .setSmooth(false)
        );
        return request;
    }

    private Request<?> buildAccountIDRequest(Map<String, String> settings) {
        Request<AccountID> accountIDRequest = new RequestImpl<>();
        accountIDRequest.setBody(new AccountID(settings.get(ACCOUNT_ID)));
        return accountIDRequest;
    }

    private Request<OrderCreateRequest> buildCreateMarketIfTouchedOrderRequest(Map<String, String> settings) {
        AccountID accountID = new AccountID(settings.get(ACCOUNT_ID));
        OrderCreateRequest orderCreateRequest = new OrderCreateRequest(accountID)
                .setOrder(createMarketIfTouchedOrderRequest(settings));
        Request<OrderCreateRequest> request = new RequestImpl<>();
        request.setBody(orderCreateRequest);
        return request;
    }

    private Request<OrderCreateRequest> buildCreateMarketOrderRequest(Map<String, String> settings) {
        AccountID accountID = new AccountID(settings.get(ACCOUNT_ID));
        OrderCreateRequest orderCreateRequest = new OrderCreateRequest(accountID)
                .setOrder(createMarketOrderRequest(settings));
        Request<OrderCreateRequest> request = new RequestImpl<>();
        request.setBody(orderCreateRequest);
        return request;
    }

    private OrderRequest createMarketOrderRequest(Map<String, String> settings){
        return  new MarketOrderRequest()
                .setInstrument(settings.get(INSTRUMENT))
                .setUnits(parseStringToDouble(settings.get(UNITS_SIZE)));
    }

    private OrderRequest createMarketIfTouchedOrderRequest(Map<String, String> settings){
        StopLossDetails stopLossDetails = new StopLossDetails()
                .setPrice(parseStringToDouble(settings.get(TRADE_STOP_LOSS_PRICE)));
        return   new MarketIfTouchedOrderRequest()
                .setInstrument(settings.get(INSTRUMENT))
                .setUnits(parseStringToDouble(settings.get(UNITS_SIZE)))
                .setStopLossOnFill(stopLossDetails)
                .setPrice(parseStringToDouble(settings.get(TRADE_ENTRY_PRICE)));
    }

    private Request<List<Object>> buildOrderSpecifierRequest(Map<String,String> settings) {
        Request<List<Object>> request = new RequestImpl<>();
        OrderSpecifier orderSpecifier = new OrderSpecifier(settings.get("orderID"));
        AccountID accountID = new AccountID(settings.get(ACCOUNT_ID));
        List<Object> sets = new ArrayList<>();
        sets.add(accountID);
        sets.add(orderSpecifier);
        request.setBody(sets);
        return request;
    }

    private Request<TradeSetDependentOrdersRequest> buildSetStopLossPriceRequest(Map<String,String> settings) {
        Request<TradeSetDependentOrdersRequest> request = new RequestImpl<>();
        TradeSpecifier tradeSpecifier = new TradeSpecifier(settings.get("tradeID"));
        StopLossDetails stopLossDetails = new StopLossDetails().setPrice(settings.get("price"));
        AccountID accountID = new AccountID(settings.get(ACCOUNT_ID));
        TradeSetDependentOrdersRequest tradeSetDependentOrdersRequest = new TradeSetDependentOrdersRequest(accountID, tradeSpecifier).setStopLoss(stopLossDetails);
        request.setBody(tradeSetDependentOrdersRequest);
        return request;
    }

    private void initialInputValidation(String requestType, Map<String, String> settings) {
        if(requestType == null || settings == null)
            throw new NullArgumentException();
        if(settings.size() == 0)
            throw new EmptyArgumentException();
    }

    private double parseStringToDouble(String str){
        try{
            return  Double.parseDouble(str);
        } catch (RuntimeException e){
            throw new BadRequestException();
        }
    }

    private long parseQuantity(Map<String, String> settings) {
        long candlesQuantity = 0L;
        try{
            candlesQuantity = Long.parseLong(settings.get(QUANTITY));
        } catch (RuntimeException e) {
            throw new BadRequestException();
        }
        checkForNegativeNumber(candlesQuantity);
        return candlesQuantity;
    }

    private void checkForNegativeNumber(long number) {
        if(number < 1)
            throw new OutOfBoundaryException();
    }

    private void validateGranularity(Map<String, String> settings) {
        try{
            CandleGranularity.valueOf(settings.get(GRANULARITY).trim().toUpperCase());
        } catch (RuntimeException e){
            throw new BadRequestException();
        }
    }

    private CandlestickGranularity extractGranularity(String granularity) {
        return CandlestickGranularity.valueOf(granularity);
    }
}
