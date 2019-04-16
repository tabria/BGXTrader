package trader.requestor;

public interface Request<T> {

    T getBody();

    void setBody(T dataStructure);
}