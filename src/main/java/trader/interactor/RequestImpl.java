package trader.interactor;

import trader.requestor.Request;

public class RequestImpl<T> implements Request<T> {

    private T body;

    public T getBody() {
        return body;
    }

    @Override
    public void setBody(T dataStructure) {
        this.body = dataStructure;
    }
}
