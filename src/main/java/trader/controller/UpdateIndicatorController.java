package trader.controller;

import trader.exception.NullArgumentException;
import trader.requestor.RequestBuilder;
import trader.requestor.UseCaseFactory;

public class UpdateIndicatorController {

    private RequestBuilder requestBuilder;
    private UseCaseFactory useCaseFactory;

    public UpdateIndicatorController(RequestBuilder requestBuilder, UseCaseFactory useCaseFactory) {
        if(requestBuilder == null || useCaseFactory == null)
            throw new NullArgumentException();
        this.requestBuilder = requestBuilder;
        this.useCaseFactory = useCaseFactory;
    }

}
