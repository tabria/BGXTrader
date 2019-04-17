package trader.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import trader.requestor.*;
import trader.responder.Response;
import trader.configuration.TradingStrategyConfiguration;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(RequestBuilderCreator.class)
public class CreateBGXConfigurationControllerTest {

    private static final String BGX_CONFIGURATION_CONTROLLER = "CreateBGXConfigurationController";

    private Map<String, Object> settings;
    private UseCaseFactory useCaseFactoryMock;
    private TradingStrategyConfiguration configurationMock;
    private UseCase useCaseMock;
    private RequestBuilder requestBuilderMock;
    private Request requestMock;
    private Response responseMock;
    private CreateBGXConfigurationController createBgxConfigurationController;

    public CreateBGXConfigurationControllerTest() {
    }

    @Before
    public void setUp() throws Exception {

        settings = new HashMap<>();
        useCaseFactoryMock = mock(UseCaseFactory.class);
        configurationMock = mock(TradingStrategyConfiguration.class);
        useCaseMock = mock(UseCase.class);
        requestMock = mock(Request.class);
        requestBuilderMock = mock(RequestBuilder.class);
        responseMock = mock(Response.class);
        createBgxConfigurationController = new CreateBGXConfigurationController(useCaseFactoryMock);
    }

    @Test
    public void givenCorrectSettings_WhenCallGetRequest_ThenReturnCorrectRequest(){
        setFakeRequestFactoryCreator();
        setFakeRequestFactory();
        setFakeRequestBody();

        Request configurationRequest = createBgxConfigurationController.getRequest(BGX_CONFIGURATION_CONTROLLER, settings);

        assertEquals(configurationMock.getClass(), configurationRequest.getBody().getClass());
    }

    @Test
    public void givenCorrectSettings_WhenCallMake_ThenReturnCorrectUseCase(){
        setFakeUseCaseFactory();
        UseCase useCase = createBgxConfigurationController.make(BGX_CONFIGURATION_CONTROLLER);

        assertEquals(useCaseMock, useCase);
    }

    @Test
    public void givenCorrectSettings_WhenCallExecute_ReturnCorrectResponse(){
        setFakeRequestFactoryCreator();
        setFakeRequestFactory();
        setFakeRequestFactory();
        setFakeUseCaseFactory();
        setFakeUseCase();
        setFakeResponseBody();

       // setExecuteSettings(BGX_CONFIGURATION_CONTROLLER);
        Response<TradingStrategyConfiguration> bgxConfigurationResponse = createBgxConfigurationController.execute(settings);

        assertEquals(configurationMock, bgxConfigurationResponse.getBody());
    }

    private void setFakeRequestFactoryCreator(){
        PowerMockito.mockStatic(RequestBuilderCreator.class);
        PowerMockito.when(RequestBuilderCreator.create(any())).thenReturn(requestBuilderMock);
    }

    private void setFakeRequestFactory(){
        when(requestBuilderMock.build(settings)).thenReturn(requestMock);
    }

    private void setFakeRequestBody(){
        when(requestMock.getBody()).thenReturn(configurationMock);
    }

    private void setFakeUseCaseFactory(){
        when(useCaseFactoryMock.make(anyString())).thenReturn(useCaseMock);
    }

    private void setFakeUseCase(){
        when(useCaseMock.execute(requestMock)).thenReturn(responseMock);
    }

    private void setFakeResponseBody(){
        when(responseMock.getBody()).thenReturn(configurationMock);
    }
}
