package trader.indicator;

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

    public List<String> candlesClosePrices = new ArrayList<>(Arrays.asList(
            "1.16114", "1.16214", "1.16314", "1.16414", "1.16514", "1.16614", "1.16714",
            "1.16814", "1.16914", "1.16114", "1.16214", "1.16314", "1.16414", "1.15714"
    ));

//    protected List<String> candlesClosePrices = new ArrayList<>(Arrays.asList(
//    "1181.75", "1154.59998", "1133.19995", "1132.15002", "1167.40002", "1122.90002","1099.40002","1097.59998","1097.34998","1094.90002","1111.94995","1125.94995","1138.40002","1138.40002","1149.90002","1152.15002","1149.40002","1153.19995","1145.05005","1135.44995","1131.19995","1145.5","1136.80005","1122.90002","1138.30005","1134.59998","1103.84998","1103.84998","1103.84998","1100.80005","1080.19995","1061.5","1020"
//            ));

    public List<String> candlesDateTime = new ArrayList<>(Arrays.asList(
            "2018-08-01T09:39:00Z", "2018-08-01T09:40:00Z", "2018-08-01T09:41:00Z", "2018-08-01T09:42:00Z",
            "2018-08-01T09:43:00Z", "2018-08-01T09:44:00Z", "2018-08-01T09:45:00Z", "2018-08-01T09:46:00Z",
            "2018-08-01T09:47:00Z", "2018-08-01T09:48:00Z", "2018-08-01T09:49:00Z", "2018-08-01T09:50:00Z",
            "2018-08-01T09:51:00Z", "2018-08-01T09:52:00Z"

    ));

//    protected List<String> candlesDateTime = new ArrayList<>(Arrays.asList(
//            "2018-08-01T09:39:00Z", "2018-08-01T09:40:00Z", "2018-08-01T09:41:00Z", "2018-08-01T09:42:00Z",
//            "2018-08-01T09:43:00Z", "2018-08-01T09:44:00Z", "2018-08-01T09:45:00Z", "2018-08-01T09:46:00Z",
//            "2018-08-01T09:47:00Z", "2018-08-01T09:48:00Z", "2018-08-01T09:49:00Z", "2018-08-01T09:50:00Z",
//            "2018-08-01T09:51:00Z", "2018-08-01T09:52:00Z", "2018-08-01T09:39:00Z", "2018-08-01T09:40:00Z", "2018-08-01T09:41:00Z", "2018-08-01T09:42:00Z",
//            "2018-08-01T09:43:00Z", "2018-08-01T09:44:00Z", "2018-08-01T09:45:00Z", "2018-08-01T09:46:00Z",
//            "2018-08-01T09:47:00Z", "2018-08-01T09:48:00Z", "2018-08-01T09:49:00Z", "2018-08-01T09:50:00Z",
//            "2018-08-01T09:51:00Z", "2018-08-01T09:52:00Z","2018-08-01T09:48:00Z", "2018-08-01T09:49:00Z", "2018-08-01T09:50:00Z",
//            "2018-08-01T09:51:00Z", "2018-08-01T09:52:00Z"
//
//    ));



    public List<Candlestick> candlestickList;
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

    public void fillCandlestickListWithZeros(){
        candlestickList = new ArrayList<>();
        for (int candlestickNumber = 0; candlestickNumber < candlesClosePrices.size() ; candlestickNumber++) {
            candlesClosePrices.set(candlestickNumber, "0");
            addCandlestick(candlestickNumber);
        }
    }

    public Candlestick createCandlestickMock() {
        Candlestick newCandlestick = mock(Candlestick.class);
        when(newCandlestick.getClosePrice()).thenReturn(UPDATED_CANDLESTICK_PRICE);
        when(newCandlestick.getOpenPrice()).thenReturn(UPDATED_CANDLESTICK_PRICE);
        when(newCandlestick.getLowPrice()).thenReturn(UPDATED_CANDLESTICK_PRICE);
        when(newCandlestick.getHighPrice()).thenReturn(UPDATED_CANDLESTICK_PRICE);
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
        when(candlestick.getOpenPrice()).thenReturn(candlestickClosePrice);
        when(candlestick.getHighPrice()).thenReturn(candlestickClosePrice);
        when(candlestick.getLowPrice()).thenReturn(candlestickClosePrice);
        return candlestick;
    }

//    private PriceValue createCandlePriceValue(int candlestickNumber) {
//        PriceValue candlestickPriceValue = mock(PriceValue.class);
//        when(candlestickPriceValue.toString()).thenReturn(candlesClosePrices.get(candlestickNumber));
//        return candlestickPriceValue;
//    }
}