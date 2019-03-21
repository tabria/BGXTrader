package trader.strategy;

public abstract class BaseStrategy implements Strategy {

    private Thread strategyThread;

    protected BaseStrategy(){
        strategyThread = new Thread(this);
    }

    @Override
    public void execute(){
        strategyThread.start();
    }


}
