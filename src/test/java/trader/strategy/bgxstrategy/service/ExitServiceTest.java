package trader.strategy.bgxstrategy.service;

import org.junit.Before;
import org.junit.Test;
import trader.exception.NullArgumentException;
import trader.presenter.Presenter;
import trader.requestor.UseCaseFactory;

import static org.mockito.Mockito.mock;

public class ExitServiceTest {

    private UseCaseFactory useCaseFactoryMock;
    private ExitService service;
    private Presenter presenterMock;

    @Before
    public void setUp() throws Exception {
        presenterMock = mock(Presenter.class);
        useCaseFactoryMock = mock(UseCaseFactory.class);
        service = new ExitService(useCaseFactoryMock, presenterMock);
    }

    @Test(expected = NullArgumentException.class)
    public void givenNullOrderStrategyName_WhenCallCreateOrderStrategy_ThenThrowException(){
        service.createExitStrategy(null);
    }
//
//    @Test
//    public void givenCorrectOrderStrategyName_WhenCallCreateOrderStrategy_ThenReturnCorrectResult(){
//        setFakeOrderStrategy();
//        OrderStrategy strategy = service.createOrderStrategy("standard");
//
//        assertEquals(orderStrategyMock, strategy);
//    }
//
//    private void setFakeOrderStrategy() {
//        when(responseMock.getBody()).thenReturn(orderStrategyMock);
//        when(useCaseMock.execute(any(Request.class))).thenReturn(responseMock);
//        when(useCaseFactoryMock.make(anyString())).thenReturn(useCaseMock);
//    }

}
