package trader.controller;

import trader.presenter.Presenter;
import trader.requestor.*;

public class CreateTradeController<T> extends BaseController<T> {


    public CreateTradeController(UseCaseFactory useCaseFactory, Presenter presenter) {
        super(useCaseFactory, presenter);
    }
}
