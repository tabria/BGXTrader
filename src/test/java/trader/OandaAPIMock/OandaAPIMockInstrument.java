package trader.OandaAPIMock;

import com.oanda.v20.ExecuteException;
import com.oanda.v20.RequestException;
import com.oanda.v20.instrument.*;
import com.oanda.v20.pricing_common.PriceValue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OandaAPIMockInstrument extends OandaAPIMock {

    private static final String DEFAULT_DATE_TIME = "2012-06-30T12:30:40Z";
    private static final long DEFAULT_VOLUME = 12L;
    private static final BigDecimal DEFAULT_PRICE = new BigDecimal(1.1234).setScale(5, RoundingMode.HALF_UP);

    private InstrumentCandlesRequest mockRequest;
    private InstrumentCandlesResponse mockResponse;
    private InstrumentContext mockInstrumentContext;
    private CandlestickData mockCandlestickData;
    private Candlestick mockCandlestick;
    private Candlestick mockNonTradableCandlestick;
    private PriceValue mockPriceValue;
    private List<Candlestick> candlestickList;

    public OandaAPIMockInstrument(int listCapacity) {
        candlestickList = new ArrayList<>();
        mockInstrumentContext = mock(InstrumentContext.class);
        mockContext.instrument = mockInstrumentContext;
        mockRequest = mock(InstrumentCandlesRequest.class);
        mockResponse = mock(InstrumentCandlesResponse.class);
        mockCandlestick = mock(Candlestick.class);
        mockNonTradableCandlestick = mock(Candlestick.class);
        setMockNonTradableCandlestick();
        mockCandlestickData = mock(CandlestickData.class);
        mockPriceValue = mock(PriceValue.class);
        init(listCapacity);
    }

    public InstrumentContext getMockInstrumentContext() {
        return mockInstrumentContext;
    }

    public InstrumentCandlesRequest getMockRequest() {
        return mockRequest;
    }

//    public InstrumentCandlesResponse getMockResponse() {
//        return mockResponse;
//    }

//    public CandlestickData getMockCandlestickData() {
//        return mockCandlestickData;
//    }

    public Candlestick getMockCandlestick() {
        return mockCandlestick;
    }

//    public PriceValue getMockPriceValue() {
//        return mockPriceValue;
//    }

    public List<Candlestick> getMockCandlestickList() {
        return candlestickList;
    }


    public void init(int listCapacity)  {
        when(mockPriceValue.bigDecimalValue())
                .thenReturn(DEFAULT_PRICE);
        when(mockDateTime.toString())
                .thenReturn(DEFAULT_DATE_TIME);
        try {
            when(mockContext.instrument.candles(mockRequest)).thenReturn(mockResponse);
            when(mockInstrumentContext.candles(mockRequest)).thenReturn(mockResponse);
        } catch (ExecuteException | RequestException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        candlestickList=setOandaCandlestickList(listCapacity);
        when(mockResponse.getCandles())
                .thenReturn(candlestickList);
        setOandaMockCandlestickDataActions();
        setOandaMockCandlestickDataActions();
        setOandaCandlestickActions(DEFAULT_VOLUME, true);
    }

//    public void setMockRequestToCandles() throws ExecuteException, RequestException {
//        when(mockContext.instrument.candles(mockRequest)).thenReturn(mockResponse);
//        when(mockInstrumentContext.candles(mockRequest)).thenReturn(mockResponse);
//    }

    public void setMockResponseToGetCandles(List<Candlestick> candlestickList){
        when(mockResponse.getCandles()).thenReturn(candlestickList);
    }

//   public void setMockResponseToGetCandles(){
//        when(mockResponse.getCandles()).thenReturn(candlestickList);
//    }

    public void setExceptionForMockInstrumentContext(InstrumentCandlesRequest request) throws ExecuteException, RequestException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

        Constructor<RequestException> declaredConstructor = RequestException.class.getDeclaredConstructor(int.class);
        declaredConstructor.setAccessible(true);
        RequestException requestException = declaredConstructor.newInstance(5);

        when(mockInstrumentContext.candles(request)).thenThrow(requestException);
    }

    public void setOandaCandlestickActions(long volume, boolean complete){
        when(mockCandlestick.getMid()).thenReturn(mockCandlestickData);
        when(mockCandlestick.getVolume()).thenReturn(volume);
        when(mockCandlestick.getTime()).thenReturn(mockDateTime);
        when(mockCandlestick.getComplete()).thenReturn(complete);
    }

    public void setOandaMockCandlestickDataActions(){
        when(mockCandlestickData.getO()).thenReturn(mockPriceValue);
        when(mockCandlestickData.getH()).thenReturn(mockPriceValue);
        when(mockCandlestickData.getL()).thenReturn(mockPriceValue);
        when(mockCandlestickData.getC()).thenReturn(mockPriceValue);

    }

    public List<Candlestick> setOandaCandlestickList(int listCapacity) {
        List<Candlestick> candlesList = new ArrayList<>(listCapacity);
        for (int i = 0; i <listCapacity; i++)
            candlesList.add(mockCandlestick);
        return candlesList;
    }

    public void setMockNonTradableCandlestick(){
        when(mockNonTradableCandlestick.getComplete()).thenReturn(false);
    }

    public void addNonTradableCandlesticksToOandaCandlestickList(int amount) {
        for (int i = 0; i < amount; i++) {
            addCandleToOandaCandlestickList(mockNonTradableCandlestick);
        }
    }

    public void addCandleToOandaCandlestickList(Candlestick candle) {
            candlestickList.add(candle);
    }

    public void setOandaMockDateTimeActions(String dateTime){
        when(mockDateTime.toString()).thenReturn(dateTime);
    }

//    public void setOandaMockDateTimeActions(){
//        when(mockDateTime.toString()).thenReturn(DEFAULT_DATE_TIME);
//    }

    public void setOandaMockPriceValueActions(BigDecimal value){
        when(mockPriceValue.bigDecimalValue()).thenReturn(value);
    }

//    public void setOandaMockPriceValueActions(){
//        when(mockPriceValue.bigDecimalValue()).thenReturn(DEFAULT_PRICE);
//    }
}
