package trader.connectors.oanda;

import com.oanda.v20.Context;
import com.oanda.v20.ContextBuilder;
import com.oanda.v20.account.*;
import com.oanda.v20.primitives.InstrumentName;
import trader.connectors.ApiConnectors;

public class OandaConnector extends ApiConnectors {

    private final String url = "https://api-fxtrade.oanda.com";
    private final String token = "7fc3b8a323e95d1c0f35b3e12dfb0a29-da034ca81b764fe499946a2e4b092f12";
    private final AccountID accountID = new AccountID("001-004-1942536-001");
    private final InstrumentName INSTRUMENT  = new InstrumentName("EUR_USD");

    private OandaAccountValidator oandaAccountValidator ;
    private Context context;


    private OandaConnector(){
        setContext();
        setOandaValidator();
        validateAccount();
    }

    public Context getContext(){
        return context;
    }

    public AccountID getAccountID(){
        return accountID;
    }

    public String getUrl(){
        return url;
    }

    private void setContext(){
        context = new ContextBuilder(url)
                .setToken(token)
                .setApplication("Context")
                .build();
    }

    private void setOandaValidator() {
        oandaAccountValidator = new OandaAccountValidator(this);
    }

    private void validateAccount(){
        oandaAccountValidator.validateAccount();
        oandaAccountValidator.validateAccountBalance();
    }

}
