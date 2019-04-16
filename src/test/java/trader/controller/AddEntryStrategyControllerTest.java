package trader.controller;

import org.junit.Before;
import org.junit.Test;
import trader.entry.EntryStrategy;
import trader.exception.NullArgumentException;
import trader.responder.Response;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AddEntryStrategyControllerTest extends BaseControllerTest {

    private TraderController<EntryStrategy> controller;
    private EntryStrategy entryStrategyMock;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        entryStrategyMock = mock(EntryStrategy.class);
        controller = new AddEntryStrategyController<>(requestOLDBuilderMock, useCaseFactoryMock);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreateControllerWithNullRequestBuilder_Exception(){
        new AddEntryStrategyController<>(null, useCaseFactoryMock);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreateControllerWithNullUseCaseFactory_Exception(){
        new AddEntryStrategyController<>(requestOLDBuilderMock, null);
    }

    @Test
    public void WhenCallGetRequestWithCorrectSettings_ReturnCorrectResult(){

        setExecuteSettings("AddEntryStrategyController");
        when(responseMock.getBody()).thenReturn(entryStrategyMock);
        Response<EntryStrategy> response = controller.execute(settings);

        assertEquals(entryStrategyMock, response.getBody());
    }

}
