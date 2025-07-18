package com.example.train.business.service;


import com.example.train.business.domain.*;
import com.example.train.business.enums.ConfirmOrderStatusEnum;
import com.example.train.business.feign.MemberFeign;
import com.example.train.business.mapper.ConfirmOrderMapper;
import com.example.train.business.mapper.DailyTrainSeatMapper;

import com.example.train.business.mapper.cust.DailyTrainTicketMapperCust;
import com.example.train.business.req.ConfirmOrderTicketReq;
import com.example.train.common.context.LoginMemberContext;
import com.example.train.common.req.MemberTicketReq;
import com.example.train.common.resp.CommonResp;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


import java.util.Date;
import java.util.List;

@Service
public class AfterConfirmOrderService {
    public static final Logger LOG= LoggerFactory.getLogger(AfterConfirmOrderService.class);

    @Resource
    private DailyTrainSeatMapper dailyTrainSeatMapper;
    @Resource
    private DailyTrainTicketMapperCust dailyTrainTicketMapperCust;
    @Resource
    private MemberFeign memberFeign;
    @Resource
    private ConfirmOrderMapper confirmOrderMapper;

    /**
     *  选中座位后事务处理（尽量短事务
     *         座位表修改售卖情况sell
     *         余票详情表修改余票
     *         为会员增加购票记录
     *         更新确认订单为成功
     */
    //@Transactional
    /*@GlobalTransactional*/
    public void afterDoConfirm(DailyTrainTicket dailyTrainTicket, List<DailyTrainSeat> finalSeatList,
                               List<ConfirmOrderTicketReq> tickets,  ConfirmOrder confirmOrder) throws Exception {
       /* LOG.info("seata全局事务ID:{}", RootContext.getXID());*/
        for(int j=0;j<finalSeatList.size();j++){
            DailyTrainSeat dailyTrainSeat = finalSeatList.get(j);
            //只更新Sell值
            DailyTrainSeat seatForUpdate = new DailyTrainSeat();
            seatForUpdate.setId(dailyTrainSeat.getId());
            seatForUpdate.setSell(dailyTrainSeat.getSell());
            seatForUpdate.setUpdateTime(new Date());
            dailyTrainSeatMapper.updateByPrimaryKeySelective(seatForUpdate);

            //计算这个站卖出去后，影响了哪些站的余票库存
            //影响的库存：本次选座之前没卖过票的，和本次购买的区间有交集的区间
            Integer startIndex = dailyTrainTicket.getStartIndex();
            Integer endIndex = dailyTrainTicket.getEndIndex();
            char[] chars = seatForUpdate.getSell().toCharArray();
            Integer maxStartIndex=endIndex-1;
            Integer minEndIndex=startIndex+1;
            Integer minStartIndex=0;
            for (int i = startIndex-1; i >=0; i--) {
                char aChar=chars[i];
                if(aChar=='1'){
                    minStartIndex=i+1;
                    break;
                }
            }
            LOG.info("影响出发站区间："+minStartIndex+"~"+maxStartIndex);

            Integer maxEndIndex=seatForUpdate.getSell().length();
            for (int i = endIndex; i <seatForUpdate.getSell().length() ; i++) {
                char aChar=chars[i];
                if(aChar=='1'){
                    maxEndIndex=i;
                    break;
                }
            }
            LOG.info("影响到达站区间："+minEndIndex+"~"+maxEndIndex);

            dailyTrainTicketMapperCust.updateCountBySell(
                    dailyTrainSeat.getDate(),
                    dailyTrainSeat.getTrainCode(),
                    dailyTrainSeat.getSeatType(),
                    minStartIndex,
                    maxStartIndex,
                    minEndIndex,
                    maxEndIndex);

            //调用会员服务接口，为会员增加一张车票
            MemberTicketReq memberTicketReq = new MemberTicketReq();
            memberTicketReq.setMemberId(LoginMemberContext.getId());
            memberTicketReq.setPassengerId(tickets.get(j).getPassengerId());
            memberTicketReq.setPassengerName(tickets.get(j).getPassengerName());
            memberTicketReq.setTrainDate(dailyTrainTicket.getDate());
            memberTicketReq.setTrainCode(dailyTrainTicket.getTrainCode());
            memberTicketReq.setCarriageIndex(dailyTrainSeat.getCarriageIndex());
            memberTicketReq.setSeatRow(dailyTrainSeat.getRow());
            memberTicketReq.setSeatCol(dailyTrainSeat.getCol());
            memberTicketReq.setStartStation(dailyTrainTicket.getStart());
            memberTicketReq.setStartTime(dailyTrainTicket.getStartTime());
            memberTicketReq.setEndStation(dailyTrainTicket.getEnd());
            memberTicketReq.setEndTime(dailyTrainTicket.getEndTime());
            memberTicketReq.setSeatType(dailyTrainSeat.getSeatType());
            CommonResp<Object> commonResp=memberFeign.save(memberTicketReq);
            LOG.info("调用member接口，返回：{}",commonResp);

            //更新订单状态为成功
            ConfirmOrder confirmOrderForUpdate = new ConfirmOrder();
            confirmOrderForUpdate.setId(confirmOrder.getId());
            confirmOrderForUpdate.setUpdateTime(new Date());
            confirmOrderForUpdate.setStatus(ConfirmOrderStatusEnum.SUCCESS.getCode());
            confirmOrderMapper.updateByPrimaryKeySelective(confirmOrderForUpdate);

            //模拟调用方出现异常
            /*Thread.sleep(10000);*/
            /*if(1==1){
                throw new Exception("测试异常");
            }*/
        }
    }

}
