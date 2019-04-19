package trader.controller;

import org.junit.Before;
import org.junit.Test;
import trader.requestor.*;
import trader.responder.Response;
import trader.configuration.TradingStrategyConfiguration;
import java.util.HashMap;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class CreateBGXConfigurationControllerTest extends BaseControllerTest {

    private static final String BGX_CONFIGURATION_CONTROLLER = "CreateBGXConfigurationController";

    private TradingStrategyConfiguration configurationMock;
    private CreateBGXConfigurationController createBgxConfigurationController;

    public CreateBGXConfigurationControllerTest() { }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        configurationMock = mock(TradingStrategyConfiguration.class);
        createBgxConfigurationController = new CreateBGXConfigurationController(useCaseFactoryMock);
    }

    @Test
    public void givenCorrectSettings_WhenCallGetRequest_ThenReturnCorrectRequest(){
        setFakeRequestFactoryCreator();
        setFakeRequestFactory();

        Request configurationRequest = createBgxConfigurationController.getRequest(BGX_CONFIGURATION_CONTROLLER, settings);

        assertEquals(requestMock, configurationRequest);
    }

    @Test
    public void givenCorrectSettings_WhenCallMake_ThenReturnCorrectUseCase(){
        setFakeUseCaseFactory();
        UseCase useCase = createBgxConfigurationController.make(BGX_CONFIGURATION_CONTROLLER);

        assertEquals(useCaseMock, useCase);
    }

    @Test
    public void givenCorrectSettings_WhenCallExecute_ReturnCorrectResponse(){
        setFakes();
        setFakeResponseBody();

        Response<TradingStrategyConfiguration> bgxConfigurationResponse = createBgxConfigurationController.execute(settings);

        assertEquals(configurationMock, bgxConfigurationResponse.getBody());
    }

    private void setFakeResponseBody(){
        when(responseMock.getBody()).thenReturn(configurationMock);
    }
}
