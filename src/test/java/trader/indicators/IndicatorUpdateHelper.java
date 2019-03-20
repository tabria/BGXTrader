package trader.indicators;

import com.oanda.v20.primitives.DateTime;
import trader.candle.Candlestick;
import trader.candle.CandlestickPriceType;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class IndicatorUpdateHelper {

    private static final BigDecimal UPDATED_CANDLESTICK_PRICE = BigDecimal.valueOf(1.16264);

    protected List<String> candlesClosePrices = new ArrayList<>(Arrays.asList(
            "1.16114", "1.16214", "1.16314", "1.16414", "1.16514", "1.16614", "1.16714",
            "1.16814", "1.16914", "1.16114", "1.16214", "1.16314", "1.16414", "1.15714"
    ));
    protected List<String> candlesDateTime = new ArrayList<>(Arrays.asList(
            "2018-08-01T09:39:00Z", "2018-08-01T09:40:00Z", "2018-08-01T09:41:00Z", "2018-08-01T09:42:00Z",
            "2018-08-01T09:43:00Z", "2018-08-01T09:44:00Z", "2018-08-01T09:45:00Z", "2018-08-01T09:46:00Z",
            "2018-08-01T09:47:00Z", "2018-08-01T09:48:00Z", "2018-08-01T09:49:00Z", "2018-08-01T09:50:00Z",
            "2018-08-01T09:51:00Z", "2018-08-01T09:52:00Z"

    ));


    protected List<Candlestick> candlestickList;
    protected CandlestickPriceType mockCandlestickPriceType;

    public IndicatorUpdateHelper(CandlestickPriceType mockCandlestickPriceType) {
        this.mockCandlestickPriceType = mockCandlestickPriceType;
    }

    public List<Candlestick> getCandlestickList() {
        return candlestickList;
    }

    public List<String> getCandlesClosePrices() {
        return candlesClosePrices;
    }

    public List<String> getCandlesDateTime() {
        return candlesDateTime;
    }

    public void setCandlesDateTime(String dateTime, int index){
        this.candlesDateTime.set(index, dateTime);
    }

    public void fillCandlestickList(){
        candlestickList = new ArrayList<>();
        for (int candlestickNumber = 0; candlestickNumber < candlesClosePrices.size() ; candlestickNumber++)
            addCandlestick(candlestickNumber);
    }

    public Candlestick createCandlestickMock() {
        Candlestick newCandlestick = mock(Candlestick.class);
        when(newCandlestick.getClosePrice()).thenReturn(UPDATED_CANDLESTICK_PRICE);
        when(newCandlestick.getDateTime()).thenReturn(ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC")));
        return newCandlestick;
    }

    private void addCandlestick(int candlestickNumber) {
        Candlestick candlestick = mock(Candlestick.class);
        setCandlestickDateTime(candlestick, candlestickNumber);
        setCandlestickPrice(candlestick, candlestickNumber);
        candlestickList.add(candlestick);
    }

    private void setCandlestickDateTime(Candlestick candlestick, int candlestickNumber) {
        ZonedDateTime candleDateTime = ZonedDateTime
                .parse(candlesDateTime.get(candlestickNumber)).withZoneSameInstant(ZoneId.of("UTC"));
        when(candlestick.getDateTime()).thenReturn(candleDateTime);
    }

//    private ZonedDateTime createCandleDateTime(int candlestickNumber) {
//        DateTime candleDateTime = mock(DateTime.class);
//        when(candleDateTime.toString()).thenReturn(candlesDateTime.get(candlestickNumber));
//        return candleDateTime;
//    }

//    private void setCandlestickPrice(Candlestick candlestick, int candlestickNumber) {
//        Candlestick candle = createCandlestickPriceData(candlestickNumber);
//        when(candlestick.getClosePrice()).thenReturn(candle);
// //       when(this.mockCandlestickPriceType.extractPrice(candlestickPriceData)).thenReturn(new BigDecimal(candlesClosePrices.get(candlestickNumber)));
//    }

    private Candlestick setCandlestickPrice(Candlestick candlestick, int candlestickNumber) {
        BigDecimal candlestickClosePrice = new BigDecimal(candlesClosePrices.get(candlestickNumber));
        when(candlestick.getClosePrice()).thenReturn(candlestickClosePrice);
        return candlestick;
    }

//    private PriceValue createCandlePriceValue(int candlestickNumber) {
//        PriceValue candlestickPriceValue = mock(PriceValue.class);
//        when(candlestickPriceValue.toString()).thenReturn(candlesClosePrices.get(candlestickNumber));
//        return candlestickPriceValue;
//    }
}