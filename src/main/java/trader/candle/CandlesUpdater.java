package trader.candle;

import com.oanda.v20.Context;
import com.oanda.v20.ExecuteException;
import com.oanda.v20.RequestException;
import com.oanda.v20.instrument.Candlestick;
import com.oanda.v20.instrument.InstrumentCandlesRequest;
import com.oanda.v20.instrument.InstrumentCandlesResponse;
import com.oanda.v20.primitives.DateTime;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CandlesUpdater {

    private static final DateTime DEFAULT_DATE_TIME = new DateTime("2018-01-01T01:01:01Z");
    private static final int SLEEP_TIME_MILLISECONDS = 1000;

    private final Context context;
    private List<Candlestick> candlestickList;
    private final CandleGranularity candleTimeFrame;
    private InstrumentCandlesRequest request;

    public CandlesUpdater(Context context, InstrumentCandlesRequest request, CandleGranularity candleGranularity){
        this.context = context;
        this.candleTimeFrame = candleGranularity;
        this.request = request;
        this.candlestickList = new ArrayList<>();

        this.requestCandles();
    }

    public List<Candlestick> getCandles(){
        return Collections.unmodifiableList(this.candlestickList);
    }

    public boolean updateCandles(DateTime candleDateTime){
        if(isUpdatable(candleDateTime)){
            executeUpdate(getLastCandleDateTime());
            return true;
        }
        return false;
    }

    /**
     * Sometimes new candle came with time like old ones. Which cause multiple updates
     * This loop will check last candle time before and after updateIndicator to assure that new candle have different time
     * @param lastCandleDateTimeBeforeUpdate last candle's datetime before updating
     * @see DateTime
     */
    private void executeUpdate(DateTime lastCandleDateTimeBeforeUpdate){
        while(true){
            this.requestCandles();
            if (noChangeInDateTime(lastCandleDateTimeBeforeUpdate))
                threadSleep(SLEEP_TIME_MILLISECONDS);
            break;
        }
    }

    private DateTime getLastCandleDateTime(){
        if (haveCandlesticks())
            return lastCandle().getTime();
        return DEFAULT_DATE_TIME;
    }

        private boolean haveCandlesticks() {
            return this.candlestickList != null && this.candlestickList.size() > 0;
        }

        private Candlestick lastCandle() {
            return this.candlestickList.get(this.candlestickList.size()-1);
        }

    private ZonedDateTime nextCandleOpenDateTime(DateTime lastCandleDateTime) {
        long timeFrameSeconds = candleTimeFrame.extractSeconds();
        ZonedDateTime nextCandleOpenDateTime = this.convertDateTimeToZonnedDateTime(lastCandleDateTime);

        return nextCandleOpenDateTime.plusSeconds(timeFrameSeconds);
    }

        private ZonedDateTime convertDateTimeToZonnedDateTime(DateTime dateTime){
            Instant instantDateTime = Instant.parse(dateTime.toString());
            ZoneId zoneId = ZoneId.of("UTC");
            return ZonedDateTime.ofInstant(instantDateTime, zoneId);
        }

    private boolean isUpdatable(DateTime candleDateTime) {
        ZonedDateTime nextCandleOpenDateTime = this.nextCandleOpenDateTime(getLastCandleDateTime());
        ZonedDateTime newCandleOpenDateTime = this.convertDateTimeToZonnedDateTime(candleDateTime);
        return newCandleOpenDateTime.compareTo(nextCandleOpenDateTime) > 0;
    }

    private boolean noChangeInDateTime(DateTime lastCandleDateTimeBeforeUpdate) {
        return lastCandleDateTimeBeforeUpdate.equals(this.getLastCandleDateTime());
    }

    private void threadSleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Get candle from OANDA
     * @see InstrumentCandlesRequest
     */
    private void requestCandles(){
        try {
            InstrumentCandlesResponse response = this.context.instrument.candles(this.request);
            this.candlestickList = response.getCandles();
        } catch (RequestException | ExecuteException e) {
            System.out.println(e.getMessage());
        }
    }

}
