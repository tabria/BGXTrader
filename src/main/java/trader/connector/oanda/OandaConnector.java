package trader.connector.oanda;

import com.oanda.v20.Context;
import com.oanda.v20.ContextBuilder;
import com.oanda.v20.ExecuteException;
import com.oanda.v20.RequestException;
import com.oanda.v20.account.*;
import trader.candlestick.Candlestick;
import trader.connector.BaseConnector;
import trader.order.Order;
import trader.price.Pricing;
import trader.trade.entitie.Trade;

import java.util.List;

import static trader.connector.oanda.OandaConfig.*;

public class OandaConnector extends BaseConnector {

    List<AccountProperties> accountProperties;

    private OandaAccountValidator oandaAccountValidator ;

    private OandaPriceResponse oandaPriceResponse;
    private OandaCandlesResponse oandaCandlesResponse;
    private Context context;

    public OandaConnector(){

        initialize();
        try {
            accountProperties = context.account.list().getAccounts();
        } catch (RequestException | ExecuteException e) {
            e.printStackTrace();
        }

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
    public Candlestick updateCandle(){
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

//    AccountID getAccountID(){
//        return oandaConfig.getAccountID();
//    }
//
//    String getToken(){
//        return oandaConfig.getToken();
//    }
//
//    String getUrl(){
//        return oandaConfig.getUrl();
//    }



    private void setContext(){
        context = new ContextBuilder(URL)
                .setToken(TOKEN)
                .setApplication("Context")
                .build();
    }

    private void setOandaValidator() {
        oandaAccountValidator = new OandaAccountValidator();
    }

    private void setOandaPriceResponse() {
        oandaPriceResponse = new OandaPriceResponse(this);
    }

    private void setOandaCandlesResponse() {
        oandaCandlesResponse = new OandaCandlesResponse(this);
    }

    private void validateAccount(){
        oandaAccountValidator.validateAccount(this);
        oandaAccountValidator.validateAccountBalance(this);
    }

    private void initialize() {
        setContext();
        setOandaValidator();
        validateAccount();
        setOandaPriceResponse();
        setOandaCandlesResponse();
    }

}
