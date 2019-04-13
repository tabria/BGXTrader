package trader.entity.trade;

import trader.exception.NegativeNumberException;
import java.math.BigDecimal;

public final class TradeImpl implements Trade {

    private static final BigDecimal DEFAULT_PRICE = BigDecimal.valueOf(0.0001);

    private BigDecimal entryPrice;
    private BigDecimal stopLossPrice;
    private boolean tradable;
    private Direction direction;

    public TradeImpl() {
        direction = Direction.FLAT;
        tradable = false;
        entryPrice = DEFAULT_PRICE;
        stopLossPrice = DEFAULT_PRICE;
    }

    @Override
    public Direction getDirection() {
        return this.direction;
    }

    @Override
    public void setDirection(String direction) {
        if (verifyInput(direction)) return;
        this.direction = Direction.valueOf(direction.trim().toUpperCase());
    }

    @Override
    public boolean getTradable(){
        return this.tradable;
    }

    @Override
    public void setTradable(String tradable) {
        if (verifyInput(tradable)) return;
        this.tradable = Boolean.parseBoolean(tradable.trim());
    }

    @Override
    public BigDecimal getEntryPrice(){
        return this.entryPrice;
    }

    @Override
    public void setEntryPrice(String entryPrice) {
        if (verifyInput(entryPrice)) return;
        checkForNegativeNumbers(entryPrice.trim());
        this.entryPrice = new BigDecimal(entryPrice.trim());
    }


    @Override
    public BigDecimal getStopLossPrice(){
        return this.stopLossPrice;
    }

    @Override
    public void setStopLossPrice(String stopLossPrice) {
        if (verifyInput(stopLossPrice)) return;
        checkForNegativeNumbers(stopLossPrice.trim());
        this.stopLossPrice = new BigDecimal(stopLossPrice.trim());
    }

    @Override
    public String toString() {
        return "Trade{" +
                "entryPrice=" + entryPrice .toString() +
                ", stopLossPrice=" + stopLossPrice.toString() +
                ", tradable=" + tradable +
                ", direction=" + direction.toString() +
                '}';
    }

    private boolean verifyInput(String str) {
        return str == null || str.trim().isEmpty();
    }

    private void checkForNegativeNumbers(String entryPrice) {
        BigDecimal price = new BigDecimal(entryPrice);
        if(price.compareTo(BigDecimal.ZERO)<=0)
            throw new NegativeNumberException();
    }
}
