package trader.interactor;

import trader.requestor.Request;

public class RequestImpl<T> implements Request<T> {

    private T dataStructure;

    @Override
    public T getDataStructure() {
        return dataStructure;
    }

    @Override
    public void setDataStructure(T dataStructure) {
        this.dataStructure = dataStructure;
    }
}
