package trader.controller;

import trader.presenter.Presenter;
import trader.requestor.*;

public class CreateEntryStrategyController<T>  extends BaseController<T> {

    public CreateEntryStrategyController(UseCaseFactory useCaseFactory, Presenter presenter) {
        super(useCaseFactory, presenter);
    }
}
