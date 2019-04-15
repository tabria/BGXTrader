package trader.validation;

import trader.exception.EmptyArgumentException;
import trader.exception.NullArgumentException;

public class Validator {

    public Validator() { }

    public static void validateString(String... stringArguments) {
        for (String str: stringArguments) {
            if(str == null)
                throw new NullArgumentException();
            if(str.trim().isEmpty())
                throw new EmptyArgumentException();
        }
    }
}