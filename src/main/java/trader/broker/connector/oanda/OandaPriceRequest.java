package trader.broker.connector.oanda;

import com.oanda.v20.account.AccountID;
import com.oanda.v20.pricing.PricingGetRequest;
import trader.exception.NullArgumentException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OandaPriceRequest {


    public PricingGetRequest createRequest(HashMap<String, String> settings) {
        if(settings == null || settingNotPresent(settings, "accountID") ||
                               settingNotPresent(settings, "instrument"))
            throw new NullArgumentException();
        List<String> instruments = new ArrayList<>();
        instruments.add(settings.get("instrument"));
        AccountID accountId = new AccountID(settings.get("accountID"));
        return new PricingGetRequest(accountId, instruments);
    }

    private boolean settingNotPresent(HashMap<String, String> settings, String settingName) {
        return !settings.containsKey(settingName) || settings.get(settingName) == null;
    }

}
