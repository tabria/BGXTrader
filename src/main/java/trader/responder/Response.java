package trader.responder;

public interface Response<T> {

    T getBody();

    void setBody(T dataStructure);

}
