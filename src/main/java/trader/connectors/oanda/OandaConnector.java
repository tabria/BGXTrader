package trader.connectors.oanda;

import com.oanda.v20.Context;
import com.oanda.v20.ContextBuilder;
import com.oanda.v20.account.*;
import trader.candle.Candlestick;
import trader.connectors.ApiConnector;
import trader.prices.Pricing;

import java.util.List;

public class OandaConnector extends ApiConnector {

    private OandaConfig oandaConfig;
    private OandaAccountValidator oandaAccountValidator ;
    private OandaPriceResponse oandaPriceResponse;
    private OandaCandlesResponse oandaCandlesResponse;
    private Context context;

    public OandaConnector(){
        oandaConfig = new OandaConfig();
        initialize();

    }



    public Pricing getPrice() {
        return oandaPriceResponse.getPrice();
    }

    public List<Candlestick> getInitialCandles() {
        return oandaCandlesResponse.getInitialCandles();
    }

    public Candlestick getUpdateCandle(){
        return oandaCandlesResponse.getUpdateCandle();
    }

    Context getContext(){
        return context;
    }

    AccountID getAccountID(){
        return oandaConfig.getAccountID();
    }

    String getToken(){
        return oandaConfig.getToken();
    }

    String getUrl(){
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

    private void setOandaCandlesResponse() {
        oandaCandlesResponse = new OandaCandlesResponse(this);
    }

    private void validateAccount(){
        oandaAccountValidator.validateAccount();
        oandaAccountValidator.validateAccountBalance();
    }

    private void initialize() {
        setContext();
        setOandaValidator();
        validateAccount();
        setOandaPriceResponse();
        setOandaCandlesResponse();
    }

}
