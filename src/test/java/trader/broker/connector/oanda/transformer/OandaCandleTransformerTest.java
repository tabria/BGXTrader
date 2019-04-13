package trader.broker.connector.oanda.transformer;

import com.oanda.v20.instrument.CandlestickData;
import com.oanda.v20.instrument.InstrumentCandlesResponse;
import com.oanda.v20.pricing_common.PriceValue;
import com.oanda.v20.primitives.DateTime;
import org.junit.Before;
import org.junit.Test;
import trader.broker.connector.oanda.transformer.OandaCandleTransformer;
import trader.entity.candlestick.Candlestick;
import trader.responder.Response;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OandaCandleTransformerTest {

    private static final BigDecimal CLOSE_PRICE = BigDecimal.valueOf(1.1).setScale(5, RoundingMode.HALF_UP);
    private static final long VOLUME = 1234;
    private static final ZonedDateTime DEFAULT_DATE_TIME = ZonedDateTime.parse("2012-06-30T12:30:40Z[UTC]");
    private static final String DEFAULT_OANDA_TIME = "2012-06-30T12:30:40Z";


    private OandaCandleTransformer transformer;
    private Response responseMock;
    private InstrumentCandlesResponse instrumentCandlesResponseMock;
    private List<com.oanda.v20.instrument.Candlestick> oandaCandles;

    @Before
    public void setUp() throws Exception {
        responseMock = mock(Response.class);
        instrumentCandlesResponseMock = mock(InstrumentCandlesResponse.class);
        oandaCandles = new ArrayList<>();
        when(instrumentCandlesResponseMock.getCandles()).thenReturn(oandaCandles);
        transformer = new OandaCandleTransformer();
    }

    @Test
    public void WhenCallTransformCandlesticksWithNullResponse_EmptyList(){
        List<Candlestick> candlesticks = transformer.transformCandlesticks(null);

        assertEquals(0, candlesticks.size());
    }

    @Test
    public void WhenCallTransformCandlesticksWithNonTradableCandles_DoNotAddThem(){
        oandaCandles.add(makeFakeOandaCandlestickList( CLOSE_PRICE, false, VOLUME, DEFAULT_OANDA_TIME));
        oandaCandles.add(makeFakeOandaCandlestickList( CLOSE_PRICE, false, VOLUME, DEFAULT_OANDA_TIME));
        oandaCandles.add(makeFakeOandaCandlestickList( CLOSE_PRICE, false, VOLUME, DEFAULT_OANDA_TIME));
        List<Candlestick> candlesticks = transformer.transformCandlesticks(responseMock);

        assertEquals(0, candlesticks.size());
    }

    @Test
    public void WhenCallTransformCandlesticksWithCorrectInput_CorrectResult(){
        oandaCandles.add(makeFakeOandaCandlestickList( CLOSE_PRICE, true, VOLUME, DEFAULT_OANDA_TIME));
        oandaCandles.add(makeFakeOandaCandlestickList( CLOSE_PRICE, true, VOLUME, DEFAULT_OANDA_TIME));
        oandaCandles.add(makeFakeOandaCandlestickList( CLOSE_PRICE, true, VOLUME, DEFAULT_OANDA_TIME));
        oandaCandles.add(makeFakeOandaCandlestickList( CLOSE_PRICE, true, VOLUME, DEFAULT_OANDA_TIME));
        List<Candlestick> candlesticks = transformer.transformCandlesticks(responseMock);

        assertEquals(4, candlesticks.size());
        assertEquality(candlesticks);
    }

    private void assertEquality(List<Candlestick> candlesticks) {
        Candlestick candlestick = candlesticks.get(0);
        assertEquals(CLOSE_PRICE, candlestick.getClosePrice());
        assertEquals(CLOSE_PRICE, candlestick.getOpenPrice());
        assertEquals(CLOSE_PRICE, candlestick.getLowPrice());
        assertEquals(CLOSE_PRICE, candlestick.getHighPrice());
        assertEquals(DEFAULT_DATE_TIME, candlestick.getDateTime());
        assertEquals(VOLUME, candlestick.getVolume());
        assertTrue(candlestick.isComplete());
    }

    private com.oanda.v20.instrument.Candlestick makeFakeOandaCandlestickList(BigDecimal price,  boolean complete, long volume, String dateTime) {
        com.oanda.v20.instrument.Candlestick oandaCandlestickMock = mock(com.oanda.v20.instrument.Candlestick.class);
        CandlestickData candlestickDataMock = mock(CandlestickData.class);
        PriceValue priceValueClosePriceMock = mock(PriceValue.class);
        DateTime dateTimeMock = mock(DateTime.class);
        when(responseMock.getResponseDataStructure()).thenReturn(instrumentCandlesResponseMock);
        setFakeCandleComplete(oandaCandlestickMock, complete);
        setFakeCandlestickData(oandaCandlestickMock, candlestickDataMock);
        setFakeCandlePrices(candlestickDataMock, priceValueClosePriceMock);
        setFakeCandlePriceValue(priceValueClosePriceMock, price);
        setFakeCandleVolume(oandaCandlestickMock, volume);
        setFakeCandleDateTime(oandaCandlestickMock, dateTimeMock, dateTime);

        return oandaCandlestickMock;
    }

    private void setFakeCandlestickData(com.oanda.v20.instrument.Candlestick candlestick, CandlestickData candlestickData) {
        when(candlestick.getMid()).thenReturn(candlestickData);
    }

    private void setFakeCandleComplete(com.oanda.v20.instrument.Candlestick candlestick, boolean completeFlag) {
        when(candlestick.getComplete()).thenReturn(completeFlag);
    }

    private void setFakeCandleVolume(com.oanda.v20.instrument.Candlestick candlestick, long volume) {
        when(candlestick.getVolume()).thenReturn(volume);
    }

    private void setFakeCandlePriceValue(PriceValue priceValue, BigDecimal price) {
        when(priceValue.bigDecimalValue()).thenReturn(price);
    }

    private void setFakeCandleDateTime(com.oanda.v20.instrument.Candlestick candlestick, DateTime dateTime, String dateValue) {
        when(candlestick.getTime()).thenReturn(dateTime);
        when(dateTime.toString()).thenReturn(dateValue);
    }

    private void setFakeCandlePrices(CandlestickData candlestickData, PriceValue priceValue) {
        when(candlestickData.getC()).thenReturn(priceValue);
        when(candlestickData.getH()).thenReturn(priceValue);
        when(candlestickData.getL()).thenReturn(priceValue);
        when(candlestickData.getO()).thenReturn(priceValue);
    }
}




