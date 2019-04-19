package trader.controller;

import trader.presenter.Presenter;
import trader.requestor.*;

public class CreateBrokerConnectorController<T> extends BaseController<T> {

    public CreateBrokerConnectorController(UseCaseFactory useCaseFactory, Presenter presenter) {
        super(useCaseFactory, presenter);
    }
}
