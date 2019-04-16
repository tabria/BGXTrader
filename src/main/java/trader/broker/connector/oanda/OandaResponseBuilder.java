package trader.broker.connector.oanda;

import com.oanda.v20.Context;
import com.oanda.v20.ExecuteException;
import com.oanda.v20.RequestException;
import com.oanda.v20.account.AccountID;
import com.oanda.v20.instrument.InstrumentCandlesRequest;
import com.oanda.v20.instrument.InstrumentCandlesResponse;
import com.oanda.v20.order.OrderCancelResponse;
import com.oanda.v20.order.OrderCreateRequest;
import com.oanda.v20.order.OrderCreateResponse;
import com.oanda.v20.order.OrderSpecifier;
import com.oanda.v20.pricing.*;
import com.oanda.v20.trade.TradeSetDependentOrdersRequest;
import com.oanda.v20.trade.TradeSetDependentOrdersResponse;
import trader.connection.Connection;
import trader.exception.EmptyArgumentException;
import trader.exception.NullArgumentException;
import trader.interactor.ResponseImpl;
import trader.requestor.Request;
import trader.responder.Response;

import java.util.List;

public class OandaResponseBuilder {

    private static final String CANCEL_ORDER = "cancelOrder";
    private static final String SET_STOP_LOSS_PRICE = "setStopLossPrice";

    private Context context;
    private String url;

    OandaResponseBuilder(Context context, String url){
        verifyInput(context, url);
        this.context = context;
        this.url = url.trim();
    }

    public <T, E> Response<E> buildResponse(String type, Request<T> request) {
        verifyInput(request, type);
        if(type.trim().equalsIgnoreCase("price"))
            return setResponse((E) createPriceResponse(request));
        if(type.trim().equalsIgnoreCase("candle"))
            return setResponse((E) createCandlesResponse(request));
        if(type.trim().equalsIgnoreCase("marketIfTouchedOrder"))
            return setResponse((E) createOrderCreateResponse(request));
        if(type.trim().equalsIgnoreCase("cancelOrder"))
            return setResponse((E) createCancelOrderResponce(request));
        if(type.trim().equalsIgnoreCase("setStopLossPrice"))
            return setResponse((E) createSetStopLossPriceResponse(request));
        return null;
    }

    private <T> TradeSetDependentOrdersResponse createSetStopLossPriceResponse(Request<T> request) {

        TradeSetDependentOrdersRequest requestDataStructure = (TradeSetDependentOrdersRequest) request.getBody();
        try {
            return this.context.trade.setDependentOrders(requestDataStructure);
        } catch (ExecuteException | RequestException e) {
            Connection.waitToConnect(url);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private <T> OrderCancelResponse createCancelOrderResponce(Request<T> request) {
        try {

            List<Object> requestDataStructure = (List<Object>) request.getBody();
            AccountID account = (AccountID) requestDataStructure.get(0);
            OrderSpecifier order = (OrderSpecifier) requestDataStructure.get(1);
            return context.order.cancel(account, order);
        } catch (ExecuteException | RequestException e) {
            Connection.waitToConnect(url);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private <T> OrderCreateResponse createOrderCreateResponse(Request<T> createMarketIfTouchedOrderRequest) {
        try {
            OrderCreateRequest request = (OrderCreateRequest) createMarketIfTouchedOrderRequest.getBody();
            return context.order.create(request);
        } catch (ExecuteException | RequestException e) {
            Connection.waitToConnect(url);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private <T> PricingGetResponse createPriceResponse(Request<T> priceRequest) {
        try {
            PricingGetRequest request = (PricingGetRequest) priceRequest.getBody();
            return context.pricing.get(request);
        } catch (ExecuteException | RequestException e) {
            Connection.waitToConnect(url);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private <T> InstrumentCandlesResponse createCandlesResponse(Request<T> candlesRequest) {
        try{
            InstrumentCandlesRequest request = (InstrumentCandlesRequest) candlesRequest.getBody();
            return context.instrument.candles(request);
        } catch (ExecuteException | RequestException e) {
            Connection.waitToConnect(url);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private void verifyInput(Object object, String str) {
        if(object == null || str == null)
            throw new NullArgumentException();
        if(str.trim().isEmpty())
            throw new EmptyArgumentException();
    }

    private <E> Response<E> setResponse(E responseValue) {
        Response<E> response = new ResponseImpl<>();
        if(responseValue == null)
            return null;
        response.setResponseDataStructure(responseValue);
        return response;
    }
}
