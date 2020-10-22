package com.think.quantstarter.pilot.service.bch;

import com.alibaba.fastjson.JSON;
import com.think.quantstarter.rest.bean.swap.param.CancelOrderAlgo;
import com.think.quantstarter.rest.bean.swap.param.ClosePosition;
import com.think.quantstarter.rest.bean.swap.param.PpOrder;
import com.think.quantstarter.rest.bean.swap.param.SwapOrderParam;
import com.think.quantstarter.rest.bean.swap.result.*;
import com.think.quantstarter.rest.enums.FuturesTransactionTypeEnum;
import com.think.quantstarter.rest.exception.APIException;
import com.think.quantstarter.rest.service.swap.SwapMarketAPIService;
import com.think.quantstarter.rest.service.swap.SwapTradeAPIService;
import com.think.quantstarter.rest.service.swap.SwapUserAPIServive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author mpthink
 * @date 2020/10/13 17:02
 */
@Slf4j
@Service
public class BchTradeService {

    private static final String bch_instrument_id = "BCH-USDT-SWAP";
    private static Integer order_size = 1;
    private static final String order_type = "4"; //4：市价委托
    private static final String trigger_type = "2"; //1:限价 2:市场价；止盈触发价格类型，默认是限价；为市场价时，委托价格不必填；
    private static final String algo_order_type = "5"; //5：止盈止损

    @Resource
    private SwapTradeAPIService swapTradeAPIService;
    @Resource
    private SwapUserAPIServive swapUserAPIServive;
    @Resource
    private SwapMarketAPIService swapMarketAPIService;


    /**
     * 定时更改order size的大小
     */
    @Scheduled(cron = "0 59 23 15 * ?")
    @Retryable(include = {APIException.class}, maxAttempts = 3)
    public void changeOrderSize() {
        AccountsInfo bchAccountInfo = getBchAccountInfo();
        String total_avail_balance = bchAccountInfo.getTotal_avail_balance();
        String ticker = swapMarketAPIService.getTickerApi(bch_instrument_id);
        TickerVO tickerVO = JSON.parseObject(ticker, TickerVO.class);
        Double total = Double.valueOf(total_avail_balance);
        Double last = tickerVO.getLast();
        int planSize = (int) (total * 100 * 0.25 / last);
        if (planSize > order_size) {
            order_size = planSize;
        }
    }


    public TickerVO getTicker(){
        String ticker = swapMarketAPIService.getTickerApi(bch_instrument_id);
        TickerVO tickerVO = JSON.parseObject(ticker, TickerVO.class);
        return tickerVO;
    }

    /**
     * 所有单据都市价成交
     *
     * @param type
     * @return
     */
    public PerOrderResult order(FuturesTransactionTypeEnum type) {
        PpOrder order = PpOrder.builder()
                .size(order_size.toString())
                .type(String.valueOf(type.code()))
                .instrument_id(bch_instrument_id)
                .order_type(order_type)
                .build();
        return swapTradeAPIService.order(order);
    }

    /**
     * 所有单据都市价成交
     *
     * @param order_id
     * @return
     */
    public CancelOrder cancelOrder(String order_id) {
        String cancelOrderByOrderId = swapTradeAPIService.cancelOrderByOrderId(bch_instrument_id, order_id);
        return JSON.parseObject(cancelOrderByOrderId, CancelOrder.class);
    }

    /**
     * 所有单据都市价成交
     *
     * @param type             1:开多 2:开空 3:平多 4:平空
     * @param tp_trigger_price 止盈触发价格
     * @param sl_trigger_price 止损触发价格
     * @return
     */
    public SwapOrderResultVO swapOrderAlgo(FuturesTransactionTypeEnum type, String tp_trigger_price, String sl_trigger_price) {
        SwapOrderParam swapOrderParam = SwapOrderParam.builder()
                .instrument_id(bch_instrument_id)
                .type(String.valueOf(type.code()))
                .order_type(algo_order_type)
                .size(order_size.toString())
                .tp_trigger_price(tp_trigger_price)
                .tp_trigger_type(trigger_type) //1:限价 2:市场价；止盈触发价格类型，默认是限价；为市场价时，委托价格不必填；
                .sl_trigger_price(sl_trigger_price)
                .sl_trigger_type(trigger_type)
                .build();
        return JSON.parseObject(swapTradeAPIService.swapOrderAlgo(swapOrderParam), SwapOrderResultVO.class);
    }

    public CancelAlgoOrder cancelOrderAlgo(List<String> algo_ids) {
        CancelOrderAlgo cancelOrderAlgo = CancelOrderAlgo.builder()
                .instrument_id(bch_instrument_id)
                .algo_ids(algo_ids)
                .order_type(algo_order_type)
                .build();
        String orderAlgo = swapTradeAPIService.cancelOrderAlgo(cancelOrderAlgo);
        return JSON.parseObject(orderAlgo, CancelAlgoOrderVO.class).getData();
    }

    public SwapOrders checkAlgoOrder(String algo_id) {
        String swapOrders = swapTradeAPIService.getSwapOrders(bch_instrument_id, algo_order_type, "", algo_id, "", "", "");
        return JSON.parseObject(swapOrders, SwapOrdersVO.class).getOrderStrategyVOS().get(0);
    }

    public OrderInfo getOrderInfo(String order_id) {
        String info = swapUserAPIServive.selectOrderByOrderId(bch_instrument_id, order_id);
        return JSON.parseObject(info, OrderInfo.class);
    }

    public String getDealDetail(String order_id) {
        String dealDetail = swapUserAPIServive.selectDealDetail(bch_instrument_id, order_id, "", "", "");
        return dealDetail;
    }

    public AccountsInfo getBchAccountInfo() {
        String result = swapUserAPIServive.selectAccount(bch_instrument_id);
        return JSON.parseObject(result, SingleAccountsVO.class).getInfo();
    }

    public PositionVO getPosition(){
        String result = swapUserAPIServive.getPosition(bch_instrument_id);
        return JSON.parseObject(result, PositionVO.class);
    }

    public void closeAllPositions(){
        try{
            ClosePosition longP = new ClosePosition();
            longP.setInstrument_id(bch_instrument_id);
            longP.setDirection("long");
            swapTradeAPIService.closePosition(longP);
        }catch (APIException e){
            log.error("BCH close long has error");
        }

        try{
            ClosePosition shortP = new ClosePosition();
            shortP.setInstrument_id(bch_instrument_id);
            shortP.setDirection("short");
            swapTradeAPIService.closePosition(shortP);
        }catch (APIException e){
            log.error("BCH close short has error");
        }
    }

}
