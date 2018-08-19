package trader.candles;

import com.oanda.v20.Context;
import com.oanda.v20.ExecuteException;
import com.oanda.v20.RequestException;
import com.oanda.v20.instrument.Candlestick;
import com.oanda.v20.instrument.CandlestickGranularity;
import com.oanda.v20.instrument.InstrumentCandlesRequest;
import com.oanda.v20.instrument.InstrumentCandlesResponse;
import com.oanda.v20.primitives.DateTime;
import com.sun.istack.internal.NotNull;
import trader.prices.PriceObservable;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CandlesUpdater {

    private static final long MINUTE = 60;
    private static final long HOUR = 3600;
    private static final long DAY = 86_400;
    private static final long WEEK = 604_800;
    private static final long MONTH = 2_629_746;

    private final Context context;
    private List<Candlestick> candlestickList;
    private final CandlestickGranularity candlesTimeFrame;
    private InstrumentCandlesRequest request;
    private final DateTime defaultDateTime;

    public CandlesUpdater(Context context, InstrumentCandlesRequest request, CandlestickGranularity candlesTimeFrame){
        this.context = context;
        this.candlesTimeFrame = candlesTimeFrame;
        this.request = request;
        this.candlestickList = new ArrayList<>();
        this.defaultDateTime = new DateTime("2018-01-01T01:01:01Z");

        this.requestCandles();
    }

    public List<Candlestick> getCandles(){
        return Collections.unmodifiableList(this.candlestickList);
    }

    /**
     *
     * @param dateTime dateTime of the new price
     * @return {@link Boolean} true if updated else return false
     * @see DateTime
     * @see PriceObservable
     */
    public boolean updateCandles(DateTime dateTime){
        DateTime lastCandleTimeBeforeUpdate = getLastCandleDateTime();

        ZonedDateTime nextCandleOpenDateTime = this.nextCandleOpenTime(lastCandleTimeBeforeUpdate);
        ZonedDateTime newCandleOpenDateTime = this.dateTimeConversion(dateTime);

        if(newCandleOpenDateTime.compareTo(nextCandleOpenDateTime) > 0){

            this.singleUpdate(lastCandleTimeBeforeUpdate);
            return true;
        }
        return false;
    }

    /**
     * Sometimes new candles came with time like old ones. Which cause multiple updates
     * This loop will check last candle time before and after update to assure that new candle have different time
     * @param lastCandleTimeBeforeUpdate last candle time before update
     * @see DateTime
     */
    private void singleUpdate(DateTime lastCandleTimeBeforeUpdate){
        while(true){
            this.requestCandles();
            if (lastCandleTimeBeforeUpdate.equals(this.getLastCandleDateTime())){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            } else {
                break;
            }
        }
    }

    /**
     * get the dateTime from the last candlestick from the list of candlesticks
     * @return {@link DateTime} value ot the candle
     * @see DateTime
     */
    private DateTime getLastCandleDateTime(){
        if (this.candlestickList.size() > 0){
            return this.candlestickList.get(this.candlestickList.size()-1).getTime();
        } else {
            //default DateTime if no candles
            return this.defaultDateTime;
        }
    }

    /**
     * Transform next candle dateTime in utc zone
     * @return {@link ZonedDateTime} next candle open time
     *
     */
    private ZonedDateTime nextCandleOpenTime(@NotNull DateTime lastCandleDateTime) {

        long timeFrameSeconds = timeFrameToSeconds();
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
    private ZonedDateTime dateTimeConversion(@NotNull DateTime dateTime){
        Instant instantDateTime = Instant.parse(dateTime.toString());
        ZoneId zoneId = ZoneId.of("UTC");
        return ZonedDateTime.ofInstant(instantDateTime, zoneId);
    }

    /**
     * Convert MA's candlestick Granularity to seconds
     * @return {@link long} converted value for the candle in seconds
     * @see CandlestickGranularity
     */
    private long timeFrameToSeconds(){
        String timeFrameType = this.candlesTimeFrame.name().toLowerCase().substring(0, 1);
        String timeFrameNumber = this.candlesTimeFrame.name().toLowerCase().substring(1);
        switch (timeFrameType){
            case "s": return Integer.parseInt(timeFrameNumber);
            case "m": return this.candlesTimeFrame.toString().length() > 1 ? Integer.parseInt(timeFrameNumber) * MINUTE : MONTH;
            case "h": return Integer.parseInt(timeFrameNumber) * HOUR;
            case "d": return DAY;
            case "w": return WEEK;
            default: return 0;
        }
    }

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
