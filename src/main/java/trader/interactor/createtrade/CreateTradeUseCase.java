package trader.interactor.createtrade;

import trader.entity.trade.Direction;
import trader.entity.trade.Trade;
import trader.entity.trade.TradeImpl;
import trader.interactor.ResponseImpl;
import trader.presenter.Presenter;
import trader.requestor.Request;
import trader.requestor.UseCase;
import trader.responder.Response;

import java.util.Map;

import static trader.interactor.createtrade.enums.Constants.*;

public class CreateTradeUseCase implements UseCase {

    private Presenter presenter;

    public CreateTradeUseCase(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public <T, E> Response<E> execute(Request<T> request) {
        Map<String, Object> settings = (Map<String, Object>) request.getBody();
        Trade trade = setTrade(settings);
        Response<E> response = setResponse((E) trade);
        if(!trade.getDirection().equals(Direction.FLAT))
            presenter.execute(response);
        return response;
    }

    private Trade setTrade(Map<String,Object> inputSettings) {
        Trade trade = new TradeImpl();
        Map<String, String> settings = (Map<String, String>) inputSettings.get("settings");
        if(settings != null && settings.size() > 0){
            trade.setTradable(settings.get(TRADABLE.toString()));
            trade.setDirection(settings.get(DIRECTION.toString()));
            trade.setEntryPrice(settings.get(ENTRY_PRICE.toString()));
            trade.setStopLossPrice(settings.get(STOP_LOSS_PRICE.toString()));
        }
        return trade;
    }

    private <E> Response<E> setResponse(E indicator) {
        Response<E> response = new ResponseImpl<>();
        response.setBody(indicator);
        return response;
    }
}
