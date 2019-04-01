package trader.responder;

public interface Response<T> {

    T getResponseDataStructure();

    void setResponseDataStructure(T dataStructure);

}
