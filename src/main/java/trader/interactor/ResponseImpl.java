package trader.interactor;

import trader.responder.Response;

public class ResponseImpl<T> implements Response<T> {

    private T dataStructure;

    @Override
    public T getBody() {
        return dataStructure;
    }

    @Override
    public void setResponseDataStructure(T dataStructure) {
        this.dataStructure = dataStructure;
    }
}
