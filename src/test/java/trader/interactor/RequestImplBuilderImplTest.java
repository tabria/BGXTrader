package trader.interactor;

import org.junit.Before;
import org.junit.Test;
import trader.requestor.Request;
import trader.requestor.RequestBuilder;
import trader.exception.EmptyArgumentException;
import trader.exception.NoSuchDataStructureException;
import trader.exception.NullArgumentException;
import trader.entity.indicator.ma.SimpleMovingAverage;
import trader.entity.indicator.rsi.RelativeStrengthIndex;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class RequestImplBuilderImplTest {


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

    @Test(expected = NoSuchDataStructureException.class)
    public void WhenCallBuildWithNotExistingDataStrucTureName_Exception(){
        requestBuilder.build("trahIndicator", settings);
    }

    @Test
    public void whenCallBuildWithRSIIndicatorDataStructureName_ReturnRSIRequest(){
        Request<?> rsiIndicatorRequest = requestBuilder.build("rsiIndicator", settings);

        assertEquals(RelativeStrengthIndex.class, rsiIndicatorRequest.getRequestDataStructure().getClass());
    }

    @Test
    public void whenCallBuildWithRSIIndicatorDataStructureNameWithExtraSpaces_ReturnRSIRequest(){
        Request<?> rsiIndicatorRequest = requestBuilder.build("  rsiIndicator   ", settings);

        assertEquals(RelativeStrengthIndex.class, rsiIndicatorRequest.getRequestDataStructure().getClass());
    }

    @Test
    public void whenCallBuildWithMovingAverageIndicatorDataStructureName_ReturnMARequest(){
        Request<?> smaIndicator = requestBuilder.build("smaIndicator", settings);

        assertEquals(SimpleMovingAverage.class, smaIndicator.getRequestDataStructure().getClass());
    }

}
