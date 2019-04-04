package trader.broker.connector.oanda;

import com.oanda.v20.Context;
import com.oanda.v20.ContextBuilder;
import com.oanda.v20.account.*;
import trader.broker.connector.BaseConnector;
import trader.exception.BadRequestException;
import trader.exception.EmptyArgumentException;
import trader.exception.NullArgumentException;

import java.util.List;


public class OandaConnector extends BaseConnector {

    private String fileLocation;
    private String url;
    private String token;
    private String accountID;
    private String instrument;
    private Context context;
    private OandaAccountValidator oandaAccountValidator ;
    private OandaPriceResponse oandaPriceResponse;
    private OandaCandlesResponse oandaCandlesResponse;


    List<AccountProperties> accountProperties;


    public OandaConnector(){
        oandaAccountValidator = new OandaAccountValidator();

//        initialize();
//        try {
//            accountProperties = context.account.list().getAccounts();
//        } catch (RequestException | ExecuteException e) {
//            e.printStackTrace();
//        }

    }

    @Override
    public String getFileLocation() {
        return fileLocation;
    }

    @Override
    public void setFileLocation(String fileLocation) {
        validateInputFileLocation(fileLocation);
        this.fileLocation = fileLocation.trim();
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public void setUrl(String url) {
        validateInput(url);
        this.url = url.trim();
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public void setToken(String token) {
        validateInput(token);
        this.token = token.trim();
    }

    @Override
    public String getAccountID() {
        return accountID;
    }

    @Override
    public void setAccountID(String accountId) {
        validateInput(accountId);
        this.accountID = accountId.trim();
    }

    @Override
    public String getInstrument() {
        return instrument;
    }

    @Override
    public void setInstrument(String instrument) {
        validateInput(instrument);
        this.instrument = instrument.trim();
    }

    private void validateInputFileLocation(String fileLocation) {
        validateInput(fileLocation);
        if(isNotYamlFile(fileLocation))
            throw new BadRequestException();
    }

    private void validateInput(String input) {
        if(input == null)
            throw new NullArgumentException();
        if(input.isEmpty())
            throw new EmptyArgumentException();
    }

    private boolean isNotYamlFile(String fileLocation) {
        if(fileLocation.contains(".yaml"))
            return false;
        return !fileLocation.contains(".yml");
    }


//    @Override
//    public Pricing getPrice() {
//        return oandaPriceResponse.getPrice();
//    }
//
//    @Override
//    public List<Candlestick> getInitialCandles() {
//        return oandaCandlesResponse.getInitialCandles();
//    }
//
//    @Override
//    public Candlestick updateCandle(){
//        return oandaCandlesResponse.getUpdateCandle();
//    }
//
//    @Override
//    public List<Order> getOpenOrders() {
//        return null;
//    }
//
//    @Override
//    public List<Trade> getOpenTrades() {
//        return null;
//    }

//    Context getContext(){
//        return context;
//    }

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
        context = new ContextBuilder(url)
                .setToken(token)
                .setApplication("Context")
                .build();
    }
//
//    private void setOandaValidator() {
//        oandaAccountValidator = new OandaAccountValidator();
//    }
//
//    private void setOandaPriceResponse() {
//        oandaPriceResponse = new OandaPriceResponse(this);
//    }
//
//    private void setOandaCandlesResponse() {
//        oandaCandlesResponse = new OandaCandlesResponse(this);
//    }
//
//    private void validateAccount(){
//        oandaAccountValidator.validateAccount(this);
//        oandaAccountValidator.validateAccountBalance(this);
//    }
//
//    private void initialize() {
//        setContext();
//        setOandaValidator();
//        validateAccount();
//        setOandaPriceResponse();
//        setOandaCandlesResponse();
//    }

}
