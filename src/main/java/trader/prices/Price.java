package trader.prices;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;

public class Price {

    private BigDecimal ask;
    private BigDecimal bid;
    private ZonedDateTime dateTime;
    private boolean isTradable;

    private Price(PriceBuilder priceBuilder){
        ask = priceBuilder.ask;
        bid = priceBuilder.bid;
        dateTime = priceBuilder.dateTime;
        isTradable = priceBuilder.isTradable;
    }

    public BigDecimal getAsk(){
        return ask;
    }

    public BigDecimal getBid(){
        return bid;
    }

    public ZonedDateTime getDateTime(){
        return dateTime;
    }

    public boolean isTradable() {
        return isTradable;
    }

    public static class PriceBuilder{

        private static final BigDecimal DEFAULT_ASK = new BigDecimal(0.01)
                .setScale(5, RoundingMode.HALF_UP);
        private static final BigDecimal DEFAULT_BID = new BigDecimal(0.02)
                .setScale(5, RoundingMode.HALF_UP);
        private static final ZonedDateTime DEFAULT_DATE_TIME = ZonedDateTime.parse("2012-06-30T12:30:40Z[UTC]");

        private BigDecimal ask;
        private BigDecimal bid;
        private ZonedDateTime dateTime;
        private boolean isTradable;

        public PriceBuilder(){
            ask = DEFAULT_ASK;
            bid = DEFAULT_BID;
            dateTime = DEFAULT_DATE_TIME;
            isTradable = true;
        }

        public Price build(){
            return new Price(this);
        }

        public PriceBuilder setAsk(BigDecimal newAsk){
            checkNull(newAsk);
            checkNegativeNumber(newAsk);
            ask = newAsk;
            return this;
        }

        public PriceBuilder setBid(BigDecimal newBid){
            checkNull(newBid);
            checkNegativeNumber(newBid);
            bid = newBid;
            return this;
        }

        public PriceBuilder setDateTime(ZonedDateTime newDateTime){
            checkNull(newDateTime);
            dateTime = newDateTime;
            return this;
        }

        public PriceBuilder setIsTradable(boolean newIsTradable){
                isTradable = newIsTradable;
                return this;
        }

        private void checkNull(Object argument) {
            if(argument == null){
                throw new NullArgumentException();
            }
        }

        private void checkNegativeNumber(BigDecimal number) {
            if(number.compareTo(BigDecimal.ZERO)<0){
                throw new NegativeNumberException();
            }
        }

    }

    static class NullArgumentException extends RuntimeException{};
    static class NegativeNumberException extends RuntimeException{};

}
