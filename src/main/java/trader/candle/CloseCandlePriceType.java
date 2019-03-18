package trader.candle;

public class CloseCandlePriceType implements CandlePriceType {


    private final String priceType = "CLOSE";

    @Override
    public String getType() {
        return priceType;
    }

    @Override
    public String toString() {
        return "CLOSE";
    }
}
