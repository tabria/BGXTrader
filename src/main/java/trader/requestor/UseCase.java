package trader.requestor;

import trader.responder.Response;


public interface UseCase {

//    ///////////////to be removed//////////////////////////
//   default <T, E> Response<E> execute(Request<T> request){return null;}
//   ///////////to be removed//////////////////////////////////

  default <T, E> Response<E> execute(Request<T> request){return null;}

}
