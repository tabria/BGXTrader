package trader.candlestick.candle;

import trader.candlestick.Candlestick;
import trader.exception.NegativeNumberException;
import trader.exception.NullArgumentException;
import trader.exception.OverflowException;
import trader.exception.UnderflowException;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public final class Candle implements Candlestick {

    private final long timeFrame;
    private final boolean complete;
    private final long volume;
    private final BigDecimal openPrice;
    private final BigDecimal highPrice;
    private final BigDecimal lowPrice;
    private final BigDecimal closePrice;
    private final ZonedDateTime dateTime;

    private Candle(CandleBuilder candleBuilder) {
        timeFrame = candleBuilder.timeFrame;
        complete = candleBuilder.complete;
        volume = candleBuilder.volume;
        openPrice = candleBuilder.openPrice;
        highPrice = candleBuilder.highPrice;
        lowPrice = candleBuilder.lowPrice;
        closePrice = candleBuilder.closePrice;
        dateTime = candleBuilder.dateTime;
    }

    @Override
    public long getTimeFrame() {
        return timeFrame;
    }

    @Override
    public boolean isComplete() {
        return complete;
    }

    @Override
    public long getVolume() {
        return volume;
    }

    @Override
    public BigDecimal getOpenPrice() {
        return openPrice;
    }

    @Override
    public BigDecimal getHighPrice() {
        return highPrice;
    }

    @Override
    public BigDecimal getLowPrice() {
        return lowPrice;
    }

    @Override
    public BigDecimal getClosePrice() {
        return closePrice;
    }

    @Override
    public ZonedDateTime getDateTime() {
        return dateTime;
    }

    public static class CandleBuilder {

        private static final long MIN_CANDLE_TIME_FRAME_IN_SECONDS = 5L;
        private static final long MAX_CANDLE_TIME_FRAME_IN_SECONDS = 2629800L;
        private static final long DEFAULT_CANDLE_TIME_FRAME_IN_SECONDS = 1_800L;
        private static final long DEFAULT_VOLUME = 1L;
        private static final BigDecimal DEFAULT_PRICE = new BigDecimal(0.00001).setScale(5, BigDecimal.ROUND_HALF_UP);
        private static final ZonedDateTime DEFAULT_ZONED_DATE_TIME = ZonedDateTime.parse("2012-06-30T12:30:40Z[UTC]");

        private long timeFrame;
        private boolean complete;
        private long volume;
        private BigDecimal openPrice;
        private BigDecimal highPrice;
        private BigDecimal lowPrice;
        private BigDecimal closePrice;
        private ZonedDateTime dateTime;

        public CandleBuilder(){
            timeFrame = DEFAULT_CANDLE_TIME_FRAME_IN_SECONDS;
            complete = true;
            volume = DEFAULT_VOLUME;
            openPrice = DEFAULT_PRICE;
            highPrice = DEFAULT_PRICE;
            lowPrice = DEFAULT_PRICE;
            closePrice = DEFAULT_PRICE;
            dateTime = DEFAULT_ZONED_DATE_TIME;
        }

        public Candle build(){
            return new Candle(this);
        }

        public CandleBuilder setTimeFrame(long timeFrameInSeconds){
            checkTimeFrameBoundaries(timeFrameInSeconds);
            timeFrame = timeFrameInSeconds;
            return this;
        }

        public CandleBuilder setComplete(boolean isCompleted){
            complete = isCompleted;
            return this;
        }

        public CandleBuilder setVolume(long newVolume){
            checkNegativeNumber(newVolume);
            volume = newVolume;
            return this;
        }

        public CandleBuilder setOpenPrice(BigDecimal price){
            checkNull(price);
            checkNegativeNumber(price);
            openPrice = price;
            return this;
        }

        public CandleBuilder setHighPrice(BigDecimal price){
            checkNull(price);
            checkNegativeNumber(price);
            highPrice = price;
            return this;
        }

        public CandleBuilder setLowPrice(BigDecimal price){
            checkNull(price);
            checkNegativeNumber(price);
            lowPrice = price;
            return this;
        }

        public CandleBuilder setClosePrice(BigDecimal price){
            checkNull(price);
            checkNegativeNumber(price);
            closePrice = price;
            return this;
        }

        public CandleBuilder setDateTime(ZonedDateTime dateTime){
            checkNull(dateTime);
            this.dateTime = dateTime;
            return this;
        }

        private void checkNegativeNumber(BigDecimal number) {
            if(number.compareTo(BigDecimal.ZERO)<0){
                throw new NegativeNumberException();
            }
        }

        private void checkNegativeNumber(long number) {
            if(number<0L){
                throw new NegativeNumberException();
            }
        }

        private void checkTimeFrameBoundaries(long timeFrameInSeconds) {
            if(timeFrameInSeconds < MIN_CANDLE_TIME_FRAME_IN_SECONDS){
                throw new UnderflowException();
            }
            if(timeFrameInSeconds > MAX_CANDLE_TIME_FRAME_IN_SECONDS){
                throw new OverflowException();
            }
        }

        private void checkNull(Object argument) {
            if(argument == null){
                throw new NullArgumentException();
            }
        }
    }
}
