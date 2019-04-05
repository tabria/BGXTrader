package trader.broker.connector.oanda;

import com.oanda.v20.account.AccountID;
import com.oanda.v20.pricing.PricingGetRequest;
import trader.exception.NullArgumentException;

import java.util.ArrayList;
import java.util.List;

class OandaPriceRequest {

    PricingGetRequest getPriceRequest(String accountID, String instrument) {
        if(accountID == null || instrument == null)
            throw new NullArgumentException();
        List<String> instruments = new ArrayList<>();
        instruments.add(instrument);
        AccountID accountId = new AccountID(accountID);
        return new PricingGetRequest(accountId, instruments);
    }
}
