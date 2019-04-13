package trader.broker.connector.oanda.transformer;

import com.oanda.v20.order.Order;
import com.oanda.v20.trade.TradeSummary;
import org.junit.Before;
import org.junit.Test;
import trader.CommonTestClassMembers;
import trader.entity.candlestick.Candlestick;
import trader.entity.price.Price;
import trader.entity.trade.BrokerTradeDetails;
import trader.responder.Response;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OandaTransformerTest {

    private OandaTradeSummaryTransformer tradeSummaryTransformerMock;
    private BrokerTradeDetails tradeDetailsMock;
    private OandaOrderTransformer orderTransformerMock;
    private trader.entity.order.Order orderMock;
    private OandaCandleTransformer candleTransformerMock;
    private Response responseMock;
    private OandaPriceTransformer priceTransformerMock;
    private OandaTransformer transformer;
    private Price priceMock;
    private CommonTestClassMembers commonMembers;

    @Before
    public void setUp() throws Exception {
        tradeSummaryTransformerMock = mock(OandaTradeSummaryTransformer.class);
        tradeDetailsMock = mock(BrokerTradeDetails.class);
        orderTransformerMock = mock(OandaOrderTransformer.class);
        orderMock = mock(trader.entity.order.Order.class);
        candleTransformerMock = mock(OandaCandleTransformer.class);
        responseMock = mock(Response.class);
        priceTransformerMock = mock(OandaPriceTransformer.class);
        priceMock = mock(Price.class);
        transformer = new OandaTransformer();
        commonMembers = new CommonTestClassMembers();
    }


    @Test
    public void WhenCallTransformTradeSummary_CorrectResult(){
        when(tradeSummaryTransformerMock.transformTradeSummary(
                any(TradeSummary.class), any(List.class))).thenReturn(tradeDetailsMock);
        commonMembers.changeFieldObject(
                transformer, "oandaTradeSummaryTransformer",tradeSummaryTransformerMock);
        TradeSummary tradeSummaryMock = mock(TradeSummary.class);
        BrokerTradeDetails tradeDetails = transformer.transformTradeSummary(tradeSummaryMock, new ArrayList<>());

        assertEquals(tradeDetailsMock, tradeDetails);
    }

    @Test
    public void WhenCallTransformOrder_CorrectResult(){
        when(orderTransformerMock.transformOrder(any(Order.class))).thenReturn(orderMock);
        commonMembers.changeFieldObject(transformer, "oandaOrderTransformer", orderTransformerMock);
        Order order = mock(Order.class);
        trader.entity.order.Order orderResult = transformer.transformOrder(order);

        assertEquals(orderMock, orderResult);
    }

    @Test
    public void WhenCallTransformCandlesticks_CorrectResult(){
        List<Candlestick> expectedList = new ArrayList<>();
        when(candleTransformerMock.transformCandlesticks(responseMock)).thenReturn(expectedList);
        commonMembers.changeFieldObject(transformer, "oandaCandlesTransformer", candleTransformerMock);
        List listResult = transformer.transformCandlesticks(responseMock);

        assertEquals(expectedList, listResult);
    }

    @Test
    public void WhenCallTransformForPrice_CorrectResult(){
        when(priceTransformerMock.transformToPrice(responseMock)).thenReturn(priceMock);
        commonMembers.changeFieldObject(transformer, "oandaPriceTransformer", priceTransformerMock);

        Price price = transformer.transformToPrice(responseMock);

        assertEquals(priceMock, price);
    }

}