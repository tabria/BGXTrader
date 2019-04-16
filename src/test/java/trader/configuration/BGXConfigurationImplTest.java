package trader.configuration;

import org.junit.Before;
import org.junit.Test;
import trader.entity.candlestick.candle.CandleGranularity;
import trader.exception.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

public class BGXConfigurationImplTest {

    private static final String DEFAULT_INSTRUMENT = "EUR_USD";
    private static final long DEFAULT_INITIAL_CANDLES_QUANTITY = 4999L;
    private static final long DEFAULT_UPDATE_CANDLES_QUANTITY = 2L;
    private static final String DEFAULT_BGX_CONFIG_FILE_LOCATION = "bgxStrategyConfig.yaml";
    private static final BigDecimal DEFAULT_SPREAD = BigDecimal.valueOf(0.0002)
            .setScale(5, BigDecimal.ROUND_HALF_UP);
    private static final BigDecimal DEFAULT_RISK_PER_TRADE = BigDecimal.valueOf(0.01)
            .setScale(5, BigDecimal.ROUND_HALF_UP);
    private static final BigDecimal DEFAULT_ENTRY_FILTER = BigDecimal.valueOf(0.0020)
            .setScale(5, BigDecimal.ROUND_HALF_UP);
    private static final BigDecimal DEFAULT_STOP_LOSS_FILTER = BigDecimal.valueOf(0.0005)
            .setScale(5, BigDecimal.ROUND_HALF_UP);
    private static final BigDecimal FIRST_TARGET = BigDecimal.valueOf(0.0050)
            .setScale(5, RoundingMode.HALF_UP);
    private static final BigDecimal RSI_FILTER = BigDecimal.valueOf(50)
            .setScale(5, RoundingMode.HALF_UP);
    private static final CandleGranularity DEFAULT_EXIT_GRANULARITY = CandleGranularity.M30;
    private static final String DEFAULT_ENTRY_STRATEGY = "standard";
    private static final String DEFAULT_ORDER_STRATEGY = "standard";
    private static final String DEFAULT_EXIT_STRATEGY = "fullClose";


    private BGXConfigurationImpl config;

    @Before
    public void setUp() {
        config = new BGXConfigurationImpl();
    }

