package trader.trades.generators;

import org.junit.Before;
import org.junit.Test;
import trader.indicators.Indicator;
import trader.indicators.ma.SimpleMA;
import trader.indicators.ma.WeightedMA;
import trader.indicators.rsi.RelativeStrengthIndex;
import trader.trades.entities.Trade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BGXTradeGeneratorTest {



    private BGXTradeGenerator signalGenerator;
    private Indicator mockSlowWma;
    private Indicator mockMiddleWma;
    private Indicator mockFastWma;
    private Indicator mockPriceSma;
    private Indicator mockDailySma;
    private Indicator mockRsi;


    @Before
    public void before() throws Exception {


        this.mockSlowWma = mock(WeightedMA.class);
        this.mockMiddleWma = mock(WeightedMA.class);
        this.mockFastWma = mock(WeightedMA.class);
        this.mockPriceSma = mock(SimpleMA.class);
        this.mockDailySma = mock(SimpleMA.class);
        this.mockRsi = mock(RelativeStrengthIndex.class);

        this.signalGenerator = new BGXTradeGenerator(this.mockFastWma, this.mockMiddleWma, this.mockSlowWma, this.mockPriceSma, this.mockDailySma, this.mockRsi);

    }

    @Test
    public void WhenFastWMACrossMiddleWMAFromBelowThenGenerateCorrectLongSignal() {

        List<BigDecimal> fastWMAValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.22889), BigDecimal.valueOf(1.23339), BigDecimal.valueOf(1.23339)
        ));

        List<BigDecimal> middleWMAValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.23119), BigDecimal.valueOf(1.23196), BigDecimal.valueOf(1.23196)
        ));

        List<BigDecimal> slowWMAValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.22889), BigDecimal.valueOf(1.22739), BigDecimal.valueOf(1.22639)
        ));

        List<BigDecimal> dailyValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.5656), BigDecimal.valueOf(1.5656), BigDecimal.valueOf(1.5656)
        ));

        List<BigDecimal> rsiValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(49), BigDecimal.valueOf(50), BigDecimal.valueOf(22)
        ));

        when(this.mockRsi.getValues()).thenReturn(rsiValues);

        when(this.mockFastWma.getValues()).thenReturn(fastWMAValues);
        when(this.mockMiddleWma.getValues()).thenReturn(middleWMAValues);
        when(this.mockDailySma.getValues()).thenReturn(dailyValues);
        when(this.mockSlowWma.getValues()).thenReturn(slowWMAValues);
        //slowWMA and priceSma line segments are irrelevant for this test

        when(this.mockPriceSma.getValues()).thenReturn(middleWMAValues);

        Trade trade = this.signalGenerator.generateTrade();

        BigDecimal entryPrice = trade.getEntryPrice();
        int compareEntry = BigDecimal.valueOf(1.23386).compareTo(entryPrice);

        BigDecimal stopLossPrice = trade.getStopLossPrice();
        int compareStopLoss = BigDecimal.valueOf(1.23116).compareTo(stopLossPrice);

        assertTrue("Trade must be tradable", trade.getTradable());
        assertEquals(0, compareEntry);
        assertEquals(0, compareStopLoss);

    }

    @Test
    public void WhenFastWMACrossMiddleWMAFromAboveThenGenerateCorrectShortSignal() {

        List<BigDecimal> fastWMAValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.23219), BigDecimal.valueOf(1.23196), BigDecimal.valueOf(1.23196)

        ));

        List<BigDecimal> middleWMAValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.22889), BigDecimal.valueOf(1.23339), BigDecimal.valueOf(1.23339)
        ));

        List<BigDecimal> slowWMAValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.23889), BigDecimal.valueOf(1.23739), BigDecimal.valueOf(1.23639)
        ));

        List<BigDecimal> dailyValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.5656), BigDecimal.valueOf(1.5656), BigDecimal.valueOf(1.5656)
        ));


        List<BigDecimal> rsiValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(49), BigDecimal.valueOf(50), BigDecimal.valueOf(22)
        ));

        when(this.mockRsi.getValues()).thenReturn(rsiValues);
        when(this.mockFastWma.getValues()).thenReturn(fastWMAValues);
        when(this.mockMiddleWma.getValues()).thenReturn(middleWMAValues);
        when(this.mockSlowWma.getValues()).thenReturn(slowWMAValues);
        when(this.mockDailySma.getValues()).thenReturn(dailyValues);

        //slowWMA and priceSma line segments are irrelevant for this test

        when(this.mockPriceSma.getValues()).thenReturn(middleWMAValues);

        Trade trade = this.signalGenerator.generateTrade();

        BigDecimal entryPrice = trade.getEntryPrice();
        int compareEntry = BigDecimal.valueOf(1.23003).compareTo(entryPrice);

        BigDecimal stopLossPrice = trade.getStopLossPrice();
        int compareStopLoss = BigDecimal.valueOf(1.23273).compareTo(stopLossPrice);

        assertTrue("Trade must be tradable", trade.getTradable());
        assertEquals(0, compareEntry);
        assertEquals(0, compareStopLoss);

    }

    @Test
    public void WhenFastWMACrossMiddleWMAFromAboveWithRSIAboveFilterThenGenerateNonTradableSignal() {

        List<BigDecimal> fastWMAValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.23219), BigDecimal.valueOf(1.23196), BigDecimal.valueOf(1.23196)

        ));

        List<BigDecimal> middleWMAValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.22889), BigDecimal.valueOf(1.23339), BigDecimal.valueOf(1.23339)
        ));

        List<BigDecimal> dailyValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.5656), BigDecimal.valueOf(1.5656), BigDecimal.valueOf(1.5656)
        ));

        List<BigDecimal> rsiValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(51), BigDecimal.valueOf(51), BigDecimal.valueOf(51)
        ));

        when(this.mockRsi.getValues()).thenReturn(rsiValues);

        when(this.mockFastWma.getValues()).thenReturn(fastWMAValues);
        when(this.mockMiddleWma.getValues()).thenReturn(middleWMAValues);
        when(this.mockDailySma.getValues()).thenReturn(dailyValues);

        //slowWMA and priceSma line segments are irrelevant for this test
        when(this.mockSlowWma.getValues()).thenReturn(middleWMAValues);
        when(this.mockPriceSma.getValues()).thenReturn(middleWMAValues);

        Trade trade = this.signalGenerator.generateTrade();


        assertFalse("Trade must not be tradable", trade.getTradable());

    }

    @Test
    public void WhenFastWMACrossMiddleWMAFromBelowWithRSIBelowFilterThenGenerateNonTradableSignal() {

        List<BigDecimal> fastWMAValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.23119), BigDecimal.valueOf(1.23296), BigDecimal.valueOf(1.23296)

        ));

        List<BigDecimal> middleWMAValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.23159), BigDecimal.valueOf(1.23239), BigDecimal.valueOf(1.23239)
        ));

        List<BigDecimal> dailyValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.5656), BigDecimal.valueOf(1.5656), BigDecimal.valueOf(1.5656)
        ));

        List<BigDecimal> rsiValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(49), BigDecimal.valueOf(49), BigDecimal.valueOf(49)
        ));

        when(this.mockRsi.getValues()).thenReturn(rsiValues);

        when(this.mockFastWma.getValues()).thenReturn(fastWMAValues);
        when(this.mockMiddleWma.getValues()).thenReturn(middleWMAValues);
        when(this.mockDailySma.getValues()).thenReturn(dailyValues);

        //slowWMA and priceSma line segments are irrelevant for this test
        when(this.mockSlowWma.getValues()).thenReturn(middleWMAValues);
        when(this.mockPriceSma.getValues()).thenReturn(middleWMAValues);

        Trade trade = this.signalGenerator.generateTrade();


        assertFalse("Trade must not be tradable", trade.getTradable());

    }


    @Test
    public void WhenFastWMACrossMiddleWMAWithFirstFastWMAPointOnTopOfFirstMiddleWMAPointThenGenerateCorrectSignal() {

        List<BigDecimal> fastWMAValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.23119), BigDecimal.valueOf(1.23339), BigDecimal.valueOf(1.23339)


        ));

        List<BigDecimal> middleWMAValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.23119), BigDecimal.valueOf(1.23196), BigDecimal.valueOf(1.23196)
        ));

        List<BigDecimal> slowWMAValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.23019), BigDecimal.valueOf(1.23039), BigDecimal.valueOf(1.23039)
        ));

        List<BigDecimal> dailyValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.5656), BigDecimal.valueOf(1.5656), BigDecimal.valueOf(1.5656)
        ));


        List<BigDecimal> rsiValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(49), BigDecimal.valueOf(50), BigDecimal.valueOf(22)
        ));

        when(this.mockRsi.getValues()).thenReturn(rsiValues);

        when(this.mockFastWma.getValues()).thenReturn(fastWMAValues);
        when(this.mockMiddleWma.getValues()).thenReturn(middleWMAValues);
        when(this.mockDailySma.getValues()).thenReturn(dailyValues);
        when(this.mockSlowWma.getValues()).thenReturn(slowWMAValues);

        //slowWMA and priceSma line segments are irrelevant for this test

        when(this.mockPriceSma.getValues()).thenReturn(middleWMAValues);

        Trade trade = this.signalGenerator.generateTrade();

        BigDecimal entryPrice = trade.getEntryPrice();
        int compareEntry = BigDecimal.valueOf(1.23339).compareTo(entryPrice);

        BigDecimal stopLossPrice = trade.getStopLossPrice();
        int compareStopLoss = BigDecimal.valueOf(1.23069).compareTo(stopLossPrice);

        assertTrue("Trade must be tradable", trade.getTradable());
        assertEquals(0, compareEntry);
        assertEquals(0, compareStopLoss);

    }


    @Test
    public void WhenPriceSMACrossMiddleWMAFromBelowThenGenerateCorrectLongSignal() {

        List<BigDecimal> fastWMAValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.22789), BigDecimal.valueOf(1.22839), BigDecimal.valueOf(1.22939)
        ));

        List<BigDecimal> priceSMAValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.22889), BigDecimal.valueOf(1.23339), BigDecimal.valueOf(1.23339)
        ));

        List<BigDecimal> middleWMAValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.23119), BigDecimal.valueOf(1.23196), BigDecimal.valueOf(1.23196)
        ));

        List<BigDecimal> slowWMAValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.22689), BigDecimal.valueOf(1.22739), BigDecimal.valueOf(1.22639)
        ));

        List<BigDecimal> dailyValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.5656), BigDecimal.valueOf(1.5656), BigDecimal.valueOf(1.5656)
        ));

        List<BigDecimal> rsiValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(49), BigDecimal.valueOf(50), BigDecimal.valueOf(22)
        ));

        when(this.mockRsi.getValues()).thenReturn(rsiValues);

        when(this.mockFastWma.getValues()).thenReturn(fastWMAValues);
        when(this.mockPriceSma.getValues()).thenReturn(priceSMAValues);
        when(this.mockMiddleWma.getValues()).thenReturn(middleWMAValues);
        when(this.mockDailySma.getValues()).thenReturn(dailyValues);
        when(this.mockSlowWma.getValues()).thenReturn(slowWMAValues);


        Trade trade = this.signalGenerator.generateTrade();

        BigDecimal entryPrice = trade.getEntryPrice();
        int compareEntry = BigDecimal.valueOf(1.23386).compareTo(entryPrice);

        BigDecimal stopLossPrice = trade.getStopLossPrice();
        int compareStopLoss = BigDecimal.valueOf(1.23116).compareTo(stopLossPrice);

        assertTrue("Trade must be tradable", trade.getTradable());
        assertEquals(0, compareEntry);
        assertEquals(0, compareStopLoss);

    }

    @Test
    public void WhenPriceSMACrossMiddleWMAFromAboveThenGenerateCorrectShortSignal() {

        List<BigDecimal> fastWMAValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.23519), BigDecimal.valueOf(1.23496), BigDecimal.valueOf(1.23496)

        ));

        List<BigDecimal> priceSMAValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.23219), BigDecimal.valueOf(1.23196), BigDecimal.valueOf(1.23196)

        ));

        List<BigDecimal> middleWMAValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.22889), BigDecimal.valueOf(1.23339), BigDecimal.valueOf(1.23339)
        ));

        List<BigDecimal> slowWMAValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.23889), BigDecimal.valueOf(1.23739), BigDecimal.valueOf(1.23639)
        ));

        List<BigDecimal> dailyValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.5656), BigDecimal.valueOf(1.5656), BigDecimal.valueOf(1.5656)
        ));


        List<BigDecimal> rsiValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(49), BigDecimal.valueOf(50), BigDecimal.valueOf(22)
        ));

        when(this.mockRsi.getValues()).thenReturn(rsiValues);
        when(this.mockFastWma.getValues()).thenReturn(fastWMAValues);
        when(this.mockPriceSma.getValues()).thenReturn(priceSMAValues);
        when(this.mockMiddleWma.getValues()).thenReturn(middleWMAValues);
        when(this.mockSlowWma.getValues()).thenReturn(slowWMAValues);
        when(this.mockDailySma.getValues()).thenReturn(dailyValues);

        Trade trade = this.signalGenerator.generateTrade();

        BigDecimal entryPrice = trade.getEntryPrice();
        int compareEntry = BigDecimal.valueOf(1.23003).compareTo(entryPrice);

        BigDecimal stopLossPrice = trade.getStopLossPrice();
        int compareStopLoss = BigDecimal.valueOf(1.23273).compareTo(stopLossPrice);

        assertTrue("Trade must be tradable", trade.getTradable());
        assertEquals(0, compareEntry);
        assertEquals(0, compareStopLoss);

    }

    @Test
    public void WhenPriceSMACrossMiddleWMAFromBelowWithRSIBelowFilterThenGenerateNonTradableSignal() {

        List<BigDecimal> fastWMAValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.23419), BigDecimal.valueOf(1.23419), BigDecimal.valueOf(1.23419)

        ));

        List<BigDecimal> priceSMAValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.23219), BigDecimal.valueOf(1.23376), BigDecimal.valueOf(1.23386)

        ));

        List<BigDecimal> middleWMAValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.23289), BigDecimal.valueOf(1.23339), BigDecimal.valueOf(1.23339)
        ));

        List<BigDecimal> dailyValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.5656), BigDecimal.valueOf(1.5656), BigDecimal.valueOf(1.5656)
        ));

        List<BigDecimal> rsiValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(49), BigDecimal.valueOf(49), BigDecimal.valueOf(22)
        ));

        when(this.mockRsi.getValues()).thenReturn(rsiValues);

        when(this.mockFastWma.getValues()).thenReturn(fastWMAValues);
        when(this.mockPriceSma.getValues()).thenReturn(priceSMAValues);
        when(this.mockMiddleWma.getValues()).thenReturn(middleWMAValues);
        when(this.mockDailySma.getValues()).thenReturn(dailyValues);


        when(this.mockSlowWma.getValues()).thenReturn(middleWMAValues);


        Trade trade = this.signalGenerator.generateTrade();


        assertFalse("Trade must not be tradable", trade.getTradable());

    }

    @Test
    public void WhenPriceSMACrossMiddleWMAFromAboveWithRSIAboveFilterThenGenerateNonTradableSignal() {

        List<BigDecimal> fastWMAValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.23419), BigDecimal.valueOf(1.23419), BigDecimal.valueOf(1.23419)

        ));

        List<BigDecimal> priceSMAValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.23219), BigDecimal.valueOf(1.23196), BigDecimal.valueOf(1.23196)

        ));

        List<BigDecimal> middleWMAValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.22889), BigDecimal.valueOf(1.23339), BigDecimal.valueOf(1.23339)
        ));

        List<BigDecimal> dailyValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.5656), BigDecimal.valueOf(1.5656), BigDecimal.valueOf(1.5656)
        ));

        List<BigDecimal> rsiValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(51), BigDecimal.valueOf(51), BigDecimal.valueOf(51)
        ));

        when(this.mockRsi.getValues()).thenReturn(rsiValues);

        when(this.mockFastWma.getValues()).thenReturn(fastWMAValues);
        when(this.mockPriceSma.getValues()).thenReturn(priceSMAValues);
        when(this.mockMiddleWma.getValues()).thenReturn(middleWMAValues);
        when(this.mockDailySma.getValues()).thenReturn(dailyValues);


        when(this.mockSlowWma.getValues()).thenReturn(middleWMAValues);


        Trade trade = this.signalGenerator.generateTrade();


        assertFalse("Trade must not be tradable", trade.getTradable());

    }

    @Test
    public void WhenPriceSMACrossMiddleWMAWithFirstPriceWMAPointOnTopOfFirstMiddleWMAPointThenGenerateCorrectLongSignal() {

        List<BigDecimal> fastWMAValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.23419), BigDecimal.valueOf(1.23439), BigDecimal.valueOf(1.23439)
        ));

        List<BigDecimal> priceSMAValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.23119), BigDecimal.valueOf(1.23339), BigDecimal.valueOf(1.23339)
        ));

        List<BigDecimal> middleWMAValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.23119), BigDecimal.valueOf(1.23196), BigDecimal.valueOf(1.23196)
        ));

        List<BigDecimal> slowWMAValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.23019), BigDecimal.valueOf(1.23039), BigDecimal.valueOf(1.23039)
        ));

        List<BigDecimal> dailyValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.5656), BigDecimal.valueOf(1.5656), BigDecimal.valueOf(1.5656)
        ));


        List<BigDecimal> rsiValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(49), BigDecimal.valueOf(50), BigDecimal.valueOf(22)
        ));

        when(this.mockRsi.getValues()).thenReturn(rsiValues);

        when(this.mockFastWma.getValues()).thenReturn(fastWMAValues);
        when(this.mockPriceSma.getValues()).thenReturn(priceSMAValues);
        when(this.mockMiddleWma.getValues()).thenReturn(middleWMAValues);
        when(this.mockDailySma.getValues()).thenReturn(dailyValues);
        when(this.mockSlowWma.getValues()).thenReturn(slowWMAValues);


        Trade trade = this.signalGenerator.generateTrade();

        BigDecimal entryPrice = trade.getEntryPrice();
        int compareEntry = BigDecimal.valueOf(1.23339).compareTo(entryPrice);

        BigDecimal stopLossPrice = trade.getStopLossPrice();
        int compareStopLoss = BigDecimal.valueOf(1.23069).compareTo(stopLossPrice);

        assertTrue("Trade must be tradable", trade.getTradable());
        assertEquals(0, compareEntry);
        assertEquals(0, compareStopLoss);

    }

    @Test
    public void WhenPriceSMACrossSlowWMAFromBelowThenGenerateCorrectLongSignal() {

        List<BigDecimal> fastWMAValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.22989), BigDecimal.valueOf(1.23019), BigDecimal.valueOf(1.23019)
        ));

        List<BigDecimal> priceSMAValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.22889), BigDecimal.valueOf(1.22993), BigDecimal.valueOf(1.22999)
        ));

        List<BigDecimal> middleWMAValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.23119), BigDecimal.valueOf(1.23196), BigDecimal.valueOf(1.23196)
        ));

        List<BigDecimal> slowWMAValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.22909), BigDecimal.valueOf(1.22939), BigDecimal.valueOf(1.22939)
        ));

        List<BigDecimal> dailyValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.5656), BigDecimal.valueOf(1.5656), BigDecimal.valueOf(1.5656)
        ));

        List<BigDecimal> rsiValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(49), BigDecimal.valueOf(50), BigDecimal.valueOf(22)
        ));

        when(this.mockRsi.getValues()).thenReturn(rsiValues);

        when(this.mockFastWma.getValues()).thenReturn(fastWMAValues);
        when(this.mockPriceSma.getValues()).thenReturn(priceSMAValues);
        when(this.mockMiddleWma.getValues()).thenReturn(middleWMAValues);
        when(this.mockDailySma.getValues()).thenReturn(dailyValues);
        when(this.mockSlowWma.getValues()).thenReturn(slowWMAValues);


        Trade trade = this.signalGenerator.generateTrade();

        BigDecimal entryPrice = trade.getEntryPrice();
        int compareEntry = BigDecimal.valueOf(1.23995).compareTo(entryPrice);

        BigDecimal stopLossPrice = trade.getStopLossPrice();
        int compareStopLoss = BigDecimal.valueOf(1.23725).compareTo(stopLossPrice);

        assertTrue("Trade must be tradable", trade.getTradable());
        assertEquals(0, compareEntry);
        assertEquals(0, compareStopLoss);

    }

    @Test
    public void WhenPriceSMACrossSlowWMAFromAboveThenGenerateCorrectShortSignal() {

        List<BigDecimal> fastWMAValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.23639), BigDecimal.valueOf(1.23496), BigDecimal.valueOf(1.23496)

        ));

        List<BigDecimal> priceSMAValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.23991), BigDecimal.valueOf(1.23716), BigDecimal.valueOf(1.23716)

        ));

        List<BigDecimal> middleWMAValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.23619), BigDecimal.valueOf(1.23239), BigDecimal.valueOf(1.23339)
        ));

        List<BigDecimal> slowWMAValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.23889), BigDecimal.valueOf(1.23739), BigDecimal.valueOf(1.23739)
        ));

        List<BigDecimal> dailyValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.5656), BigDecimal.valueOf(1.5656), BigDecimal.valueOf(1.5656)
        ));


        List<BigDecimal> rsiValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(49), BigDecimal.valueOf(50), BigDecimal.valueOf(22)
        ));

        when(this.mockRsi.getValues()).thenReturn(rsiValues);
        when(this.mockFastWma.getValues()).thenReturn(fastWMAValues);
        when(this.mockPriceSma.getValues()).thenReturn(priceSMAValues);
        when(this.mockMiddleWma.getValues()).thenReturn(middleWMAValues);
        when(this.mockSlowWma.getValues()).thenReturn(slowWMAValues);
        when(this.mockDailySma.getValues()).thenReturn(dailyValues);

        Trade trade = this.signalGenerator.generateTrade();

        BigDecimal entryPrice = trade.getEntryPrice();
        int compareEntry = BigDecimal.valueOf(1.24765).compareTo(entryPrice);

        BigDecimal stopLossPrice = trade.getStopLossPrice();
        int compareStopLoss = BigDecimal.valueOf(1.25035).compareTo(stopLossPrice);

        assertTrue("Trade must be tradable", trade.getTradable());
        assertEquals(0, compareEntry);
        assertEquals(0, compareStopLoss);

    }

    @Test
    public void WhenPriceSMACrossSlowWMAFromAboveWithRSIAboveFilterThenGenerateNonTradableSignal() {

        List<BigDecimal> fastWMAValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.23639), BigDecimal.valueOf(1.23496), BigDecimal.valueOf(1.23496)

        ));

        List<BigDecimal> priceSMAValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.23991), BigDecimal.valueOf(1.23716), BigDecimal.valueOf(1.23716)

        ));

        List<BigDecimal> middleWMAValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.23619), BigDecimal.valueOf(1.23239), BigDecimal.valueOf(1.23339)
        ));

        List<BigDecimal> slowWMAValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.23889), BigDecimal.valueOf(1.23739), BigDecimal.valueOf(1.23739)
        ));

        List<BigDecimal> dailyValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.5656), BigDecimal.valueOf(1.5656), BigDecimal.valueOf(1.5656)
        ));

        List<BigDecimal> rsiValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(51), BigDecimal.valueOf(51), BigDecimal.valueOf(51)
        ));

        when(this.mockRsi.getValues()).thenReturn(rsiValues);

        when(this.mockFastWma.getValues()).thenReturn(fastWMAValues);
        when(this.mockPriceSma.getValues()).thenReturn(priceSMAValues);
        when(this.mockMiddleWma.getValues()).thenReturn(middleWMAValues);
        when(this.mockDailySma.getValues()).thenReturn(dailyValues);


        when(this.mockSlowWma.getValues()).thenReturn(slowWMAValues);


        Trade trade = this.signalGenerator.generateTrade();


        assertFalse("Trade must not be tradable", trade.getTradable());

    }

    @Test
    public void WhenPriceSMACrossSlowWMAFromBelowWithRSIBelowFilterThenGenerateNonTradableSignal() {

        List<BigDecimal> fastWMAValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.23639), BigDecimal.valueOf(1.23496), BigDecimal.valueOf(1.23496)

        ));

        List<BigDecimal> priceSMAValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.23789), BigDecimal.valueOf(1.23826), BigDecimal.valueOf(1.23826)

        ));

        List<BigDecimal> middleWMAValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.23619), BigDecimal.valueOf(1.23239), BigDecimal.valueOf(1.23339)
        ));

        List<BigDecimal> slowWMAValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.23791), BigDecimal.valueOf(1.23793), BigDecimal.valueOf(1.23793)
        ));

        List<BigDecimal> dailyValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.5656), BigDecimal.valueOf(1.5656), BigDecimal.valueOf(1.5656)
        ));

        List<BigDecimal> rsiValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(49), BigDecimal.valueOf(49), BigDecimal.valueOf(49)
        ));

        when(this.mockRsi.getValues()).thenReturn(rsiValues);

        when(this.mockFastWma.getValues()).thenReturn(fastWMAValues);
        when(this.mockPriceSma.getValues()).thenReturn(priceSMAValues);
        when(this.mockMiddleWma.getValues()).thenReturn(middleWMAValues);
        when(this.mockDailySma.getValues()).thenReturn(dailyValues);


        when(this.mockSlowWma.getValues()).thenReturn(slowWMAValues);


        Trade trade = this.signalGenerator.generateTrade();


        assertFalse("Trade must not be tradable", trade.getTradable());

    }

    @Test
    public void WhenPriceSMACrossSlowWMAWithFirstPriceWMAPointOnTopOfFirstSlowWMAPointThenGenerateCorrectLongSignal() {

        List<BigDecimal> fastWMAValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.23109), BigDecimal.valueOf(1.23109), BigDecimal.valueOf(1.23109)
        ));

        List<BigDecimal> priceSMAValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.23019), BigDecimal.valueOf(1.23099), BigDecimal.valueOf(1.23099)
        ));

        List<BigDecimal> middleWMAValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.23119), BigDecimal.valueOf(1.23196), BigDecimal.valueOf(1.23196)
        ));

        List<BigDecimal> slowWMAValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.23019), BigDecimal.valueOf(1.23039), BigDecimal.valueOf(1.23039)
        ));

        List<BigDecimal> dailyValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(1.5656), BigDecimal.valueOf(1.5656), BigDecimal.valueOf(1.5656)
        ));


        List<BigDecimal> rsiValues = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(49), BigDecimal.valueOf(50), BigDecimal.valueOf(22)
        ));

        when(this.mockRsi.getValues()).thenReturn(rsiValues);

        when(this.mockFastWma.getValues()).thenReturn(fastWMAValues);
        when(this.mockPriceSma.getValues()).thenReturn(priceSMAValues);
        when(this.mockMiddleWma.getValues()).thenReturn(middleWMAValues);
        when(this.mockDailySma.getValues()).thenReturn(dailyValues);
        when(this.mockSlowWma.getValues()).thenReturn(slowWMAValues);


        Trade trade = this.signalGenerator.generateTrade();

        BigDecimal entryPrice = trade.getEntryPrice();
        int compareEntry = BigDecimal.valueOf(1.25906).compareTo(entryPrice);

        BigDecimal stopLossPrice = trade.getStopLossPrice();
        int compareStopLoss = BigDecimal.valueOf(1.25636).compareTo(stopLossPrice);

        assertTrue("Trade must be tradable", trade.getTradable());
        assertEquals(0, compareEntry);
        assertEquals(0, compareStopLoss);

    }
}