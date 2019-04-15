package trader.broker.connector.oanda;

import com.oanda.v20.Context;
import com.oanda.v20.account.Account;
import com.oanda.v20.order.*;
import com.oanda.v20.primitives.AccountUnits;
import com.oanda.v20.trade.TradeSetDependentOrdersResponse;
import com.oanda.v20.trade.TradeSummary;
import com.oanda.v20.transaction.Transaction;
import com.oanda.v20.transaction.TransactionID;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import trader.CommonTestClassMembers;
import trader.broker.connector.BaseGateway;
import trader.broker.connector.BrokerConnector;
import trader.broker.connector.Transformable;
import trader.broker.connector.oanda.transformer.OandaCandleTransformer;
import trader.broker.connector.oanda.transformer.OandaOrderTransformer;
import trader.broker.connector.oanda.transformer.OandaPriceTransformer;
import trader.broker.connector.oanda.transformer.OandaTransformer;
import trader.entity.candlestick.Candlestick;
import trader.entity.price.Price;
import trader.entity.price.PriceImpl;
import trader.entity.trade.BrokerTradeDetails;
import trader.exception.BadRequestException;
import trader.exception.EmptyArgumentException;
import trader.exception.NullArgumentException;
import trader.requestor.Request;
import trader.responder.Response;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static junit.framework.TestCase.assertSame;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class OandaGatewayTest {

    private static final String URL = "xxx.com";
    private static final String FAKE_ACCOUNT_ID = "12345";
    private CommonTestClassMembers commonMembers;
    private OandaGateway oandaGateway;
    private Context contextMock;
    private Price mockPrice;
    private Response responseMock;
    private BrokerConnector connectorMock;
    private Transaction transactionMock;
    private TransactionID transactionIDMock;
    private OandaResponseBuilder mockResponseBuilder;
    private OandaRequestBuilder mockRequestBuilder;
    private ArgumentCaptor<HashMap> argument;
    private Account accountMock;
    private Transformable oandaTransformerMock;


    @Before
    public void setUp() {
        commonMembers = new CommonTestClassMembers();
        contextMock = mock(Context.class);
        mockPrice = mock(PriceImpl.class);
        responseMock = mock(Response.class);
        connectorMock = mock(BrokerConnector.class);
        when(connectorMock.getUrl()).thenReturn(URL);
        transactionMock = mock(Transaction.class);
        transactionIDMock = mock(TransactionID.class);
        mockResponseBuilder = mock(OandaResponseBuilder.class);
        mockRequestBuilder = mock(OandaRequestBuilder.class);
        argument = ArgumentCaptor.forClass(HashMap.class);
        accountMock = mock(Account.class);
        oandaTransformerMock = mock(OandaTransformer.class);
        oandaGateway = (OandaGateway) BaseGateway.create("Oanda", connectorMock);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void WhenCreatedThenPriceSettingsMustNotBeNull(){
        HashMap<String, String> priceSettings = (HashMap<String, String>) commonMembers.extractFieldObject(oandaGateway, "priceSettings");

        assertNotNull(priceSettings);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void WhenCreatedThenAccountSettingsMustNotBeNull(){
        HashMap<String, String> accountSettings = (HashMap<String, String>) commonMembers.extractFieldObject(oandaGateway, "accountSettings");

        assertNotNull(accountSettings);
    }

    @Test
    public void WhenCallGetConnector_CorrectConnector(){
        assertEquals(connectorMock, oandaGateway.getConnector());
    }

    @Test
    public void WhenCallGetMarginUsedThenReturnCorrectResult(){
        AccountUnits accountUnitsMock = setFalseAccountUnits(accountMock);
        when(accountMock.getMarginUsed()).thenReturn(accountUnitsMock);

        assertEquals(BigDecimal.TEN, oandaGateway.getMarginUsed());
    }

    @Test
    public void WhenCallGetAvailableMarginThenReturnCorrectResult(){
        AccountUnits accountUnitsMock = setFalseAccountUnits(accountMock);
        when(accountMock.getMarginAvailable()).thenReturn(accountUnitsMock);

        assertEquals(BigDecimal.TEN, oandaGateway.getAvailableMargin());
    }

    @Test
    public void WhenCallGetBalanceThenReturnCorrectResult(){
        AccountUnits accountUnitsMock = setFalseAccountUnits(accountMock);
        when(accountMock.getBalance()).thenReturn(accountUnitsMock);

        assertEquals(BigDecimal.TEN, oandaGateway.getBalance());
    }

    @Test
    public void WhenCallValidateConnector_ContextMustNotBeNull(){
        setOandaValidator();
        oandaGateway.validateConnector();

        assertNotNull(oandaGateway.getContext());
    }

    @Test
    public void getContextReturnCorrectContext(){
        setFakeContext();

        assertEquals(oandaGateway.getContext(), contextMock);
    }

    @Test
    public void testGetPriceToReturnCorrectValue(){
        setFakePrice();
        Price actualPrice = oandaGateway.getPrice("EUR_USD");

        assertEquals(mockPrice, actualPrice);
    }

    @Test
    public void testGetCandlesToReturnCorrectValue(){
        List<Candlestick> candles = new ArrayList<>();
        HashMap<String, String> settings = new HashMap<>();
        setFakeContext();
        setFakeBuilders();
        setFakeCandlestickList(candles);
        List<Candlestick> actualCandles = oandaGateway.getCandles(settings);

        assertSame(candles, actualCandles);
    }


    @Test
    public void WhenCallTotalOpenTradesSize_CorrectReturnValue(){
        List<TradeSummary> trades = new ArrayList<>();
        setFakeTradeSummaryList(trades);

        assertEquals(0,  oandaGateway.totalOpenTradesSize());
    }

    @Test
    public void WhenCallTotalOpenOrdersSize_CorrectReturnValue(){
        List<TradeSummary> trades = new ArrayList<>();
        setFakeTradeSummaryList(trades);

        assertEquals(0,  oandaGateway.totalOpenOrdersSize());
    }

    @Test
    public void WhenCallGetOrderAndThereAreNoOrders_ReturnNull(){
        setFakeContext();
        setFakeBuilders();
        List<Order> orderList = new ArrayList<>();
        when(responseMock.getResponseDataStructure()).thenReturn(accountMock);
        when(accountMock.getOrders()).thenReturn(orderList);

        trader.entity.order.Order order = oandaGateway.getOrder(trader.entity.order.enums.OrderType.MARKET_IF_TOUCHED);

        assertNull(order);
    }

    @Test
    public void WhenCallGetOrderWithMarketIfTouchedOrderType_CorrectOrder(){
        setFakeContext();
        setFakeBuilders();
        MarketIfTouchedOrder orderMock = setFakeMarketIFTouchedOrder();
        setFakeOrderList(orderMock);
        trader.entity.order.Order expected = mock(trader.entity.order.Order.class);
        setFakeOrderTransformer(expected);

        trader.entity.order.Order order = oandaGateway.getOrder(trader.entity.order.enums.OrderType.MARKET_IF_TOUCHED);

        assertEquals(order, expected);
    }

    @Test
    public void WhenCallPlaceMarketIfTouchedOrder_CorrectResult(){
        String expectedID = "18";
        makeFakeOrder(expectedID);
        String result = oandaGateway.placeMarketIfTouchedOrder(new HashMap<>());


        assertEquals(expectedID, result);
    }

    @Test
    public void WhenCallPlaceMarketIfTouchedOrderCheckForValidSettings(){
        String expectedID = "2";
        makeFakeOrder(expectedID);
        oandaGateway.placeMarketIfTouchedOrder(new HashMap<>());
        verify(mockRequestBuilder).build(anyString(), argument.capture());
        HashMap<String, String> settings = argument.getValue();

        assertEquals(1, settings.size());
        assertEquals(FAKE_ACCOUNT_ID, settings.get("accountID"));
    }

    @Test
    public void WhenCallPlaceMarketOrder_CorrectResult(){
        String expectedID = "13";
        makeFakeOrder(expectedID);
        String result = oandaGateway.placeMarketOrder(new HashMap<>());

        assertEquals(expectedID, result);
    }

    @Test
    public void WhenCallPlaceMarketOrderCheckForValidSettings(){
        String expectedID = "2";
        makeFakeOrder(expectedID);
        oandaGateway.placeMarketOrder(new HashMap<>());
        verify(mockRequestBuilder).build(anyString(), argument.capture());
        HashMap<String, String> settings = argument.getValue();

        assertEquals(1, settings.size());
        assertEquals(FAKE_ACCOUNT_ID, settings.get("accountID"));
    }

    @Test(expected = BadRequestException.class)
    public void WhenCallGetTradeDetailsAndThereAreNoTrades_ThrowException(){
        setFakeContext();
        setFakeBuilders();
        List<TradeSummary> tradesList = new ArrayList<>();
        when(responseMock.getResponseDataStructure()).thenReturn(accountMock);
        when(accountMock.getTrades()).thenReturn(tradesList);

        oandaGateway.getTradeDetails(2);
    }

    @Test
    public void WhenCallGetTradeDetailsWithExistingTradeIndex_CorrectResult(){
        setFakeContext();
        setFakeBuilders();
        TradeSummary tradeSummaryMock = mock(TradeSummary.class);
        BrokerTradeDetails tradeDetailsMock = mock(BrokerTradeDetails.class);
        List<TradeSummary> tradesList = new ArrayList<>();
        tradesList.add(tradeSummaryMock);
        setFakeTradeSummaryList(tradesList);

        when(oandaTransformerMock.transformTradeSummary(tradeSummaryMock, new ArrayList<>())).thenReturn(tradeDetailsMock);
        setFakeTransformer();

        BrokerTradeDetails tradeDetails = oandaGateway.getTradeDetails(0);

        assertEquals(tradeDetailsMock, tradeDetails);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallSetTradeStopLossPriceWithNullTradeID_Exception(){
        oandaGateway.setTradeStopLossPrice(null, "1.12");
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallSetTradeStopLossPriceWithNullStopLossPrice_Exception(){
        oandaGateway.setTradeStopLossPrice("1", null);
    }

    @Test(expected = EmptyArgumentException.class)
    public void WhenCallSetTradeStopLossPriceWithEmptyTradeID_Exception(){
        oandaGateway.setTradeStopLossPrice("  ", "1.11");
    }

    @Test(expected = EmptyArgumentException.class)
    public void WhenCallSetTradeStopLossPriceWithEmptyStopLossPrice_Exception(){
        oandaGateway.setTradeStopLossPrice("1", "   ");
    }

    @Test
    public void WhenCallSetTradeStopLossPriceWithCorrectTradeID_CorrectResult(){
        String expectedID = "14";
        String tradeID = "12";
        String price = "1.2222";
        setFakeContext();
        setFakeConnector();
        setFakeBuilders();
        TradeSetDependentOrdersResponse response = mock(TradeSetDependentOrdersResponse.class);
        TransactionID transactionID = setFakeTransactionID(expectedID);
        when(response.getLastTransactionID()).thenReturn(transactionID);
        when(responseMock.getResponseDataStructure()).thenReturn(response);

        String actual = oandaGateway.setTradeStopLossPrice(tradeID, price);
        verify(mockRequestBuilder).build(anyString(), argument.capture());
        HashMap<String, String> settings = argument.getValue();

        assertEquals(expectedID, actual);
        assertEquals(FAKE_ACCOUNT_ID, settings.get("accountID"));
        assertEquals(tradeID, settings.get("tradeID"));
        assertEquals(price, settings.get("price"));
    }


    @Test(expected = NullArgumentException.class)
    public void WhenCallCancelOrderWithNullOrderID_Exception(){
        oandaGateway.cancelOrder(null);
    }

    @Test(expected = EmptyArgumentException.class)
    public void WhenCallCancelOrderWithEmptyOrderID_Exception(){
        oandaGateway.cancelOrder("  ");
    }

    @Test
    public void WhenCallCancelWithCorrectTradeID_CorrectResult(){
        String expectedID = "14";
        String orderID = "12";
        setFakeContext();
        setFakeConnector();
        setFakeBuilders();
        OrderCancelResponse orderCancelResponseMock = mock(OrderCancelResponse.class);
        TransactionID transactionID = setFakeTransactionID(expectedID);
        when(orderCancelResponseMock.getLastTransactionID()).thenReturn(transactionID);
        when(responseMock.getResponseDataStructure()).thenReturn(orderCancelResponseMock);

        String actual = oandaGateway.cancelOrder(orderID);
        verify(mockRequestBuilder).build(anyString(), argument.capture());
        HashMap<String, String> settings = argument.getValue();

        assertEquals(expectedID, actual);
        assertEquals(FAKE_ACCOUNT_ID, settings.get("accountID"));
        assertEquals(orderID, settings.get("orderID"));
    }

    private TransactionID setFakeTransactionID(String id){
        TransactionID transactionID = mock(TransactionID.class);
        when((transactionID.toString())).thenReturn(id);
        return transactionID;
    }

    private void setFakeOrderList(MarketIfTouchedOrder orderMock) {
        List<Order> orderList = new ArrayList<>();
        orderList.add(orderMock);
        when(responseMock.getResponseDataStructure()).thenReturn(accountMock);
        when(accountMock.getOrders()).thenReturn(orderList);
    }

    private void setFakeOrderTransformer(trader.entity.order.Order expected){

        when(oandaTransformerMock.transformOrder(any(Order.class))).thenReturn(expected);
        setFakeTransformer();
    }

    private MarketIfTouchedOrder setFakeMarketIFTouchedOrder() {
        MarketIfTouchedOrder orderMock = mock(MarketIfTouchedOrder.class);
        when(orderMock.getType()).thenReturn(OrderType.MARKET_IF_TOUCHED);
        return orderMock;
    }

    private void makeFakeOrder(String transactionID) {
        setFakeContext();
        setFakeBuilders();
        setFakeConnector();
        setFakeTransaction(transactionID);
        OrderCreateResponse orderResponseMock = mock(OrderCreateResponse.class);
        when(orderResponseMock.getOrderCreateTransaction()).thenReturn(transactionMock);
        when(responseMock.getResponseDataStructure()).thenReturn(orderResponseMock);
        //return oandaGateway.placeMarketIfTouchedOrder(new HashMap<>());
    }

    private void setFakeTransaction(String expectedID) {
        when(transactionIDMock.toString()).thenReturn(expectedID);
        when(transactionMock.getId()).thenReturn(transactionIDMock);
    }

    private AccountUnits setFalseAccountUnits(Account accountMock) {
        AccountUnits accountUnitsMock = mock(AccountUnits.class);
        when(accountUnitsMock.bigDecimalValue()).thenReturn(BigDecimal.TEN);
        setFakeContext();
        setFakeBuilders();
        when(responseMock.getResponseDataStructure()).thenReturn(accountMock);
        return accountUnitsMock;
    }

    private void setFakeTradeSummaryList(List<TradeSummary> trades) {
        setFakeContext();
        setFakeBuilders();
        when(responseMock.getResponseDataStructure()).thenReturn(accountMock);
        when(accountMock.getTrades()).thenReturn(trades);
    }



    private void setOandaValidator() {
        OandaAccountValidator mockValidator = mock(OandaAccountValidator.class);
        doNothing().when(mockValidator).validateAccount(connectorMock, contextMock);
        doNothing().when(mockValidator).validateAccountBalance(connectorMock, contextMock);
        commonMembers.changeFieldObject(oandaGateway, "oandaAccountValidator", mockValidator);
    }

    @SuppressWarnings("unchecked")
    private void setFakePrice() {
        setFakeContext();
        setFakeBuilders();
        setFakePriceTransformer();
        when(connectorMock.getUrl()).thenReturn("dd");
    }

    @SuppressWarnings("unchecked")
    private void setFakePriceTransformer() {
        when(oandaTransformerMock.transformToPrice(responseMock)).thenReturn(mockPrice);
        setFakeTransformer();
    }

    @SuppressWarnings("unchecked")
    private void setFakeCandlestickList(List<Candlestick> candles) {
        when(oandaTransformerMock.transformCandlesticks(responseMock)).thenReturn(candles);
        setFakeTransformer();
    }

    private void setFakeTransformer() {
        commonMembers.changeFieldObject(oandaGateway, "oandaTransformer", oandaTransformerMock);
    }

    private void setFakeConnector(){
        when(connectorMock.getAccountID()).thenReturn(FAKE_ACCOUNT_ID);
        commonMembers.changeFieldObject(oandaGateway, "connector", connectorMock);
    }

    @SuppressWarnings("unchecked")
    private void setFakeBuilders() {
        Request requestMock = mock(Request.class);
        commonMembers.changeFieldObject(oandaGateway, "oandaRequestBuilder", mockRequestBuilder);
        commonMembers.changeFieldObject(oandaGateway, "oandaResponseBuilder", mockResponseBuilder);
        when(mockRequestBuilder.build(anyString(), any(HashMap.class))).thenReturn(requestMock);
        when(mockResponseBuilder.buildResponse(anyString(), eq(requestMock))).thenReturn(responseMock);
    }

    private void setFakeContext() {
        commonMembers.changeFieldObject(oandaGateway, "context", contextMock);
    }
}
