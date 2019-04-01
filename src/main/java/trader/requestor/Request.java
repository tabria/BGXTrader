package trader.requestor;

public interface Request<T> {
    T getDataStructure();

    void setDataStructure(T dataStructure);
}
