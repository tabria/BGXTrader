package trader.connector.oanda;

import com.oanda.v20.Context;
import com.oanda.v20.ContextBuilder;
import com.oanda.v20.account.*;
import trader.candlestick.Candlestick;
import trader.connector.BaseConnector;
import trader.order.Order;
import trader.price.Pricing;
import trader.trade.entitie.Trade;

import java.util.List;

public class OandaConnector extends BaseConnector {

    private OandaConfig oandaConfig;
    private OandaAccountValidator oandaAccountValidator ;
    private OandaPriceResponse oandaPriceResponse;
    private OandaCandlesResponse oandaCandlesResponse;
    private Context context;

    public OandaConnector(){
        oandaConfig = new OandaConfig();
        initialize();

    }

    @Override
    public Pricing getPrice() {
        return oandaPriceResponse.getPrice();
    }

    @Override
    public List<Candlestick> getInitialCandles() {
        return oandaCandlesResponse.getInitialCandles();
    }

    @Override
    public Candlestick getUpdatedCandle(){
        return oandaCandlesResponse.getUpdateCandle();
    }

    @Override
    public List<Order> getOpenOrders() {
        return null;
    }

    @Override
    public List<Trade> getOpenTrades() {
        return null;
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
