package trader.requestor;

import org.junit.Before;
import org.junit.Test;
import trader.exception.EmptyArgumentException;
import trader.exception.NoSuchRequestBuilderException;
import trader.exception.NullArgumentException;
import trader.interactor.addbgxconfiguration.AddBGXConfigurationRequestNewBuilder;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class RequestNewBuilderCreatorTest {

    private RequestNewBuilder requestMock;
    private RequestNewBuilderCreator requestNewBuilderCreator;

    @Before
    public void setUp() throws Exception {
        requestMock = mock(RequestNewBuilder.class);
        requestNewBuilderCreator = new RequestNewBuilderCreator();
    }

    @Test(expected = NullArgumentException.class)
    public void givenNullControllerName_WhenCallCreate_ThenException(){
        requestNewBuilderCreator.create(null);
    }

    @Test(expected = EmptyArgumentException.class)
    public void givenEmptyStringForControllerName_WhenCallCreate_ThenException(){
        requestNewBuilderCreator.create("  ");
    }

    @Test
    public void givenCorrectControllerName_WhenCallComposeClassName_ThenCorrectClassName(){
        String classForName = requestNewBuilderCreator.composeClassForName("AddBGXConfigurationController");

        assertEquals("trader.interactor.addbgxconfiguration.AddBGXConfigurationRequestNewBuilder", classForName);
    }

    @Test
    public void givenCorrectController_WhenCallCreate_CreateCorrectRequestBuilder(){
        RequestNewBuilder requestNewBuilder = requestNewBuilderCreator.create(" AddBGXConfigurationController ");

        assertEquals(AddBGXConfigurationRequestNewBuilder.class, requestNewBuilder.getClass());
    }

    @Test(expected = NoSuchRequestBuilderException.class)
    public void givenNonexistentControllerName_WhenCallCreate_ThenException(){
        RequestNewBuilder requestNewBuilder = requestNewBuilderCreator.create(" BlqBGXConfigurationController ");
    }

//    @Test(expected = NoSuchUseCaseException.class)
//    public void WhenCallWithBadUseCaseName_Exception(){
//        useCaseFactory.make("BlqUseCase");
//    }
//

}
