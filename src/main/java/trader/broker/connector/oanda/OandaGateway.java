package trader.broker.connector.oanda;

import com.oanda.v20.Context;
import com.oanda.v20.ContextBuilder;
import com.oanda.v20.account.*;
import com.oanda.v20.instrument.InstrumentCandlesResponse;
import com.oanda.v20.pricing.PricingGetResponse;
import trader.broker.connector.*;
import trader.entity.candlestick.Candlestick;
import trader.price.Price;
import trader.requestor.Request;
import trader.responder.Response;

import java.util.HashMap;
import java.util.List;


public class OandaGateway extends BaseGateway {

    private static final String ACCOUNT_ID = "accountID";
    private static final String INSTRUMENT = "instrument";
    private static final String PRICE = "price";
    private static final String CANDLE = "candle";

    private Context context;
    private BrokerConnector connector;
    private OandaAccountValidator oandaAccountValidator ;
    private OandaRequestBuilder oandaRequestBuilder;
    private OandaResponseBuilder oandaResponseBuilder;
    private PriceTransformable oandaPriceTransformer;
    private CandlestickTransformable oandaCandlesTransformer;
    private HashMap<String, String> priceSettings;



    List<AccountProperties> accountProperties;


    private OandaGateway(BrokerConnector connector){
        this.connector = connector;
        oandaAccountValidator = new OandaAccountValidator();
        oandaRequestBuilder = new OandaRequestBuilder();
        oandaResponseBuilder = new OandaResponseBuilder();
        //to be tested
        oandaPriceTransformer = new OandaPriceTransformer();
        oandaCandlesTransformer = new OandaCandleTransformer();
        priceSettings = setPriceSettings();

//        initialize();
//        try {
//            accountProperties = context.account.list().getAccounts();
//        } catch (RequestException | ExecuteException e) {
//            e.printStackTrace();
//        }

    }

    @Override
    public Price getPrice(String instrument) {
        priceSettings.put(INSTRUMENT, instrument);
        Request<?> priceRequest = oandaRequestBuilder.build(PRICE, priceSettings);
        Response<PricingGetResponse> priceResponse = oandaResponseBuilder.buildResponse(PRICE, context, connector.getUrl(), priceRequest);
        return oandaPriceTransformer.transformToPrice(priceResponse);
    }

    //to be tested
    @Override
    public List<Candlestick> getCandles(HashMap<String, String> settings) {
        Request<?> candleRequest = oandaRequestBuilder.build(CANDLE, settings);
        Response<InstrumentCandlesResponse> candlesResponse = oandaResponseBuilder.buildResponse(CANDLE, context, connector.getUrl(),candleRequest);
        return oandaCandlesTransformer.transformCandlesticks(candlesResponse);
    }

    @Override
    public void validateConnector() {
        setContext();
        oandaAccountValidator.validateAccount(connector, context);
        oandaAccountValidator.validateAccountBalance(connector, context);
    }

    Context getContext(){
        return context;
    }

//    private void validateInputFileLocation(String fileLocation) {
//        validateInput(fileLocation);
//        if(isNotYamlFile(fileLocation))
//            throw new BadRequestException();
//    }
//
//    private void validateInput(String input) {
//        if(input == null)
//            throw new NullArgumentException();
//        if(input.isEmpty())
//            throw new EmptyArgumentException();
//    }
//
//    private boolean isNotYamlFile(String fileLocation) {
//        if(fileLocation.contains(".yaml"))
//            return false;
//        return !fileLocation.contains(".yml");
//    }

    private void setContext(){
        context = new ContextBuilder(connector.getUrl())
                .setToken(connector.getToken())
                .setApplication("Context")
                .build();
    }

    private HashMap<String,String> setPriceSettings() {
        HashMap<String, String> settings = new HashMap<>();
        settings.put(ACCOUNT_ID, connector.getAccountID());
        return settings;
    }



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

//
//    private void setOandaValidator() {
//        oandaAccountValidator = new OandaAccountValidator();
//    }
//
//    private void setOandaPriceResponse() {
//        oandaResponseBuilder = new OandaResponseBuilder(this);
//    }
//
//    private void setOandaCandlesResponse() {
//        oandaCandlesResponse = new OandaCandlesResponse(this);
//    }
//
//
//    private void initialize() {
//        setContext();
//        setOandaValidator();
//        validateAccount();
//        setOandaPriceResponse();
//        setOandaCandlesResponse();
//    }

}
