package trader.controller;

import trader.requestor.*;

public class CreateBrokerConnectorController<T> extends BaseController<T> {

    public CreateBrokerConnectorController(UseCaseFactory useCaseFactory) {
        super(useCaseFactory);
    }
}
