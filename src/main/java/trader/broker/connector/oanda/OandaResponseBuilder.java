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
import trader.exception.NoSuchDataStructureException;
import trader.exception.NullArgumentException;
import trader.interactor.ResponseImpl;
import trader.presenter.Presenter;
import trader.requestor.Request;
import trader.responder.Response;

import java.util.List;

public class OandaResponseBuilder {

    private Context context;
    private String url;
    private Presenter presenter;

    OandaResponseBuilder(Context context, String url, Presenter presenter){
        verifyInput(context, url);
        this.context = context;
        this.url = url.trim();
        setPresenter(presenter);
    }

    public <T, E> Response<E> buildResponse(String type, Request<T> request) {
        verifyInput(request, type);
        if(type.trim().equalsIgnoreCase("accountid"))
            return setResponse((E) createAccountResponse(request));
        if(type.trim().equalsIgnoreCase("price"))
            return setResponse((E) createPriceResponse(request));
        if(type.trim().equalsIgnoreCase("candle"))
            return setResponse((E) createCandlesResponse(request));
        if(type.trim().equalsIgnoreCase("marketIfTouchedOrder") || type.trim().equalsIgnoreCase("marketOrder"))
            return setResponse((E) createOrderCreateResponse(request));
        if(type.trim().equalsIgnoreCase("cancelOrder"))
            return setResponse((E) createCancelOrderResponse(request));
        if(type.trim().equalsIgnoreCase("setStopLossPrice"))
            return setResponse((E) createSetStopLossPriceResponse(request));
        throw new NoSuchDataStructureException();
    }

    private <T> Object createAccountResponse(Request<T> request) {
        try{
            AccountID accountID = (AccountID) request.getBody();
            return context.account.get(accountID).getAccount();
        } catch (ExecuteException | RequestException e) {
            Connection.waitToConnect(url, presenter);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private <T> TradeSetDependentOrdersResponse createSetStopLossPriceResponse(Request<T> request) {
        TradeSetDependentOrdersRequest requestDataStructure = (TradeSetDependentOrdersRequest) request.getBody();
        try {
            return this.context.trade.setDependentOrders(requestDataStructure);
        } catch (ExecuteException | RequestException e) {
            Connection.waitToConnect(url, presenter);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private <T> OrderCancelResponse createCancelOrderResponse(Request<T> request) {
        try {

            List<Object> requestDataStructure = (List<Object>) request.getBody();
            AccountID account = (AccountID) requestDataStructure.get(0);
            OrderSpecifier order = (OrderSpecifier) requestDataStructure.get(1);
            return context.order.cancel(account, order);
        } catch (ExecuteException | RequestException e) {
            Connection.waitToConnect(url, presenter);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private <T> OrderCreateResponse createOrderCreateResponse(Request<T> createOrderRequest) {
        try {
            OrderCreateRequest request = (OrderCreateRequest) createOrderRequest.getBody();
            return context.order.create(request);
        } catch (ExecuteException | RequestException e) {
            Connection.waitToConnect(url, presenter);
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
            Connection.waitToConnect(url, presenter);
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
            Connection.waitToConnect(url, presenter);
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

    public void setPresenter(Presenter presenter) {
        if(presenter == null)
            throw new NullArgumentException();
        this.presenter = presenter;
    }

    private <E> Response<E> setResponse(E responseValue) {
        Response<E> response = new ResponseImpl<>();
        if(responseValue == null)
            return null;
        response.setBody(responseValue);
        return response;
    }
}
