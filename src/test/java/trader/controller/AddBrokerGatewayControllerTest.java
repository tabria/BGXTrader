package trader.controller;

import org.junit.Before;
import org.junit.Test;
import trader.broker.BrokerGateway;
import trader.exception.NullArgumentException;
import trader.requestor.UseCase;
import trader.requestor.Request;
import trader.responder.Response;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AddBrokerGatewayControllerTest extends BaseConfigurationControllerTest<BrokerGateway> {

    private static final String BROKER_CONFIGURATION_CONTROLLER = "AddBrokerGatewayController";

    private AddBrokerGatewayController addBrokerGatewayController;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        setConfigurationMock(mock(BrokerGateway.class));
        addBrokerGatewayController = new AddBrokerGatewayController(requestBuilderMock, useCaseFactoryMock);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreateControllerWithNullRequestBuilder_Exception(){
        new AddBrokerGatewayController(null, useCaseFactoryMock);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreateControllerWithNullUserCaseFactory_Exception(){
        new AddBrokerGatewayController(requestBuilderMock, null);
    }

    @Test
    public void WhenCallGetRequestWithCorrectSettings_ReturnCorrectResult(){
        when(requestMock.getRequestDataStructure()).thenReturn(configurationMock);
        when(requestBuilderMock.build(BROKER_CONFIGURATION_CONTROLLER, settings)).thenReturn(requestMock);
        Request<?> configurationRequest = addBrokerGatewayController.getRequest(BROKER_CONFIGURATION_CONTROLLER, settings);

        assertEquals(configurationMock, configurationRequest.getRequestDataStructure());
    }

    @Test
    public void WhenCallMakeWithCorrectSetting_CorrectResult(){
        when(useCaseFactoryMock.make(BROKER_CONFIGURATION_CONTROLLER)).thenReturn(useCaseMock);
        UseCase useCase = addBrokerGatewayController.make(BROKER_CONFIGURATION_CONTROLLER);

        assertEquals(useCaseMock, useCase);
    }

    @Test
    public void WhenCallExecuteWithCorrectSettings_CorrectResponse(){
        setExecuteSettings(BROKER_CONFIGURATION_CONTROLLER);
        Response<BrokerGateway> brokerConfigurationResponse = addBrokerGatewayController.execute(settings);

        assertEquals(configurationMock, brokerConfigurationResponse.getResponseDataStructure());
    }
}