package trader.entry;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import trader.CommonTestClassMembers;
import trader.strategy.TradingStrategyConfiguration;
import trader.controller.TraderController;
import trader.entity.indicator.Indicator;
import trader.entity.trade.point.Point;
import trader.entity.trade.Direction;
import trader.entity.trade.Trade;
import trader.entry.standard.StandardEntryStrategy;
import trader.exception.BadRequestException;
import trader.exception.NoSuchStrategyException;
import trader.exception.NullArgumentException;
import trader.responder.Response;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StandardEntryStrategyTest {

    private List<BigDecimal> rsiValues = createIndicatorValues(49, 50, 50);
    private List<BigDecimal> priceSMAValues = createIndicatorValues(1.22889, 1.22889, 1.23339);
    private List<BigDecimal> slowWMAValues = createIndicatorValues(1.22889, 1.22739, 1.22639);
    private List<BigDecimal> fastWMAValues = createIndicatorValues(1.22889, 1.22889, 1.23339);
    private List<BigDecimal> dailyValues = createIndicatorValues(1.5656, 1.5656, 1.5656);
    private List<BigDecimal> middleWMAValues = createIndicatorValues(1.23119,1.23119, 1.23196);


    private static final BigDecimal DEFAULT_ENTRY_FILTER = BigDecimal.valueOf(0.0020)
            .setScale(5, RoundingMode.HALF_UP);
    private static final BigDecimal DEFAULT_SPREAD = BigDecimal.valueOf(0.0002)
            .setScale(5, RoundingMode.HALF_UP);
    private static final BigDecimal DEFAULT_STOP_LOSS_FILTER = BigDecimal.valueOf(0.0005)
            .setScale(5, RoundingMode.HALF_UP);
    private static final BigDecimal FIRST_TARGET = BigDecimal.valueOf(0.0050)
            .setScale(5, RoundingMode.HALF_UP);
    private static final BigDecimal RSI_FILTER = BigDecimal.valueOf(50);


    private CommonTestClassMembers commonMembers;
    private Response responseMock;
    private Trade tradeMock;
    private ArgumentCaptor<HashMap> argument;
    private TraderController<Trade> tradeControllerMock;
    private List<Indicator> indicators;
    private TradingStrategyConfiguration configurationMock;
    private StandardEntryStrategy standardEntryStrategy;



    @Before
    public void before(){
        tradeMock = mock(Trade.class);
        responseMock = mock(Response.class);
        when(responseMock.getBody()).thenReturn(tradeMock);
        tradeControllerMock = mock(TraderController.class);
        when(tradeControllerMock.execute(any(HashMap.class))).thenReturn(responseMock);;
        argument = ArgumentCaptor.forClass(HashMap.class);
        setFalseInitialIndicators(rsiValues, priceSMAValues, slowWMAValues, fastWMAValues, dailyValues, middleWMAValues);
        commonMembers = new CommonTestClassMembers();
        configurationMock = mock(TradingStrategyConfiguration.class);
        setConfiguration();
        standardEntryStrategy = new StandardEntryStrategy();
        standardEntryStrategy.setConfiguration(configurationMock);
        standardEntryStrategy.setIndicators(indicators);
        standardEntryStrategy.setCreateTradeController(tradeControllerMock);
    }

    @Test
    public void WhenCreatedThenCreateTradeControllerMustBeSet(){
        Object createTradeController = commonMembers.extractFieldObject(standardEntryStrategy, "createTradeController");

        assertNotNull(createTradeController);
    }

    @Test
    public void WhenInstantiateThenDirectionMustBeFlat(){
        Direction direction = (Direction) commonMembers.extractFieldObject(standardEntryStrategy, "direction");

        assertNotNull(direction);
        assertEquals(Direction.FLAT, direction);
    }

    @Test(expected = NoSuchStrategyException.class)
    public void WhenInstantiateWithIndicatorsListSizeAboveRequired_Exception(){
        new StandardEntryStrategy();
        standardEntryStrategy.setIndicators(createFalseListOfIndicators(7));
        standardEntryStrategy.generateTrade();
    }

    @Test(expected = NoSuchStrategyException.class)
    public void WhenInstantiateWithIndicatorListSizeBelowRequired_Exception(){
        new StandardEntryStrategy();
        standardEntryStrategy.setIndicators(createFalseListOfIndicators(5));
        standardEntryStrategy.generateTrade();
    }

    @Test(expected = NullArgumentException.class)
    public void WhenSetConfigurationWithNull_Exception(){
        standardEntryStrategy.setConfiguration(null);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenSetCreateTraderControllerWithNull_Exception(){
        standardEntryStrategy.setCreateTradeController(null);
    }

    @Test
    public void WhenInstantiateThenFillIndicatorsFields(){
        Indicator fastWMA = getField("fastWMA");
        Indicator middleWMA = getField("middleWMA");
        Indicator slowWMA = getField("slowWMA");
        Indicator dailySMA = getField("dailySMA");
        Indicator priceSMA = getField("priceSMA");
        Indicator rsi = getField("rsi");

        assertEquals("fast", fastWMA.getPosition());
        assertEquals("rsi", rsi.getPosition());
        assertEquals("middle", middleWMA.getPosition());
        assertEquals("slow", slowWMA.getPosition());
        assertEquals("price", priceSMA.getPosition());
        assertEquals("daily", dailySMA.getPosition());
    }

    @Test(expected = BadRequestException.class)
    public void WhenInstantiateThenFillIndicatorsFieldsWithIndicatorWithNonExistingType_Exception(){
        indicators.remove(0);
        indicators.add(createFalseIndicator("miole", rsiValues));
        standardEntryStrategy = new StandardEntryStrategy();
        standardEntryStrategy.setIndicators(indicators);
    }

   @Test
    public void WhenFastWMACrossMiddleWMAFromBelowThenGenerateLongTradeDifferentFromDefault() {
        Trade trade = standardEntryStrategy.generateTrade();
        verify(tradeControllerMock).execute(argument.capture());
        int argumentSize = argument.getValue().size();

        assertTrue(argumentSize>0);
        assertEquals(tradeMock, trade);
    }

    @Test
    public void WhenFastWMACrossMiddleWMAFromAboveThenGenerateShortTradeDifferentFromDefault() {
        List<BigDecimal> slowWMAValues = createIndicatorValues(1.23889, 1.23739, 1.23639);
        List<BigDecimal> fastWMAValues = createIndicatorValues(1.23219, 1.23219, 1.23196);
        List<BigDecimal> middleWMAValues = createIndicatorValues(1.22889, 1.22889, 1.23339);
        setFalseInitialIndicators(rsiValues, priceSMAValues, slowWMAValues, fastWMAValues, dailyValues, middleWMAValues);

        assertForTradableTrade();
    }


    @Test
    public void WhenFastWMACrossMiddleWMAFromAboveWithRSIAboveFilterThenGenerateDefaultNonTradableTrade() {
        List<BigDecimal> priceSMAValues = createIndicatorValues(1.23119, 1.23096, 1.23096);
        List<BigDecimal> fastWMAValues = createIndicatorValues(1.23219, 1.23196, 1.23196);
        List<BigDecimal> middleWMAValues = createIndicatorValues(1.22889, 1.23339, 1.23339);
        List<BigDecimal> rsiValues = createIndicatorValues(51, 51, 51);
        setFalseInitialIndicators(rsiValues, priceSMAValues, slowWMAValues, fastWMAValues, dailyValues, middleWMAValues);

        assertForNonTradableDefaultTrade();
    }

    @Test
    public void WhenFastWMACrossMiddleWMAFromBelowWithRSIBelowFilterThenGenerateNonTradableSignal() {
        List<BigDecimal> rsiValues = createIndicatorValues(49, 49, 49);
        setFalseInitialIndicators(rsiValues, priceSMAValues, slowWMAValues, fastWMAValues, dailyValues, middleWMAValues);

        assertForNonTradableDefaultTrade();
    }

    @Test
    public void WhenFastWMACrossMiddleWMAWithFirstFastWMAPointOnTopOfFirstMiddleWMAPointThenGenerateLongNonDefaultTrade() {
        List<BigDecimal> fastWMAValues = createIndicatorValues(1.23119, 1.23119, 1.23339);
        List<BigDecimal> middleWMAValues = createIndicatorValues(1.23119, 1.23119, 1.23196);
        List<BigDecimal> slowWMAValues = createIndicatorValues(1.23019, 1.23039, 1.23039);
        List<BigDecimal> rsiValues = createIndicatorValues(49, 50, 50);
        setFalseInitialIndicators(rsiValues, priceSMAValues, slowWMAValues, fastWMAValues, dailyValues, middleWMAValues);

        assertForTradableTrade();
    }

    @Test
    public void WhenPriceSMACrossMiddleWMAFromBelowThenGenerateLongNonDefaultTrade() {
        List<BigDecimal> fastWMAValues = createIndicatorValues(1.22789, 1.22839, 1.22939);
        List<BigDecimal> slowWMAValues = createIndicatorValues(1.22689, 1.22739, 1.22639);
        setFalseInitialIndicators(rsiValues, priceSMAValues, slowWMAValues, fastWMAValues, dailyValues, middleWMAValues);

        assertForTradableTrade();
    }

    @Test
    public void WhenPriceSMACrossMiddleWMAFromAboveThenGenerateShortNonDefaultTrade() {
        List<BigDecimal> fastWMAValues = createIndicatorValues(1.23519, 1.23496, 1.23496);
        List<BigDecimal> priceSMAValues = createIndicatorValues(1.23219, 1.23219, 1.23196);
        List<BigDecimal> middleWMAValues = createIndicatorValues(1.22889, 1.22889, 1.23339);
        List<BigDecimal> slowWMAValues = createIndicatorValues(1.23889, 1.23739, 1.23639);
        setFalseInitialIndicators(rsiValues, priceSMAValues, slowWMAValues, fastWMAValues, dailyValues, middleWMAValues);

        assertForTradableTrade();

    }

    @Test
    public void WhenPriceSMACrossMiddleWMAFromBelowWithRSIBelowFilterThenGenerateNonTradableDefaultTrade() {
        List<BigDecimal> fastWMAValues = createIndicatorValues(1.23419, 1.23419, 1.23419);
        List<BigDecimal> priceSMAValues = createIndicatorValues(1.23219, 1.23376, 1.23386);
        List<BigDecimal> middleWMAValues = createIndicatorValues(1.23289, 1.23339, 1.23339);
        List<BigDecimal> dailyValues = createIndicatorValues(1.5656, 1.5656, 1.5656);
        List<BigDecimal> rsiValues = createIndicatorValues(49, 49, 22);
        setFalseInitialIndicators(rsiValues, priceSMAValues, slowWMAValues, fastWMAValues, dailyValues, middleWMAValues);

        assertForNonTradableDefaultTrade();
    }

    @Test
    public void WhenPriceSMACrossMiddleWMAFromAboveWithRSIAboveFilterThenGenerateNonTradableDefaultTrade() {
        List<BigDecimal> fastWMAValues = createIndicatorValues(1.23419, 1.23419, 1.23419);
        List<BigDecimal> priceSMAValues = createIndicatorValues(1.23219, 1.23196, 1.23196);
        List<BigDecimal> middleWMAValues = createIndicatorValues(1.22889, 1.23339, 1.23339);
        List<BigDecimal> dailyValues = createIndicatorValues(1.5656, 1.5656, 1.5656);
        List<BigDecimal> rsiValues = createIndicatorValues(51, 51, 51);
        setFalseInitialIndicators(rsiValues, priceSMAValues, slowWMAValues, fastWMAValues, dailyValues, middleWMAValues);

        assertForNonTradableDefaultTrade();
    }

    @Test
    public void WhenPriceSMACrossMiddleWMAWithFirstPriceWMAPointOnTopOfFirstMiddleWMAPointThenGenerateCorrectLongSignal() {
        List<BigDecimal> fastWMAValues = createIndicatorValues(1.23419, 1.23439, 1.23439);
        List<BigDecimal> priceSMAValues = createIndicatorValues(1.23119, 1.23119, 1.23339);
        List<BigDecimal> middleWMAValues = createIndicatorValues(1.23119, 1.23119, 1.23196);
        List<BigDecimal> slowWMAValues = createIndicatorValues(1.23019, 1.23039, 1.23039);
        setFalseInitialIndicators(rsiValues, priceSMAValues, slowWMAValues, fastWMAValues, dailyValues, middleWMAValues);

        assertForTradableTrade();
    }

    @Test
    public void WhenPriceSMACrossSlowWMAFromBelowThenGenerateLongNonDefaultTrade() {
        List<BigDecimal> fastWMAValues = createIndicatorValues(1.22989, 1.23019, 1.23019);
        List<BigDecimal> priceSMAValues = createIndicatorValues(1.22889, 1.22889, 1.22999);
        List<BigDecimal> middleWMAValues = createIndicatorValues(1.23119, 1.23196, 1.23196);
        List<BigDecimal> slowWMAValues = createIndicatorValues(1.22909, 1.22909, 1.22939);
        setFalseInitialIndicators(rsiValues, priceSMAValues, slowWMAValues, fastWMAValues, dailyValues, middleWMAValues);

        assertForTradableTrade();
    }

    @Test
    public void WhenPriceSMACrossSlowWMAFromAboveThenGenerateShortNonDefaultTrade() {
        List<BigDecimal> fastWMAValues = createIndicatorValues(1.23639, 1.23496, 1.23496);
        List<BigDecimal> priceSMAValues = createIndicatorValues(1.23991, 1.23991, 1.23716);
        List<BigDecimal> middleWMAValues = createIndicatorValues(1.23619, 1.23239, 1.23339);
        List<BigDecimal> slowWMAValues = createIndicatorValues(1.23889, 1.23889, 1.23739);
        setFalseInitialIndicators(rsiValues, priceSMAValues, slowWMAValues, fastWMAValues, dailyValues, middleWMAValues);

        assertForTradableTrade();
    }

    @Test
    public void WhenPriceSMACrossSlowWMAFromAboveWithRSIAboveFilterThenGenerateNonTradableDefaultTrade() {
        List<BigDecimal> fastWMAValues = createIndicatorValues(1.23639, 1.23496, 1.23496);
        List<BigDecimal> priceSMAValues = createIndicatorValues(1.23991, 1.23716, 1.23716);
        List<BigDecimal> middleWMAValues = createIndicatorValues(1.23619, 1.23239, 1.23339);
        List<BigDecimal> slowWMAValues = createIndicatorValues(1.23889, 1.23739, 1.23739);
        List<BigDecimal> dailyValues = createIndicatorValues(1.5656, 1.5656, 1.5656);
        List<BigDecimal> rsiValues = createIndicatorValues(51, 51, 51);
        setFalseInitialIndicators(rsiValues, priceSMAValues, slowWMAValues, fastWMAValues, dailyValues, middleWMAValues);

        assertForNonTradableDefaultTrade();
    }

    @Test
    public void WhenPriceSMACrossSlowWMAFromBelowWithRSIBelowFilterThenGenerateNonTradableDefaultTrade() {
        List<BigDecimal> fastWMAValues = createIndicatorValues(1.23639, 1.23496, 1.23496);
        List<BigDecimal> priceSMAValues = createIndicatorValues(1.23789, 1.23826, 1.23826);
        List<BigDecimal> middleWMAValues = createIndicatorValues(1.23619, 1.23239, 1.23339);
        List<BigDecimal> slowWMAValues = createIndicatorValues(1.23791, 1.23793, 1.23793);
        List<BigDecimal> dailyValues = createIndicatorValues(1.5656, 1.5656, 1.5656);
        List<BigDecimal> rsiValues = createIndicatorValues(49, 49, 49);
        setFalseInitialIndicators(rsiValues, priceSMAValues, slowWMAValues, fastWMAValues, dailyValues, middleWMAValues);

        assertForNonTradableDefaultTrade();
    }

    @Test
    public void WhenPriceSMACrossSlowWMAWithFirstPriceWMAPointOnTopOfFirstSlowWMAPointThenGenerateLongNonDefaultTrade() {
        List<BigDecimal> fastWMAValues = createIndicatorValues(1.23109, 1.23109, 1.23109);
        List<BigDecimal> priceSMAValues = createIndicatorValues(1.23019, 1.23019, 1.23099);
        List<BigDecimal> middleWMAValues = createIndicatorValues(1.23119, 1.23196, 1.23196);
        List<BigDecimal> slowWMAValues = createIndicatorValues(1.23019, 1.23019, 1.23039);
        List<BigDecimal> dailyValues = createIndicatorValues(1.5656, 1.5656, 1.5656);
        List<BigDecimal> rsiValues = createIndicatorValues(49, 50, 50);
        setFalseInitialIndicators(rsiValues, priceSMAValues, slowWMAValues, fastWMAValues, dailyValues, middleWMAValues);

        assertForTradableTrade();
    }

    @Test
    public void givenCorrectSettings_WhenCallToString_ThenReturnCorrectString(){
        assertEquals("Entry strategy: STANDARD", standardEntryStrategy.toString());
    }

    private void assertForTradableTrade() {
        Response responseMock = mock(Response.class);
        Point pointMock = mock(Point.class);
        when(responseMock.getBody()).thenReturn(pointMock);
        standardEntryStrategy = new StandardEntryStrategy();
        when(configurationMock.getRsiFilter()).thenReturn(RSI_FILTER);
        standardEntryStrategy.setConfiguration(configurationMock);
        standardEntryStrategy.setIndicators(indicators);
        standardEntryStrategy.setCreateTradeController(tradeControllerMock);
        Trade trade = standardEntryStrategy.generateTrade();
        verify(tradeControllerMock).execute(argument.capture());
        int argumentSize = argument.getValue().size();

        assertTrue(argumentSize>0);
        assertEquals(tradeMock, trade);
    }

    private void assertForNonTradableDefaultTrade() {
        standardEntryStrategy = new StandardEntryStrategy();
        when(configurationMock.getRsiFilter()).thenReturn(RSI_FILTER);
        standardEntryStrategy.setConfiguration(configurationMock);
        standardEntryStrategy.setIndicators(indicators);
        standardEntryStrategy.setCreateTradeController(tradeControllerMock);
        Trade trade = standardEntryStrategy.generateTrade();
        verify(tradeControllerMock).execute(argument.capture());
        int argumentSize = argument.getValue().size();

        assertEquals(0, argumentSize);
        assertEquals(tradeMock, trade);
    }

    private Indicator getField(String fieldName) {
        return (Indicator) commonMembers.extractFieldObject(standardEntryStrategy, fieldName);
    }

    private List<Indicator> createFalseListOfIndicators(int size){
        List<Indicator> indicatorList = new ArrayList<>(size);
        for (int i = 0; i <size ; i++) {
            indicatorList.add(mock(Indicator.class));
        }
        return  indicatorList;
    }

    private void setFalseInitialIndicators(List<BigDecimal>... indicatorValues) {
        indicators = new ArrayList<>();
        indicators.add(createFalseIndicator("rsi", indicatorValues[0]));
        indicators.add(createFalseIndicator("price", indicatorValues[1]));
        indicators.add(createFalseIndicator( "slow", indicatorValues[2]));
        indicators.add(createFalseIndicator("fast", indicatorValues[3]));
        indicators.add(createFalseIndicator("daily", indicatorValues[4]));
        indicators.add(createFalseIndicator("middle", indicatorValues[5]));
    }

    private Indicator createFalseIndicator(String position, List<BigDecimal> indicatorValues){
        Indicator indicator = mock(Indicator.class);
        when(indicator.getPosition()).thenReturn(position);
        when(indicator.getValues()).thenReturn(Collections.unmodifiableList(indicatorValues));
        return indicator;
    }

    private List<BigDecimal> createIndicatorValues(double ... prices) {
        List<BigDecimal> maValues = new ArrayList<>();
        for (double price :prices)
            maValues.add( BigDecimal.valueOf(price));
        return maValues;
    }

    private void setConfiguration() {
        when(configurationMock.getSpread()).thenReturn(DEFAULT_SPREAD);
        when(configurationMock.getEntryFilter()).thenReturn(DEFAULT_ENTRY_FILTER);
        when(configurationMock.getStopLossFilter()).thenReturn(DEFAULT_STOP_LOSS_FILTER);
        when(configurationMock.getTarget()).thenReturn(FIRST_TARGET);
        when(configurationMock.getRsiFilter()).thenReturn(RSI_FILTER);
    }
}