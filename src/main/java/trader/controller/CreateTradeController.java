package trader.controller;

import trader.requestor.*;

public class CreateTradeController<T> extends BaseController<T> {


    public CreateTradeController(UseCaseFactory useCaseFactory) {
        super(useCaseFactory);
    }
}
