package trader.interactor;

import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.yaml.snakeyaml.error.YAMLException;
import trader.broker.connector.BrokerConnector;
import trader.exception.BadRequestException;
import trader.exception.NullArgumentException;
import trader.requestor.Request;
import trader.responder.Response;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public class AddBrokerGatewayUseCaseTest {

    private static final String TEST_BROKER_CONFIG_FILE_LOCATION = "oandaBrokerConfig.yaml";

    private BrokerConnector configurationMock;
    private Request requestMock;
    private AddBrokerGatewayUseCase addBrokerGatewayUseCase;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        configurationMock = mock(BrokerConnector.class);
        requestMock = mock(Request.class);
        addBrokerGatewayUseCase = new AddBrokerGatewayUseCase();
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallExecuteWithNull_Exception(){
        addBrokerGatewayUseCase.execute(null);
    }
    @Test
    public void whenCallExecuteWithBadFileLocation_Exception(){
        exception.expect(BadRequestException.class);
        exception.expectCause(IsInstanceOf.instanceOf(YAMLException.class));

        when(configurationMock.getFileLocation()).thenReturn("broker.yaml");
        when(requestMock.getRequestDataStructure()).thenReturn(configurationMock);
        addBrokerGatewayUseCase.execute(requestMock);
    }

    @Test
    public void WhenCallExecuteWithCorrectRequest_CorrectResult(){
        when(configurationMock.getFileLocation()).thenReturn(TEST_BROKER_CONFIG_FILE_LOCATION);
        when(requestMock.getRequestDataStructure()).thenReturn(configurationMock);
        Response<BrokerConnector> brokerConnectorResponse = addBrokerGatewayUseCase.execute(requestMock);

        BrokerConnector configuration = brokerConnectorResponse.getResponseDataStructure();

        assertEquals(configurationMock, configuration);
    }

}