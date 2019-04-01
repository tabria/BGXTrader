package trader.interactor;

import trader.exception.EmptyArgumentException;
import trader.exception.NoSuchUseCaseException;
import trader.exception.NullArgumentException;
import trader.requestor.UseCaseFactory;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class UseCaseFactoryImpl implements UseCaseFactory {

    private static final String USE_CASE_LOCATION = "trader.interactor.";
    private static final String USE_CASE = "UseCase";

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
            Class<?> useCaseClass = Class.forName(USE_CASE_LOCATION + composeUseCaseClassName(useCaseName.trim()));
            Constructor<?> declaredConstructor = useCaseClass.getDeclaredConstructor();
            return (UseCase) declaredConstructor.newInstance();
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new NoSuchUseCaseException();
        }
    }

    private String composeUseCaseClassName(String inputName){
        char initialChar = Character.toUpperCase(inputName.charAt(0));
        inputName = inputName.substring(1);
        return initialChar + inputName + USE_CASE;
    }
}
