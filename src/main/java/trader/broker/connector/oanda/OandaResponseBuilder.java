package trader.broker.connector.oanda;

import com.oanda.v20.Context;
import com.oanda.v20.ExecuteException;
import com.oanda.v20.RequestException;
import com.oanda.v20.instrument.InstrumentCandlesRequest;
import com.oanda.v20.instrument.InstrumentCandlesResponse;
import com.oanda.v20.pricing.*;
import trader.connection.Connection;
import trader.exception.EmptyArgumentException;
import trader.exception.NullArgumentException;
import trader.interactor.ResponseImpl;
import trader.requestor.Request;
import trader.responder.Response;

public class OandaResponseBuilder {

    OandaResponseBuilder(){}

    public <T, E> Response<E> buildResponse(String type, Context context, String url, Request<T> request) {
        verifyInput(type, context, url, request);
        if(type.trim().equalsIgnoreCase("price"))
            return setResponse((E) createPriceResponse(context, url, request));
        if(type.trim().equalsIgnoreCase("candle"))
            return setResponse((E) createCandlesResponse(context, url, request));
        return null;
    }

    private <T> PricingGetResponse createPriceResponse(Context context, String url, Request<T> priceRequest) {
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

    private <T> InstrumentCandlesResponse createCandlesResponse(Context context, String url, Request<T> candlesRequest) {
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

    private <T> void verifyInput(String type, Context context, String url, Request<T> priceRequest) {
        if(type == null || context == null || priceRequest == null || url == null)
            throw new NullArgumentException();
        if(type.trim().isEmpty() || url.trim().isEmpty())
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
