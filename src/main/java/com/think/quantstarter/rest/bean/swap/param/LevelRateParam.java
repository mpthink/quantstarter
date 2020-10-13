package com.think.quantstarter.rest.bean.swap.param;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class LevelRateParam extends JSONObject {

    /**
     * 1.LONG
     * 2.SHORT
     * 3.全仓杠杆
     */
    private String side;
    private String leverage;

}


