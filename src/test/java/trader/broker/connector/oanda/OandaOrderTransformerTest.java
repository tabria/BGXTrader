package trader.broker.connector.oanda;

import com.oanda.v20.order.MarketIfTouchedOrder;
import com.oanda.v20.order.OrderID;
import com.oanda.v20.order.OrderType;
import com.oanda.v20.pricing_common.PriceValue;
import com.oanda.v20.primitives.DecimalNumber;
import com.oanda.v20.primitives.Instrument;
import com.oanda.v20.primitives.InstrumentName;
import com.oanda.v20.transaction.StopLossDetails;
import org.junit.Before;
import org.junit.Test;
import trader.entity.order.Order;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OandaOrderTransformerTest {

    private OandaOrderTransformer transformer;
    private MarketIfTouchedOrder orderMock;
    private OrderID orderID;
    private StopLossDetails stopLossDetailsMock;
    private PriceValue priceValueMock;
    private InstrumentName instrumentNameMock;
    private DecimalNumber decimalNumberMock;


    @Before
    public void setUp() throws Exception {

        orderID = mock(OrderID.class);
        stopLossDetailsMock = mock(StopLossDetails.class);
        priceValueMock = mock(PriceValue.class);
        instrumentNameMock = mock(InstrumentName.class);
        decimalNumberMock = mock(DecimalNumber.class);
        orderMock = mock(MarketIfTouchedOrder.class);
        transformer = new OandaOrderTransformer();


    }

    @Test
    public void WhenCallTransformOrderWithNullOrder_ReturnNull(){
        Order order = transformer.transformOrder(null);

        assertNull(order);
    }

    @Test
    public void WhenCallTransformWithCorrectOrder_CorrectResult(){
        String id = "11";
        String instrument ="EUR_USD";
        BigDecimal stopLossPrice = BigDecimal.valueOf(1.2020);
        BigDecimal units = BigDecimal.valueOf(100);
        setFakeMarketIfTouchedOrder(id, instrument, stopLossPrice, units);

        Order order = transformer.transformOrder(orderMock);

        assertEquals(id, order.getId());
        assertEquals(instrument, order.getInstrument());
        assertEquals(stopLossPrice, order.getStopLossPrice());
        assertEquals(units, order.getUnits());

    }

    private void setFakeMarketIfTouchedOrder(String id, String instrument, BigDecimal stopLossPrice, BigDecimal units) {
        when(decimalNumberMock.bigDecimalValue()).thenReturn(units);
        when(priceValueMock.bigDecimalValue()).thenReturn(stopLossPrice);
        when(stopLossDetailsMock.getPrice()).thenReturn(priceValueMock);
        when(instrumentNameMock.toString()).thenReturn(instrument);
        when(orderID.toString()).thenReturn(id);
        when(orderMock.getId()).thenReturn(orderID);
        when(orderMock.getType()).thenReturn(OrderType.MARKET_IF_TOUCHED);
        when(orderMock.getInstrument()).thenReturn(instrumentNameMock);
        when(orderMock.getStopLossOnFill()).thenReturn(stopLossDetailsMock);
        when(orderMock.getUnits()).thenReturn(decimalNumberMock);
    }
}
