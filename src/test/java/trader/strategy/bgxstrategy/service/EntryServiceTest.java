package trader.strategy.bgxstrategy.service;

import org.junit.Before;
import org.junit.Test;
import trader.controller.TraderController;
import trader.entry.EntryStrategy;
import trader.exception.NullArgumentException;
import trader.requestor.Request;
import trader.requestor.UseCase;
import trader.requestor.UseCaseFactory;
import trader.responder.Response;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class EntryServiceTest {

    private Response responseMock;
    private UseCase useCaseMock;
    private EntryStrategy entryStrategyMock;
    private UseCaseFactory useCaseFactoryMock;
    private EntryService entryService;

    @Before
    public void setUp() throws Exception {

        responseMock = mock(Response.class);
        useCaseMock = mock(UseCase.class);
        entryStrategyMock = mock(EntryStrategy.class);
        useCaseFactoryMock = mock(UseCaseFactory.class);
        entryService = new EntryService(useCaseFactoryMock);

    }

    @Test(expected = NullArgumentException.class)
    public void givenNullEntryStrategyName_WhenCallCreateEntryStrategy_ThenThrowException(){
        entryService.createEntryStrategy(null, new ArrayList<>());
    }

    @Test(expected = NullArgumentException.class)
    public void givenNullIndicators_WhenCallCreateEntryStrategy_ThenThrowException(){
        entryService.createEntryStrategy("standard", null);
    }

    @Test
    public void givenCorrectSettings_WhenCallCreateEntryStrategy_ThenSetEntryStrategyIndicators(){
        setFakeEntryStrategy();
        entryService.createEntryStrategy("standard", new ArrayList<>());

        verify(entryStrategyMock, times(1)).setIndicators(anyList());
    }

    @Test
    public void givenCorrectSettings_WhenCallCreateEntryStrategy_ThenSetTradeController(){
        setFakeEntryStrategy();
        entryService.createEntryStrategy("standard", new ArrayList<>());

        verify(entryStrategyMock, times(1)).setCreateTradeController(any(TraderController.class));
    }

    @Test
    public void givenCorrectEntryStrategyName_WhenCallCreateEntryStrategy_ThenReturnCorrectResult(){
        setFakeEntryStrategy();
        EntryStrategy strategy = entryService.createEntryStrategy("standard", new ArrayList<>());

        assertEquals(entryStrategyMock, strategy);
    }

    private void setFakeEntryStrategy() {
        doNothing().when(entryStrategyMock).setCreateTradeController(any(TraderController.class));
        doNothing().when(entryStrategyMock).setIndicators(anyList());
        when(responseMock.getBody()).thenReturn(entryStrategyMock);
        when(useCaseMock.execute(any(Request.class))).thenReturn(responseMock);
        when(useCaseFactoryMock.make(anyString())).thenReturn(useCaseMock);
    }

}
