package trader.interactor;

import trader.exception.EmptyArgumentException;
import trader.exception.NoSuchUseCaseException;
import trader.exception.NullArgumentException;
import trader.presenter.Presenter;
import trader.requestor.UseCase;
import trader.requestor.UseCaseFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class UseCaseFactoryImpl implements UseCaseFactory {

    @Override
    public UseCase make(String useCaseName, Presenter presenter) {
        checkInputName(useCaseName, presenter);
        return createUseCaseInstance(useCaseName, presenter);
    }

    private void checkInputName(String useCaseName, Presenter presenter) {
        if(useCaseName == null || presenter == null)
            throw new NullArgumentException();
        if(useCaseName.trim().isEmpty())
            throw new EmptyArgumentException();
    }

    private UseCase createUseCaseInstance(String useCaseName, Presenter presenter) {
        try {
            Class<?> useCaseClass = Class.forName(composeUseCaseClassName(useCaseName));
            Constructor<?> declaredConstructor = useCaseClass.getDeclaredConstructor(Presenter.class);
            return (UseCase) declaredConstructor.newInstance(presenter);
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new NoSuchUseCaseException();
        }
    }

    private String composeUseCaseClassName(String inputName){
        String noControllerExtensions = inputName.trim().replace("Controller", "");
        return String.format("%s.%s.%s.%s", "trader", "interactor", noControllerExtensions.toLowerCase(), noControllerExtensions + "UseCase");
    }
}
