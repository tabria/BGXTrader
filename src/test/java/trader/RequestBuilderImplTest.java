package trader;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import trader.exception.EmptyArgumentException;
import trader.exception.NoSuchDataStructureException;
import trader.exception.NullArgumentException;
import trader.indicator.ma.SimpleMovingAverage;
import trader.indicator.rsi.RelativeStrengthIndex;

import java.util.HashMap;

import static org.junit.Assert.*;

public class RequestBuilderImplTest {


    private RequestBuilder requestBuilder;
    private HashMap<String, String> settings;

    @Before
    public void setUp(){
        requestBuilder = new RequestBuilderImpl();
        settings = new HashMap<>();

    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallBuildWithNullDataStructureName_Exception(){
        requestBuilder.build(null, null);
    }

    @Test(expected = EmptyArgumentException.class)
    public void WhenCallBuildWithEmptyDataStructureName_Exception(){
        requestBuilder.build("",new HashMap<>());
    }

    @Test(expected = NullArgumentException.class)
    public void whenCallBuildWithNullSettings_Exception(){
        requestBuilder.build("tr", null);
    }

    @Test(expected = NoSuchDataStructureException.class)
    public void whenCallBuildWithNotExistingDataStructureName_Exception(){
        requestBuilder.build("trah", settings);
    }

    @Test
    public void whenCallBuildWithRSIIndicatorDataStructureName_ReturnRSIRequest(){
        Request<?> rsiIndicatorRequest = requestBuilder.build("rsiIndicator", settings);

        assertEquals(RelativeStrengthIndex.class, rsiIndicatorRequest.dataStructure.getClass());
    }

    @Test
    public void whenCallBuildWithMovingAverageIndicatorDataStructureName_ReturnMARequest(){
        Request<?> smaIndicator = requestBuilder.build("smaIndicator", settings);

        assertEquals(SimpleMovingAverage.class, smaIndicator.dataStructure.getClass());
    }

}
