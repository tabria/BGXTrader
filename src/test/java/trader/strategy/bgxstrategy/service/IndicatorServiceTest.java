package trader.strategy.bgxstrategy.service;

import org.junit.Before;
import org.junit.Test;
import trader.entity.indicator.Indicator;
import trader.exception.EmptyArgumentException;
import trader.requestor.Request;
import trader.requestor.UseCase;
import trader.requestor.UseCaseFactory;
import trader.responder.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class IndicatorServiceTest {

    private UseCaseFactory useCaseFactoryMock;
    private UseCase useCaseMock;
    private Response responseMock;
    private Indicator indicatorMock;
    private IndicatorService service;


    @Before
    public void setUp() throws Exception {
        useCaseFactoryMock = mock(UseCaseFactory.class);
        useCaseMock = mock(UseCase.class);
        responseMock = mock(Response.class);
        indicatorMock = mock(Indicator.class);
        service = new IndicatorService(useCaseFactoryMock);
    }

    @Test(expected = EmptyArgumentException.class)
    public void givenLessThanMinimumIndicators_WhenCallCreateIndicators_ThenThrowException(){
        List<Map<String, String>> indicators = new ArrayList<>();
        indicators.add(new HashMap<>());
        service.createIndicators(indicators);
    }

    @Test
    public void givenCorrectSettings_WhenCallCreateIndicators_ThenReturnCorrectCollection(){
        List<Map<String, String>> indicatorsSettings = new ArrayList<>();
        indicatorsSettings.add(setIndicatorSettings("rsi", " "));
        indicatorsSettings.add(setIndicatorSettings("simple", "daily"));
        indicatorsSettings.add(setIndicatorSettings("weighted", "price"));
        indicatorsSettings.add(setIndicatorSettings("exponential", "slow"));
        indicatorsSettings.add(setIndicatorSettings("weighted", "fast"));
        indicatorsSettings.add(setIndicatorSettings("simple", "middle"));

        when(useCaseFactoryMock.make(anyString())).thenReturn(useCaseMock);
        when(useCaseMock.execute(any(Request.class))).thenReturn(responseMock);
        when(responseMock.getBody()).thenReturn(indicatorMock);

        List<Indicator> indicators = service.createIndicators(indicatorsSettings);

        assertEquals(6, indicators.size());
        for (Indicator indicator: indicators)
            assertEquals(indicatorMock, indicator);
    }

    private Map<String, String> setIndicatorSettings(String typeValue, String positionValue) {
        Map<String, String> indicatorSettings = new HashMap<>();
        indicatorSettings.put("type", typeValue);
        indicatorSettings.put("position", positionValue);
        return indicatorSettings;
    }
}
