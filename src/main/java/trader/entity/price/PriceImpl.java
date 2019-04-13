package trader.entity.price;

import trader.exception.NegativeNumberException;
import trader.exception.NullArgumentException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.Objects;


public class PriceImpl implements Price {

    private BigDecimal ask;
    private BigDecimal bid;
    private ZonedDateTime dateTime;
    private boolean isTradable;
    private BigDecimal availableUnits;

    private PriceImpl(PriceBuilder priceBuilder){
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
        PriceImpl priceImpl = (PriceImpl) o;
        return isTradable == priceImpl.isTradable &&
                Objects.equals(ask, priceImpl.ask) &&
                Objects.equals(bid, priceImpl.bid) &&
                Objects.equals(dateTime, priceImpl.dateTime) &&
                Objects.equals(availableUnits, priceImpl.availableUnits);
    }

    @Override
    public int hashCode() {

        return Objects.hash(ask, bid, dateTime, isTradable, availableUnits);
    }

    @Override
    public String toString() {
        return "PriceImpl{" +
                "ask=" + ask +
                ", bid=" + bid +
                ", dateTime=" + dateTime +
                ", isTradable=" + isTradable +
                ", availableUnits=" + availableUnits +
                '}';
    }

    public static class PriceBuilder{

        private static final BigDecimal DEFAULT_ASK = new BigDecimal(0.01)
                .setScale(5, RoundingMode.HALF_UP);
        private static final BigDecimal DEFAULT_BID = new BigDecimal(0.02)
                .setScale(5, RoundingMode.HALF_UP);
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

        public PriceImpl build(){
            return new PriceImpl(this);
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