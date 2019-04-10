package trader.order;

public class OrderStrategyImpl implements OrderStrategy {



//    public void sendNewTradeOrder(Account account, BigDecimal bid){
//
//        //check if orderList have pending trade (market if touch trade, not stop losses)
//        //if don't have open trade or pending market orders, then generate new trade
//        //if trade is not yet generated
//
////        if(account.getTrades().size() != 0 || hasWaitingTrades(account) || this.tradeGenerator.isGenerated() ){
////            return;
////        }
//
//        Trade newTrade = this.tradeGenerator.generateTrade();
//
//        if (!newTrade.getTradable()) {
//            return;
//        }
//
//        BigDecimal unitsSize = calculateUnitsSize(account, newTrade, bid);
//        BigDecimal availableMargin = account.getMarginAvailable().bigDecimalValue();
//        BigDecimal futureMargin = this.calculateTradeMargin(account, unitsSize);
//        if (availableMargin.compareTo(futureMargin)>0 && unitsSize.compareTo(BigDecimal.ZERO)!=0){
//
//            //create order request
//            OrderCreateRequest request = this.createOrderRequest(unitsSize, newTrade);
//            try {
//                this.orderCreateResponse = this.context.order.create(request);
//                TransactionID id = this.orderCreateResponse.getOrderCreateTransaction().getId();
//                DateTime time = this.orderCreateResponse.getOrderCreateTransaction().getTime();
//                System.out.println("New TradeImpl has been added with id: " +id.toString() + " and time: " +time.toString() );
//            } catch (RequestException | ExecuteException e){
//                throw new RuntimeException(e);
//            }
//        }
//    }


}
