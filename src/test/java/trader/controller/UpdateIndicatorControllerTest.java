package trader.controller;

import org.junit.Before;
import org.junit.Test;
import trader.exception.NullArgumentException;
import trader.requestor.RequestBuilder;
import trader.requestor.UseCaseFactory;

import static org.mockito.Mockito.mock;

public class UpdateIndicatorControllerTest {

    private UseCaseFactory useCaseFactoryMock;
    private RequestBuilder requestBuilderMock;

    @Before
    public void setUp() throws Exception {
        useCaseFactoryMock = mock(UseCaseFactory.class);
        requestBuilderMock = mock(RequestBuilder.class);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreateControllerWithNullRequestBuilder_Exception(){
        new UpdateIndicatorController(null, useCaseFactoryMock);
    }

    @Test(expected = NullArgumentException.class)
    public void WhenCreateControllerWithNullUserCaseFactory_Exception(){
        new UpdateIndicatorController(requestBuilderMock, null);
    }

}
