package trader.strategy.bgxstrategy.configuration;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.matchers.Null;
import trader.exception.NullArgumentException;
import trader.exception.OverflowException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class ConfigurationTest {

    private static final String DEFAULT_INSTRUMENT = "EUR_USD";
    private static final Long DEFAULT_INITIAL_CANDLES_QUANTITY = 4999L;
    private static final Long DEFAULT_UPDATE_CANDLES_QUANTITY = 2L;

    private Configuration config;

    @Before
    public void setUp() {
        config = new Configuration();
    }

    @Test
    public void whenCallGetIndicators_ReturnHashMap(){
        assertEquals(HashMap.class, config.getIndicators().getClass());
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallSetIndicatorsWithNull_Exception(){
        config.setIndicators(null);
    }

    @Test
    public void WhenCallSetIndicatorsWithCorrectInput_CorrectResult(){
        HashMap<String, String> settings = new HashMap<>();
        config.setIndicators(settings);

        assertEquals(settings, config.getIndicators());
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
    public void WhenCallGetCandlesQuantity_ReturnCorrectType(){
        assertEquals(List.class, config.getCandlesQuantity().getClass());
    }

    @Test
    public void WhenCreateThenDefaultQuantities(){
        assertDefaultValues();
    }

    private void assertDefaultValues() {
        List<Long> candlesQuantity = config.getCandlesQuantity();
        assertEquals(DEFAULT_INITIAL_CANDLES_QUANTITY, candlesQuantity.get(0));
        assertEquals(DEFAULT_UPDATE_CANDLES_QUANTITY, candlesQuantity.get(1));
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallSetCandlesQuantityWithNull_Exception(){
        config.setCandlesQuantity(null);
    }

    @Test
    public void WhenCallSetCandlesWithEmptyQuantities_Defaults(){
        config.setCandlesQuantity(new ArrayList<>());

        assertDefaultValues();
    }

    @Test(expected = OverflowException.class)
    public void WhenCallSetCandlesQuantityWithListWithMoreThanMaxElement_Exception(){
        ArrayList<Long> quantities = new ArrayList<>();
        quantities.add(1L);
        quantities.add(2L);
        quantities.add(3L);
        config .setCandlesQuantity(quantities);
    }
}
