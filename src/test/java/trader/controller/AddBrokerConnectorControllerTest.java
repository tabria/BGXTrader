package trader.controller;

import org.junit.Before;
import org.junit.Test;
import trader.broker.BrokerConnector;
import trader.exception.NullArgumentException;
import trader.interactor.UseCase;
import trader.requestor.Request;
import trader.responder.Response;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AddBrokerConnectorControllerTest extends BaseConfigurationControllerTest<BrokerConnector> {

    private static final String BROKER_CONFIGURATION_CONTROLLER = "AddBrokerConnectorController";

    private AddBrokerConnectorController addBrokerConnectorController;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        setConfigurationMock(mock(BrokerConnector.class));
        addBrokerConnectorController = new AddBrokerConnectorController(requestBuilderMock, useCaseFactoryMock);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreateControllerWithNullRequestBuilder_Exception(){
        new AddBrokerConnectorController(null, useCaseFactoryMock);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreateControllerWithNullUserCaseFactory_Exception(){
        new AddBrokerConnectorController(requestBuilderMock, null);
    }

    @Test
    public void WhenCallGetRequestWithCorrectSettings_ReturnCorrectResult(){
        when(requestMock.getRequestDataStructure()).thenReturn(configurationMock);
        when(requestBuilderMock.build(BROKER_CONFIGURATION_CONTROLLER, settings)).thenReturn(requestMock);
        Request<?> configurationRequest = addBrokerConnectorController.getRequest(BROKER_CONFIGURATION_CONTROLLER, settings);

        assertEquals(configurationMock, configurationRequest.getRequestDataStructure());
    }

    @Test
    public void WhenCallMakeWithCorrectSetting_CorrectResult(){
        when(useCaseFactoryMock.make(BROKER_CONFIGURATION_CONTROLLER)).thenReturn(useCaseMock);
        UseCase useCase = addBrokerConnectorController.make(BROKER_CONFIGURATION_CONTROLLER);

        assertEquals(useCaseMock, useCase);
    }

    @Test
    public void WhenCallExecuteWithCorrectSettings_CorrectResponse(){
        setExecuteSettings(BROKER_CONFIGURATION_CONTROLLER);
        Response<BrokerConnector> brokerConfigurationResponse = addBrokerConnectorController.execute(settings);

        assertEquals(configurationMock, brokerConfigurationResponse.getResponseDataStructure());
    }
}