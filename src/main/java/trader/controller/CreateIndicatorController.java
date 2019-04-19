package trader.controller;

import trader.presenter.Presenter;
import trader.requestor.*;

public class CreateIndicatorController<T> extends BaseController<T> {

    public CreateIndicatorController(UseCaseFactory useCaseFactory, Presenter presenter) {
        super(useCaseFactory, presenter);
    }
}
