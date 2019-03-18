package trader.connectors.oanda;

import com.oanda.v20.Context;
import com.oanda.v20.ContextBuilder;
import com.oanda.v20.account.*;
import trader.connectors.ApiConnector;
import trader.prices.Pricing;

public class OandaConnector extends ApiConnector {

    private OandaConfig oandaConfig;
    private OandaAccountValidator oandaAccountValidator ;
    private OandaPriceResponse oandaPriceResponse;
    private Context context;

    private OandaConnector(){
        oandaConfig = new OandaConfig();
        setContext();
        setOandaValidator();
        validateAccount();
        setOandaPriceResponse();

    }

    @Override
    public Pricing getPrice() {
        return oandaPriceResponse.getPrice();
    }

    public Context getContext(){
        return context;
    }

    public AccountID getAccountID(){
        return oandaConfig.getAccountID();
    }

    public String getToken(){
        return oandaConfig.getToken();
    }

    public String getUrl(){
        return oandaConfig.getUrl();
    }



    private void setContext(){
        context = new ContextBuilder(oandaConfig.getUrl())
                .setToken(oandaConfig.getToken())
                .setApplication("Context")
                .build();
    }

    private void setOandaValidator() {
        oandaAccountValidator = new OandaAccountValidator(this);
    }

    private void setOandaPriceResponse() {
        oandaPriceResponse = new OandaPriceResponse(this);
    }

    private void validateAccount(){
        oandaAccountValidator.validateAccount();
        oandaAccountValidator.validateAccountBalance();
    }
}
