package trader.strategy.bgxstrategy.configuration;

import org.junit.Before;
import org.junit.Test;
import trader.exception.BadRequestException;
import trader.exception.NegativeNumberException;
import trader.exception.NullArgumentException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

public class BGXConfigurationImplTest {

    private static final String DEFAULT_INSTRUMENT = "EUR_USD";
    private static final long DEFAULT_INITIAL_CANDLES_QUANTITY = 4999L;
    private static final long DEFAULT_UPDATE_CANDLES_QUANTITY = 2L;
    private static final String DEFAULT_BGX_CONFIG_FILE_LOCATION = "bgxStrategyConfig.yaml";
    private static final BigDecimal DEFAULT_SPREAD = BigDecimal.valueOf(0.0002).setScale(5, BigDecimal.ROUND_HALF_UP);
    public static final BigDecimal DEFAULT_RISK_PER_TRADE = BigDecimal.valueOf(0.01).setScale(5, BigDecimal.ROUND_HALF_UP);


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

    @Test
    public void WhenCallGetInstrument_ReturnCorrectType(){
        assertEquals(String.class, config.getInstrument().getClass());
    }

    @Test
    public void WhenCreateThenDefaultInstrument(){
        assertEquals(DEFAULT_INSTRUMENT, config.getInstrument());
    }

    @Test
    public void WhenCallSetInitialCandlesQuantityWithCorrectNumber_CorrectResult(){
        long initialCandlesQuantity = config.getInitialCandlesQuantity();
        config.setInitialCandlesQuantity(15L);
        long initialCandlesQuantityUpdated = config.getInitialCandlesQuantity();

        assertNotEquals(initialCandlesQuantity, initialCandlesQuantityUpdated);
        assertEquals(15L, initialCandlesQuantityUpdated);
    }

    @Test
    public void WhenCallSetInitialCandlesQuantityWithNegativeValue_Defaults(){
        config.setInitialCandlesQuantity(-1L);

        assertEquals(DEFAULT_INITIAL_CANDLES_QUANTITY, config.getInitialCandlesQuantity());
    }

    @Test
    public void WhenCallSetUpdateCandlesQuantityWithCorrectNumber_CorrectResult(){
        long updateCandlesQuantity = config.getUpdateCandlesQuantity();
        config.setUpdateCandlesQuantity(7L);
        long newUpdateCandlesQuantity = config.getUpdateCandlesQuantity();

        assertNotEquals(updateCandlesQuantity, newUpdateCandlesQuantity);
        assertEquals(7L, newUpdateCandlesQuantity);
    }

    @Test
    public void WhenCallSetUpdateCandlesQuantityWithNegativeValue_Defaults(){
        config.setUpdateCandlesQuantity(-1L);

        assertEquals(DEFAULT_UPDATE_CANDLES_QUANTITY, config.getUpdateCandlesQuantity());
    }

    @Test
    public void WhenCreateFilePathSetToDefault(){
        String fileLocation = config.getFileLocation();

        assertEquals(DEFAULT_BGX_CONFIG_FILE_LOCATION, fileLocation);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallSetFileLocationWithNull_Exception(){
        config.setFileLocation(null);
    }

    @Test
    public void WhenCallSetFileLocationWithEmptyString_Default(){
        config.setFileLocation("");
        assertEquals(DEFAULT_BGX_CONFIG_FILE_LOCATION, config.getFileLocation());
    }

    @Test(expected = BadRequestException.class)
    public void WhenCallSetFileLocationWithStringWithoutYamlOrYmlExtension_Exception(){
        config.setFileLocation("gtre.exe");
    }

    @Test
    public void WhenCallSetFileLocationWithCorrectStringContainingExtraSpaces_TrimAndSet(){
        config.setFileLocation("   bgx.yaml ");

        assertEquals("bgx.yaml", config.getFileLocation());
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


}
