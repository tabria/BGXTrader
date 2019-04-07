package trader.interactor;

import trader.exception.NullArgumentException;
import trader.requestor.Request;
import trader.requestor.UseCase;
import trader.responder.Response;

public class CreateIndicatorUseCase implements UseCase {


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
