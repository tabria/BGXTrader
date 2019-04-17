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

    @Test
    public void whenCallGetIndicators_ReturnList(){
        assertEquals(ArrayList.class, config.getIndicators().getClass());
    }

    @Test(expected = NullArgumentException.class)
    public void givenNull_WhenCallAddIndicator_ThenThrowException(){
        config.addIndicator(null);
    }

    @Test
    public void givenCorrectSettings_WhenCallAddIndicator_ThenReturnCorrectResult(){
        HashMap<String, String> indicator = new HashMap<>();
        indicator.put("type", "ema");
        config.addIndicator(indicator);

        assertEquals(indicator, config.getIndicators().get(0));
    }

    @Test
    public void givenInitialSettings_WhenCallGetInstrument_ReturnString(){
        assertEquals(String.class, config.getInstrument().getClass());
    }

    @Test
    public void givenInitialSettings_WhenInstantiate_ThenSetToDefaultInstrument(){
        assertEquals(DEFAULT_INSTRUMENT, config.getInstrument());
    }

    @Test
    public void givenCorrectSettings_WhenCallGetSpread_ThenReturnDefaultValue(){
        assertEquals(DEFAULT_SPREAD ,config.getSpread());
    }

    @Test
    public void givenNull_WhenCallSetInitialCandlesQuantity_ThenSetDefaultValue(){
        config.setInitialCandlesQuantity(null);

        assertEquals(DEFAULT_INITIAL_CANDLES_QUANTITY, config.getInitialCandlesQuantity());
    }

    @Test
    public void givenEmpty_WhenCallSetInitialCandlesQuantity_ThenSetDefaultValue(){
        config.setInitialCandlesQuantity("  ");

        assertEquals(DEFAULT_INITIAL_CANDLES_QUANTITY, config.getInitialCandlesQuantity());
    }

    @Test(expected = NotANumberException.class)
    public void givenNonNumberString_WhenCallSetInitialCandlesQuantity_ThenThrowException(){
        config.setInitialCandlesQuantity("xxx");
    }

    @Test(expected = UnderflowException.class)
    public void givenStringWithNegativeValuedOrZero_WhenCallSetInitialCandlesQuantity_ThenException(){
        config.setInitialCandlesQuantity("0");
    }

    @Test(expected = OverflowException.class)
    public void givenStringWithMoreThanMaxQuantity_WhenCallSetInitialCandlesQuantity_ThenException(){
        config.setInitialCandlesQuantity("5000");
    }

    @Test
    public void givenCorrectString_WhenCallSetInitialQuantity_SetCorrectValue(){
        long initialCandlesQuantity = config.getInitialCandlesQuantity();
        config.setInitialCandlesQuantity("15");
        long initialCandlesQuantityUpdated = config.getInitialCandlesQuantity();

        assertNotEquals(initialCandlesQuantity, initialCandlesQuantityUpdated);
        assertEquals(15L, initialCandlesQuantityUpdated);
    }

    @Test
    public void givenNull_WhenCallSetUpdateCandlesQuantity_ThenSetDefaultValue(){
        config.setUpdateCandlesQuantity(null);

        assertEquals(DEFAULT_UPDATE_CANDLES_QUANTITY, config.getUpdateCandlesQuantity());
    }

    @Test
    public void givenEmpty_WhenCallSetUpdateCandlesQuantity_ThenSetDefaultValue(){
        config.setUpdateCandlesQuantity("  ");

        assertEquals(DEFAULT_UPDATE_CANDLES_QUANTITY, config.getUpdateCandlesQuantity());
    }

    @Test(expected = NotANumberException.class)
    public void givenNonNumberString_WhenCallSetUpdateCandlesQuantity_ThenThrowException(){
        config.setUpdateCandlesQuantity("xxx");
    }

    @Test(expected = UnderflowException.class)
    public void givenStringWithNegativeValuedOrZero_WhenCallSetUpdateCandlesQuantity_ThenException(){
        config.setUpdateCandlesQuantity("0");
    }

    @Test(expected = OverflowException.class)
    public void givenStringWithMoreThanMaxQuantity_WhenCallSetUpdateCandlesQuantity_ThenException(){
        config.setUpdateCandlesQuantity("5000");
    }

    @Test
    public void givenCorrectString_WhenCallSetUpdateQuantity_SetCorrectValue(){
        long updateCandlesQuantity = config.getUpdateCandlesQuantity();
        config.setUpdateCandlesQuantity("22");
        long updateCandlesQuantityActual = config.getUpdateCandlesQuantity();

        assertNotEquals(updateCandlesQuantity, updateCandlesQuantityActual);
        assertEquals(22L, updateCandlesQuantityActual);
    }

    @Test
    public void givenInitialSettings_WhenInstantiateRiskPerTrade_ThenDefaultValue(){
        assertEquals(DEFAULT_RISK_PER_TRADE, config.getRiskPerTrade());
    }

    @Test
    public void givenNull_WhenCallSetRiskPerTrade_ThenDefaultValue(){
        config.setRiskPerTrade(null);

        assertEquals(DEFAULT_RISK_PER_TRADE, config.getRiskPerTrade());
    }


    @Test
    public void givenEmpty_WhenCallSetRiskPerTrade_ThenSetDefaultValue(){
        config.setRiskPerTrade("  ");

        assertEquals(DEFAULT_RISK_PER_TRADE, config.getRiskPerTrade());
    }

    @Test(expected = NotANumberException.class)
    public void givenNonNumberString_WhenCallSetRiskPerTrade_ThenThrowException(){
        config.setRiskPerTrade("xxx");
    }

    @Test(expected = UnderflowException.class)
    public void givenNegativeValuedString_WhenCallSetRiskPerTrade_ThenThrowException(){
        config.setRiskPerTrade("-20");
    }

    @Test
    public void givenCorrectSettings_WhenCallSetRiskPerTrade_ThenReturnCorrectValue(){
        String expected = " 0.07 ";
        config.setRiskPerTrade(expected);

        assertEquals(new BigDecimal(expected.trim()), config.getRiskPerTrade());
    }

    @Test
    public void givenNull_WhenCallSetEntryStrategy_ThenDefaultValue(){
        config.setEntryStrategy(null);

        assertEquals(DEFAULT_ENTRY_STRATEGY, config.getEntryStrategy());
    }

    @Test
    public void givenEmpty_WhenCallSetEntryStrategy_ThenDefault(){
        config.setEntryStrategy("  ");

        assertEquals(DEFAULT_ENTRY_STRATEGY, config.getEntryStrategy());
    }

    @Test
    public void givenCorrectSettings_WhenCallSetEntryStrategy_ThenCorrectUpdate(){
        config.setEntryStrategy(" bestEntry ");

        assertEquals("bestEntry", config.getEntryStrategy());
    }

    @Test
    public void givenNull_WhenCallSetOrderStrategy_ThenDefaultValue(){
        config.setOrderStrategy(null);

        assertEquals(DEFAULT_ORDER_STRATEGY, config.getOrderStrategy());
    }

    @Test
    public void givenEmpty_WhenCallSetOrderStrategy_ThenDefaultValue(){
        config.setOrderStrategy("  ");

        assertEquals(DEFAULT_ORDER_STRATEGY, config.getOrderStrategy());
    }

    @Test
    public void givenCorrectSettings_WhenCallSetOrderStrategy_CorrectUpdate(){
        config.setOrderStrategy(" bestOrder ");

        assertEquals("bestOrder", config.getOrderStrategy());
    }

    @Test
    public void givenNull_WhenCallSetExitStrategy_ThenrDefaultValue(){
        config.setExitStrategy(null);

        assertEquals(DEFAULT_EXIT_STRATEGY, config.getExitStrategy());
    }

    @Test
    public void givenEmpty_WhenCallSetExitStrategy_ThenDefaultValue(){
        config.setExitStrategy("   ");

        assertEquals(DEFAULT_EXIT_STRATEGY, config.getExitStrategy());
    }

    @Test
    public void givenCorrectSettings_WhenCallSetExitStrategy_CorrectUpdate(){
        config.setExitStrategy("  bestExit  ");

        assertEquals("bestExit", config.getExitStrategy());
    }

    @Test
    public void givenInitialSettings_WhenInitializeStopLossFilter_ThenDefaultValue(){
        assertEquals(DEFAULT_STOP_LOSS_FILTER, config.getStopLossFilter());
    }

    @Test
    public void givenNull_WhenCallSetStopLossFilter_ThenDefaultValue(){
        config.setStopLossFilter(null);

        assertEquals(DEFAULT_STOP_LOSS_FILTER, config.getStopLossFilter());
    }

    @Test
    public void givenEmpty_WhenCallSetStopLossFilter_ThenDefaultValue(){
        config.setStopLossFilter(" ");

        assertEquals(DEFAULT_STOP_LOSS_FILTER, config.getStopLossFilter());
    }

    @Test(expected = NotANumberException.class)
    public void givenNonNumberString_WhenCallSetStopLossFilter_ThenThrowException(){
        config.setStopLossFilter("xxx");
    }

    @Test(expected = UnderflowException.class)
    public void givenNegativeValuedString_WhenCallSetStopLossFilter_ThenThrowException(){
        config.setStopLossFilter("-1");
    }

    @Test
    public void givenCorrectSettings_WhenCallSetStopLossFilter_ThenReturnCorrectValue(){
        String expected = " 0.01 ";
        config.setStopLossFilter(expected);

        assertEquals(new BigDecimal(expected.trim()), config.getStopLossFilter());
    }

    @Test
    public void givenInitialSettings_WhenInitializeTarget_ThenDefaultValue(){
        assertEquals(FIRST_TARGET, config.getTarget());
    }

    @Test
    public void givenNull_WhenCallSetTarget_ThenDefaultValue(){
        config.setTarget(null);

        assertEquals(FIRST_TARGET, config.getTarget());
    }

    @Test
    public void givenEmpty_WhenCallSetTarget_ThenDefaultValue(){
        config.setTarget(" ");

        assertEquals(FIRST_TARGET, config.getTarget());
    }

    @Test(expected = NotANumberException.class)
    public void givenNonNumberString_WhenCallSetTarget_ThenThrowException(){
        config.setTarget(" xxx ");
    }

    @Test(expected = UnderflowException.class)
    public void givenNegativeValuedString_WhenCallSetTarget_ThenThrowException(){
        config.setTarget("0");
    }

    @Test
    public void givenCorrectSettings_WhenCallSetTarget_ThenReturnCorrectValue(){
        String expected = " 0.08 ";
        config.setTarget(expected);

        assertEquals(new BigDecimal(expected.trim()), config.getTarget());
    }

    @Test
    public void givenInitialSettings_WhenInitializeRSIFilter_ThenDefaultValue(){
        assertEquals(RSI_FILTER, config.getRsiFilter());
    }

    @Test
    public void givenNull_WhenCallSetRSIFilter_ThenDefaultValue(){
        config.setRsiFilter(null);

        assertEquals(RSI_FILTER, config.getRsiFilter());
    }

    @Test
    public void givenEmpty_WhenCallSetRSIFilter_ThenDefaultValue(){
        config.setRsiFilter(" ");

        assertEquals(RSI_FILTER, config.getRsiFilter());
    }

    @Test(expected = NotANumberException.class)
    public void givenNonNumberString_WhenCallSetRSIFilter_ThenThrowException(){
        config.setRsiFilter(" xxx ");
    }

    @Test(expected = UnderflowException.class)
    public void givenNegativeValuedString_WhenCallSetRSIFilter_ThenThrowException(){
        config.setRsiFilter("0");
    }

    @Test
    public void givenCorrectSettings_WhenCallSetRSIFilter_ThenReturnCorrectValue(){
        String expected = " 0.08 ";
        config.setRsiFilter(expected);

        assertEquals(new BigDecimal(expected.trim()), config.getRsiFilter());
    }

    @Test
    public void givenInitialSettings_WhenInitializeEntryFilter_ThenDefaultValue(){
        assertEquals(DEFAULT_ENTRY_FILTER, config.getEntryFilter());
    }

    @Test
    public void givenNull_WhenCallSetEntryFilter_ThenDefaultValue(){
        config.setEntryFilter(null);

        assertEquals(DEFAULT_ENTRY_FILTER, config.getEntryFilter());
    }

    @Test
    public void givenEmpty_WhenCallSetEntryFilter_ThenDefaultValue(){
        config.setEntryFilter(" ");

        assertEquals(DEFAULT_ENTRY_FILTER, config.getEntryFilter());
    }

    @Test(expected = NotANumberException.class)
    public void givenNonNumberString_WhenCallSetEntryFilter_ThenThrowException(){
        config.setEntryFilter(" xxx ");
    }

    @Test(expected = UnderflowException.class)
    public void givenNegativeValuedString_WhenCallSetEntryFilter_ThenThrowException(){
        config.setEntryFilter("0");
    }

    @Test
    public void givenCorrectSettings_WhenCallSetEntryFilter_ThenReturnCorrectValue(){
        String expected = " 0.08 ";
        config.setEntryFilter(expected);

        assertEquals(new BigDecimal(expected.trim()), config.getEntryFilter());
    }

    @Test
    public void givenInitialSettings_WhenInitializeExitGranularity_ThenDefaultValue(){
        assertEquals(DEFAULT_EXIT_GRANULARITY, config.getExitGranularity());
    }

    @Test
    public void givenNull_WhenCallSetExitGranularity_ThenDefaultValue(){
        config.setExitGranularity(null);

        assertEquals(DEFAULT_EXIT_GRANULARITY, config.getExitGranularity());
    }

    @Test
    public void givenEmpty_WhenCallSetExitGranularity_ThenDefaultValue(){
        config.setExitGranularity(" ");

        assertEquals(DEFAULT_EXIT_GRANULARITY, config.getExitGranularity());
    }

    @Test(expected = EmptyArgumentException.class)
    public void givenNotExistentGranularity_WhenCallSetExitGranularity_ThenThrowException(){
        config.setExitGranularity(" MR ");
    }

    @Test
    public void givenCorrectSettings_WhenCallSetExitGranularity_CorrectUpdate(){
        config.setExitGranularity(" M1 ");

        assertEquals(CandleGranularity.M1, config.getExitGranularity());
    }

}
