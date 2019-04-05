package trader.broker.connector.oanda;

import com.oanda.v20.instrument.InstrumentCandlesRequest;
import com.oanda.v20.pricing.PricingGetRequest;
import org.junit.Before;
import org.junit.Test;
import trader.exception.*;
import trader.requestor.Request;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.assertEquals;


public class OandaRequestBuilderTest {

    private static final String ACCOUNT_ID = "accountID";
    private static final String INSTRUMENT = "instrument";
    private static final String EXPECTED_ACCOUNT_ID = "100";
    private static final String EXPECTED_INSTRUMENT = "EUR_USD";
    private static final String QUANTITY = "quantity";
    private static final String EXPECTED_QUANTITY = "5";
    public static final String GRANULARITY = "granularity";
    public static final String EXPECTED_GRANULARITY = "M30";

    private OandaRequestBuilder request;
    private HashMap<String, String> settings;

    @Before
    public void setUp() {
        request = new OandaRequestBuilder();
        settings = new HashMap<>();
    }

    ///////////////////
    @Test(expected = NullArgumentException.class)
    public void WhenCreateRequestWithNullInput_Exception(){
        request.build(null, null);
    }

    @Test(expected = EmptyArgumentException.class)
    public void WhenCreateRequestWithEmptySettings_Exception(){
        request.build("price", new HashMap<>());
    }

    @Test(expected = BadRequestException.class)
    public void WhenCreatingPriceRequestOnlyWithAccountID_Exception(){
        settings.put("accountID", "xxx");
        request.build("price", settings);
    }

    @Test(expected = BadRequestException.class)
    public void WhenCreatingPriceRequestOnlyWithInstrument_Exception(){
        settings.put("instrument", "EUR");
        request.build("price", settings);
    }


