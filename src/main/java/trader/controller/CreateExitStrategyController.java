package trader.controller;

import trader.presenter.Presenter;
import trader.requestor.*;

public class CreateExitStrategyController<T> extends BaseController<T> {

    public CreateExitStrategyController(UseCaseFactory useCaseFactory, Presenter presenter) {
        super(useCaseFactory, presenter);
    }
}
