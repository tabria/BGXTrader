package trader.strategy.BGXStrategy;

import trader.candlestick.candle.CandlePriceType;
import trader.connector.ApiConnector;
import trader.indicator.Indicator;
import trader.indicator.IndicatorObserver;
import trader.indicator.ma.MovingAverageBuilder;
import trader.indicator.ma.enums.MAType;
import trader.indicator.rsi.RSIBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static trader.strategy.BGXStrategy.configuration.StrategyConfig.*;
import static trader.strategy.BGXStrategy.configuration.StrategyConfig.RSI_SETTINGS;
import static trader.strategy.BGXStrategy.configuration.StrategyConfig.SLOW_WMA_SETTINGS;

class ConstructIndicatorsService {

    private List<IndicatorObserver> indicatorObservers;
    private ApiConnector apiConnector;

    ConstructIndicatorsService(ApiConnector connector) {
        indicatorObservers = new ArrayList<>();
        apiConnector = connector;
        createIndicatorObservers();
    }

    List<IndicatorObserver> getIndicatorObservers() {
        return Collections.unmodifiableList(indicatorObservers);
    }

    private void createIndicatorObservers() {
        fillIndicatorObserversList(apiConnector, PRICE_SMA_SETTINGS);
        fillIndicatorObserversList(apiConnector, DAILY_SMA_SETTINGS);
        fillIndicatorObserversList(apiConnector, FAST_WMA_SETTINGS);
        fillIndicatorObserversList(apiConnector, MIDDLE_WMA_SETTINGS);
        fillIndicatorObserversList(apiConnector, SLOW_WMA_SETTINGS);
        fillIndicatorObserversList(apiConnector, RSI_SETTINGS);
    }


    private void fillIndicatorObserversList(ApiConnector connector, String[] indicatorSettings) {
        Indicator indicator = buildIndicator(connector, indicatorSettings);
        IndicatorObserver observer = wrapIndicatorIntoObserver(indicator);
        indicatorObservers.add(observer);
    }

    private Indicator buildIndicator(ApiConnector connector, String[] indicatorSettings) {
        return indicatorSettings.length > 2
                ? buildMovingAverage(connector, indicatorSettings) :
                buildRSI(connector, indicatorSettings);
    }

    private Indicator buildMovingAverage(ApiConnector connector, String[] indicatorSettings) {
        MovingAverageBuilder movingAverageBuilder = new MovingAverageBuilder(connector);
        movingAverageBuilder.setPeriod(Long.parseLong(indicatorSettings[0]));
        movingAverageBuilder.setCandlePriceType(CandlePriceType.valueOf(indicatorSettings[1]));
        movingAverageBuilder.setMAType(MAType.valueOf(indicatorSettings[2]));
        return movingAverageBuilder.build();
    }

    private Indicator buildRSI(ApiConnector connector, String[] indicatorSettings) {
        RSIBuilder rsiBuilder = new RSIBuilder(connector);
        rsiBuilder.setPeriod(Long.parseLong(indicatorSettings[0]));
        rsiBuilder.setCandlePriceType(CandlePriceType.valueOf(indicatorSettings[1]));
        return rsiBuilder.build();
    }

    private IndicatorObserver wrapIndicatorIntoObserver(Indicator indicator) {
        return IndicatorObserver.create(indicator);
    }
}