package trader.strategy.bgxstrategy.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import trader.broker.BrokerGateway;
import trader.broker.connector.BaseGateway;
import trader.broker.connector.BrokerConnector;
import trader.exception.BadRequestException;
import trader.exception.NoSuchGatewayException;
import trader.presenter.Presenter;
import trader.requestor.Request;
import trader.requestor.UseCase;
import trader.requestor.UseCaseFactory;
import trader.responder.Response;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(BaseGateway.class)
public class BrokerServiceTest {

    private BaseGateway baseGatewayMock;
    private UseCaseFactory useCaseFactoryMock;
    private UseCase useCaseMock;
    private Response responseMock;
    private BrokerConnector connectorMock;
    private BrokerService service;
    private Presenter presenterMock;

    @Before
    public void setUp(){

        baseGatewayMock = mock(BaseGateway.class);
        useCaseFactoryMock = mock(UseCaseFactory.class);
        useCaseMock = mock(UseCase.class);
        responseMock = mock(Response.class);
        connectorMock = mock(BrokerConnector.class);
        presenterMock = mock(Presenter.class);
        service = new BrokerService(useCaseFactoryMock, presenterMock);
    }

    @Test
    public void givenCorrectSettings_WhenCallCreateBrokerGateway_ThenReturnCorrectObject(){
        setFakeUseCase();
        setFakeResponse();
        setFakeBaseGateway();

        BrokerGateway oanda = service.createBrokerGateway("Oanda", "oandaBrokerConfig.yaml");

        assertEquals(baseGatewayMock, oanda);
    }

    @Test(expected = NoSuchGatewayException.class)
    public void givenIncorrectBrokerName_WhenCallCreateBrokerGateway_ThenException(){
        setFakeUseCase();
        setFakeResponse();

        service.createBrokerGateway("eee", "oandaBrokerConfig.yaml");
    }

    @Test(expected = BadRequestException.class)
    public void givenIncorrectBrokerConfigurationFileName_WhenCallCreateBrokerGateway_ThenException(){
        setFakeUseCase();
        setFakeResponse();

        service.createBrokerGateway("Oanda", "bff.yaml");
    }

    private void setFakeResponse() {
        when(responseMock.getBody()).thenReturn(connectorMock);
    }

    private void setFakeUseCase() {
        when(useCaseFactoryMock.make(anyString(), any(Presenter.class))).thenReturn(useCaseMock);
        when(useCaseMock.execute(any(Request.class))).thenReturn(responseMock);
    }

    private void setFakeBaseGateway() {
        PowerMockito.mockStatic(BaseGateway.class);
        PowerMockito.when(BaseGateway.create(anyString(), any(BrokerConnector.class))).thenReturn(baseGatewayMock);
    }

}
