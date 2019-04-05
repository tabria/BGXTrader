package trader.broker.connector;

import trader.entity.candlestick.Candlestick;
import trader.responder.Response;

import java.util.List;

public interface CandlestickTransformable {

    <T> List<Candlestick> transformCandlesticks(Response<T> response);
}
