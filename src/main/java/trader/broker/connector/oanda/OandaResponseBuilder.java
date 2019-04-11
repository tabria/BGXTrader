package trader.broker.connector.oanda;

import com.oanda.v20.Context;
import com.oanda.v20.ExecuteException;
import com.oanda.v20.RequestException;
import com.oanda.v20.instrument.InstrumentCandlesRequest;
import com.oanda.v20.instrument.InstrumentCandlesResponse;
import com.oanda.v20.order.OrderCreateRequest;
import com.oanda.v20.order.OrderCreateResponse;
import com.oanda.v20.pricing.*;
import com.oanda.v20.primitives.DateTime;
import com.oanda.v20.transaction.TransactionID;
import trader.connection.Connection;
import trader.exception.EmptyArgumentException;
import trader.exception.NullArgumentException;
import trader.interactor.ResponseImpl;
import trader.requestor.Request;
import trader.responder.Response;

public class OandaResponseBuilder {

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
        return null;
    }

    private <T> OrderCreateResponse createOrderCreateResponse(Request<T> createMarketIfTouchedOrderRequest) {
        try {
            OrderCreateRequest request = (OrderCreateRequest) createMarketIfTouchedOrderRequest.getRequestDataStructure();
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
            PricingGetRequest request = (PricingGetRequest) priceRequest.getRequestDataStructure();
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
            InstrumentCandlesRequest request = (InstrumentCandlesRequest) candlesRequest.getRequestDataStructure();
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
