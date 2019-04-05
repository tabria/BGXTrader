package trader.requestor;

import trader.requestor.Request;
import trader.responder.Response;

public interface UseCase {

    <T, E> Response<E> execute(Request<T> request);

}
