package trader.validation;

import org.junit.Before;
import org.junit.Test;
import trader.exception.EmptyArgumentException;
import trader.exception.NullArgumentException;

public class ValidatorTest {


    @Before
    public void setUp(){

    }

    @Test(expected = NullArgumentException.class)
    public void givenNullString_WhenCallValidateString_ThenThrowException() {
        Validator.validateStrings(null, " ");
    }

    @Test(expected = EmptyArgumentException.class)
    public void givenEmptyString_WhenCallValidateString_ThenThrowException(){
        Validator.validateStrings("     ");
    }

    @Test(expected = NullArgumentException.class)
    public void givenNullObject_WhenCallValidateForNull_ThenException(){
        Validator.validateForNull(null);
    }

}
