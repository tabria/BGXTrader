package trader.controller;

import trader.presenter.Presenter;
import trader.requestor.*;
import trader.responder.Response;

import java.util.Map;

public abstract class BaseController<T>  implements TraderController<T> {
    protected UseCaseFactory useCaseFactory;
    protected Presenter presenter;

    public BaseController(UseCaseFactory useCaseFactory, Presenter presenter) {
        this.useCaseFactory = useCaseFactory;
        this.presenter = presenter;
    }

    public Response<T> execute(Map<String, Object> settings) {
        String controllerName = this.getClass().getSimpleName().trim();
        Request<?> request = getRequest(controllerName, settings);
        UseCase useCase = make(controllerName, presenter);
        return useCase.execute(request);
    }

    Request<?> getRequest(String controllerName, Map<String, Object> settings) {
        RequestBuilder builder = RequestBuilderCreator.create(controllerName);
        return builder.build(settings);
    }

    UseCase make(String controllerName, Presenter presenter){
        return useCaseFactory.make(controllerName, presenter);
    }
}
