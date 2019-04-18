package trader.interactor.createindicator;

import org.junit.Before;
import org.junit.Test;
import trader.entity.indicator.ma.ExponentialMovingAverage;
import trader.entity.indicator.ma.SimpleMovingAverage;
import trader.entity.indicator.ma.WeightedMovingAverage;
import trader.entity.indicator.rsi.RelativeStrengthIndex;
import trader.exception.EmptyArgumentException;
import trader.exception.NoSuchDataStructureException;
import trader.exception.WrongIndicatorSettingsException;
import trader.requestor.Request;
import trader.requestor.UseCase;
import trader.responder.Response;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CreateIndicatorUseCaseTest {

    private Request requestMock;
    private UseCase createIndicatorUseCase;

    @Before
    public void setUp() throws Exception {
        createIndicatorUseCase = new CreateIndicatorUseCase();
        requestMock = mock(Request.class);
    }

    @Test(expected = EmptyArgumentException.class)
    public void givenEmptySettings_WhenCallExecute_ThenThrowException(){
        Map<String, Object> settings = new HashMap<>();
        Map<String, String> indicatorSettings = new HashMap<>();
        settings.put("settings", indicatorSettings);
        when(requestMock.getBody()).thenReturn(settings);

        createIndicatorUseCase.execute(requestMock);
    }

    @Test(expected = WrongIndicatorSettingsException.class)
    public void givenSettingsWithoutTypeKeyName_WhenCallExecute_ThenThrowException(){
        Map<String, String> indicatorSettings = new HashMap<>();
        indicatorSettings.put("key", " s");
        Map<String, Object> settings = new HashMap<>();
        settings.put("settings", indicatorSettings);
        when(requestMock.getBody()).thenReturn(settings);

        createIndicatorUseCase.execute(requestMock);
    }

    @Test(expected = WrongIndicatorSettingsException.class)
    public void givenNullValueForType_WhenCallExecute_ThenThrowException(){
        setFakeSettings(null, "slow");

        createIndicatorUseCase.execute(requestMock);
    }

    @Test(expected = WrongIndicatorSettingsException.class)
    public void givenEmptyValueForType_WhenCallExecute_ThenThrowException(){

        setFakeSettings("  ", "slow");

        createIndicatorUseCase.execute(requestMock);
    }

    @Test
    public void givenCorrectSettingsForRSI_WhenCallExecute_ThenReturnRSIIndicator(){

        setFakeSettings("rsi", "slow");

        Response response = createIndicatorUseCase.execute(requestMock);
        Object body = response.getBody();

        assertEquals(RelativeStrengthIndex.class, body.getClass());
    }

    @Test
    public void givenCorrectSettingsForSMA_WhenCallExecute_ThenReturnSimpleMovingAverageIndicator(){
        setFakeSettings("simple", "daily");

        Response response = createIndicatorUseCase.execute(requestMock);
        Object body = response.getBody();

        assertEquals(SimpleMovingAverage.class, body.getClass());
    }

    @Test
    public void givenCorrectSettingsForWMA_WhenCallExecute_ThenReturnWeightedMovingAverageIndicator(){
        setFakeSettings("weighted", "fast");

        Response response = createIndicatorUseCase.execute(requestMock);
        Object body = response.getBody();

        assertEquals(WeightedMovingAverage.class, body.getClass());
    }

    @Test
    public void givenCorrectSettingsForEMA_WhenCallExecute_ThenReturnExponentialMovingAverageIndicator(){
        setFakeSettings("exponential", "fast");

        Response response = createIndicatorUseCase.execute(requestMock);
        Object body = response.getBody();

        assertEquals(ExponentialMovingAverage.class, body.getClass());
    }

    @Test(expected = NoSuchDataStructureException.class)
    public void givenSettingsForNotExistingIndicator_WhenCallExecute_ThenException(){
       setFakeSettings("blqlbq", "fast");

        createIndicatorUseCase.execute(requestMock);
    }

    private void setFakeSettings(String typeValue, String positionValue) {
        Map<String, String> indicatorSettings = new HashMap<>();
        indicatorSettings.put("type", typeValue);
        indicatorSettings.put("position", positionValue);
        Map<String, Object> settings = new HashMap<>();
        settings.put("settings", indicatorSettings);
        when(requestMock.getBody()).thenReturn(settings);
    }

}
