package trader.controller;

import trader.requestor.*;

public class CreateIndicatorController<T> extends BaseController<T> {

    public CreateIndicatorController(UseCaseFactory useCaseFactory) {
        super(useCaseFactory);
    }
}
