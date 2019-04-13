package trader.broker.connector.oanda;

import com.oanda.v20.order.UnitsAvailable;
import com.oanda.v20.order.UnitsAvailableDetails;
import com.oanda.v20.pricing.ClientPrice;
import com.oanda.v20.pricing.PricingGetResponse;
import com.oanda.v20.pricing_common.PriceBucket;
import com.oanda.v20.pricing_common.PriceValue;
import com.oanda.v20.primitives.DateTime;
import com.oanda.v20.primitives.DecimalNumber;
import org.junit.Before;
import org.junit.Test;
import trader.broker.connector.oanda.transformer.OandaPriceTransformer;
import trader.entity.price.Price;
import trader.responder.Response;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OandaPriceTransformerTest {


    private static final BigDecimal DEFAULT_ASK = new BigDecimal(0.01)
            .setScale(5, RoundingMode.HALF_UP);
    private static final BigDecimal DEFAULT_BID = new BigDecimal(0.02)
            .setScale(5, RoundingMode.HALF_UP);
    private static final ZonedDateTime DEFAULT_DATE_TIME = ZonedDateTime.parse("2012-06-30T12:30:40Z[UTC]");
    private static final BigDecimal DEFAULT_AVAILABLE_UNITS = BigDecimal.ZERO;


    private OandaPriceTransformer priceTransformer;
    private PricingGetResponse responseMock;
    private Response response;

    @Before
    public void setUp(){
        priceTransformer = new OandaPriceTransformer();
        responseMock = mock(PricingGetResponse.class);
        response = mock(Response.class);
        when(response.getResponseDataStructure()).thenReturn(responseMock);
    }

    @Test
    public void whenCallTransformToPriceWithNull_TransformedPriceIsNotTradable() {
        Price price = priceTransformer.transformToPrice(null);
        assertFalse(price.isTradable());
    }

    @Test
    public void whenCallTransformToPriceWithNullPriceInsideResponse_TransformedPriceIsNotTradable(){
        when(responseMock.getPrices()).thenReturn(null);
        Price price = priceTransformer.transformToPrice(response);

        assertFalse(price.isTradable());
    }

    @Test
    public void whenCallTransformToPriceWithEmptyPriceListInsideResponse_TransformedPriceIsNotTradable(){
        when(responseMock.getPrices()).thenReturn(new ArrayList<>());
        Price price = priceTransformer.transformToPrice(response);

        assertFalse(price.isTradable());
    }


    @Test
    public void getPriceReturnsCorrectValues(){
        setFakePrice();

        Price price = priceTransformer.transformToPrice(response);

        assertEquals(price.getAsk(), DEFAULT_ASK);
        assertEquals(price.getBid(), DEFAULT_BID);
        assertTrue(price.isTradable());
        assertEquals(price.getDateTime(),DEFAULT_DATE_TIME);
        assertEquals(price.getAvailableUnits(), BigDecimal.ZERO);
    }

    private void setFakePrice() {
        DateTime dateTimeMock = setDateTime();
        PriceValue askPriceValueMock = setPriceValue(DEFAULT_ASK);
        PriceValue bidPriceValueMock = setPriceValue(DEFAULT_BID);
        PriceBucket askPriceBucketMock = setPriceBucket("ask", askPriceValueMock);
        PriceBucket bidPriceBucketMock = setPriceBucket("bid", bidPriceValueMock);
        List<PriceBucket> askPriceBuckets = createPriceBuckets(askPriceBucketMock);
        List<PriceBucket> bidPriceBuckets = createPriceBuckets(bidPriceBucketMock);
        List<ClientPrice> prices = createClientPrices(dateTimeMock, askPriceBuckets, bidPriceBuckets);

        when(responseMock.getPrices()).thenReturn(prices);
    }

    private DateTime setDateTime() {
        DateTime dateTimeMock = mock(DateTime.class);
        when(dateTimeMock.toString()).thenReturn("2012-06-30T12:30:40Z");
        return dateTimeMock;
    }

    private PriceValue setPriceValue(BigDecimal defaultAsk) {
        PriceValue askPriceValueMock = mock(PriceValue.class);
        when(askPriceValueMock.bigDecimalValue()).thenReturn(defaultAsk);
        return askPriceValueMock;
    }

    private List<ClientPrice> createClientPrices(DateTime dateTimeMock, List<PriceBucket> askPriceBuckets, List<PriceBucket> bidPriceBuckets) {
        ClientPrice clientPriceMock = setClientPrice(dateTimeMock, askPriceBuckets, bidPriceBuckets);
        List<ClientPrice> prices = new ArrayList<>();
        prices.add(clientPriceMock);
        return prices;
    }

    private List<PriceBucket> createPriceBuckets(PriceBucket askPriceBucketMock) {
        List<PriceBucket> askPriceBuckets = new ArrayList<>();
        askPriceBuckets.add(askPriceBucketMock);
        return askPriceBuckets;
    }

    private PriceBucket setPriceBucket(String type, PriceValue priceValueMock) {
        PriceBucket priceBucketMock = mock(PriceBucket.class);
        if(type.equalsIgnoreCase("ask"))
            when(priceBucketMock.getPrice()).thenReturn(priceValueMock);
        when(priceBucketMock.getPrice()).thenReturn(priceValueMock);
        return priceBucketMock;
    }


    private ClientPrice setClientPrice(DateTime dateTimeMock, List<PriceBucket> askPriceBuckets, List<PriceBucket> bidPriceBuckets) {
        ClientPrice clientPriceMock = mock(ClientPrice.class);
        UnitsAvailable unitsAvailableMock = setUnitsAvailable();

        when(clientPriceMock.getUnitsAvailable()).thenReturn(unitsAvailableMock);
        when(clientPriceMock.getTradeable()).thenReturn(true);
        when(clientPriceMock.getTime()).thenReturn(dateTimeMock);
        when(clientPriceMock.getAsks()).thenReturn(askPriceBuckets);
        when(clientPriceMock.getBids()).thenReturn(bidPriceBuckets);
        return clientPriceMock;
    }

    private UnitsAvailable setUnitsAvailable() {
        UnitsAvailable unitsAvailableMock = mock(UnitsAvailable.class);

        UnitsAvailableDetails unitsAvailableDetailsMock = setUnitsAvailableDetails();

        when(unitsAvailableMock.getDefault()).thenReturn(unitsAvailableDetailsMock);
        return unitsAvailableMock;
    }

    private UnitsAvailableDetails setUnitsAvailableDetails() {
        UnitsAvailableDetails unitsAvailableDetailsMock = mock(UnitsAvailableDetails.class);
        DecimalNumber decimalNumberMock = setDecimalNumber();

        when(unitsAvailableDetailsMock.getLong()).thenReturn(decimalNumberMock);
        return unitsAvailableDetailsMock;
    }

    private DecimalNumber setDecimalNumber() {
        DecimalNumber decimalNumberMock = mock(DecimalNumber.class);
        when(decimalNumberMock.bigDecimalValue()).thenReturn(DEFAULT_AVAILABLE_UNITS);
        return decimalNumberMock;
    }

}
