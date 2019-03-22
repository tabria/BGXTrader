package trader.price;

import trader.exception.NegativeNumberException;
import trader.exception.NullArgumentException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.Objects;

import static trader.strategy.BGXStrategy.StrategyConfig.*;

public class Price implements Pricing {

    private BigDecimal ask;
    private BigDecimal bid;
    private ZonedDateTime dateTime;
    private boolean isTradable;
    private BigDecimal availableUnits;

    private Price(PriceBuilder priceBuilder){
        ask = priceBuilder.ask;
        bid = priceBuilder.bid;
        dateTime = priceBuilder.dateTime;
        isTradable = priceBuilder.isTradable;
        availableUnits = priceBuilder.availableUnits;
    }

    @Override
    public BigDecimal getAsk(){
        return ask;
    }

    @Override
    public BigDecimal getBid(){
        return bid;
    }

    @Override
    public ZonedDateTime getDateTime(){
        return dateTime;
    }

    @Override
    public boolean isTradable() {
        return isTradable;
    }

    @Override
    public BigDecimal getAvailableUnits(){
        return availableUnits;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Price price = (Price) o;
        return isTradable == price.isTradable &&
                Objects.equals(ask, price.ask) &&
                Objects.equals(bid, price.bid) &&
                Objects.equals(dateTime, price.dateTime) &&
                Objects.equals(availableUnits, price.availableUnits);
    }

    @Override
    public int hashCode() {

        return Objects.hash(ask, bid, dateTime, isTradable, availableUnits);
    }

    @Override
    public String toString() {
        return "Price{" +
                "ask=" + ask +
                ", bid=" + bid +
                ", dateTime=" + dateTime +
                ", isTradable=" + isTradable +
                ", availableUnits=" + availableUnits +
                '}';
    }

    public static class PriceBuilder{

        private static final BigDecimal DEFAULT_ASK = new BigDecimal(0.01)
                .setScale(SCALE, RoundingMode.HALF_UP);
        private static final BigDecimal DEFAULT_BID = new BigDecimal(0.02)
                .setScale(SCALE, RoundingMode.HALF_UP);
        private static final ZonedDateTime DEFAULT_DATE_TIME = ZonedDateTime.parse("2012-06-30T12:30:40Z[UTC]");
        private static final BigDecimal DEFAULT_AVAILABLE_UNITS = BigDecimal.ZERO;

        private BigDecimal ask;
        private BigDecimal bid;
        private ZonedDateTime dateTime;
        private boolean isTradable;
        private BigDecimal availableUnits;

        public PriceBuilder(){
            ask = DEFAULT_ASK;
            bid = DEFAULT_BID;
            dateTime = DEFAULT_DATE_TIME;
            isTradable = true;
            availableUnits = DEFAULT_AVAILABLE_UNITS;
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

        public PriceBuilder setAvailableUnits(BigDecimal newUnits){
            checkNull(newUnits);
            checkNegativeNumber(newUnits);
            availableUnits = newUnits;
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



}
;