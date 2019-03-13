package trader.candles;

import com.oanda.v20.Context;
import com.oanda.v20.ExecuteException;
import com.oanda.v20.RequestException;
import com.oanda.v20.instrument.Candlestick;
import com.oanda.v20.instrument.InstrumentCandlesRequest;
import com.oanda.v20.instrument.InstrumentCandlesResponse;
import com.oanda.v20.primitives.DateTime;
import trader.indicators.enums.CandleGranularity;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CandlesUpdater {

    public static final DateTime DEFAULT_DATE_TIME = new DateTime("2018-01-01T01:01:01Z");

    private static final long MINUTE = 60;
    private static final long HOUR = 3600;
    private static final long DAY = 86_400;
    private static final long WEEK = 604_800;
    private static final long MONTH = 2_629_746;
    private static final int SLEEP_TIME_MILLISECONDS = 1000;

    private final Context context;
    private List<Candlestick> candlestickList;
    private final CandleGranularity candlesTimeFrame;
    private InstrumentCandlesRequest request;

    public CandlesUpdater(Context context, InstrumentCandlesRequest request, CandleGranularity candlesTimeFrame){
        this.context = context;
        this.candlesTimeFrame = candlesTimeFrame;
        this.request = request;
        this.candlestickList = new ArrayList<>();

        this.requestCandles();
    }

    public List<Candlestick> getCandles(){
        return Collections.unmodifiableList(this.candlestickList);
    }

    public boolean updateCandles(DateTime dateTime){
        DateTime lastCandleDateTimeBeforeUpdate = getLastCandleDateTime();

        ZonedDateTime nextCandleOpenDateTime = this.nextCandleOpenDateTime(lastCandleDateTimeBeforeUpdate);
        ZonedDateTime newCandleOpenDateTime = this.dateTimeConversion(dateTime);

        if(newCandleOpenDateTime.compareTo(nextCandleOpenDateTime) > 0){

            this.singleUpdate(lastCandleDateTimeBeforeUpdate);
            return true;
        }
        return false;
    }

    /**
     * Sometimes new candles came with time like old ones. Which cause multiple updates
     * This loop will check last candle time before and after updateMovingAverage to assure that new candle have different time
     * @param lastCandleTimeBeforeUpdate last candle time before updateMovingAverage
     * @see DateTime
     */
    private void singleUpdate(DateTime lastCandleTimeBeforeUpdate){
        while(true){
            this.requestCandles();
            if (lastCandleTimeBeforeUpdate.equals(this.getLastCandleDateTime())){
                threadSleep(SLEEP_TIME_MILLISECONDS);
            } else {
                break;
            }
        }
    }

    private void threadSleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private DateTime getLastCandleDateTime(){
        if (haveCandlesticks()){
            return lastCandle().getTime();
        } else {
            return DEFAULT_DATE_TIME;
        }
    }

    private boolean haveCandlesticks() {
        return this.candlestickList.size() > 0;
    }

    private Candlestick lastCandle() {
        return this.candlestickList.get(this.candlestickList.size()-1);
    }

    private ZonedDateTime nextCandleOpenDateTime(DateTime lastCandleDateTime) {
        long timeFrameSeconds = candlesTimeFrame.extractSeconds();
        ZonedDateTime nextCandleOpenDateTime = this.dateTimeConversion(lastCandleDateTime);
        nextCandleOpenDateTime = nextCandleOpenDateTime.plusSeconds(timeFrameSeconds);

        return nextCandleOpenDateTime;
    }

    /**
     * Transform dateTime in utc zone
     * @param dateTime dateTime from OANDA
     * @return {@link ZonedDateTime}zoned DateTime value
     * @see DateTime
     */
    private ZonedDateTime dateTimeConversion( DateTime dateTime){
        Instant instantDateTime = Instant.parse(dateTime.toString());
        ZoneId zoneId = ZoneId.of("UTC");
        return ZonedDateTime.ofInstant(instantDateTime, zoneId);
    }

//    /**
//     * Convert MA's candlestick Granularity to seconds
//     * @return {@link long} converted value for the candle in seconds
//     * @see CandlestickGranularity
//     */
//    private long convertMovingAverageTimeFrameToSeconds(){
//        String timeFrameType = this.candlesTimeFrame.name().toLowerCase().substring(0, 1);
//        String timeFrameNumber = this.candlesTimeFrame.name().toLowerCase().substring(1);
//        switch (timeFrameType){
//            case "s": return Integer.parseInt(timeFrameNumber);
//            case "m": return this.candlesTimeFrame.toString().length() > 1 ? Integer.parseInt(timeFrameNumber) * MINUTE : MONTH;
//            case "h": return Integer.parseInt(timeFrameNumber) * HOUR;
//            case "d": return DAY;
//            case "w": return WEEK;
//            default: return 0;
//        }
//    }

    /**
     * Get candles from OANDA
     * @see InstrumentCandlesRequest
     */
    private void requestCandles(){
        try {

            InstrumentCandlesResponse response = this.context.instrument.candles(this.request);
            this.candlestickList = response.getCandles();
        } catch (RequestException e) {
            System.out.println(e.getErrorMessage());
            //e.printStackTrace();
        } catch (ExecuteException e) {
            System.out.println(e.getMessage());
            //e.printStackTrace();
        }
    }

}
