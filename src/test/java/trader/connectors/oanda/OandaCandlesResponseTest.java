package trader.connectors.oanda;

import com.oanda.v20.ExecuteException;
import com.oanda.v20.RequestException;
import com.oanda.v20.instrument.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import trader.CommonTestClassMembers;
import trader.OandaAPIMock.OandaAPIMockInstrument;
import trader.candle.Candlestick;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static trader.strategies.BGXStrategy.StrategyConfig.*;

public class OandaCandlesResponseTest {

    private static final long EXPECTED_VOLUME = 12L;
    private static final BigDecimal EXPECTED_PRICE = new BigDecimal(1.1234)
            .setScale(5, RoundingMode.HALF_UP);
    private static final String DEFAULT_DATE_TIME = "2012-06-30T12:30:40Z";
    private static final Class<com.oanda.v20.instrument.Candlestick> CANDLESTICK_CLASS = com.oanda.v20.instrument.Candlestick.class;

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    private CommonTestClassMembers commonMembers;
    private OandaCandlesResponse candlesResponse;
    private OandaConnector mockConnector;
    private OandaAPIMockInstrument oandaInstrument;

    @Before
    public void setUp() throws Exception {
        commonMembers = new CommonTestClassMembers();
        oandaInstrument = new OandaAPIMockInstrument();
        mockConnector = mock(OandaConnector.class);
        setMockConnectorGetContext();
        candlesResponse = new OandaCandlesResponse(mockConnector);
        oandaInstrument.init(3);

    }

    @Test
    public void getCorrectInitialCandlesQuantiy(){
        int initialRequestCandlesQuantity = 15;
        oandaInstrument.init(initialRequestCandlesQuantity);
        commonMembers
                .changeFieldObject(candlesResponse, "initialCandlesRequest", oandaInstrument.getMockRequest());
        List<Candlestick> actualList = candlesResponse.getInitialCandles();

        verifyList(initialRequestCandlesQuantity, actualList);
    }

    @Test
    public void getCorrectCandlesQuantityWhenUpdating(){
        int updateRequestCandlesQuantity = 1;
        oandaInstrument.init(updateRequestCandlesQuantity);
        commonMembers
                .changeFieldObject(candlesResponse, "updateCandlesRequest", oandaInstrument.getMockRequest());
        Candlestick actualCandlestick = candlesResponse.getUpdateCandle();

        assertCandlestick(actualCandlestick);
    }

    @Test
    public void testExtractGranularity() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method extractGranularity = commonMembers
                .getPrivateMethodForTest(candlesResponse, "extractGranularity");
        CandlestickGranularity targetGranularity = (CandlestickGranularity) extractGranularity
                .invoke(candlesResponse);

