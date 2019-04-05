package trader.broker.connector.oanda;

import com.oanda.v20.Context;
import com.oanda.v20.ExecuteException;
import com.oanda.v20.RequestException;
import com.oanda.v20.pricing.*;
import trader.connection.Connection;
import trader.exception.NullArgumentException;
import trader.requestor.Request;

public class OandaPriceResponse {

    OandaPriceResponse(){}

    public PricingGetResponse getPriceResponse(Context context, String url, Request<T> priceRequest) {
        if(context == null || priceRequest == null || url == null)
            throw new NullArgumentException();

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
}
