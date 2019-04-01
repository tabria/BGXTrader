package trader.requestor;

import trader.interactor.UseCase;

public interface UseCaseFactory {

    UseCase make(String useCaseName);

}
