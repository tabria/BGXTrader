package trader.requestor;

import trader.exception.EmptyArgumentException;
import trader.exception.NoSuchUseCaseException;
import trader.exception.NullArgumentException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class UseCaseFactoryImpl implements UseCaseFactory {

    @Override
    public UseCase make(String useCaseName) {
        checkInputName(useCaseName);
        return createUseCaseInstance(useCaseName);
    }

    private void checkInputName(String useCaseName) {
        if(useCaseName == null)
            throw new NullArgumentException();
        if(useCaseName.trim().isEmpty())
            throw new EmptyArgumentException();
    }

    private UseCase createUseCaseInstance(String useCaseName) {
        try {
            Class<?> useCaseClass = Class.forName(composeUseCaseClassName(useCaseName));
            Constructor<?> declaredConstructor = useCaseClass.getDeclaredConstructor();
            return (UseCase) declaredConstructor.newInstance();
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new NoSuchUseCaseException();
        }
    }

    private String composeUseCaseClassName(String inputName){
        return String.format("%s.%s.%s", "trader", "interactor", inputName.replace("Controller", "UseCase").trim());
    }
}
