package trader.requestor;

public interface Request<T> {
     T getRequestDataStructure();

     void setRequestDataStructure(T dataStructure);
}
