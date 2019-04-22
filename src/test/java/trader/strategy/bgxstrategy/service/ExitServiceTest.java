package trader.strategy.bgxstrategy.service;

import org.junit.Before;
import org.junit.Test;
import trader.broker.BrokerGateway;
import trader.configuration.TradingStrategyConfiguration;
import trader.exception.NullArgumentException;
import trader.exit.ExitStrategy;
import trader.presenter.Presenter;
import trader.requestor.Request;
import trader.requestor.UseCase;
import trader.requestor.UseCaseFactory;
import trader.responder.Response;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

public class ExitServiceTest {

    private UseCaseFactory useCaseFactoryMock;
    private UseCase useCaseMock;
    private Response responseMock;
    private ExitService service;
    private Presenter presenterMock;
    private BrokerGateway brokerGatewayMock;
    private TradingStrategyConfiguration configurationMock;
    private ExitStrategy exitStrategyMock;

    @Before
    public void setUp() throws Exception {
        presenterMock = mock(Presenter.class);
        responseMock = mock(Response.class);
        useCaseMock = mock(UseCase.class);
        useCaseFactoryMock = mock(UseCaseFactory.class);
        brokerGatewayMock = mock(BrokerGateway.class);
        configurationMock = mock(TradingStrategyConfiguration.class);
        exitStrategyMock = mock(ExitStrategy.class);
        service = new ExitService(useCaseFactoryMock, presenterMock, brokerGatewayMock, configurationMock);
    }

    @Test(expected = NullArgumentException.class)
    public void givenNullExitStrategyName_WhenCallCreateExitStrategy_ThenThrowException(){
        service.createExitStrategy(null);
    }

    @Test
    public void givenCorrectExitStrategyName_WhenCallCreateExitStrategy_ThenReturnCorrectResult(){
        setFakeExitStrategy();
        ExitStrategy strategy = service.createExitStrategy("fullClose");

        verify(exitStrategyMock, times(1)).setConfiguration(any(TradingStrategyConfiguration.class));
        verify(exitStrategyMock,times(1)).setBrokerGateway(any(BrokerGateway.class));
        verify(exitStrategyMock, times(1)).setPresenter(any(Presenter.class));

        assertEquals(exitStrategyMock, strategy);
    }

    private void setFakeExitStrategy() {
        when(responseMock.getBody()).thenReturn(exitStrategyMock);
        when(useCaseMock.execute(any(Request.class))).thenReturn(responseMock);
        when(useCaseFactoryMock.make(anyString(), any(Presenter.class))).thenReturn(useCaseMock);
    }

}
