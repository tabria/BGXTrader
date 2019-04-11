package trader.entity.order;

import trader.entity.order.enums.OrderType;
import trader.exception.NullArgumentException;

import java.math.BigDecimal;

public class OrderImpl implements Order {

    private String id;
    private OrderType orderType;
    private String instrument;
    private BigDecimal units;
    private BigDecimal stopLossPrice;

    public OrderImpl(String id, OrderType orderType, String instrument, BigDecimal units, BigDecimal stopLossPrice) {
        if(id == null || orderType == null || instrument == null || units == null || stopLossPrice == null)
            throw new NullArgumentException();
        this.id = id;
        this.orderType = orderType;
        this.instrument = instrument;
        this.units = units;
        this.stopLossPrice = stopLossPrice;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public OrderType getOrderType() {
        return orderType;
    }

    @Override
    public String getInstrument() {
        return instrument;
    }

    @Override
    public BigDecimal getUnits() {
        return units;
    }

    @Override
    public BigDecimal getStopLossPrice() {
        return stopLossPrice;
    }
}
