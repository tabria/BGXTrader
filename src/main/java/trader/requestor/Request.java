package trader.requestor;

public interface Request<T> {

    T getbody();

    void setRequestDataStructure(T dataStructure);
}