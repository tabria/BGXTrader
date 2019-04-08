# BGXTrader
Automated EUR/USD currency trader

Moving Average CrossOver Trading Robot
This is a trading robot based on BunnyGirl Bunny Cross(BGX). The application is using Oanda's v20 Java Wrapper API

The application can use Simple, Exponential and Weighted moving averages, and Relative Strength Index. For daily open is used 
SMA with indicatorPeriod of 1. For Price is used also SMA with indicatorPeriod of 1, applied on Close. The application can tradeImpl only on 
EUR accounts and only EUR/USD pair. Also application will open only one tradeImpl and if there is an open tradeImpl no other trades will 
be generated until current tradeImpl is closed.

Entries: The signals are generated from crosses between WMA(5) and WMA(20), priceSMA(1) and WMA(20), priceSMA(1) and WMA(100). All trades are in the 
direction of WMA(100). Entry signal is 25 pips away from the cross. Stop is on the cross. Entries are executed with Market If Touched Order. If there is an unfilled order and priceImpl move more than 5 pips below(for longs)/above(for shorts) cross point, then the waiting order will be canceled.

There are 2 strategies for exiting:

First: When priceImpl hit +32 pips, half of the position will be liquidated and stop will be set to break even. Then the stop will be 
trailed after each bar's extreme.

Second: When priceImpl hit +32 pips, stop loss is moved to +16 pips for the hole tradeImpl and then will be trailed after significant 
extremes. For example for long, when priceImpl make high, then low and then higher high than the first one, the significant extreme 
is the low and the stop will be placed there. For shorts is reversed.

