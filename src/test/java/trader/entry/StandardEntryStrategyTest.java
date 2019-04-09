package trader.entry;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import trader.CommonTestClassMembers;
import trader.controller.TraderController;
import trader.entity.indicator.Indicator;
import trader.entity.trade.Direction;
import trader.entity.trade.Trade;
import trader.exception.BadRequestException;
import trader.exception.NoSuchStrategyException;
import trader.responder.Response;
import java.math.BigDecimal;
import java.util.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StandardEntryStrategyTest {

    private List<BigDecimal> rsiValues = createIndicatorValues(49, 50, 22);
    private List<BigDecimal> priceSMAValues = createIndicatorValues(1.22889, 1.23339, 1.23339);
    private List<BigDecimal> slowWMAValues = createIndicatorValues(1.22889, 1.22739, 1.22639);
    private List<BigDecimal> fastWMAValues = createIndicatorValues(1.22889, 1.23339, 1.23339);
    private List<BigDecimal> dailyValues = createIndicatorValues(1.5656, 1.5656, 1.5656);
    private List<BigDecimal> middleWMAValues = createIndicatorValues(1.23119,1.23196, 1.23196);


    private CommonTestClassMembers commonMembers;
    private Response responseMock;
    private Trade tradeMock;
    private ArgumentCaptor<HashMap> argument;
    private TraderController<Trade> tradeControllerMock;
    private List<Indicator> indicators;
    private StandardEntryStrategy standardEntryStrategy;


    @Before
    public void before(){
        tradeMock = mock(Trade.class);
        responseMock = mock(Response.class);
        when(responseMock.getResponseDataStructure()).thenReturn(tradeMock);
        tradeControllerMock = mock(TraderController.class);
        when(tradeControllerMock.execute(any(HashMap.class))).thenReturn(responseMock);
        argument = ArgumentCaptor.forClass(HashMap.class);
        setFalseInitialIndicators(rsiValues, priceSMAValues, slowWMAValues, fastWMAValues, dailyValues, middleWMAValues);
        commonMembers = new CommonTestClassMembers();
        standardEntryStrategy = new StandardEntryStrategy(indicators, tradeControllerMock);

    }

    @Test(expected = NoSuchStrategyException.class)
    public void WhenCreatedWithNullCreateTradeController_Exception(){
        new StandardEntryStrategy(indicators, null);
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
        new StandardEntryStrategy(createFalseListOfIndicators(7), tradeControllerMock);
    }

    @Test(expected = NoSuchStrategyException.class)
    public void WhenInstantiateWithIndicatorListSizeBelowRequired_Exception(){
        new StandardEntryStrategy(createFalseListOfIndicators(5), tradeControllerMock);
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
        standardEntryStrategy = new StandardEntryStrategy(indicators, tradeControllerMock);
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
        List<BigDecimal> fastWMAValues = createIndicatorValues(1.23219, 1.23196, 1.23196);
        List<BigDecimal> middleWMAValues = createIndicatorValues(1.22889, 1.23339, 1.23339);
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
        List<BigDecimal> fastWMAValues = createIndicatorValues(1.23119, 1.23339, 1.23339);
        List<BigDecimal> middleWMAValues = createIndicatorValues(1.23119, 1.23196, 1.23196);
        List<BigDecimal> slowWMAValues = createIndicatorValues(1.23019, 1.23039, 1.23039);
        List<BigDecimal> rsiValues = createIndicatorValues(49, 50, 22);
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
        List<BigDecimal> priceSMAValues = createIndicatorValues(1.23219, 1.23196, 1.23196);
        List<BigDecimal> middleWMAValues = createIndicatorValues(1.22889, 1.23339, 1.23339);
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
        List<BigDecimal> priceSMAValues = createIndicatorValues(1.23119, 1.23339, 1.23339);
        List<BigDecimal> middleWMAValues = createIndicatorValues(1.23119, 1.23196, 1.23196);
        List<BigDecimal> slowWMAValues = createIndicatorValues(1.23019, 1.23039, 1.23039);
        setFalseInitialIndicators(rsiValues, priceSMAValues, slowWMAValues, fastWMAValues, dailyValues, middleWMAValues);

        assertForTradableTrade();
    }

    @Test
    public void WhenPriceSMACrossSlowWMAFromBelowThenGenerateLongNonDefaultTrade() {
        List<BigDecimal> fastWMAValues = createIndicatorValues(1.22989, 1.23019, 1.23019);
        List<BigDecimal> priceSMAValues = createIndicatorValues(1.22889, 1.22993, 1.22999);
        List<BigDecimal> middleWMAValues = createIndicatorValues(1.23119, 1.23196, 1.23196);
        List<BigDecimal> slowWMAValues = createIndicatorValues(1.22909, 1.22939, 1.22939);
        setFalseInitialIndicators(rsiValues, priceSMAValues, slowWMAValues, fastWMAValues, dailyValues, middleWMAValues);

        assertForTradableTrade();
    }

    @Test
    public void WhenPriceSMACrossSlowWMAFromAboveThenGenerateShortNonDefaultTrade() {
        List<BigDecimal> fastWMAValues = createIndicatorValues(1.23639, 1.23496, 1.23496);
        List<BigDecimal> priceSMAValues = createIndicatorValues(1.23991, 1.23716, 1.23716);
        List<BigDecimal> middleWMAValues = createIndicatorValues(1.23619, 1.23239, 1.23339);
        List<BigDecimal> slowWMAValues = createIndicatorValues(1.23889, 1.23739, 1.23739);
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
        List<BigDecimal> priceSMAValues = createIndicatorValues(1.23019, 1.23099, 1.23099);
        List<BigDecimal> middleWMAValues = createIndicatorValues(1.23119, 1.23196, 1.23196);
        List<BigDecimal> slowWMAValues = createIndicatorValues(1.23019, 1.23039, 1.23039);
        List<BigDecimal> dailyValues = createIndicatorValues(1.5656, 1.5656, 1.5656);
        List<BigDecimal> rsiValues = createIndicatorValues(49, 50, 22);
        setFalseInitialIndicators(rsiValues, priceSMAValues, slowWMAValues, fastWMAValues, dailyValues, middleWMAValues);

        assertForTradableTrade();
    }

    private void assertForTradableTrade() {
        standardEntryStrategy = new StandardEntryStrategy(indicators, tradeControllerMock);
        Trade trade = standardEntryStrategy.generateTrade();
        verify(tradeControllerMock).execute(argument.capture());
        int argumentSize = argument.getValue().size();

        assertTrue(argumentSize>0);
        assertEquals(tradeMock, trade);
    }

    private void assertForNonTradableDefaultTrade() {
        standardEntryStrategy = new StandardEntryStrategy(indicators, tradeControllerMock);
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

//To test for components
//    @Test
//    public void WhenFastWMACrossMiddleWMAFromBelowThenGenerateCorrectLongSignal() {
//
//        whn
//        Trade trade = standardEntryStrategy.generateTrade();
//
//        BigDecimal entryPrice = trade.getEntryPrice();
//        int compareEntry = BigDecimal.valueOf(1.23386).compareTo(entryPrice);
//
//        BigDecimal stopLossPrice = trade.getStopLossPrice();
//        int compareStopLoss = BigDecimal.valueOf(1.23116).compareTo(stopLossPrice);
//
//        assertTrue( trade.getTradable());
//        assertEquals(0, compareEntry);
//        assertEquals(0, compareStopLoss);
//
//    }


//    @Test
//    public void WhenFastWMACrossMiddleWMAFromBelowThenGenerateCorrectLongSignal() {
//
//        List<BigDecimal> fastWMAValues = createIndicatorValues(1.22889, 1.23339, 1.23339);
//        List<BigDecimal> middleWMAValues = createIndicatorValues(1.23119,1.23196, 1.23196);
//        List<BigDecimal> slowWMAValues = createIndicatorValues(1.22889, 1.22739, 1.22639);
//        List<BigDecimal> dailyValues = createIndicatorValues(1.5656, 1.5656, 1.5656);
//        List<BigDecimal> rsiValues = createIndicatorValues(49, 50, 22);
//
//
//        when(this.mockRsi.getValues()).thenReturn(rsiValues);
//        when(this.mockFastWma.getValues()).thenReturn(fastWMAValues);
//        when(this.mockMiddleWma.getValues()).thenReturn(middleWMAValues);
//        when(this.mockDailySma.getValues()).thenReturn(dailyValues);
//        //slowWMA and priceSma line segments are irrelevant for this test
//        when(this.mockSlowWma.getValues()).thenReturn(slowWMAValues);
//        when(this.mockPriceSma.getValues()).thenReturn(middleWMAValues);
//
//        TradeImpl tradeImpl = this.signalGenerator.generateTrade();
//
//        BigDecimal entryPrice = tradeImpl.getEntryPrice();
//        int compareEntry = BigDecimal.valueOf(1.23386).compareTo(entryPrice);
//
//        BigDecimal stopLossPrice = tradeImpl.getStopLossPrice();
//        int compareStopLoss = BigDecimal.valueOf(1.23116).compareTo(stopLossPrice);
//
//        assertTrue("TradeImpl must be tradable", tradeImpl.getTradable());
//        assertEquals(0, compareEntry);
//        assertEquals(0, compareStopLoss);
//
//    }

//    @Test
//    public void WhenFastWMACrossMiddleWMAFromAboveThenGenerateCorrectShortSignal() {
//
//        List<BigDecimal> fastWMAValues = createIndicatorValues(1.23219, 1.23196, 1.23196);
//        List<BigDecimal> middleWMAValues = createIndicatorValues(1.22889, 1.23339, 1.23339);
//        List<BigDecimal> slowWMAValues = createIndicatorValues(1.23889, 1.23739, 1.23639);
//        List<BigDecimal> dailyValues = createIndicatorValues(1.5656, 1.5656, 1.5656);
//        List<BigDecimal> rsiValues = createIndicatorValues(49, 50, 22);
//
//        when(this.mockRsi.getValues()).thenReturn(rsiValues);
//        when(this.mockFastWma.getValues()).thenReturn(fastWMAValues);
//        when(this.mockMiddleWma.getValues()).thenReturn(middleWMAValues);
//        when(this.mockDailySma.getValues()).thenReturn(dailyValues);
//        //slowWMA and priceSma line segments are irrelevant for this test
//        when(this.mockSlowWma.getValues()).thenReturn(slowWMAValues);
//        when(this.mockPriceSma.getValues()).thenReturn(middleWMAValues);
//
//        Trade trade = this.signalGenerator.generateTrade();
//
//        BigDecimal entryPrice = trade.getEntryPrice();
//        int compareEntry = BigDecimal.valueOf(1.23003).compareTo(entryPrice);
//
//        BigDecimal stopLossPrice = trade.getStopLossPrice();
//        int compareStopLoss = BigDecimal.valueOf(1.23273).compareTo(stopLossPrice);
//
//        assertTrue("TradeImpl must be tradable", trade.getTradable());
//        assertEquals(0, compareEntry);
//        assertEquals(0, compareStopLoss);
//    }

//    @Test
//    public void WhenFastWMACrossMiddleWMAFromAboveWithRSIAboveFilterThenGenerateNonTradableSignal() {
//
//        List<BigDecimal> fastWMAValues = createIndicatorValues(1.23219, 1.23196, 1.23196);
//        List<BigDecimal> middleWMAValues = createIndicatorValues(1.22889, 1.23339, 1.23339);
//        List<BigDecimal> dailyValues = createIndicatorValues(1.5656, 1.5656, 1.5656);
//        List<BigDecimal> rsiValues = createIndicatorValues(51, 51, 51);
//
//        when(this.mockRsi.getValues()).thenReturn(rsiValues);
//        when(this.mockFastWma.getValues()).thenReturn(fastWMAValues);
//        when(this.mockMiddleWma.getValues()).thenReturn(middleWMAValues);
//        when(this.mockDailySma.getValues()).thenReturn(dailyValues);
//        //slowWMA and priceSma line segments are irrelevant for this test
//        when(this.mockSlowWma.getValues()).thenReturn(middleWMAValues);
//        when(this.mockPriceSma.getValues()).thenReturn(middleWMAValues);
//
//        Trade trade = this.signalGenerator.generateTrade();
//
//        assertFalse("TradeImpl must not be tradable", trade.getTradable());
//    }

//    @Test
//    public void WhenFastWMACrossMiddleWMAFromBelowWithRSIBelowFilterThenGenerateNonTradableSignal() {
//
//        List<BigDecimal> fastWMAValues = createIndicatorValues(1.23119, 1.23296, 1.23296);
//        List<BigDecimal> middleWMAValues = createIndicatorValues(1.23159, 1.23239, 1.23239);
//        List<BigDecimal> dailyValues = createIndicatorValues(1.5656, 1.5656, 1.5656);
//        List<BigDecimal> rsiValues = createIndicatorValues(49, 49, 49);
//
//        when(this.mockRsi.getValues()).thenReturn(rsiValues);
//        when(this.mockFastWma.getValues()).thenReturn(fastWMAValues);
//        when(this.mockMiddleWma.getValues()).thenReturn(middleWMAValues);
//        when(this.mockDailySma.getValues()).thenReturn(dailyValues);
//        //slowWMA and priceSma line segments are irrelevant for this test
//        when(this.mockSlowWma.getValues()).thenReturn(middleWMAValues);
//        when(this.mockPriceSma.getValues()).thenReturn(middleWMAValues);
//
//        Trade trade = this.signalGenerator.generateTrade();
//
//        assertFalse("TradeImpl must not be tradable", trade.getTradable());
//    }

//    @Test
//    public void WhenFastWMACrossMiddleWMAWithFirstFastWMAPointOnTopOfFirstMiddleWMAPointThenGenerateCorrectSignal() {
//
//        List<BigDecimal> fastWMAValues = createIndicatorValues(1.23119, 1.23339, 1.23339);
//        List<BigDecimal> middleWMAValues = createIndicatorValues(1.23119, 1.23196, 1.23196);
//        List<BigDecimal> slowWMAValues = createIndicatorValues(1.23019, 1.23039, 1.23039);
//        List<BigDecimal> dailyValues = createIndicatorValues(1.5656, 1.5656, 1.5656);
//        List<BigDecimal> rsiValues = createIndicatorValues(49, 50, 22);
//
//        when(this.mockRsi.getValues()).thenReturn(rsiValues);
//        when(this.mockFastWma.getValues()).thenReturn(fastWMAValues);
//        when(this.mockMiddleWma.getValues()).thenReturn(middleWMAValues);
//        when(this.mockDailySma.getValues()).thenReturn(dailyValues);
//        //slowWMA and priceSma line segments are irrelevant for this test
//        when(this.mockSlowWma.getValues()).thenReturn(slowWMAValues);
//        when(this.mockPriceSma.getValues()).thenReturn(middleWMAValues);
//
//        Trade trade = this.signalGenerator.generateTrade();
//
//        BigDecimal entryPrice = trade.getEntryPrice();
//        int compareEntry = BigDecimal.valueOf(1.23339).compareTo(entryPrice);
//
//        BigDecimal stopLossPrice = trade.getStopLossPrice();
//        int compareStopLoss = BigDecimal.valueOf(1.23069).compareTo(stopLossPrice);
//
//        assertTrue("TradeImpl must be tradable", trade.getTradable());
//        assertEquals(0, compareEntry);
//        assertEquals(0, compareStopLoss);
//
//    }


//    @Test
//    public void WhenPriceSMACrossMiddleWMAFromBelowThenGenerateCorrectLongSignal() {
//
//        List<BigDecimal> fastWMAValues = createIndicatorValues(1.22789, 1.22839, 1.22939);
//        List<BigDecimal> priceSMAValues = createIndicatorValues(1.22889, 1.23339, 1.23339);
//        List<BigDecimal> middleWMAValues =createIndicatorValues(1.23119, 1.23196, 1.23196);
//        List<BigDecimal> slowWMAValues = createIndicatorValues(1.22689, 1.22739, 1.22639);
//        List<BigDecimal> dailyValues = createIndicatorValues(1.5656, 1.5656, 1.5656);
//        List<BigDecimal> rsiValues = createIndicatorValues(49, 50, 22);
//
//        when(this.mockRsi.getValues()).thenReturn(rsiValues);
//        when(this.mockFastWma.getValues()).thenReturn(fastWMAValues);
//        when(this.mockPriceSma.getValues()).thenReturn(priceSMAValues);
//        when(this.mockMiddleWma.getValues()).thenReturn(middleWMAValues);
//        when(this.mockDailySma.getValues()).thenReturn(dailyValues);
//        when(this.mockSlowWma.getValues()).thenReturn(slowWMAValues);
//
//        Trade trade = this.signalGenerator.generateTrade();
//
//        BigDecimal entryPrice = trade.getEntryPrice();
//        int compareEntry = BigDecimal.valueOf(1.23386).compareTo(entryPrice);
//
//        BigDecimal stopLossPrice = trade.getStopLossPrice();
//        int compareStopLoss = BigDecimal.valueOf(1.23116).compareTo(stopLossPrice);
//
//        assertTrue("TradeImpl must be tradable", trade.getTradable());
//        assertEquals(0, compareEntry);
//        assertEquals(0, compareStopLoss);
//
//    }

//    @Test
//    public void WhenPriceSMACrossMiddleWMAFromAboveThenGenerateCorrectShortSignal() {
//
//        List<BigDecimal> fastWMAValues = createIndicatorValues(1.23519, 1.23496, 1.23496);
//        List<BigDecimal> priceSMAValues = createIndicatorValues(1.23219, 1.23196, 1.23196);
//        List<BigDecimal> middleWMAValues = createIndicatorValues(1.22889, 1.23339, 1.23339);
//        List<BigDecimal> slowWMAValues = createIndicatorValues(1.23889, 1.23739, 1.23639);
//        List<BigDecimal> dailyValues = createIndicatorValues(1.5656, 1.5656, 1.5656);
//        List<BigDecimal> rsiValues = createIndicatorValues(49, 50, 22);
//
//        when(this.mockRsi.getValues()).thenReturn(rsiValues);
//        when(this.mockFastWma.getValues()).thenReturn(fastWMAValues);
//        when(this.mockPriceSma.getValues()).thenReturn(priceSMAValues);
//        when(this.mockMiddleWma.getValues()).thenReturn(middleWMAValues);
//        when(this.mockSlowWma.getValues()).thenReturn(slowWMAValues);
//        when(this.mockDailySma.getValues()).thenReturn(dailyValues);
//
//        Trade trade = this.signalGenerator.generateTrade();
//
//        BigDecimal entryPrice = trade.getEntryPrice();
//        int compareEntry = BigDecimal.valueOf(1.23003).compareTo(entryPrice);
//
//        BigDecimal stopLossPrice = trade.getStopLossPrice();
//        int compareStopLoss = BigDecimal.valueOf(1.23273).compareTo(stopLossPrice);
//
//        assertTrue("TradeImpl must be tradable", trade.getTradable());
//        assertEquals(0, compareEntry);
//        assertEquals(0, compareStopLoss);
//
//    }

//    @Test
//    public void WhenPriceSMACrossMiddleWMAFromBelowWithRSIBelowFilterThenGenerateNonTradableSignal() {
//
//        List<BigDecimal> fastWMAValues = createIndicatorValues(1.23419, 1.23419, 1.23419);
//        List<BigDecimal> priceSMAValues = createIndicatorValues(1.23219, 1.23376, 1.23386);
//        List<BigDecimal> middleWMAValues = createIndicatorValues(1.23289, 1.23339, 1.23339);
//        List<BigDecimal> dailyValues = createIndicatorValues(1.5656, 1.5656, 1.5656);
//        List<BigDecimal> rsiValues = createIndicatorValues(49, 49, 22);
//
//        when(this.mockRsi.getValues()).thenReturn(rsiValues);
//        when(this.mockFastWma.getValues()).thenReturn(fastWMAValues);
//        when(this.mockPriceSma.getValues()).thenReturn(priceSMAValues);
//        when(this.mockMiddleWma.getValues()).thenReturn(middleWMAValues);
//        when(this.mockDailySma.getValues()).thenReturn(dailyValues);
//        when(this.mockSlowWma.getValues()).thenReturn(middleWMAValues);
//
//        Trade trade = this.signalGenerator.generateTrade();
//
//        assertFalse("TradeImpl must not be tradable", trade.getTradable());
//    }

//    @Test
//    public void WhenPriceSMACrossMiddleWMAFromAboveWithRSIAboveFilterThenGenerateNonTradableSignal() {
//
//        List<BigDecimal> fastWMAValues = createIndicatorValues(1.23419, 1.23419, 1.23419);
//        List<BigDecimal> priceSMAValues = createIndicatorValues(1.23219, 1.23196, 1.23196);
//        List<BigDecimal> middleWMAValues = createIndicatorValues(1.22889, 1.23339, 1.23339);
//        List<BigDecimal> dailyValues = createIndicatorValues(1.5656, 1.5656, 1.5656);
//        List<BigDecimal> rsiValues = createIndicatorValues(51, 51, 51);
//
//        when(this.mockRsi.getValues()).thenReturn(rsiValues);
//        when(this.mockFastWma.getValues()).thenReturn(fastWMAValues);
//        when(this.mockPriceSma.getValues()).thenReturn(priceSMAValues);
//        when(this.mockMiddleWma.getValues()).thenReturn(middleWMAValues);
//        when(this.mockDailySma.getValues()).thenReturn(dailyValues);
//        when(this.mockSlowWma.getValues()).thenReturn(middleWMAValues);
//
//        Trade trade = this.signalGenerator.generateTrade();
//
//        assertFalse("TradeImpl must not be tradable", trade.getTradable());
//    }

//    @Test
//    public void WhenPriceSMACrossMiddleWMAWithFirstPriceWMAPointOnTopOfFirstMiddleWMAPointThenGenerateCorrectLongSignal() {
//
//        List<BigDecimal> fastWMAValues = createIndicatorValues(1.23419, 1.23439, 1.23439);
//        List<BigDecimal> priceSMAValues = createIndicatorValues(1.23119, 1.23339, 1.23339);
//        List<BigDecimal> middleWMAValues = createIndicatorValues(1.23119, 1.23196, 1.23196);
//        List<BigDecimal> slowWMAValues = createIndicatorValues(1.23019, 1.23039, 1.23039);
//        List<BigDecimal> dailyValues = createIndicatorValues(1.5656, 1.5656, 1.5656);
//        List<BigDecimal> rsiValues = createIndicatorValues(49, 50, 22);
//
//        when(this.mockRsi.getValues()).thenReturn(rsiValues);
//        when(this.mockFastWma.getValues()).thenReturn(fastWMAValues);
//        when(this.mockPriceSma.getValues()).thenReturn(priceSMAValues);
//        when(this.mockMiddleWma.getValues()).thenReturn(middleWMAValues);
//        when(this.mockDailySma.getValues()).thenReturn(dailyValues);
//        when(this.mockSlowWma.getValues()).thenReturn(slowWMAValues);
//
//        Trade trade = this.signalGenerator.generateTrade();
//
//        BigDecimal entryPrice = trade.getEntryPrice();
//        int compareEntry = BigDecimal.valueOf(1.23339).compareTo(entryPrice);
//
//        BigDecimal stopLossPrice = trade.getStopLossPrice();
//        int compareStopLoss = BigDecimal.valueOf(1.23069).compareTo(stopLossPrice);
//
//        assertTrue("TradeImpl must be tradable", trade.getTradable());
//        assertEquals(0, compareEntry);
//        assertEquals(0, compareStopLoss);
//    }

//    @Test
//    public void WhenPriceSMACrossSlowWMAFromBelowThenGenerateCorrectLongSignal() {
//
//        List<BigDecimal> fastWMAValues = createIndicatorValues(1.22989, 1.23019, 1.23019);
//        List<BigDecimal> priceSMAValues = createIndicatorValues(1.22889, 1.22993, 1.22999);
//        List<BigDecimal> middleWMAValues = createIndicatorValues(1.23119, 1.23196, 1.23196);
//        List<BigDecimal> slowWMAValues = createIndicatorValues(1.22909, 1.22939, 1.22939);
//        List<BigDecimal> dailyValues = createIndicatorValues(1.5656, 1.5656, 1.5656);
//        List<BigDecimal> rsiValues = createIndicatorValues(49, 50, 22);
//
//        when(this.mockRsi.getValues()).thenReturn(rsiValues);
//        when(this.mockFastWma.getValues()).thenReturn(fastWMAValues);
//        when(this.mockPriceSma.getValues()).thenReturn(priceSMAValues);
//        when(this.mockMiddleWma.getValues()).thenReturn(middleWMAValues);
//        when(this.mockDailySma.getValues()).thenReturn(dailyValues);
//        when(this.mockSlowWma.getValues()).thenReturn(slowWMAValues);
//
//        Trade trade = this.signalGenerator.generateTrade();
//
//        BigDecimal entryPrice = trade.getEntryPrice();
//        int compareEntry = BigDecimal.valueOf(1.23995).compareTo(entryPrice);
//
//        BigDecimal stopLossPrice = trade.getStopLossPrice();
//        int compareStopLoss = BigDecimal.valueOf(1.23725).compareTo(stopLossPrice);
//
//        assertTrue("TradeImpl must be tradable", trade.getTradable());
//        assertEquals(0, compareEntry);
//        assertEquals(0, compareStopLoss);
//    }

//    @Test
//    public void WhenPriceSMACrossSlowWMAFromAboveThenGenerateCorrectShortSignal() {
//
//        List<BigDecimal> fastWMAValues = createIndicatorValues(1.23639, 1.23496, 1.23496);
//        List<BigDecimal> priceSMAValues = createIndicatorValues(1.23991, 1.23716, 1.23716);
//        List<BigDecimal> middleWMAValues = createIndicatorValues(1.23619, 1.23239, 1.23339);
//        List<BigDecimal> slowWMAValues = createIndicatorValues(1.23889, 1.23739, 1.23739);
//        List<BigDecimal> dailyValues = createIndicatorValues(1.5656, 1.5656, 1.5656);
//        List<BigDecimal> rsiValues = createIndicatorValues(49, 50, 22);
//
//        when(this.mockRsi.getValues()).thenReturn(rsiValues);
//        when(this.mockFastWma.getValues()).thenReturn(fastWMAValues);
//        when(this.mockPriceSma.getValues()).thenReturn(priceSMAValues);
//        when(this.mockMiddleWma.getValues()).thenReturn(middleWMAValues);
//        when(this.mockSlowWma.getValues()).thenReturn(slowWMAValues);
//        when(this.mockDailySma.getValues()).thenReturn(dailyValues);
//
//        Trade trade = this.signalGenerator.generateTrade();
//
//        BigDecimal entryPrice = trade.getEntryPrice();
//        int compareEntry = BigDecimal.valueOf(1.24765).compareTo(entryPrice);
//
//        BigDecimal stopLossPrice = trade.getStopLossPrice();
//        int compareStopLoss = BigDecimal.valueOf(1.25035).compareTo(stopLossPrice);
//
//        assertTrue("TradeImpl must be tradable", trade.getTradable());
//        assertEquals(0, compareEntry);
//        assertEquals(0, compareStopLoss);
//
//    }

//    @Test
//    public void WhenPriceSMACrossSlowWMAFromAboveWithRSIAboveFilterThenGenerateNonTradableSignal() {
//
//        List<BigDecimal> fastWMAValues = createIndicatorValues(1.23639, 1.23496, 1.23496);
//        List<BigDecimal> priceSMAValues = createIndicatorValues(1.23991, 1.23716, 1.23716);
//        List<BigDecimal> middleWMAValues = createIndicatorValues(1.23619, 1.23239, 1.23339);
//        List<BigDecimal> slowWMAValues = createIndicatorValues(1.23889, 1.23739, 1.23739);
//        List<BigDecimal> dailyValues = createIndicatorValues(1.5656, 1.5656, 1.5656);
//        List<BigDecimal> rsiValues = createIndicatorValues(51, 51, 51);
//
//        when(this.mockRsi.getValues()).thenReturn(rsiValues);
//
//        when(this.mockFastWma.getValues()).thenReturn(fastWMAValues);
//        when(this.mockPriceSma.getValues()).thenReturn(priceSMAValues);
//        when(this.mockMiddleWma.getValues()).thenReturn(middleWMAValues);
//        when(this.mockDailySma.getValues()).thenReturn(dailyValues);
//
//        when(this.mockSlowWma.getValues()).thenReturn(slowWMAValues);
//
//        Trade trade = this.signalGenerator.generateTrade();
//
//        assertFalse("TradeImpl must not be tradable", trade.getTradable());
//    }

//    @Test
//    public void WhenPriceSMACrossSlowWMAFromBelowWithRSIBelowFilterThenGenerateNonTradableSignal() {
//
//        List<BigDecimal> fastWMAValues = createIndicatorValues(1.23639, 1.23496, 1.23496);
//        List<BigDecimal> priceSMAValues = createIndicatorValues(1.23789, 1.23826, 1.23826);
//        List<BigDecimal> middleWMAValues = createIndicatorValues(1.23619, 1.23239, 1.23339);
//        List<BigDecimal> slowWMAValues = createIndicatorValues(1.23791, 1.23793, 1.23793);
//        List<BigDecimal> dailyValues = createIndicatorValues(1.5656, 1.5656, 1.5656);
//        List<BigDecimal> rsiValues = createIndicatorValues(49, 49, 49);
//
//        when(this.mockRsi.getValues()).thenReturn(rsiValues);
//
//        when(this.mockFastWma.getValues()).thenReturn(fastWMAValues);
//        when(this.mockPriceSma.getValues()).thenReturn(priceSMAValues);
//        when(this.mockMiddleWma.getValues()).thenReturn(middleWMAValues);
//        when(this.mockDailySma.getValues()).thenReturn(dailyValues);
//        when(this.mockSlowWma.getValues()).thenReturn(slowWMAValues);
//
//        Trade trade = this.signalGenerator.generateTrade();
//
//        assertFalse("TradeImpl must not be tradable", trade.getTradable());
//    }

//    @Test
//    public void WhenPriceSMACrossSlowWMAWithFirstPriceWMAPointOnTopOfFirstSlowWMAPointThenGenerateCorrectLongSignal() {
//
//        List<BigDecimal> fastWMAValues = createIndicatorValues(1.23109, 1.23109, 1.23109);
//        List<BigDecimal> priceSMAValues = createIndicatorValues(1.23019, 1.23099, 1.23099);
//        List<BigDecimal> middleWMAValues = createIndicatorValues(1.23119, 1.23196, 1.23196);
//        List<BigDecimal> slowWMAValues = createIndicatorValues(1.23019, 1.23039, 1.23039);
//        List<BigDecimal> dailyValues = createIndicatorValues(1.5656, 1.5656, 1.5656);
//        List<BigDecimal> rsiValues = createIndicatorValues(49, 50, 22);
//
//        when(this.mockRsi.getValues()).thenReturn(rsiValues);
//        when(this.mockFastWma.getValues()).thenReturn(fastWMAValues);
//        when(this.mockPriceSma.getValues()).thenReturn(priceSMAValues);
//        when(this.mockMiddleWma.getValues()).thenReturn(middleWMAValues);
//        when(this.mockDailySma.getValues()).thenReturn(dailyValues);
//        when(this.mockSlowWma.getValues()).thenReturn(slowWMAValues);
//
//        Trade trade = this.signalGenerator.generateTrade();
//
//        BigDecimal entryPrice = trade.getEntryPrice();
//        int compareEntry = BigDecimal.valueOf(1.25906).compareTo(entryPrice);
//
//        BigDecimal stopLossPrice = trade.getStopLossPrice();
//        int compareStopLoss = BigDecimal.valueOf(1.25636).compareTo(stopLossPrice);
//
//        assertTrue("TradeImpl must be tradable", trade.getTradable());
//        assertEquals(0, compareEntry);
//        assertEquals(0, compareStopLoss);
//    }


}