package trader.broker.connector.oanda;

import com.oanda.v20.account.AccountID;
import com.oanda.v20.instrument.InstrumentCandlesRequest;
import com.oanda.v20.order.OrderCreateRequest;
import com.oanda.v20.order.OrderSpecifier;
import com.oanda.v20.pricing.PricingGetRequest;
import com.oanda.v20.trade.TradeSetDependentOrdersRequest;
import org.junit.Before;
import org.junit.Test;
import trader.exception.*;
import trader.requestor.Request;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class OandaRequestOLDBuilderTest {

    public static final String ORDER_SPECIFIER = "orderSpecifier";
    private static final String ACCOUNT_ID = "accountID";
    private static final String INSTRUMENT = "instrument";
    private static final String EXPECTED_ACCOUNT_ID = "100";
    private static final String EXPECTED_INSTRUMENT = "EUR_USD";
    private static final String QUANTITY = "quantity";
    private static final String EXPECTED_QUANTITY = "5";
    private static final String GRANULARITY = "granularity";
    private static final String EXPECTED_GRANULARITY = "M30";
    private static final String CREATE_MARKET_IF_TOUCHED_ORDER = "createMarketIfTouchedOrder";


    private OandaRequestBuilder request;
    private HashMap<String, String> settings;

    @Before
    public void setUp() {
        request = new OandaRequestBuilder();
        settings = new HashMap<>();
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreateRequestWithNullInput_Exception(){
        request.build(null, null);
    }

    @Test(expected = NoSuchDataStructureException.class)
    public void WhenCreateRequestWithEmptyType_Exception(){
        settings.put("aa", "aa");
        request.build(" ", settings);
    }

    @Test(expected = EmptyArgumentException.class)
    public void WhenCreateRequestWithEmptySettings_Exception(){
        request.build("price", new HashMap<>());
    }

    @Test
    public void getCorrectPriceRequest(){

        settings.put(ACCOUNT_ID,EXPECTED_ACCOUNT_ID);
        settings.put(INSTRUMENT, EXPECTED_INSTRUMENT);
        Request<?> price = this.request.build("price", settings);
        PricingGetRequest pricingRequest = (PricingGetRequest) price.getBody();

        assertEquals(PricingGetRequest.class, pricingRequest.getClass());
        assertEquals(EXPECTED_ACCOUNT_ID, getRequestAccount(pricingRequest));
        assertEquals(EXPECTED_INSTRUMENT, getRequestInstrument(pricingRequest));
    }

    @Test(expected = BadRequestException.class)
    public void WhenCallBuildForCandlesWithNotExistingGranularity_Exception(){
        settings.put(QUANTITY, "xxx");
        settings.put(INSTRUMENT, "EUR");
        settings.put(GRANULARITY, "M90");
        this.request.build("candle", settings);
    }

    @Test(expected = BadRequestException.class)
    public void WhenCallBuildForCandlesWithLessNotANumberQuantity_Exception(){
        settings.put(QUANTITY, "xxx");
        settings.put(INSTRUMENT, "EUR");
        settings.put(GRANULARITY, "M30");
        this.request.build("candle", settings);
    }

    @Test(expected = OutOfBoundaryException.class)
    public void WhenCallBuildForCandlesWithLessThanZeroQuantity_Exception(){
        settings.put(QUANTITY, "-1");
        settings.put(INSTRUMENT, "EUR");
        settings.put(GRANULARITY, "M30");
        this.request.build("candle", settings);
    }

    @Test
    public void getCorrectCandleRequest(){

        settings.put(QUANTITY, EXPECTED_QUANTITY);
        settings.put(INSTRUMENT, EXPECTED_INSTRUMENT);
        settings.put(GRANULARITY, EXPECTED_GRANULARITY);
        Request<?> candleRequest = this.request.build("candle", settings);
        InstrumentCandlesRequest actualRequest = (InstrumentCandlesRequest) candleRequest.getBody();

        assertEquals(EXPECTED_INSTRUMENT, getCandlesRequestInstrument(actualRequest));

        HashMap<String, Object> queryParams = actualRequest.getQueryParams();
        assertEquals(EXPECTED_QUANTITY, queryParams.get("count").toString());
        assertEquals(EXPECTED_GRANULARITY, queryParams.get(GRANULARITY).toString());
        assertEquals("false",queryParams.get("smooth").toString());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void WhenCallBuildForAccountIDWithCorrectSetting_CorrectResult(){
        String accountId = "17";
        settings.put(ACCOUNT_ID, accountId);
        Request<AccountID> accountIDRequest = (Request<AccountID>) request.build(ACCOUNT_ID, settings);
        AccountID requestDataStructure = accountIDRequest.getBody();

        assertEquals(accountId, requestDataStructure.toString());
    }

    @Test(expected = BadRequestException.class)
    public void WhenCallBuildForCreateMarketIfTouchedOrderWithNotANumberUnitsSize_Exception(){
        settings.put("accountID", "xxx");
        settings.put("instrument", "xxx");
        settings.put("unitsSize", "xxx");
        this.request.build(CREATE_MARKET_IF_TOUCHED_ORDER, settings);
    }

    @Test(expected = BadRequestException.class)
    public void WhenCallBuildForCreateMarketIfTouchedOrderWithNotANumberTradeEntryPrice_Exception(){
        settings.put("accountID", "xxx");
        settings.put("instrument", "xxx");
        settings.put("unitsSize", "xxx");
        settings.put("tradeEntryPrice", "xxx");
        this.request.build(CREATE_MARKET_IF_TOUCHED_ORDER, settings);
    }

    @Test(expected = BadRequestException.class)
    public void WhenCallBuildForCreateMarketIfTouchedOrderWithNotANumberTradeStopLossPrice_Exception(){
        settings.put("accountID", "xxx");
        settings.put("instrument", "xxx");
        settings.put("unitsSize", "xxx");
        settings.put("tradeEntryPrice", "xxx");
        settings.put("tradeStopLossPrice", "xxx");
        this.request.build(CREATE_MARKET_IF_TOUCHED_ORDER, settings);
    }

    @Test
    public void WhenCallBuildForCreateMarketIfTouchedOrderWithCorrectSettings_CorrectResult(){
        String expectedID = "12";
        settings.put("accountID", expectedID);
        settings.put("instrument", "EUR_USD");
        settings.put("unitsSize", "100");
        settings.put("tradeEntryPrice", "1.1200");
        settings.put("tradeStopLossPrice", "1.1980");
        Request<?> marketIfTouchedRequest = this.request.build(CREATE_MARKET_IF_TOUCHED_ORDER, settings);
        OrderCreateRequest request = (OrderCreateRequest) marketIfTouchedRequest.getBody();
        HashMap<String, Object> pathParams = request.getPathParams();
        AccountID accountID = (AccountID) pathParams.get("accountID");

        assertEquals("12", accountID.toString());
    }

    @Test
    public void WhenCallBuildOrderSpecifierRequestWithCorrectSettings_CorrectResult(){
        String accountID = "12";
        String orderID = "13";
        settings.put("accountID", accountID);
        settings.put("orderID", orderID);
        Request<?> orderSpecifierRequest = this.request.build("orderSpecifier", settings);
        List<Object> request = (List<Object>) orderSpecifierRequest.getBody();
        AccountID account = (AccountID) request.get(0);
        OrderSpecifier order = (OrderSpecifier) request.get(1);

        assertEquals(accountID,  account.toString());
        assertEquals(orderID, order.toString());
    }

    @Test
    public void WhenCallBuildForSetStopLossPriceRequestWithCorrectSetting_CorrectResult(){
        String accountID = "12";
        String tradeID = "13";
        settings.put("accountID", accountID);
        settings.put("tradeID", tradeID);
        settings.put("price", "1.234");
        Request<?> setStopLossPriceRequest = this.request.build("setStopLossPrice", settings);
        TradeSetDependentOrdersRequest request = (TradeSetDependentOrdersRequest) setStopLossPriceRequest.getBody();
        HashMap<String, Object> pathParams = request.getPathParams();

        assertEquals(accountID, pathParams.get("accountID").toString());
        assertEquals(tradeID, pathParams.get("tradeSpecifier").toString());

        String a ="";
    }


    private String getRequestAccount(PricingGetRequest request) {
        return request.getPathParams().get(ACCOUNT_ID).toString();
    }

    private String getRequestInstrument(PricingGetRequest request) {
        ArrayList instrumentsList = (ArrayList) request.getQueryParams().get(INSTRUMENT +"s");
        return instrumentsList.get(0).toString();
    }

    private String getCandlesRequestInstrument(InstrumentCandlesRequest request) {
        return request.getPathParams().get(INSTRUMENT).toString();
    }

}
