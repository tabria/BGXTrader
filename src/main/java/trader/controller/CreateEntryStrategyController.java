package trader.controller;

import trader.requestor.*;

public class CreateEntryStrategyController<T>  extends BaseController<T> {

    public CreateEntryStrategyController(UseCaseFactory useCaseFactory) {
        super(useCaseFactory);
    }
}
