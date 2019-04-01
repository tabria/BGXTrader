package trader.interactor;

import trader.exception.NullArgumentException;
import trader.requestor.Request;
import trader.responder.Response;

public class AddIndicatorUseCase implements UseCase {


    @Override
    public <T, E> Response<E> execute(Request<T> request) {
        if(request == null)
            throw new NullArgumentException();
        T result = request.getRequestDataStructure();
        Response<E> response = new ResponseImpl<>();
        response.setResponseDataStructure((E) result);
        return response;
    }
}
