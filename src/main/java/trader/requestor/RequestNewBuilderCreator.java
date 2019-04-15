package trader.requestor;

import trader.exception.NoSuchRequestBuilderException;
import trader.validation.Validator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class RequestNewBuilderCreator {


    public RequestNewBuilder create(String controllerName) {
        Validator.validateString(controllerName);

        try {
            Class<?> builderClass = Class.forName(composeClassForName(controllerName));
            Constructor<?> declaredConstructor = builderClass.getDeclaredConstructor();
            return (RequestNewBuilder) declaredConstructor.newInstance();
            } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new NoSuchRequestBuilderException();
        }
    }

    String composeClassForName(String inputName){
        inputName = inputName.replace("Controller", "").trim();
        return String.format("%s.%s.%s.%s%s", "trader", "interactor", inputName.toLowerCase(), inputName, "RequestNewBuilder");
    }
}
