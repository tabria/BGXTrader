package trader.interactor;

import trader.requestor.Request;

public class RequestImpl<T> implements Request<T> {

    private T dataStructure;

    @Override
    public T getbody() {
        return dataStructure;
    }

    @Override
    public void setRequestDataStructure(T dataStructure) {
        this.dataStructure = dataStructure;
    }
}
