package trader.requestor;

import org.junit.Test;
import trader.exception.EmptyArgumentException;
import trader.exception.NoSuchRequestBuilderException;
import trader.exception.NullArgumentException;
import trader.interactor.createbgxconfiguration.CreateBGXConfigurationRequestBuilder;

import static org.junit.Assert.*;



public class RequestBuilderCreatorTest {

    @Test(expected = NullArgumentException.class)
    public void givenNullControllerName_WhenCallCreate_ThenException() {

        RequestBuilderCreator.create(null);
    }

    @Test(expected = EmptyArgumentException.class)
    public void givenEmptyStringForControllerName_WhenCallCreate_ThenException(){
        RequestBuilderCreator.create("  ");
    }

    @Test
    public void givenCorrectControllerName_WhenCallComposeClassName_ThenCorrectClassName(){
        String classForName = RequestBuilderCreator.composeClassForName("CreateBGXConfigurationController");

        assertEquals("trader.interactor.createbgxconfiguration.CreateBGXConfigurationRequestBuilder", classForName);
    }

    @Test
    public void givenCorrectControllerName_WhenCallCreate_CreateCorrectRequestBuilder(){
        RequestBuilder requestBuilder = RequestBuilderCreator.create(" CreateBGXConfigurationController ");

        assertEquals(CreateBGXConfigurationRequestBuilder.class, requestBuilder.getClass());
    }

    @Test(expected = NoSuchRequestBuilderException.class)
    public void givenNonexistentControllerName_WhenCallCreate_ThenException(){
        RequestBuilderCreator.create(" BlqBGXConfigurationController ");
    }

}
