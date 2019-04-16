package trader.validation;

import trader.exception.EmptyArgumentException;
import trader.exception.NullArgumentException;

public class Validator {

    public Validator() { }

    public static void validateStrings(String... stringArguments) {
        for (String str: stringArguments) {
            validateForNull(str);
            if(str.trim().isEmpty())
                throw new EmptyArgumentException();
        }
    }

    public static void validateForNull(Object object) {
        if(object == null)
            throw new NullArgumentException();
    }
}