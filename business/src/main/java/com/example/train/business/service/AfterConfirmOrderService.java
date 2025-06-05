package com.example.train.business.service;


import com.example.train.business.domain.*;
import com.example.train.business.mapper.DailyTrainSeatMapper;

import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class AfterConfirmOrderService {
    public static final Logger LOG= LoggerFactory.getLogger(AfterConfirmOrderService.class);

    @Resource
    private DailyTrainSeatMapper dailyTrainSeatMapper;

    /**
     *  选中座位后事务处理（尽量短事务
     *         座位表修改售卖情况sell
     *         余票详情表修改余票
     *         为会员增加购票记录
     *         更新确认订单为成功
     */
    @Transactional
    public void afterDoConfirm(List<DailyTrainSeat> finalSeatList) {
        for(DailyTrainSeat dailyTrainSeat:finalSeatList){
            //只更新Sell值
            DailyTrainSeat seatForUpdate = new DailyTrainSeat();
            seatForUpdate.setId(dailyTrainSeat.getId());
            seatForUpdate.setSell(dailyTrainSeat.getSell());
            seatForUpdate.setUpdateTime(new Date());
            dailyTrainSeatMapper.updateByPrimaryKeySelective(seatForUpdate);

        }
    }

}