    @Test(expected = NullArgumentException.class)
    public void WhenCreatingPriceRequestWithNullAccountID_Exception(){
        settings.put("accountID", null);
        settings.put("instrument", "EUR");
        request.build("price", settings);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreatingPriceRequestWithNullInstrument_Exception(){
        settings.put("accountID", "xxx");
        settings.put("instrument", null);
        request.build("price", settings);
    }

    @Test(expected = EmptyArgumentException.class)
    public void WhenCreatingPriceRequestWithEmptyAccountID_Exception(){
        settings.put("accountID", " ");
        settings.put("instrument", "EUR");
        request.build("price", settings);
    }

    @Test(expected = EmptyArgumentException.class)
    public void WhenCreatingPriceRequestWithEmptyInstrument_Exception(){
        settings.put("accountID", "xxx");
        settings.put("instrument", "  ");
        request.build("price", settings);
    }

    @Test(expected = BadRequestException.class)
    public void WhenCreatingPriceRequestWithWrongAccountIDKeyName_Exception(){
        settings.put("accot", "xxx");
        settings.put("instrument", "EUR");
        request.build("price", settings);
    }

    @Test(expected = BadRequestException.class)
    public void WhenCreatingPriceRequestWithWrongInstrumentKeyName_Exception(){
        settings.put("accountID", "xxx");
        settings.put("ind", "EUR");
        request.build("price", settings);
    }

    @Test
    public void getCorrectPriceRequest(){

        settings.put("accountID",EXPECTED_ACCOUNT_ID);
        settings.put("instrument", EXPECTED_INSTRUMENT);
        Request<?> price = this.request.build("price", settings);
        PricingGetRequest pricingRequest = (PricingGetRequest) price.getRequestDataStructure();

        assertEquals(PricingGetRequest.class, pricingRequest.getClass());
        assertEquals(EXPECTED_ACCOUNT_ID, getRequestAccount(pricingRequest));
        assertEquals(EXPECTED_INSTRUMENT, getRequestInstrument(pricingRequest));
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallBuildCandleWithNullQuantity_Exception(){
        settings.put(QUANTITY, null);
        request.build("candle", settings);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallBuildCandleWithNullInstrument_Exception(){
        settings.put(QUANTITY, "12");
        settings.put(INSTRUMENT, null);
        settings.put(GRANULARITY, "M30");
        request.build("candle", settings);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallBuildCandleWithNullGranularity_Exception(){
        settings.put(QUANTITY, "12");
        settings.put(INSTRUMENT, "EUR");
        settings.put(GRANULARITY, null);
        request.build("candle", settings);
    }

    @Test(expected = EmptyArgumentException.class)
    public void WhenCallBuildForCandlesWithEmptyQuantity_Exception(){
        settings.put(QUANTITY, " ");
        settings.put(INSTRUMENT, "EUR");
        settings.put(GRANULARITY, "M30");
        request.build("candle", settings);
    }

    @Test(expected = EmptyArgumentException.class)
    public void WhenCallBuildForCandlesWithEmptyInstrument_Exception(){
        settings.put(QUANTITY, "12");
        settings.put(INSTRUMENT, " ");
        settings.put(GRANULARITY, "M30");
        request.build("candle", settings);
    }

    @Test(expected = EmptyArgumentException.class)
    public void WhenCallBuildForCandlesWithEmptygranularity_Exception(){
        settings.put(QUANTITY, "12");
        settings.put(INSTRUMENT, "EUR");
        settings.put(GRANULARITY, "  ");
        request.build("candle", settings);
    }

    @Test(expected = BadRequestException.class)
    public void WhenCallBuildForCandlesWithWrongQuantityKeyName_Exception(){
        settings.put("quan", "12");
        settings.put(INSTRUMENT, "EUR");
        settings.put(GRANULARITY, "M30");
        request.build("candle", settings);
    }

    @Test(expected = BadRequestException.class)
    public void WhenCallBuildForCandlesWithWrongInstrumentKeyName_Exception(){
        settings.put(QUANTITY, "12");
        settings.put("ins", "EUR");
        settings.put(GRANULARITY, "M30");
        request.build("candle", settings);
    }

    @Test(expected = BadRequestException.class)
    public void WhenCallBuildForCandlesWithWrongGranularityKeyName_Exception(){
        settings.put(QUANTITY, "12");
        settings.put(INSTRUMENT, "EUR");
        settings.put("gra", "M30");
        request.build("candle", settings);
    }

    @Test(expected = OutOfBoundaryException.class)
    public void WhenCallBuildForCandlesWithLessThanZeroQuantity_Exception(){
        settings.put(QUANTITY, "-1");
        settings.put(INSTRUMENT, "EUR");
        settings.put(GRANULARITY, "M30");
        this.request.build("candle", settings);
    }

    @Test(expected = BadRequestException.class)
    public void WhenCallBuildForCandlesWithNotExistingGranularity_Exception(){
        settings.put(QUANTITY, "12");
        settings.put(INSTRUMENT, "EUR");
        settings.put(GRANULARITY, "k3");
        this.request.build("candle", settings);
    }

    @Test
    public void WhenCallBuildForCandlesWithLowerCaseExistingGranularity_NoExceptions(){
        settings.put(QUANTITY, "12");
        settings.put(INSTRUMENT, "EUR");
        settings.put(GRANULARITY, "m30");
        this.request.build("candle", settings);
    }

    @Test
    public void getCorrectCandleRequest(){

        settings.put(QUANTITY, EXPECTED_QUANTITY);
        settings.put(INSTRUMENT, EXPECTED_INSTRUMENT);
        settings.put(GRANULARITY, EXPECTED_GRANULARITY);
        Request<?> candleRequest = this.request.build("candle", settings);
        InstrumentCandlesRequest actualRequest = (InstrumentCandlesRequest) candleRequest.getRequestDataStructure();

        assertEquals(EXPECTED_INSTRUMENT, getCandlesRequestInstrument(actualRequest));

        HashMap<String, Object> queryParams = actualRequest.getQueryParams();
        assertEquals(EXPECTED_QUANTITY, queryParams.get("count").toString());
        assertEquals(EXPECTED_GRANULARITY, queryParams.get(GRANULARITY).toString());
        assertEquals("false",queryParams.get("smooth").toString());

    }


    private Class<? extends PricingGetRequest> getRequestClass(PricingGetRequest request) {
        return request.getClass();
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



    //    //////////
//    @Test(expected = NullArgumentException.class)
//    public void WhenCallCreateRequestWithNullSettings_Exception(){
//        request.getPriceRequest(null, null);
//    }
//
//    @Test(expected = NullArgumentException.class)
//    public void WhenCallCreateRequestWithAccountIdKeyNameNull_Exception(){
//        request.getPriceRequest(null, EXPECTED_INSTRUMENT);
//    }
//
//    @Test(expected = NullArgumentException.class)
//    public void WhenCallCreateRequestWithInstrumentKeyNameNull_Exception(){
//        request.getPriceRequest(EXPECTED_ACCOUNT_ID,null);
//    }
}
