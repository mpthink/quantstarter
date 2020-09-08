package com.think.quantstarter.dataCollect.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.think.quantstarter.dataCollect.entity.EthCandles1d;
import com.think.quantstarter.dataCollect.mapper.EthCandles1dMapper;
import com.think.quantstarter.dataCollect.service.IEthCandles1dService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * eth 1天K线表 服务实现类
 * </p>
 *
 * @author hunter
 * @since 2020-09-07
 */
@Service
public class EthCandles1dServiceImpl extends ServiceImpl<EthCandles1dMapper, EthCandles1d> implements IEthCandles1dService {

}
