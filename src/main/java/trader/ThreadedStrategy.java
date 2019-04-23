package trader;

import trader.exception.NullArgumentException;
import trader.strategy.Strategy;

public class ThreadedStrategy implements Runnable {

    private Thread strategyThread;
    private Strategy strategy;

    public ThreadedStrategy(Strategy strategy){
        validateStrategy(strategy);
        this.strategy = strategy;
        strategyThread = new Thread(this, "Strategy");
        strategyThread.start();
    }

    private void validateStrategy(Strategy strategyName) {
        if (strategyName == null)
            throw new NullArgumentException();
    }


    @Override
    public void run() {
        strategy.execute();
    }
}