    ///////////////////////////////////////
    @Test
    public void whenCallGetIndicators_ReturnList(){
        assertEquals(ArrayList.class, config.getIndicators().getClass());
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallAddIndicatorWithNullIndicator_Exception(){
        config.addIndicator(null);
    }

    @Test
    public void WhenCallAddIndicatorWithCorrectInput_CorrectResult(){
        HashMap<String, String> indicator = new HashMap<>();
        indicator.put("type", "ema");
        config.addIndicator(indicator);

        assertEquals(indicator, config.getIndicators().get(0));
    }
    ////////////////////////

    @Test
    public void givenInitialSettings_WhenCallGetInstrument_ReturnString(){
        assertEquals(String.class, config.getInstrument().getClass());
    }

    @Test
    public void givenInitialSettings_WhenInstantiate_ThenSetToDefaultInstrument(){
        assertEquals(DEFAULT_INSTRUMENT, config.getInstrument());
    }

    @Test
    public void givenNull_WhenCallInitialCandlesQuantity_ThenSetDefaultValue(){
        config.setInitialCandlesQuantity(null);

        assertEquals(DEFAULT_INITIAL_CANDLES_QUANTITY, config.getInitialCandlesQuantity());
    }

    @Test
    public void givenEmpty_WhenCallInitialCandlesQuantity_ThenSetDefaultValue(){
        config.setInitialCandlesQuantity("  ");

        assertEquals(DEFAULT_INITIAL_CANDLES_QUANTITY, config.getInitialCandlesQuantity());
    }

    @Test(expected = NotANumberException.class)
    public void givenNonNumberString_WhenCallInitialCandlesQuantity_ThenThrowException(){
        config.setInitialCandlesQuantity("xxx");
    }

    @Test(expected = UnderflowException.class)
    public void givenStringWithNegativeValueOrZero_WhenCallInitialCandlesQuantity_ThenException(){
        config.setInitialCandlesQuantity("0");
    }

    @Test(expected = OverflowException.class)
    public void givenStringWithMoreThanMaxQuantity_WhenCallInitialCandlesQuantity_ThenException(){
        config.setInitialCandlesQuantity("5001");
    }

    @Test
    public void givenCorrectString_WhenCallSetInitialQuantity_SetCorrectValue(){
        long initialCandlesQuantity = config.getInitialCandlesQuantity();
        config.setInitialCandlesQuantity("15");
        long initialCandlesQuantityUpdated = config.getInitialCandlesQuantity();

        assertNotEquals(initialCandlesQuantity, initialCandlesQuantityUpdated);
        assertEquals(15L, initialCandlesQuantityUpdated);
    }


    ////////////////////////////////////////////////////////////

    @Test
    public void WhenCallSetInitialCandlesQuantityWithNegativeValue_Defaults(){
        config.setInitialCandlesQuantity("-1");

        assertEquals(DEFAULT_INITIAL_CANDLES_QUANTITY, config.getInitialCandlesQuantity());
    }

    @Test
    public void WhenCallSetUpdateCandlesQuantityWithCorrectNumber_CorrectResult(){
        long updateCandlesQuantity = config.getUpdateCandlesQuantity();
        config.setUpdateCandlesQuantity("7");
        long newUpdateCandlesQuantity = config.getUpdateCandlesQuantity();

        assertNotEquals(updateCandlesQuantity, newUpdateCandlesQuantity);
        assertEquals(7L, newUpdateCandlesQuantity);
    }

    @Test
    public void WhenCallSetUpdateCandlesQuantityWithNegativeValue_Defaults(){
        config.setUpdateCandlesQuantity("-1");

        assertEquals(DEFAULT_UPDATE_CANDLES_QUANTITY, config.getUpdateCandlesQuantity());
    }

    @Test
    public void whenCallGetSpreadThenReturnDefaultValue(){
        assertEquals(DEFAULT_SPREAD ,config.getSpread());
    }

    @Test(expected = NullArgumentException.class)
    public void whenCallSetRiskPerTradeWithNull_Exception(){
        config.setRiskPerTrade(null);
    }

    @Test(expected = NegativeNumberException.class)
    public void WhenCallSetRiskPerTradeWithNegativeValue_Exception(){
        config.setRiskPerTrade(new BigDecimal(-20));
    }

    @Test
    public void WhenCallSetRiskPerTradeWithCorrectValue_CorrectResult(){
        BigDecimal expected = new BigDecimal(0.07);
        config.setRiskPerTrade(expected);

        assertEquals(expected, config.getRiskPerTrade());
    }

    @Test
    public void WhenCallGetRiskPerTradeAfterInitializing_Default(){
        assertEquals(DEFAULT_RISK_PER_TRADE, config.getRiskPerTrade());
    }

    @Test
    public void WhenCallSetEntryStrategyWithNull_Default(){
        config.setEntryStrategy(null);

        assertEquals(DEFAULT_ENTRY_STRATEGY, config.getEntryStrategy());
    }

    @Test
    public void WhenCallSetOrderStrategyWithNull_Default(){
        config.setOrderStrategy(null);

        assertEquals(DEFAULT_ORDER_STRATEGY, config.getOrderStrategy());
    }

    @Test
    public void WhenCallSetExitStrategyWithNull_Default(){
        config.setExitStrategy(null);

        assertEquals(DEFAULT_EXIT_STRATEGY, config.getExitStrategy());
    }

    @Test
    public void WhenCallSetEntryStrategyWithEmpty_Default(){
        config.setEntryStrategy("  ");

        assertEquals(DEFAULT_ENTRY_STRATEGY, config.getEntryStrategy());
    }

    @Test
    public void WhenCallSetOrderStrategyWithEmpty_Default(){
        config.setOrderStrategy("  ");

        assertEquals(DEFAULT_ORDER_STRATEGY, config.getOrderStrategy());
    }

    @Test
    public void WhenCallSetExitStrategyWithEmpty_Default(){
        config.setExitStrategy("   ");

        assertEquals(DEFAULT_EXIT_STRATEGY, config.getExitStrategy());
    }

    @Test
    public void WhenCallSetEntryStrategyWithCorrectSetting_CorrectUpdate(){
        config.setEntryStrategy(" bestEntry ");

        assertEquals("bestEntry", config.getEntryStrategy());
    }

    @Test
    public void WhenCallSetOrderStrategyWithCorrectSetting_CorrectUpdate(){
        config.setOrderStrategy(" bestOrder ");

        assertEquals("bestOrder", config.getOrderStrategy());
    }

    @Test
    public void WhenCallSetExitStrategyWithCorrectSetting_CorrectUpdate(){
        config.setExitStrategy("  bestExit  ");

        assertEquals("bestExit", config.getExitStrategy());
    }

    @Test(expected = NullArgumentException.class)
    public void whenCallStopLossFilterWithNull_Exception(){
        config.setStopLossFilter(null);
    }

    @Test(expected = NegativeNumberException.class)
    public void WhenCallSetStopLossFilterWithNegativeValue_Exception(){
        config.setStopLossFilter(new BigDecimal(-20));
    }

    @Test
    public void WhenCallSetStopLossFilterWithCorrectValue_CorrectResult(){
        BigDecimal expected = new BigDecimal(0.07);
        config.setStopLossFilter(expected);

        assertEquals(expected, config.getStopLossFilter());
    }

    @Test
    public void WhenCallGetStopLossFilterAfterInitializing_Default(){
        assertEquals(DEFAULT_STOP_LOSS_FILTER, config.getStopLossFilter());
    }

    @Test(expected = NullArgumentException.class)
    public void whenCallTargetWithNull_Exception(){
        config.setTarget(null);
    }

    @Test(expected = NegativeNumberException.class)
    public void WhenCallSetTargetWithNegativeValue_Exception(){
        config.setTarget(new BigDecimal(-20));
    }

    @Test
    public void WhenCallSetTargetWithCorrectValue_CorrectResult(){
        BigDecimal expected = new BigDecimal(0.07);
        config.setTarget(expected);

        assertEquals(expected, config.getTarget());
    }

    @Test
    public void WhenCallGetTargetAfterInitializing_Default(){
        assertEquals(FIRST_TARGET, config.getTarget());
    }

    @Test(expected = NullArgumentException.class)
    public void whenCallRSIFilterWithNull_Exception(){
        config.setRsiFilter(null);
    }

    @Test(expected = NegativeNumberException.class)
    public void WhenCallSetRSIFilterWithNegativeValue_Exception(){
        config.setRsiFilter(new BigDecimal(-20));
    }

    @Test
    public void WhenCallSetRSIFilterWithCorrectValue_CorrectResult(){
        BigDecimal expected = new BigDecimal(0.07);
        config.setRsiFilter(expected);

        assertEquals(expected, config.getRsiFilter());
    }

    @Test
    public void WhenCallGetRSIFilterAfterInitializing_Default(){
        assertEquals(RSI_FILTER, config.getRsiFilter());
    }

    @Test(expected = NullArgumentException.class)
    public void whenCallEntryFilterWithNull_Exception(){
        config.setEntryFilter(null);
    }

    @Test(expected = NegativeNumberException.class)
    public void WhenCallSetEntryFilterWithNegativeValue_Exception(){
        config.setEntryFilter(new BigDecimal(-20));
    }

    @Test
    public void WhenCallSetEntryFilterWithCorrectValue_CorrectResult(){
        BigDecimal expected = new BigDecimal(0.07);
        config.setEntryFilter(expected);

        assertEquals(expected, config.getEntryFilter());
    }

    @Test
    public void WhenCallGetEntryFilterInitializing_Default(){
        assertEquals(DEFAULT_ENTRY_FILTER, config.getEntryFilter());
    }

    @Test
    public void WhenCallSetExitGranularityWithNull_Default(){
        config.setExitGranularity(null);

        assertEquals(DEFAULT_EXIT_GRANULARITY, config.getExitGranularity());
    }

    @Test
    public void WhenCallSetExitGranularityWithCorrectValue_CorrectUpdate(){
        config.setExitGranularity(CandleGranularity.M1);

        assertEquals(CandleGranularity.M1, config.getExitGranularity());
    }

}
