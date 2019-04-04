package trader.controller;

import org.junit.Before;
import org.junit.Test;
import trader.exception.NullArgumentException;
import trader.interactor.UseCase;
import trader.requestor.Request;
import trader.responder.Response;
import trader.strategy.bgxstrategy.configuration.TradingStrategyConfiguration;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AddBGXConfigurationControllerTest extends BaseConfigurationControllerTest<TradingStrategyConfiguration> {

    private static final String BGX_CONFIGURATION_CONTROLLER = "AddBGXConfigurationController";

    private AddBGXConfigurationController addBgxConfigurationController;

    @Before
    public void setUp() throws Exception {

        super.setUp();
        setConfigurationMock(mock(TradingStrategyConfiguration.class));
        addBgxConfigurationController = new AddBGXConfigurationController(requestBuilderMock, useCaseFactoryMock);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreateControllerWithNullRequestBuilder_Exception(){
        new AddBGXConfigurationController(null, useCaseFactoryMock);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreateControllerWithNullUserCaseFactory_Exception(){
        new AddBGXConfigurationController(requestBuilderMock, null);
    }

    @Test
    public void WhenCallGetRequestWithCorrectSettings_ReturnCorrectResult(){
        when(requestMock.getRequestDataStructure()).thenReturn(configurationMock);
        when(requestBuilderMock.build(BGX_CONFIGURATION_CONTROLLER, settings)).thenReturn(requestMock);
        Request<?> configurationRequest = addBgxConfigurationController.getRequest(BGX_CONFIGURATION_CONTROLLER, settings);

        assertEquals(configurationMock, configurationRequest.getRequestDataStructure());
    }

    @Test
    public void WhenCallMakeWithCorrectSetting_CorrectResult(){
        when(useCaseFactoryMock.make(BGX_CONFIGURATION_CONTROLLER)).thenReturn(useCaseMock);
        UseCase useCase = addBgxConfigurationController.make(BGX_CONFIGURATION_CONTROLLER);

        assertEquals(useCaseMock, useCase);
    }

    @Test
    public void WhenCallExecuteWithCorrectSettings_CorrectResponse(){
        setExecuteSettings(BGX_CONFIGURATION_CONTROLLER);
        Response<TradingStrategyConfiguration> bgxConfigurationResponse = addBgxConfigurationController.execute(settings);

        assertEquals(configurationMock, bgxConfigurationResponse.getResponseDataStructure());
    }

}
