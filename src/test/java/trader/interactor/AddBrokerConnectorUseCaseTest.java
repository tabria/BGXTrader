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

public class AddBrokerConnectorUseCaseTest {

    private static final String TEST_BROKER_CONFIG_FILE_LOCATION = "oandaBrokerConfig.yaml";

    private BrokerConnector configurationMock;
    private Request requestMock;
    private AddBrokerConnectorUseCase addBrokerConnectorUseCase;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        configurationMock = mock(BrokerConnector.class);
        requestMock = mock(Request.class);
        addBrokerConnectorUseCase = new AddBrokerConnectorUseCase();
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCallExecuteWithNull_Exception(){
        addBrokerConnectorUseCase.execute(null);
    }
    @Test
    public void whenCallExecuteWithBadFileLocation_Exception(){
        exception.expect(BadRequestException.class);
        exception.expectCause(IsInstanceOf.instanceOf(YAMLException.class));

        when(configurationMock.getFileLocation()).thenReturn("broker.yaml");
        when(requestMock.getRequestDataStructure()).thenReturn(configurationMock);
        addBrokerConnectorUseCase.execute(requestMock);
    }

    @Test
    public void WhenCallExecuteWithCorrectRequest_CorrectResult(){
        when(configurationMock.getFileLocation()).thenReturn(TEST_BROKER_CONFIG_FILE_LOCATION);
        when(requestMock.getRequestDataStructure()).thenReturn(configurationMock);
        Response<BrokerConnector> brokerConnectorResponse = addBrokerConnectorUseCase.execute(requestMock);

        BrokerConnector configuration = brokerConnectorResponse.getResponseDataStructure();

        assertEquals(configurationMock, configuration);
    }

}