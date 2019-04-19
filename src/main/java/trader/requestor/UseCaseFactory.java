package trader.requestor;

import trader.presenter.Presenter;

public interface UseCaseFactory {

    UseCase make(String useCaseName, Presenter presenter);

}
