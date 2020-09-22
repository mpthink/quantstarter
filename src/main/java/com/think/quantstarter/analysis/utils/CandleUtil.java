package com.think.quantstarter.analysis.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mpthink
 * @date 2020/9/11 16:23
 */
public class CandleUtil {

    /**
     * MA: 英文(Moving average)的简写，叫移动平均线指标，算法最简单的一个指标
     * 计算方式：
     * 1.N日MA=N日收市价的总和/N(即算术平均数)
     * 2.要设置多条移动平均线，一般参数设置为N1=5,N2=10,N3=20,N4=60,N5=120,N6=250
     */



    /**
     * Calculate EMA,
     *
     * @param list
     *            :Price list to calculate，the first at head, the last at tail.
     * @return
     */
    public static final Double getEMA(final List<Double> list, final int number) {
        // 开始计算EMA值，
        Double k = 2.0 / (number + 1.0);// 计算出序数
        Double ema = list.get(0);// 第一天ema等于当天收盘价
        for (int i = 1; i < list.size(); i++) {
            // 第二天以后，当天收盘 收盘价乘以系数再加上昨天EMA乘以系数-1
            ema = list.get(i) * k + ema * (1 - k);
        }
        return (double)Math.round(ema*100)/100;
    }

    public static void main(String[] args) {
        List<Double> doubleList = new ArrayList<>();
        doubleList.add(10697.6);
        doubleList.add(10700.2);
        doubleList.add(10716.0);
        doubleList.add(10721.4);
        doubleList.add(10723.8);


        doubleList.add(10720.1);
        doubleList.add(10708.9);
        doubleList.add(10703.3);
        doubleList.add(10698.5);
        doubleList.add(10685.0);

        System.out.println(getEMA(doubleList, 10));

    }

}
