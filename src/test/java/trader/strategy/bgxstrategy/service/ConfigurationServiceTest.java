package trader.strategy.bgxstrategy.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import trader.configuration.TradingStrategyConfiguration;
import trader.exception.BadRequestException;
import trader.requestor.*;
import trader.responder.Response;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(RequestBuilderCreator.class)
public class ConfigurationServiceTest {

    private static final String BGX_STRATEGY_CONFIG_FILE_NAME = "bgxStrategyConfig.yaml";

    private RequestBuilder requestBuilderMock;
    private Request requestMock;
    private UseCaseFactory useCaseFactoryMock;
    private UseCase useCaseMock;
    private Response responseMock;
    private TradingStrategyConfiguration configurationMock;
    private ConfigurationService service;

    @Before
    public void setUp() throws Exception {

        requestBuilderMock = mock(RequestBuilder.class);
        requestMock = mock(Request.class);
        useCaseFactoryMock = mock(UseCaseFactory.class);
        useCaseMock = mock(UseCase.class);
        responseMock = mock(Response.class);
        configurationMock = mock(TradingStrategyConfiguration.class);
        service = new ConfigurationService(useCaseFactoryMock);
    }

    @Test(expected = BadRequestException.class)
    public void givenIncorrectConfigurationFileName_WhenCallCreateConfiguration_ThenException(){
        service.createConfiguration("bark.yaml");
    }

    @Test
    public void givenCorrectConfigurationFileName_WhenCallCreateConfiguration_ThenReturnCorrectObject(){
        setFakeRequest();
        setFakeResponse();
        setFakeConfiguration();

        TradingStrategyConfiguration configuration = service.createConfiguration(BGX_STRATEGY_CONFIG_FILE_NAME);

        assertEquals(configurationMock, configuration);
    }

    private void setFakeRequest(){
        PowerMockito.mockStatic(RequestBuilderCreator.class);
        PowerMockito.when(RequestBuilderCreator.create(any())).thenReturn(requestBuilderMock);
        when(requestBuilderMock.build(any(HashMap.class))).thenReturn(requestMock);

    }

    private void setFakeResponse(){
        when(useCaseMock.execute(requestMock)).thenReturn(responseMock);
        when(useCaseFactoryMock.make(anyString())).thenReturn(useCaseMock);
    }

    private void setFakeConfiguration(){
        when(responseMock.getBody()).thenReturn(configurationMock);
    }

}
