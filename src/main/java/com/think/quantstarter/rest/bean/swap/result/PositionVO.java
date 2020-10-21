package com.think.quantstarter.rest.bean.swap.result;

import lombok.Data;

import java.util.List;

/**
 * @author mpthink
 * @date 2020/10/21 15:06
 */
@Data
public class PositionVO {
    private String margin_mode;
    private String timestamp;
    private List<Position> holding;
}
