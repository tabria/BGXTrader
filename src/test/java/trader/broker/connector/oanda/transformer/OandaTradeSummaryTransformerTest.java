package trader.broker.connector.oanda.transformer;

import com.oanda.v20.order.Order;
import com.oanda.v20.order.OrderID;
import com.oanda.v20.order.OrderType;
import com.oanda.v20.order.StopLossOrder;
import com.oanda.v20.pricing_common.PriceValue;
import com.oanda.v20.primitives.DecimalNumber;
import com.oanda.v20.trade.TradeID;
import com.oanda.v20.trade.TradeSummary;
import org.junit.Before;
import org.junit.Test;
import trader.entity.trade.BrokerTradeDetails;
import trader.exception.NullArgumentException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OandaTradeSummaryTransformerTest {

    private OandaTradeSummaryTransformer transformer;


    @Before
    public void setUp() throws Exception {
        transformer = new OandaTradeSummaryTransformer();
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallTransformTradeSummaryWithNulltradeSummary_Exception(){
       transformer.transformTradeSummary(null, new ArrayList<>());
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallTransformTradeSummaryWithNullOrdersList_Exception(){
        transformer.transformTradeSummary(mock(TradeSummary.class), null);
    }

    @Test
    public void WhenCallTransformWithCorrectTradeSummaryAndStopLossOrderID_CorrectResult(){
        TradeSummary tradeSummaryMock = createFakeTradeSummary("12", "13", "1.1234", "-154", "-124");
        StopLossOrder stopLossOrder = setFakeStopLossOrder("1.1212", "13");
        List<Order> orders = new ArrayList<>();
        orders.add(stopLossOrder);

        BrokerTradeDetails tradeDetails = transformer.transformTradeSummary(tradeSummaryMock, orders);

        assertEquals("12", tradeDetails.getTradeID());
        assertEquals("13", tradeDetails.getStopLossOrderID());
        assertEquals(new BigDecimal("1.1234"), tradeDetails.getOpenPrice());
        assertEquals(new BigDecimal("-154"), tradeDetails.getInitialUnits());
        assertEquals(new BigDecimal("-124"), tradeDetails.getCurrentUnits());
        assertEquals(new BigDecimal("1.1212"), tradeDetails.getStopLossPrice());
    }

    @Test
    public void WhenCallTransformWithCorrectTradeSummaryAndStopLossOrderIDDoNotMatch_StopLostPriceMustBeZero(){
        TradeSummary tradeSummaryMock = createFakeTradeSummary("12", "13", "1.1234", "-154", "-124");
        StopLossOrder stopLossOrder = setFakeStopLossOrder("1.1212", "17");
        List<Order> orders = new ArrayList<>();
        orders.add(stopLossOrder);

        BrokerTradeDetails tradeDetails = transformer.transformTradeSummary(tradeSummaryMock, orders);

        assertEquals("12", tradeDetails.getTradeID());
        assertEquals("13", tradeDetails.getStopLossOrderID());
        assertEquals(new BigDecimal("1.1234"), tradeDetails.getOpenPrice());
        assertEquals(new BigDecimal("-154"), tradeDetails.getInitialUnits());
        assertEquals(new BigDecimal("-124"), tradeDetails.getCurrentUnits());
        assertEquals(new BigDecimal("0"), tradeDetails.getStopLossPrice());
    }

    private TradeSummary createFakeTradeSummary(String tradeID, String orderID, String openPrice, String initialUnits, String currentUnits) {

        TradeID tradeIDMock = setFakeTradeID(tradeID);
        OrderID orderIDMock = setFakeOrderID(orderID);
        PriceValue priceValueMock = setFakePriceValue(openPrice);
        DecimalNumber initialDecimalNumberMock = setFakeDecimalNumber(initialUnits);
        DecimalNumber decimalNumberMock = setFakeDecimalNumber(currentUnits);


        return setFakeTradeSummary(tradeIDMock, orderIDMock, priceValueMock, initialDecimalNumberMock, decimalNumberMock);
    }

    private TradeSummary setFakeTradeSummary(TradeID tradeIDMock, OrderID orderIDMock, PriceValue priceValueMock, DecimalNumber initialDecimalNumberMock, DecimalNumber decimalNumberMock){
        TradeSummary tradeSummaryMock = mock(TradeSummary.class);
        when(tradeSummaryMock.getId()).thenReturn(tradeIDMock);
        when(tradeSummaryMock.getStopLossOrderID()).thenReturn(orderIDMock);
        when(tradeSummaryMock.getPrice()).thenReturn(priceValueMock);
        when(tradeSummaryMock.getInitialUnits()).thenReturn(initialDecimalNumberMock);
        when(tradeSummaryMock.getCurrentUnits()).thenReturn(decimalNumberMock);
        return tradeSummaryMock;
    }

    private DecimalNumber setFakeDecimalNumber(String number){
        DecimalNumber decimalNumberMock = mock(DecimalNumber.class);
        when(decimalNumberMock.toString()).thenReturn(number);
        return decimalNumberMock;
    }


    private TradeID setFakeTradeID(String id){
        TradeID tradeIDMock = mock(TradeID.class);
        when(tradeIDMock.toString()).thenReturn(id);
        return tradeIDMock;
    }

    private OrderID setFakeOrderID(String id){
        OrderID orderIDMock = mock(OrderID.class);
        when(orderIDMock.toString()).thenReturn(id);
        return orderIDMock;
    }

    private PriceValue setFakePriceValue(String price){
        PriceValue priceValueMock = mock(PriceValue.class);
        when(priceValueMock.toString()).thenReturn(price);
        return priceValueMock;
    }

    private StopLossOrder setFakeStopLossOrder(String price, String orderID){
        StopLossOrder stopLossOrderMock = mock(StopLossOrder.class);
        PriceValue priceValue = setFakePriceValue(price);
        OrderID orderid = setFakeOrderID(orderID);
        when(stopLossOrderMock.getType()).thenReturn(OrderType.STOP_LOSS);
        when(stopLossOrderMock.getPrice()).thenReturn(priceValue);
        when(stopLossOrderMock.getId()).thenReturn(orderid);
        return stopLossOrderMock;
    }

    private Order setFakeOrder(String price, String orderID){
        Order stopLossOrderMock = mock(Order.class);
        PriceValue priceValue = setFakePriceValue(price);
        OrderID orderid = setFakeOrderID(orderID);
        when(stopLossOrderMock.getType()).thenReturn(OrderType.STOP_LOSS);
        when(stopLossOrderMock.getId()).thenReturn(orderid);
        return stopLossOrderMock;
    }
}
