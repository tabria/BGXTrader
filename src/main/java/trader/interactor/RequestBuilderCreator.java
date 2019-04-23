package trader.interactor;

import trader.exception.NoSuchRequestBuilderException;
import trader.requestor.RequestBuilder;
import trader.validation.Validator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class RequestBuilderCreator {

    public static RequestBuilder create(String controllerName) {
        Validator.validateStrings(controllerName);

        try {
            Class<?> requestClass = Class.forName(composeClassForName(controllerName));
            Constructor<?> declaredConstructor = requestClass.getDeclaredConstructor();
            return (RequestBuilder) declaredConstructor.newInstance();
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new NoSuchRequestBuilderException();
        }
    }

    public static String composeClassForName(String inputName){
        inputName = inputName.replace("Controller", "").trim();
        return String.format("%s.%s.%s.%s%s", "trader", "interactor", inputName.toLowerCase(), inputName, "RequestBuilder");
    }

}
