package trader.controller;

import trader.presenter.Presenter;
import trader.requestor.*;

public class CreateBGXConfigurationController<T> extends BaseController<T> {

    public CreateBGXConfigurationController(UseCaseFactory useCaseFactory, Presenter presenter) {
        super(useCaseFactory, presenter);
    }

}
