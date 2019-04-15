package trader.validation;

import org.junit.Before;
import org.junit.Test;
import trader.exception.EmptyArgumentException;
import trader.exception.NullArgumentException;

public class ValidatorImplTest {


    private Validator validator;

    @Before
    public void setUp(){

        validator = new ValidatorImpl();

    }

    @Test(expected = NullArgumentException.class)
    public void givenNullString_WhenCallValidateString_ThenThrowException() {
        validator.validateString(null, " ");
    }

    @Test(expected = EmptyArgumentException.class)
    public void givenEmptyString_WhenCallValidateString_ThenThrowException(){
        validator.validateString("     ");
    }
}
