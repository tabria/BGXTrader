package trader.strategy.bgxstrategy.configuration;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import trader.exception.NullArgumentException;

import java.util.HashMap;

import static org.junit.Assert.*;

public class ConfigurationTest {


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
    public void WhenCallGetGranularity_ReturnCorrectType(){
//        config.getGranularity()
    }



//    systemGranularity:
//    granularity: "M30"
//    instrument:
//    instrumentName: "EUR_USD"
//    candlesQuantity:
//    initial: "4999"
//    update: "2"

}
