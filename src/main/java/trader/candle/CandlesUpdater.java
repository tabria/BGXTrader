package trader.candle;
import com.oanda.v20.Context;
import com.oanda.v20.instrument.Candlestick;
import com.oanda.v20.instrument.InstrumentCandlesRequest;
import com.oanda.v20.primitives.DateTime;
import trader.connectors.ApiConnector;

import java.util.Collections;
import java.util.List;

import static trader.strategies.BGXStrategy.StrategyConfig.*;

public final class CandlesUpdater {

//    private static final DateTime DEFAULT_DATE_TIME = new DateTime("2018-01-01T01:01:01Z");
//    private static final int SLEEP_TIME_MILLISECONDS = 1000;
//
//    /////////////To be removed//////////////////////
//    private  Context context = null;
//    private  CandleGranularity candleTimeFrame;
//    private InstrumentCandlesRequest request;
//
    public CandlesUpdater(Context context, InstrumentCandlesRequest request, CandleGranularity candleGranularity){
//        this.context = context;
//        this.candleTimeFrame = candleGranularity;
//        this.request = request;
//        this.candlestickList = new ArrayList<>();
//
//        this.requestCandles();
    }

    ///////////////////////////////////////////////

    private ApiConnector apiConnector;
    private List<Candlestick> candlestickList;


    public CandlesUpdater(ApiConnector apiConnector){
        candlestickList = initialize(INITIAL_CANDLES_QUANTITY);
        this.apiConnector = apiConnector;
    }

    private List<Candlestick> initialize(long candlesQuantity) {
       // apiConnector.


        //    private void requestCandles(){
//        try {
//            InstrumentCandlesResponse response = this.context.instrument.candles(this.request);
//            this.candlestickList = response.getCandles();
//        } catch (RequestException | ExecuteException e) {
//            System.out.println(e.getMessage());
//        }
//    }
        return null;
    }

    public List<Candlestick> getCandles(){
        return Collections.unmodifiableList(this.candlestickList);
    }

    public boolean updateCandles(DateTime candleDateTime){
//        if(isUpdatable(candleDateTime)){
//            executeUpdate(getLastCandleDateTime());
//            return true;
//        }
        return false;
    }
//
//    /**
//     * Sometimes new candle came with time like old ones. Which cause multiple updates
//     * This loop will check last candle time before and after updateIndicator to assure that new candle have different time
//     * @param lastCandleDateTimeBeforeUpdate last candle's datetime before updating
//     * @see DateTime
//     */
//    private void executeUpdate(DateTime lastCandleDateTimeBeforeUpdate){
//        while(true){
//            this.requestCandles();
//            if (noChangeInDateTime(lastCandleDateTimeBeforeUpdate))
//                threadSleep(SLEEP_TIME_MILLISECONDS);
//            break;
//        }
//    }
//
//    private DateTime getLastCandleDateTime(){
//        if (haveCandlesticks())
//            return lastCandle().getTime();
//        return DEFAULT_DATE_TIME;
//    }
//
//        private boolean haveCandlesticks() {
//            return this.candlestickList != null && this.candlestickList.size() > 0;
//        }
//
//        private Candlestick lastCandle() {
//            return this.candlestickList.get(this.candlestickList.size()-1);
//        }
//
//    private ZonedDateTime nextCandleOpenDateTime(DateTime lastCandleDateTime) {
//        long timeFrameSeconds = candleTimeFrame.extractSeconds();
//        ZonedDateTime nextCandleOpenDateTime = this.convertDateTimeToZonnedDateTime(lastCandleDateTime);
//
//        return nextCandleOpenDateTime.plusSeconds(timeFrameSeconds);
//    }
//
//        private ZonedDateTime convertDateTimeToZonnedDateTime(DateTime dateTime){
//            Instant instantDateTime = Instant.parse(dateTime.toString());
//            ZoneId zoneId = ZoneId.of("UTC");
//            return ZonedDateTime.ofInstant(instantDateTime, zoneId);
//        }
//
//    private boolean isUpdatable(DateTime candleDateTime) {
//        ZonedDateTime nextCandleOpenDateTime = this.nextCandleOpenDateTime(getLastCandleDateTime());
//        ZonedDateTime newCandleOpenDateTime = this.convertDateTimeToZonnedDateTime(candleDateTime);
//        return newCandleOpenDateTime.compareTo(nextCandleOpenDateTime) > 0;
//    }
//
//    private boolean noChangeInDateTime(DateTime lastCandleDateTimeBeforeUpdate) {
//        return lastCandleDateTimeBeforeUpdate.equals(this.getLastCandleDateTime());
//    }
//
//    private void threadSleep(long milliseconds) {
//        try {
//            Thread.sleep(milliseconds);
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        }
//    }
//
//    /**
//     * Get candle from OANDA
//     * @see InstrumentCandlesRequest
//     */
//    private void requestCandles(){
//        try {
//            InstrumentCandlesResponse response = this.context.instrument.candles(this.request);
//            this.candlestickList = response.getCandles();
//        } catch (RequestException | ExecuteException e) {
//            System.out.println(e.getMessage());
//        }
//    }

}
