package trader.responder;

public interface Response<T> {

    T getBody();

    void setResponseDataStructure(T dataStructure);

}
