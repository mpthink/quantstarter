package com.think.quantstarter;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author mpthink
 * @date 2020/8/11 16:15
 */
public class MainTest {

    //计划损失N的倍数
    private static Double[] lossNArray = new Double[]{0.5, 1.0, 1.5, 2.0, 2.5, 3.0};
    //最大损失取过去多少分钟的
    private static Integer[] lossMArray = new Integer[]{30, 40, 60, 80, 90, 120};
    //最长持有多久
    private static Integer[] handMArray = new Integer[]{35, 65, 95, 125, 155, 185, 215, 245};
    //条件判断随机
    private static Boolean[] booleanArray = new Boolean[]{true, false};
    //多长时间内不买第二单
    private static Integer[] intervalArray = new Integer[]{0, 60, 90, 120, 150, 180, 210, 240};


    static Object getRanInArr(Object[] array){
        int length=array.length;
        int index= (int) (Math.random()*length);
        return array[index];
    }

    public static void main(String[] args) throws ParseException {

        List<Integer> test = new ArrayList<>();
        test.add(1);
        test.add(2);
        List<Integer> test2 = new ArrayList<>();
        test2.add(2);
        test.removeAll(test2);
        System.out.println(test);

//        for(int i=0;i<20;i++)
//        System.out.println(getRanInArr(lossNArray));
//
//
//



//        Date date = DateUtils.parseUTCTime("2020-09-08T12:40:00.000Z");
//        System.out.println(date);

//        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
//        String candleTime = "2020-09-04T22:35:00.000Z";
//        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
//        Date date = DateUtils.parseUTCTime(candleTime);
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(date);
//        int hour = calendar.get(Calendar.HOUR_OF_DAY);
//        int plus4hour = hour/4 * 4;
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//        String time = format.format(DateUtils.parseUTCTime(candleTime));
//        Date onlyDate = format.parse(time);
//        calendar.setTime(onlyDate);
//        calendar.add(Calendar.HOUR, plus4hour);
//        Date calendarTime = calendar.getTime();
//        String time1 = DateUtils.timeToString(calendarTime, 8);
//        System.out.println(time1);
//
//        System.out.println(DateUtils.addMinutes(time1, -240));


//        List<Double> dataList = new ArrayList<>();
////        dataList.add(369.67);
////        dataList.add(369.06);
////        dataList.add(383.2);
////        dataList.add(374.83);
////        dataList.add(377.43);
//
//        dataList.add(373.66);
//        dataList.add(375.92);

//        System.out.println(CandleUtil.getEMA(dataList, 10));

//        List<Double> doubleList = new ArrayList<>();
//        doubleList.add(6785.36);
//        doubleList.add(6943.01);
//
//        System.out.println(CandleUtil.getEMA(doubleList, 10));


//        String test = "{\"table\":\"swap/candle900s\",\"data\":[{\"candle\":[\"2020-08-11T11:30:00.000Z\",\"11761.9\",\"11769.9\",\"11753.8\",\"11763.7\",\"4211\",\"35.7934\"],\"instrument_id\":\"BTC-USD-SWAP\"}]}";
//
//        JSONObject jsonObject = JSON.parseObject(test);
//
//
//
//        System.out.println(jsonObject.getString("table"));
//
//
//        String ticker = "{\"table\":\"swap/ticker\",\"data\":[{\"last\":\"170.91\",\"open_24h\":\"198.4\",\"best_bid\":\"170.92\",\"high_24h\":\"199.03\",\"low_24h\":\"166\",\"volume_24h\":\"31943233\",\"volume_token_24h\":\"1730040.0174\",\"best_ask\":\"170.97\",\"open_interest\":\"4162489\",\"instrument_id\":\"ETH-USD-SWAP\",\"timestamp\":\"2020-03-12T08:30:41.738Z\",\"best_bid_size\":\"101\",\"best_ask_size\":\"2050\",\"last_qty\":\"1\"}]}";
//
//        JSONObject object = JSON.parseObject(ticker);
//
//        System.out.println(object.getJSONArray("data").getString(0));
//
//        String test2 = "[[\"2020-08-11T11:15:00.000Z\",\"11765.1\",\"11765.5\",\"11726\",\"11759.9\",\"32144\",\"273.7298\"],[\"2020-08-11T11:00:00.000Z\",\"11749.9\",\"11772.1\",\"11728\",\"11765.1\",\"37774\",\"321.2776\"],[\"2020-08-11T10:45:00.000Z\",\"11725.5\",\"11765.9\",\"11725.3\",\"11748.2\",\"35255\",\"300.1103\"],[\"2020-08-11T10:30:00.000Z\",\"11730.6\",\"11732.3\",\"11701.6\",\"11725.4\",\"31865\",\"271.7843\"],[\"2020-08-11T10:15:00.000Z\",\"11708.5\",\"11734.9\",\"11693.7\",\"11730.7\",\"28463\",\"242.945\"],[\"2020-08-11T10:00:00.000Z\",\"11706.6\",\"11715.4\",\"11657\",\"11708\",\"121179\",\"1036.7949\"],[\"2020-08-11T09:45:00.000Z\",\"11736.6\",\"11737.3\",\"11703.5\",\"11706.6\",\"55254\",\"471.253\"],[\"2020-08-11T09:30:00.000Z\",\"11756.9\",\"11770.7\",\"11731.5\",\"11736.6\",\"34676\",\"295.0965\"],[\"2020-08-11T09:15:00.000Z\",\"11759.4\",\"11777.8\",\"11754.6\",\"11757\",\"36068\",\"306.4752\"],[\"2020-08-11T09:00:00.000Z\",\"11721.5\",\"11775.1\",\"11689.9\",\"11759.3\",\"87609\",\"745.8405\"],[\"2020-08-11T08:45:00.000Z\",\"11727\",\"11753.8\",\"11702.5\",\"11722\",\"44462\",\"379.1894\"],[\"2020-08-11T08:30:00.000Z\",\"11742.2\",\"11747.9\",\"11712\",\"11727\",\"32654\",\"278.3742\"],[\"2020-08-11T08:15:00.000Z\",\"11750.5\",\"11773.9\",\"11730\",\"11742.2\",\"29374\",\"249.8686\"],[\"2020-08-11T08:00:00.000Z\",\"11756.7\",\"11777.5\",\"11742.3\",\"11750.5\",\"32058\",\"272.4963\"],[\"2020-08-11T07:45:00.000Z\",\"11752.3\",\"11773\",\"11751.2\",\"11756.7\",\"19443\",\"165.2491\"],[\"2020-08-11T07:30:00.000Z\",\"11763.6\",\"11766.8\",\"11711\",\"11752.3\",\"59066\",\"503.2402\"],[\"2020-08-11T07:15:00.000Z\",\"11778.7\",\"11778.7\",\"11730\",\"11763.7\",\"92378\",\"786.1898\"],[\"2020-08-11T07:00:00.000Z\",\"11763\",\"11794.4\",\"11762.9\",\"11778.7\",\"29624\",\"251.3774\"],[\"2020-08-11T06:45:00.000Z\",\"11782\",\"11786\",\"11722.6\",\"11763\",\"103492\",\"880.5217\"],[\"2020-08-11T06:30:00.000Z\",\"11787\",\"11804.1\",\"11722.7\",\"11782.1\",\"193878\",\"1647.3524\"]]";
//
//        JSONArray testArrray = JSON.parseArray(test2);
//        System.out.println(testArrray);
//        System.out.println(testArrray.toJSONString());

    }
}
