package trader.broker.connector.oanda;

import com.oanda.v20.Context;
import com.oanda.v20.ExecuteException;
import com.oanda.v20.RequestException;
import com.oanda.v20.pricing.*;
import trader.connection.Connection;
import trader.exception.NullArgumentException;
import trader.interactor.ResponseImpl;
import trader.requestor.Request;
import trader.responder.Response;

public class OandaPriceResponse {

    OandaPriceResponse(){}

    public <T, E> Response<E> getPriceResponse(Context context, String url, Request<T> priceRequest) {
        if(context == null || priceRequest == null || url == null)
            throw new NullArgumentException();
        try {
            PricingGetRequest request = (PricingGetRequest) priceRequest.getRequestDataStructure();
            PricingGetResponse pricingGetResponse = context.pricing.get(request);
            return setResponse((E) pricingGetResponse);
        } catch (ExecuteException | RequestException e) {
            Connection.waitToConnect(url);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private <E> Response<E> setResponse(E responseValue) {
        Response<E> response = new ResponseImpl<>();
        response.setResponseDataStructure(responseValue);
        return response;
    }
}
