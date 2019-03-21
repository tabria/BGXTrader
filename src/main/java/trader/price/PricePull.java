package trader.price;


import trader.core.Observable;

public final class PricePull implements Runnable {

    private final Thread thread;
    private final Observable observable;

    public PricePull(String name, Observable observable){
        this.thread = new Thread(this, name);
        this.observable = observable;
        this.thread.start();
    }

    @Override
    public void run() {
        this.observable.execute();
    }
}
