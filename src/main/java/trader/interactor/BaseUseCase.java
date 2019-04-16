package trader.interactor;

import trader.exception.NullArgumentException;
import trader.requestor.Request;
import trader.requestor.UseCase;
import trader.responder.Response;

abstract class BaseUseCase implements UseCase {

    @Override
    public <T, E> Response<E> execute(Request<T> request) {
        if(request == null)
            throw new NullArgumentException();
        T dataResult = request.getbody();
        Response<E> tradeResponse = new ResponseImpl<>();
        tradeResponse.setResponseDataStructure((E) dataResult);
        return tradeResponse;
    }
}