        assertEquals(CANDLE_GRANULARITY.toString(), targetGranularity.toString());
    }

    @Test
    public void WhenCreateOandaCandlesResponse_CorrectInitialCandlesRequest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        InstrumentCandlesRequest initialCandlesRequest = getCandlesRequest("initialCandlesRequest", INITIAL_CANDLES_QUANTITY);

        assertCandlesRequest(initialCandlesRequest, INITIAL_CANDLES_QUANTITY);
    }

    @Test
    public void WhenCreateOandaCandlesResponse_CorrectUpdateCandlesRequest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        InstrumentCandlesRequest updateCandlesRequest = getCandlesRequest( "updateCandlesRequest", UPDATE_CANDLES_QUANTITY);

        assertCandlesRequest(updateCandlesRequest, UPDATE_CANDLES_QUANTITY);
    }

    @Test
    public void testForCorrectInstrumentContext() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method getInstrumentContext = commonMembers
                .getPrivateMethodForTest(candlesResponse, "getInstrumentContext");
        InstrumentContext instrumentContext = (InstrumentContext) getInstrumentContext
                .invoke(candlesResponse);

        assertEquals(oandaInstrument.getMockInstrumentContext(), instrumentContext);
    }

    @Test(expected = InvocationTargetException.class)
    public void testCandlesResponseWithNullRequest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, RequestException, ExecuteException, InstantiationException {
//       thrown.expect(NullPointerException.class);
//       Throwable cause = new BadRequestException();
//       thrown.expectCause(IsEqual.equalTo(cause));
//        setMockConnectorGetContext();

        oandaInstrument.setExceptionForMockInstrumentContext(null);
        InstrumentCandlesRequest icr = null;
        Method getInitialCandlesResponse = getMethodCandlesResponse();
        getInitialCandlesResponse.invoke(candlesResponse, icr);
    }

    @Test
    public void whenCallGetCandlesResponse_ReturnInstrumentCandlesResponse() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method getInitialCandlesResponse = getMethodCandlesResponse();
        InstrumentCandlesResponse invoke =
                (InstrumentCandlesResponse) getInitialCandlesResponse
                .invoke(candlesResponse, oandaInstrument.getMockRequest());

        assertSame(invoke.getClass(), InstrumentCandlesResponse.class);
    }

    @SuppressWarnings(value = "unchecked")
    @Test
    public void testGetOandaCandles() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method getInitialCandlesResponse = commonMembers
                .getPrivateMethodForTest(candlesResponse, "getOandaCandles", InstrumentCandlesRequest.class);
        List<com.oanda.v20.instrument.Candlestick> candlesticks =
                (List<com.oanda.v20.instrument.Candlestick>)getInitialCandlesResponse
                .invoke(candlesResponse, oandaInstrument.getMockRequest());

        assertEquals(oandaInstrument.getMockCandlestickList(), candlesticks);
    }

    @SuppressWarnings(value = "unchecked")
    @Test
    public void testTransformOandaCandleList() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method transformToTradeCandlestickList = commonMembers
                .getPrivateMethodForTest(candlesResponse, "transformToTradeCandlestickList", List.class);
        List<Candlestick> actualList = (List<Candlestick>) transformToTradeCandlestickList
                .invoke(candlesResponse, oandaInstrument.getMockCandlestickList());

        verifyList(oandaInstrument.getMockCandlestickList().size(), actualList);
    }

    @Test
    public void testConvertingOandaCandleToTradeCandle() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method convertToTradeCandlestick = commonMembers
                .getPrivateMethodForTest(candlesResponse, "convertToTradeCandlestick", CANDLESTICK_CLASS);
        Candlestick actualCandlestick = (Candlestick) convertToTradeCandlestick
                .invoke(candlesResponse, oandaInstrument.getMockCandlestick());

        assertCandlestick(actualCandlestick);
    }

    private void verifyList(int initialRequestCandlesQuantity, List<Candlestick> actualList) {
        assertEquals(initialRequestCandlesQuantity, actualList.size());
        for (Candlestick candlestick:actualList)
            assertCandlestick(candlestick);
    }

    private InstrumentCandlesRequest getCandlesRequest(String fieldName, long quantity) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method candlesRequest = commonMembers
                .getPrivateMethodForTest(candlesResponse, "createCandlesRequest", long.class);
        candlesRequest.invoke(candlesResponse, quantity);
        return (InstrumentCandlesRequest) commonMembers.extractFieldObject(candlesResponse, fieldName);
    }

    private Method getMethodCandlesResponse() throws NoSuchMethodException {
        return commonMembers
                .getPrivateMethodForTest(candlesResponse, "candlesResponse", InstrumentCandlesRequest.class);
    }

    private void assertCandlesRequest(InstrumentCandlesRequest updateCandlesRequest, long updateCandlesQuantity) {
        HashMap<String, Object> queryParams = updateCandlesRequest.getQueryParams();
        HashMap<String, Object> pathParams = updateCandlesRequest.getPathParams();

        assertEquals(INSTRUMENT_NAME, pathParams.get("instrument").toString());
        assertEquals(CANDLE_GRANULARITY.toString(), queryParams.get("granularity").toString());
        assertEquals(updateCandlesQuantity, queryParams.get("count"));
        assertEquals(false, queryParams.get("smooth"));
    }

    private void assertCandlestick(Candlestick invoke) {
        assertEquals(createZonedDateTime(DEFAULT_DATE_TIME), invoke.getDateTime());
        assertEquals(EXPECTED_VOLUME, invoke.getVolume());
        assertTrue(invoke.isComplete());
        assertEquals(EXPECTED_PRICE, invoke.getHighPrice());
        assertEquals(EXPECTED_PRICE, invoke.getLowPrice());
        assertEquals(EXPECTED_PRICE, invoke.getOpenPrice());
        assertEquals(EXPECTED_PRICE, invoke.getClosePrice());
    }

    private void setOandaCandlestick(){
        oandaInstrument.setOandaMockPriceValueActions(EXPECTED_PRICE);
        oandaInstrument.setOandaMockDateTimeActions(DEFAULT_DATE_TIME);
        oandaInstrument.setOandaCandlestickActions(EXPECTED_VOLUME, true);
    }

    private ZonedDateTime createZonedDateTime(String dateTime){
        return ZonedDateTime.parse(dateTime).withZoneSameInstant(ZoneId.of("UTC"));
    }

    private void setMockConnectorGetContext() {
        when(mockConnector.getContext()).thenReturn(oandaInstrument.getContext());
    }
}
