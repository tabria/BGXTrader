package trader.requestor;

import trader.exception.NotImplementedException;
import trader.requestor.Request;
import trader.responder.Response;

import java.util.HashMap;
import java.util.List;

public interface UseCase {

    default <T, E> Response<E> execute(Request<T> request) {throw new NotImplementedException();
    };


    default <T, E> Response<E> execute(Request<T> request, HashMap<String, Object> settings) {throw new NotImplementedException();
    };

    default <T, E> Response<E> execute(Request<T> request, List<Object> settings) {throw new NotImplementedException();
    };

}
