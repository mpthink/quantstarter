package com.think.quantstarter.dataCollect.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * bch 30分钟K线表
 * </p>
 *
 * @author hunter
 * @since 2020-09-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("bch_candles_30m")
public class BchCandles30m extends Model<BchCandles30m> {

    private static final long serialVersionUID=1L;

    /**
     * 系统时间
     */
    @TableId(value = "candle_time", type = IdType.INPUT)
    private String candleTime;

    /**
     * 开盘价格
     */
    @TableField("open")
    private Double open;

    /**
     * 最高价格
     */
    @TableField("high")
    private Double high;

    /**
     * 最低价格
     */
    @TableField("low")
    private Double low;

    /**
     * 收盘价格
     */
    @TableField("close")
    private Double close;

    /**
     * 交易量（按张折算）
     */
    @TableField("volume")
    private Double volume;

    /**
     * 交易量（按币折算）
     */
    @TableField("currency_volume")
    private Double currencyVolume;

    @TableField("gmt_create")
    private LocalDateTime gmtCreate;


    @Override
    protected Serializable pkVal() {
        return this.candleTime;
    }

}
