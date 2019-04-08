package trader.entry;

import org.junit.Before;
import org.junit.Test;
import trader.CommonTestClassMembers;
import trader.broker.connector.BaseGateway;
import trader.entity.candlestick.Candlestick;
import trader.entity.candlestick.candle.CandleGranularity;
import trader.entity.candlestick.candle.CandlePriceType;
import trader.entity.indicator.Indicator;
import trader.entity.indicator.IndicatorUpdateHelper;
import trader.entity.indicator.ma.SimpleMovingAverage;
import trader.entity.indicator.ma.WeightedMovingAverage;
import trader.entity.indicator.rsi.RelativeStrengthIndex;
import trader.entity.trade.Direction;
import trader.entity.trade.TradeImpl;
import trader.entry.StandardEntryStrategy;
import trader.exception.EmptyArgumentException;
import trader.exception.NoSuchStrategyException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StandardEntryStrategyTest {


    public static final int INDICATOR_COUNT = 6;
    private StandardEntryStrategy signalGenerator;
    private CommonTestClassMembers commonMembers;
    private BaseGateway baseGateway;
    private CandlePriceType candlePriceType = CandlePriceType.CLOSE;
    private IndicatorUpdateHelper indicatorUpdateHelper;
    private Indicator mockSlowWma;
    private Indicator mockMiddleWma;
    private Indicator mockFastWma;
    private Indicator mockPriceSma;
    private Indicator mockDailySma;
    private Indicator mockRsi;


    List<BigDecimal> fastWMAValues = createIndicatorValues(1.22889, 1.23339, 1.23339);
    List<BigDecimal> middleWMAValues = createIndicatorValues(1.23119,1.23196, 1.23196);
    List<BigDecimal> slowWMAValues = createIndicatorValues(1.22889, 1.22739, 1.22639);
    List<BigDecimal> dailyValues = createIndicatorValues(1.5656, 1.5656, 1.5656);
    List<BigDecimal> rsiValues = createIndicatorValues(49, 50, 22);
    List<BigDecimal> priceSMAValues = createIndicatorValues(1.22889, 1.23339, 1.23339);

    private List<Indicator> indicators;
    private StandardEntryStrategy standardEntryStrategy;


    @Before
    public void before() throws Exception {

        this.mockSlowWma = mock(WeightedMovingAverage.class);
        this.mockMiddleWma = mock(WeightedMovingAverage.class);
        this.mockFastWma = mock(WeightedMovingAverage.class);
        this.mockPriceSma = mock(SimpleMovingAverage.class);
        this.mockDailySma = mock(SimpleMovingAverage.class);
        this.mockRsi = mock(RelativeStrengthIndex.class);

        this.baseGateway = mock(BaseGateway.class);
       // this.indicatorUpdateHelper = new IndicatorUpdateHelper(this.candlePriceType);
        this.indicatorUpdateHelper = new IndicatorUpdateHelper();
        init();

        setFalseInitialIndicators();
        commonMembers = new CommonTestClassMembers();
        standardEntryStrategy = new StandardEntryStrategy(indicators);


        this.signalGenerator = new StandardEntryStrategy(this.mockFastWma, this.mockMiddleWma, this.mockSlowWma, this.mockPriceSma, this.mockDailySma, this.mockRsi);

    }

    @Test
    public void WhenInstantiateThenDirectionMustBeFlat(){
        Direction direction = (Direction) commonMembers.extractFieldObject(standardEntryStrategy, "direction");

        assertNotNull(direction);
        assertEquals(Direction.FLAT, direction);
    }

    @Test(expected = NoSuchStrategyException.class)
    public void WhenInstantiateWithIndicatorsListSizeAboveRequired_Exception(){
        new StandardEntryStrategy(createFalseListOfIndicators(7));
    }

    @Test(expected = NoSuchStrategyException.class)
    public void WhenInstantiateWithIndicatorListSizeBelowRequired_Exception(){
        new StandardEntryStrategy(createFalseListOfIndicators(5));
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

    @Test
    public void WhenFastWMACrossMiddleWMAFromBelowThenGenerateCorrectLongSignal() {


        TradeImpl tradeImpl = standardEntryStrategy.generateTrade();

        BigDecimal entryPrice = tradeImpl.getEntryPrice();
        int compareEntry = BigDecimal.valueOf(1.23386).compareTo(entryPrice);

        BigDecimal stopLossPrice = tradeImpl.getStopLossPrice();
        int compareStopLoss = BigDecimal.valueOf(1.23116).compareTo(stopLossPrice);

        assertTrue("TradeImpl must be tradable", tradeImpl.getTradable());
        assertEquals(0, compareEntry);
        assertEquals(0, compareStopLoss);

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

    private void setFalseInitialIndicators() {
        indicators = new ArrayList<>();
        indicators.add(createFalseIndicator("rsi", rsiValues));
        indicators.add(createFalseIndicator("price", priceSMAValues));
        indicators.add(createFalseIndicator( "slow", slowWMAValues));
        indicators.add(createFalseIndicator("fast", fastWMAValues));
        indicators.add(createFalseIndicator("daily", dailyValues));
        indicators.add(createFalseIndicator("middle", middleWMAValues));
    }

    private Indicator createFalseIndicator(String position, List<BigDecimal> indicatorValues){
        Indicator indicator = mock(Indicator.class);
        when(indicator.getPosition()).thenReturn(position);
        when(indicator.getValues()).thenReturn(Collections.unmodifiableList(indicatorValues));
        return indicator;
    }



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

    @Test
    public void WhenFastWMACrossMiddleWMAFromAboveThenGenerateCorrectShortSignal() {

        List<BigDecimal> fastWMAValues = createIndicatorValues(1.23219, 1.23196, 1.23196);
        List<BigDecimal> middleWMAValues = createIndicatorValues(1.22889, 1.23339, 1.23339);
        List<BigDecimal> slowWMAValues = createIndicatorValues(1.23889, 1.23739, 1.23639);
        List<BigDecimal> dailyValues = createIndicatorValues(1.5656, 1.5656, 1.5656);
        List<BigDecimal> rsiValues = createIndicatorValues(49, 50, 22);

        when(this.mockRsi.getValues()).thenReturn(rsiValues);
        when(this.mockFastWma.getValues()).thenReturn(fastWMAValues);
        when(this.mockMiddleWma.getValues()).thenReturn(middleWMAValues);
        when(this.mockDailySma.getValues()).thenReturn(dailyValues);
        //slowWMA and priceSma line segments are irrelevant for this test
        when(this.mockSlowWma.getValues()).thenReturn(slowWMAValues);
        when(this.mockPriceSma.getValues()).thenReturn(middleWMAValues);

        TradeImpl tradeImpl = this.signalGenerator.generateTrade();

        BigDecimal entryPrice = tradeImpl.getEntryPrice();
        int compareEntry = BigDecimal.valueOf(1.23003).compareTo(entryPrice);

        BigDecimal stopLossPrice = tradeImpl.getStopLossPrice();
        int compareStopLoss = BigDecimal.valueOf(1.23273).compareTo(stopLossPrice);

        assertTrue("TradeImpl must be tradable", tradeImpl.getTradable());
        assertEquals(0, compareEntry);
        assertEquals(0, compareStopLoss);
    }

    @Test
    public void WhenFastWMACrossMiddleWMAFromAboveWithRSIAboveFilterThenGenerateNonTradableSignal() {

        List<BigDecimal> fastWMAValues = createIndicatorValues(1.23219, 1.23196, 1.23196);
        List<BigDecimal> middleWMAValues = createIndicatorValues(1.22889, 1.23339, 1.23339);
        List<BigDecimal> dailyValues = createIndicatorValues(1.5656, 1.5656, 1.5656);
        List<BigDecimal> rsiValues = createIndicatorValues(51, 51, 51);

        when(this.mockRsi.getValues()).thenReturn(rsiValues);
        when(this.mockFastWma.getValues()).thenReturn(fastWMAValues);
        when(this.mockMiddleWma.getValues()).thenReturn(middleWMAValues);
        when(this.mockDailySma.getValues()).thenReturn(dailyValues);
        //slowWMA and priceSma line segments are irrelevant for this test
        when(this.mockSlowWma.getValues()).thenReturn(middleWMAValues);
        when(this.mockPriceSma.getValues()).thenReturn(middleWMAValues);

        TradeImpl tradeImpl = this.signalGenerator.generateTrade();

        assertFalse("TradeImpl must not be tradable", tradeImpl.getTradable());
    }

    @Test
    public void WhenFastWMACrossMiddleWMAFromBelowWithRSIBelowFilterThenGenerateNonTradableSignal() {

        List<BigDecimal> fastWMAValues = createIndicatorValues(1.23119, 1.23296, 1.23296);
        List<BigDecimal> middleWMAValues = createIndicatorValues(1.23159, 1.23239, 1.23239);
        List<BigDecimal> dailyValues = createIndicatorValues(1.5656, 1.5656, 1.5656);
        List<BigDecimal> rsiValues = createIndicatorValues(49, 49, 49);

        when(this.mockRsi.getValues()).thenReturn(rsiValues);
        when(this.mockFastWma.getValues()).thenReturn(fastWMAValues);
        when(this.mockMiddleWma.getValues()).thenReturn(middleWMAValues);
        when(this.mockDailySma.getValues()).thenReturn(dailyValues);
        //slowWMA and priceSma line segments are irrelevant for this test
        when(this.mockSlowWma.getValues()).thenReturn(middleWMAValues);
        when(this.mockPriceSma.getValues()).thenReturn(middleWMAValues);

        TradeImpl tradeImpl = this.signalGenerator.generateTrade();

        assertFalse("TradeImpl must not be tradable", tradeImpl.getTradable());
    }

    @Test
    public void WhenFastWMACrossMiddleWMAWithFirstFastWMAPointOnTopOfFirstMiddleWMAPointThenGenerateCorrectSignal() {

        List<BigDecimal> fastWMAValues = createIndicatorValues(1.23119, 1.23339, 1.23339);
        List<BigDecimal> middleWMAValues = createIndicatorValues(1.23119, 1.23196, 1.23196);
        List<BigDecimal> slowWMAValues = createIndicatorValues(1.23019, 1.23039, 1.23039);
        List<BigDecimal> dailyValues = createIndicatorValues(1.5656, 1.5656, 1.5656);
        List<BigDecimal> rsiValues = createIndicatorValues(49, 50, 22);

        when(this.mockRsi.getValues()).thenReturn(rsiValues);
        when(this.mockFastWma.getValues()).thenReturn(fastWMAValues);
        when(this.mockMiddleWma.getValues()).thenReturn(middleWMAValues);
        when(this.mockDailySma.getValues()).thenReturn(dailyValues);
        //slowWMA and priceSma line segments are irrelevant for this test
        when(this.mockSlowWma.getValues()).thenReturn(slowWMAValues);
        when(this.mockPriceSma.getValues()).thenReturn(middleWMAValues);

        TradeImpl tradeImpl = this.signalGenerator.generateTrade();

        BigDecimal entryPrice = tradeImpl.getEntryPrice();
        int compareEntry = BigDecimal.valueOf(1.23339).compareTo(entryPrice);

        BigDecimal stopLossPrice = tradeImpl.getStopLossPrice();
        int compareStopLoss = BigDecimal.valueOf(1.23069).compareTo(stopLossPrice);

        assertTrue("TradeImpl must be tradable", tradeImpl.getTradable());
        assertEquals(0, compareEntry);
        assertEquals(0, compareStopLoss);

    }


    @Test
    public void WhenPriceSMACrossMiddleWMAFromBelowThenGenerateCorrectLongSignal() {

        List<BigDecimal> fastWMAValues = createIndicatorValues(1.22789, 1.22839, 1.22939);
        List<BigDecimal> priceSMAValues = createIndicatorValues(1.22889, 1.23339, 1.23339);
        List<BigDecimal> middleWMAValues =createIndicatorValues(1.23119, 1.23196, 1.23196);
        List<BigDecimal> slowWMAValues = createIndicatorValues(1.22689, 1.22739, 1.22639);
        List<BigDecimal> dailyValues = createIndicatorValues(1.5656, 1.5656, 1.5656);
        List<BigDecimal> rsiValues = createIndicatorValues(49, 50, 22);

        when(this.mockRsi.getValues()).thenReturn(rsiValues);
        when(this.mockFastWma.getValues()).thenReturn(fastWMAValues);
        when(this.mockPriceSma.getValues()).thenReturn(priceSMAValues);
        when(this.mockMiddleWma.getValues()).thenReturn(middleWMAValues);
        when(this.mockDailySma.getValues()).thenReturn(dailyValues);
        when(this.mockSlowWma.getValues()).thenReturn(slowWMAValues);

        TradeImpl tradeImpl = this.signalGenerator.generateTrade();

        BigDecimal entryPrice = tradeImpl.getEntryPrice();
        int compareEntry = BigDecimal.valueOf(1.23386).compareTo(entryPrice);

        BigDecimal stopLossPrice = tradeImpl.getStopLossPrice();
        int compareStopLoss = BigDecimal.valueOf(1.23116).compareTo(stopLossPrice);

        assertTrue("TradeImpl must be tradable", tradeImpl.getTradable());
        assertEquals(0, compareEntry);
        assertEquals(0, compareStopLoss);

    }

    @Test
    public void WhenPriceSMACrossMiddleWMAFromAboveThenGenerateCorrectShortSignal() {

        List<BigDecimal> fastWMAValues = createIndicatorValues(1.23519, 1.23496, 1.23496);
        List<BigDecimal> priceSMAValues = createIndicatorValues(1.23219, 1.23196, 1.23196);
        List<BigDecimal> middleWMAValues = createIndicatorValues(1.22889, 1.23339, 1.23339);
        List<BigDecimal> slowWMAValues = createIndicatorValues(1.23889, 1.23739, 1.23639);
        List<BigDecimal> dailyValues = createIndicatorValues(1.5656, 1.5656, 1.5656);
        List<BigDecimal> rsiValues = createIndicatorValues(49, 50, 22);

        when(this.mockRsi.getValues()).thenReturn(rsiValues);
        when(this.mockFastWma.getValues()).thenReturn(fastWMAValues);
        when(this.mockPriceSma.getValues()).thenReturn(priceSMAValues);
        when(this.mockMiddleWma.getValues()).thenReturn(middleWMAValues);
        when(this.mockSlowWma.getValues()).thenReturn(slowWMAValues);
        when(this.mockDailySma.getValues()).thenReturn(dailyValues);

        TradeImpl tradeImpl = this.signalGenerator.generateTrade();

        BigDecimal entryPrice = tradeImpl.getEntryPrice();
        int compareEntry = BigDecimal.valueOf(1.23003).compareTo(entryPrice);

        BigDecimal stopLossPrice = tradeImpl.getStopLossPrice();
        int compareStopLoss = BigDecimal.valueOf(1.23273).compareTo(stopLossPrice);

        assertTrue("TradeImpl must be tradable", tradeImpl.getTradable());
        assertEquals(0, compareEntry);
        assertEquals(0, compareStopLoss);

    }

    @Test
    public void WhenPriceSMACrossMiddleWMAFromBelowWithRSIBelowFilterThenGenerateNonTradableSignal() {

        List<BigDecimal> fastWMAValues = createIndicatorValues(1.23419, 1.23419, 1.23419);
        List<BigDecimal> priceSMAValues = createIndicatorValues(1.23219, 1.23376, 1.23386);
        List<BigDecimal> middleWMAValues = createIndicatorValues(1.23289, 1.23339, 1.23339);
        List<BigDecimal> dailyValues = createIndicatorValues(1.5656, 1.5656, 1.5656);
        List<BigDecimal> rsiValues = createIndicatorValues(49, 49, 22);

        when(this.mockRsi.getValues()).thenReturn(rsiValues);
        when(this.mockFastWma.getValues()).thenReturn(fastWMAValues);
        when(this.mockPriceSma.getValues()).thenReturn(priceSMAValues);
        when(this.mockMiddleWma.getValues()).thenReturn(middleWMAValues);
        when(this.mockDailySma.getValues()).thenReturn(dailyValues);
        when(this.mockSlowWma.getValues()).thenReturn(middleWMAValues);

        TradeImpl tradeImpl = this.signalGenerator.generateTrade();

        assertFalse("TradeImpl must not be tradable", tradeImpl.getTradable());
    }

    @Test
    public void WhenPriceSMACrossMiddleWMAFromAboveWithRSIAboveFilterThenGenerateNonTradableSignal() {

        List<BigDecimal> fastWMAValues = createIndicatorValues(1.23419, 1.23419, 1.23419);
        List<BigDecimal> priceSMAValues = createIndicatorValues(1.23219, 1.23196, 1.23196);
        List<BigDecimal> middleWMAValues = createIndicatorValues(1.22889, 1.23339, 1.23339);
        List<BigDecimal> dailyValues = createIndicatorValues(1.5656, 1.5656, 1.5656);
        List<BigDecimal> rsiValues = createIndicatorValues(51, 51, 51);

        when(this.mockRsi.getValues()).thenReturn(rsiValues);
        when(this.mockFastWma.getValues()).thenReturn(fastWMAValues);
        when(this.mockPriceSma.getValues()).thenReturn(priceSMAValues);
        when(this.mockMiddleWma.getValues()).thenReturn(middleWMAValues);
        when(this.mockDailySma.getValues()).thenReturn(dailyValues);
        when(this.mockSlowWma.getValues()).thenReturn(middleWMAValues);

        TradeImpl tradeImpl = this.signalGenerator.generateTrade();

        assertFalse("TradeImpl must not be tradable", tradeImpl.getTradable());
    }

    @Test
    public void WhenPriceSMACrossMiddleWMAWithFirstPriceWMAPointOnTopOfFirstMiddleWMAPointThenGenerateCorrectLongSignal() {

        List<BigDecimal> fastWMAValues = createIndicatorValues(1.23419, 1.23439, 1.23439);
        List<BigDecimal> priceSMAValues = createIndicatorValues(1.23119, 1.23339, 1.23339);
        List<BigDecimal> middleWMAValues = createIndicatorValues(1.23119, 1.23196, 1.23196);
        List<BigDecimal> slowWMAValues = createIndicatorValues(1.23019, 1.23039, 1.23039);
        List<BigDecimal> dailyValues = createIndicatorValues(1.5656, 1.5656, 1.5656);
        List<BigDecimal> rsiValues = createIndicatorValues(49, 50, 22);

        when(this.mockRsi.getValues()).thenReturn(rsiValues);
        when(this.mockFastWma.getValues()).thenReturn(fastWMAValues);
        when(this.mockPriceSma.getValues()).thenReturn(priceSMAValues);
        when(this.mockMiddleWma.getValues()).thenReturn(middleWMAValues);
        when(this.mockDailySma.getValues()).thenReturn(dailyValues);
        when(this.mockSlowWma.getValues()).thenReturn(slowWMAValues);

        TradeImpl tradeImpl = this.signalGenerator.generateTrade();

        BigDecimal entryPrice = tradeImpl.getEntryPrice();
        int compareEntry = BigDecimal.valueOf(1.23339).compareTo(entryPrice);

        BigDecimal stopLossPrice = tradeImpl.getStopLossPrice();
        int compareStopLoss = BigDecimal.valueOf(1.23069).compareTo(stopLossPrice);

        assertTrue("TradeImpl must be tradable", tradeImpl.getTradable());
        assertEquals(0, compareEntry);
        assertEquals(0, compareStopLoss);
    }

    @Test
    public void WhenPriceSMACrossSlowWMAFromBelowThenGenerateCorrectLongSignal() {

        List<BigDecimal> fastWMAValues = createIndicatorValues(1.22989, 1.23019, 1.23019);
        List<BigDecimal> priceSMAValues = createIndicatorValues(1.22889, 1.22993, 1.22999);
        List<BigDecimal> middleWMAValues = createIndicatorValues(1.23119, 1.23196, 1.23196);
        List<BigDecimal> slowWMAValues = createIndicatorValues(1.22909, 1.22939, 1.22939);
        List<BigDecimal> dailyValues = createIndicatorValues(1.5656, 1.5656, 1.5656);
        List<BigDecimal> rsiValues = createIndicatorValues(49, 50, 22);

        when(this.mockRsi.getValues()).thenReturn(rsiValues);
        when(this.mockFastWma.getValues()).thenReturn(fastWMAValues);
        when(this.mockPriceSma.getValues()).thenReturn(priceSMAValues);
        when(this.mockMiddleWma.getValues()).thenReturn(middleWMAValues);
        when(this.mockDailySma.getValues()).thenReturn(dailyValues);
        when(this.mockSlowWma.getValues()).thenReturn(slowWMAValues);

        TradeImpl tradeImpl = this.signalGenerator.generateTrade();

        BigDecimal entryPrice = tradeImpl.getEntryPrice();
        int compareEntry = BigDecimal.valueOf(1.23995).compareTo(entryPrice);

        BigDecimal stopLossPrice = tradeImpl.getStopLossPrice();
        int compareStopLoss = BigDecimal.valueOf(1.23725).compareTo(stopLossPrice);

        assertTrue("TradeImpl must be tradable", tradeImpl.getTradable());
        assertEquals(0, compareEntry);
        assertEquals(0, compareStopLoss);
    }

    @Test
    public void WhenPriceSMACrossSlowWMAFromAboveThenGenerateCorrectShortSignal() {

        List<BigDecimal> fastWMAValues = createIndicatorValues(1.23639, 1.23496, 1.23496);
        List<BigDecimal> priceSMAValues = createIndicatorValues(1.23991, 1.23716, 1.23716);
        List<BigDecimal> middleWMAValues = createIndicatorValues(1.23619, 1.23239, 1.23339);
        List<BigDecimal> slowWMAValues = createIndicatorValues(1.23889, 1.23739, 1.23739);
        List<BigDecimal> dailyValues = createIndicatorValues(1.5656, 1.5656, 1.5656);
        List<BigDecimal> rsiValues = createIndicatorValues(49, 50, 22);

        when(this.mockRsi.getValues()).thenReturn(rsiValues);
        when(this.mockFastWma.getValues()).thenReturn(fastWMAValues);
        when(this.mockPriceSma.getValues()).thenReturn(priceSMAValues);
        when(this.mockMiddleWma.getValues()).thenReturn(middleWMAValues);
        when(this.mockSlowWma.getValues()).thenReturn(slowWMAValues);
        when(this.mockDailySma.getValues()).thenReturn(dailyValues);

        TradeImpl tradeImpl = this.signalGenerator.generateTrade();

        BigDecimal entryPrice = tradeImpl.getEntryPrice();
        int compareEntry = BigDecimal.valueOf(1.24765).compareTo(entryPrice);

        BigDecimal stopLossPrice = tradeImpl.getStopLossPrice();
        int compareStopLoss = BigDecimal.valueOf(1.25035).compareTo(stopLossPrice);

        assertTrue("TradeImpl must be tradable", tradeImpl.getTradable());
        assertEquals(0, compareEntry);
        assertEquals(0, compareStopLoss);

    }

    @Test
    public void WhenPriceSMACrossSlowWMAFromAboveWithRSIAboveFilterThenGenerateNonTradableSignal() {

        List<BigDecimal> fastWMAValues = createIndicatorValues(1.23639, 1.23496, 1.23496);
        List<BigDecimal> priceSMAValues = createIndicatorValues(1.23991, 1.23716, 1.23716);
        List<BigDecimal> middleWMAValues = createIndicatorValues(1.23619, 1.23239, 1.23339);
        List<BigDecimal> slowWMAValues = createIndicatorValues(1.23889, 1.23739, 1.23739);
        List<BigDecimal> dailyValues = createIndicatorValues(1.5656, 1.5656, 1.5656);
        List<BigDecimal> rsiValues = createIndicatorValues(51, 51, 51);

        when(this.mockRsi.getValues()).thenReturn(rsiValues);

        when(this.mockFastWma.getValues()).thenReturn(fastWMAValues);
        when(this.mockPriceSma.getValues()).thenReturn(priceSMAValues);
        when(this.mockMiddleWma.getValues()).thenReturn(middleWMAValues);
        when(this.mockDailySma.getValues()).thenReturn(dailyValues);

        when(this.mockSlowWma.getValues()).thenReturn(slowWMAValues);

        TradeImpl tradeImpl = this.signalGenerator.generateTrade();

        assertFalse("TradeImpl must not be tradable", tradeImpl.getTradable());
    }

    @Test
    public void WhenPriceSMACrossSlowWMAFromBelowWithRSIBelowFilterThenGenerateNonTradableSignal() {

        List<BigDecimal> fastWMAValues = createIndicatorValues(1.23639, 1.23496, 1.23496);
        List<BigDecimal> priceSMAValues = createIndicatorValues(1.23789, 1.23826, 1.23826);
        List<BigDecimal> middleWMAValues = createIndicatorValues(1.23619, 1.23239, 1.23339);
        List<BigDecimal> slowWMAValues = createIndicatorValues(1.23791, 1.23793, 1.23793);
        List<BigDecimal> dailyValues = createIndicatorValues(1.5656, 1.5656, 1.5656);
        List<BigDecimal> rsiValues = createIndicatorValues(49, 49, 49);

        when(this.mockRsi.getValues()).thenReturn(rsiValues);

        when(this.mockFastWma.getValues()).thenReturn(fastWMAValues);
        when(this.mockPriceSma.getValues()).thenReturn(priceSMAValues);
        when(this.mockMiddleWma.getValues()).thenReturn(middleWMAValues);
        when(this.mockDailySma.getValues()).thenReturn(dailyValues);
        when(this.mockSlowWma.getValues()).thenReturn(slowWMAValues);

        TradeImpl tradeImpl = this.signalGenerator.generateTrade();

        assertFalse("TradeImpl must not be tradable", tradeImpl.getTradable());
    }

    @Test
    public void WhenPriceSMACrossSlowWMAWithFirstPriceWMAPointOnTopOfFirstSlowWMAPointThenGenerateCorrectLongSignal() {

        List<BigDecimal> fastWMAValues = createIndicatorValues(1.23109, 1.23109, 1.23109);
        List<BigDecimal> priceSMAValues = createIndicatorValues(1.23019, 1.23099, 1.23099);
        List<BigDecimal> middleWMAValues = createIndicatorValues(1.23119, 1.23196, 1.23196);
        List<BigDecimal> slowWMAValues = createIndicatorValues(1.23019, 1.23039, 1.23039);
        List<BigDecimal> dailyValues = createIndicatorValues(1.5656, 1.5656, 1.5656);
        List<BigDecimal> rsiValues = createIndicatorValues(49, 50, 22);

        when(this.mockRsi.getValues()).thenReturn(rsiValues);
        when(this.mockFastWma.getValues()).thenReturn(fastWMAValues);
        when(this.mockPriceSma.getValues()).thenReturn(priceSMAValues);
        when(this.mockMiddleWma.getValues()).thenReturn(middleWMAValues);
        when(this.mockDailySma.getValues()).thenReturn(dailyValues);
        when(this.mockSlowWma.getValues()).thenReturn(slowWMAValues);

        TradeImpl tradeImpl = this.signalGenerator.generateTrade();

        BigDecimal entryPrice = tradeImpl.getEntryPrice();
        int compareEntry = BigDecimal.valueOf(1.25906).compareTo(entryPrice);

        BigDecimal stopLossPrice = tradeImpl.getStopLossPrice();
        int compareStopLoss = BigDecimal.valueOf(1.25636).compareTo(stopLossPrice);

        assertTrue("TradeImpl must be tradable", tradeImpl.getTradable());
        assertEquals(0, compareEntry);
        assertEquals(0, compareStopLoss);
    }

    private List<BigDecimal> createIndicatorValues(double ... prices) {
        List<BigDecimal> maValues = new ArrayList<>();
        for (double price :prices)
            maValues.add( BigDecimal.valueOf(price));
        return maValues;
    }

    private void init() {
//        this.indicatorUpdateHelper.fillCandlestickList();
//        addExtraCandlesToTestList(170);
//        when(baseGateway.getInitialCandles()).thenReturn(indicatorUpdateHelper.getCandlestickList());
    }

    private void addExtraCandlesToTestList(int quantity) {
        for (int i = 0; i <quantity ; i++) {
            Candlestick candlestickMock = this.indicatorUpdateHelper.createCandlestickMock();
            this.indicatorUpdateHelper.candlestickList.add(candlestickMock);
        }
    }
}