package com.example.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.example.train.business.domain.*;
import com.example.train.business.enums.ConfirmOrderStatusEnum;
import com.example.train.business.enums.SeatColEnum;
import com.example.train.business.enums.SeatTypeEnum;
import com.example.train.business.req.ConfirmOrderTicketReq;
import com.example.train.common.context.LoginMemberContext;
import com.example.train.common.exception.BusinessException;
import com.example.train.common.exception.BusinessExceptionEnum;
import com.example.train.common.resp.PageResp;
import com.example.train.common.util.SnowUtil;
import com.example.train.business.mapper.ConfirmOrderMapper;
import com.example.train.business.req.ConfirmOrderQueryReq;
import com.example.train.business.req.ConfirmOrderDoReq;
import com.example.train.business.resp.ConfirmOrderQueryResp;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Service
public class ConfirmOrderService {
    public static final Logger LOG= LoggerFactory.getLogger(ConfirmOrderService.class);

    @Resource
    private ConfirmOrderMapper confirmOrderMapper;
    @Resource
    private DailyTrainTicketService  dailyTrainTicketService ;
    @Resource
    private DailyTrainCarriageService dailyTrainCarriageService;
    @Resource
    private DailyTrainSeatService dailyTrainSeatService;
    @Resource
    private AfterConfirmOrderService afterConfirmOrderService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redissonClient;


    @Transactional
    public void save(ConfirmOrderDoReq req){
        DateTime now = DateTime.now();
        ConfirmOrder confirmOrder = BeanUtil.copyProperties(req, ConfirmOrder.class);
        if (ObjectUtil.isNull(confirmOrder.getId())) {
            confirmOrder.setId(SnowUtil.getSnowflakeNextId());
            confirmOrder.setCreateTime(now);
            confirmOrder.setUpdateTime(now);
            confirmOrderMapper.insert(confirmOrder);  // 新增数据
        } else {
            confirmOrder.setUpdateTime(now);
            confirmOrderMapper.updateByPrimaryKey(confirmOrder);  // 更新数据
        }
    }
    public PageResp<ConfirmOrderQueryResp> queryList(ConfirmOrderQueryReq req){
        ConfirmOrderExample confirmOrderExample = new ConfirmOrderExample();
        confirmOrderExample.setOrderByClause("id asc");
        ConfirmOrderExample.Criteria criteria = confirmOrderExample.createCriteria();

        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        List<ConfirmOrder> confirmOrderList = confirmOrderMapper.selectByExample(confirmOrderExample);

        PageInfo<ConfirmOrder> pageInfo = new PageInfo<>(confirmOrderList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        List<ConfirmOrderQueryResp> list = BeanUtil.copyToList(confirmOrderList, ConfirmOrderQueryResp.class);
        PageResp<ConfirmOrderQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }
    public void delete(Long id){
        confirmOrderMapper.deleteByPrimaryKey(id);
    }

    @SentinelResource(value="doConfirm",blockHandler = "doConfirmBlock")
    public void doConfirm(ConfirmOrderDoReq req){
        String lockKey= DateUtil.formatDate(req.getDate())+"-"+req.getTrainCode();
        Boolean setIfAbsent = redisTemplate.opsForValue().setIfAbsent(lockKey,lockKey, 5, TimeUnit.SECONDS);
        if(Boolean.TRUE.equals(setIfAbsent)){
            LOG.info("恭喜，抢到锁");
        }else{
            //只是没抢到锁，不知道票是否卖完，抛出请稍后重试
            LOG.info("很遗憾，没抢到锁");
            throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_LOCK_FAIL);
        }

       /* RLock lock=null;*/

        try{
            /*//使用Redisson,自带看门狗
            lock=redissonClient.getLock(lockKey);
            boolean tryLock=lock.tryLock(0,  TimeUnit.SECONDS);
            if(tryLock){
                LOG.info("恭喜，抢到锁");
                for(int i=0;i<30;i++){
                    Long expire=redisTemplate.opsForValue().getOperations().getExpire(lockKey);
                    LOG.info("锁过期时间还有：{}",expire);
                    Thread.sleep(1000);
                }
            }else{
                LOG.info("很遗憾，没抢到锁");
                throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_LOCK_FAIL);
            }*/

            //业务数据校验，同乘客同车次是否已买过等
            Date date=req.getDate();
            String trainCode=req.getTrainCode();
            String start= req.getStart();
            String end= req.getEnd();
            List<ConfirmOrderTicketReq> tickets=req.getTickets();

            //保存确认订单表。状态初始
            DateTime now = DateTime.now();
            ConfirmOrder confirmOrder = new ConfirmOrder();
            confirmOrder.setId(SnowUtil.getSnowflakeNextId());
            confirmOrder.setCreateTime(now);
            confirmOrder.setUpdateTime(now);
            confirmOrder.setMemberId(LoginMemberContext.getId());
            confirmOrder.setDate(date);
            confirmOrder.setTrainCode(trainCode);
            confirmOrder.setStart(start);
            confirmOrder.setEnd(end);
            confirmOrder.setDailyTrainTicketId(req.getDailyTrainTicketId());
            confirmOrder.setStatus(ConfirmOrderStatusEnum.INIT.getCode());
            confirmOrder.setTickets(JSON.toJSONString(tickets));
            confirmOrderMapper.insert(confirmOrder);

            //查出余票记录，需要得到真实的库存
            DailyTrainTicket dailyTrainTicket = dailyTrainTicketService.selectByUnique(date, trainCode, start, end);
            LOG.info("查出余票记录：{}", dailyTrainTicket);

            //预扣减余票数量，并判断余票是否足够
            reduceTickets(req, dailyTrainTicket);

            //最终的选座结果
            List<DailyTrainSeat> finalSeatList=new ArrayList<>();

            //计算相对第一个座位的偏移值
            //比如选择的是C1,D2，则偏移值是【0，5】
            //比如选择的是A1,B1,C1，则偏移值是【0，1，2】
            ConfirmOrderTicketReq ticketReq0= tickets.get(0);
            if(StrUtil.isNotBlank(ticketReq0.getSeat())){
                LOG.info("本次购票有选座");
                //查出本次选座的座位类型都有哪些，用于计算所选座位与第一个座位的偏移值
                List<SeatColEnum> colEnumList = SeatColEnum.getColsByType(ticketReq0.getSeatTypeCode());
                LOG.info("本次选座的座位类型包含的列：{}", colEnumList);
                //组成和前端两排选座一样的列表，用于作参照的座位列表
                //比如referSeatList={A1,C1,D1,F1,A2,C2,D2,F2}
                List<String> referSeatList= new ArrayList<>();
                for (int i = 1; i <=2; i++) {
                    for (SeatColEnum seatColEnum : colEnumList) {
                        referSeatList.add(seatColEnum.getCode()+i);
                    }
                }
                LOG.info("用于作参照的两排座位：{}", referSeatList);

                List<Integer> offsetList= new ArrayList<>();
                //绝对偏移值
                List<Integer> aboluteOffsetList= new ArrayList<>();
                for(ConfirmOrderTicketReq ticketReq: tickets){
                    int index = referSeatList.indexOf(ticketReq.getSeat());
                    aboluteOffsetList.add(index);
                }
                LOG.info("计算得到所有座位的绝对偏移值：{}", aboluteOffsetList);
                for (Integer index: aboluteOffsetList) {
                    int offset=index-aboluteOffsetList.get(0);
                    offsetList.add(offset);
                }
                LOG.info("计算得到相对第一个座位的偏移值：{}",offsetList);
                getSeat(finalSeatList,
                        date,
                        trainCode,
                        ticketReq0.getSeatTypeCode(),
                        ticketReq0.getSeat().split("")[0],
                        offsetList,
                        dailyTrainTicket.getStartIndex(),
                        dailyTrainTicket.getEndIndex());

            }else{
                LOG.info("本次购票没有选座");
                for(ConfirmOrderTicketReq ticketReq: tickets){
                    getSeat(finalSeatList,
                            date,
                            trainCode,
                            ticketReq.getSeatTypeCode(),
                            null,null,
                            dailyTrainTicket.getStartIndex(),
                            dailyTrainTicket.getEndIndex());
                }
            }
            //最终的选座
            LOG.info("最终的选座：{}", finalSeatList);

            //选中座位后事务处理（尽量短事务
            //座位表修改售卖情况sell
            //余票详情表修改余票
            //为会员增加购票记录
            //更新确认订单为成功
            try{
                afterConfirmOrderService.afterDoConfirm(dailyTrainTicket,finalSeatList, tickets,confirmOrder);
            }catch (Exception e){
                LOG.error("保存购票信息失效",e);
                throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_EXCEPTION);
            }
            //删除分布式锁
           /* LOG.info("购票流程结束，释放锁！lockKey:{}", lockKey);
            redisTemplate.delete(lockKey);*/
        /*}catch (InterruptedException e){
            LOG.info("购票异常",e);*/
        }finally {
            LOG.info("购票流程结束，释放锁！lockKey:{}", lockKey);
            redisTemplate.delete(lockKey);
            /*LOG.info("购票流程结束，释放锁！");
            if(null!=lock && lock.isHeldByCurrentThread()){
                lock.unlock();//释放锁
            }*/
        }
    }

    /**
     * 购票限流处理
     * */
    public void doConfirmBlock(ConfirmOrderDoReq req, BlockException e){
        LOG.info("购票请求被限流：{}", req);
        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_FLOW_EXCEPTION);
    }

    private void getSeat(List<DailyTrainSeat> finalSeatList,Date date, String trainCode, String seatType,
                         String column,List<Integer> offsetList ,Integer startIndex,Integer endIndex){
        List<DailyTrainSeat> getSeatList=new ArrayList<>();
        List<DailyTrainCarriage> carriageList = dailyTrainCarriageService.selectBySeatType(date, trainCode, seatType);
        LOG.info("共查出{}个符合条件的车厢", carriageList.size());

        //一个车厢一个车厢的获取座位数据，循环
        for(DailyTrainCarriage dailyTrainCarriage: carriageList){
            LOG.info("开始从车厢{}选座", dailyTrainCarriage.getIndex());
            getSeatList=new ArrayList<>();
            List<DailyTrainSeat> seatList = dailyTrainSeatService.selectByCarriage(date, trainCode, dailyTrainCarriage.getIndex());
            LOG.info("车厢{}的座位数：{}",dailyTrainCarriage.getIndex(), seatList.size());
            for(int i=0;i<seatList.size();i++){
                DailyTrainSeat dailyTrainSeat = seatList.get(i);
                Integer seatIndex = dailyTrainSeat.getCarriageSeatIndex();
                String col = dailyTrainSeat.getCol();
                //判断当前座位不能被选中
                boolean alreadyChooseFlag=false;
                for(DailyTrainSeat finalSeat : finalSeatList){
                    if(finalSeat.getId().equals(dailyTrainSeat.getId())){
                        alreadyChooseFlag=true;
                        break;
                    }
                }
                if(alreadyChooseFlag){
                    LOG.info("座位{}已被选中，不能重复选中，继续判断下一个座位", seatIndex);
                    continue;
                }

                //判断column，有值的话要对比列号
                if(StrUtil.isBlank(column)){
                    LOG.info("无选座");
                }else{
                    if(!column.equals(col)){
                        LOG.info("座位{}列值不对，继续判断下一个座位, 当前列值：{},目标列值：{}",
                                seatIndex,col,column);
                        continue;
                    }
                }

                boolean isChoose=calSell(dailyTrainSeat,startIndex,endIndex);
                if(isChoose){
                    LOG.info("选中座位");
                    getSeatList.add(dailyTrainSeat);
                }else{
                    continue;
                }
                //根据offset选剩下的座位
                boolean isGetAllOffsetSeat=true;
                if(CollUtil.isNotEmpty(offsetList)){
                    LOG.info("有偏移值:{},校验偏移的座位是否可选",offsetList);
                    for(int j=1;j<offsetList.size();j++){
                        Integer offset = offsetList.get(j);
                        //座位在库的索引是从1开始，所以要-1
                        /*int nextIndex= seatIndex+offset-1;*/
                        int nextIndex= i+offset;
                        //有选座时一定在同一个车厢
                        if(nextIndex >= seatList.size()){
                            LOG.info("座位{}不可选，偏移后的索引超出了这个车厢的座位数",nextIndex);
                            isGetAllOffsetSeat=false;
                            break;
                        }

                        DailyTrainSeat nextDailyTrainSeat = seatList.get(nextIndex);
                        boolean isChooseNext=calSell(nextDailyTrainSeat,startIndex,endIndex);
                        if(isChooseNext){
                            LOG.info("座位{}被选中",nextDailyTrainSeat.getCarriageSeatIndex());
                            getSeatList.add(nextDailyTrainSeat);
                        }else{
                            LOG.info("座位{}不可选",nextDailyTrainSeat.getCarriageSeatIndex());
                            isGetAllOffsetSeat=false;
                            break;

                        }
                    }

                }
                if(!isGetAllOffsetSeat){
                    //清空
                    getSeatList=new ArrayList<>();
                    continue;
                }

                //保存选好的座位
                finalSeatList.addAll(getSeatList);
                return;
            }
        }



    }

    /**
     * 计算座位在区间内是否可卖
     * 选中后，要计算购票后的sell
     */

    private boolean calSell(DailyTrainSeat dailyTrainSeat,Integer startIndex,Integer endIndex){
        String sell=dailyTrainSeat.getSell();
        String sellPart = sell.substring(startIndex, endIndex);
        if(Integer.parseInt(sellPart)>0){
            LOG.info("座位{}在本次车站区间{}~{}已售过票，不可选中该座位",
                    dailyTrainSeat.getCarriageSeatIndex(),startIndex,endIndex);
            return false;
        }else{
            LOG.info("座位{}在本次车站区间{}~{}未售过票，可选中该座位",
                    dailyTrainSeat.getCarriageSeatIndex(),startIndex,endIndex);
            //111
            String curSell = sellPart.replace('0', '1');
            //0111
            curSell=StrUtil.fillBefore(curSell,'0',endIndex);
            //01110
            curSell=StrUtil.fillAfter(curSell,'0',sell.length());
            //当前区间售票信息curSell与已售信息Sell按位与，即可得到该座位卖出此票后的售票详情
            int newSellInt = NumberUtil.binaryToInt(curSell)|
            NumberUtil.binaryToInt(sell);
            String newSell=NumberUtil.getBinaryStr(newSellInt);
            curSell=StrUtil.fillBefore(newSell,'0',sell.length());
            LOG.info("座位{}被选中，原售票信息：{}，车站区间：{}~{},即：{}，最终售票信息：{}",
                    dailyTrainSeat.getCarriageSeatIndex(),sell,startIndex,endIndex,curSell,newSell);
            dailyTrainSeat.setSell(newSell);
            return true;
        }

    }

    private static void reduceTickets(ConfirmOrderDoReq req, DailyTrainTicket dailyTrainTicket) {
        for (ConfirmOrderTicketReq ticketReq: req.getTickets()) {
            String seatTypeCode = ticketReq.getSeatTypeCode();
            SeatTypeEnum  seatTypeEnum = EnumUtil.getBy(SeatTypeEnum::getCode, seatTypeCode);
            switch(seatTypeEnum){
                case YDZ ->{
                    int countLeft= dailyTrainTicket.getYdz()-1;
                     if(countLeft<0){
                         throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                     dailyTrainTicket.setYdz(countLeft);
                }
                case EDZ ->{
                    int countLeft= dailyTrainTicket.getEdz()-1;
                     if(countLeft<0){
                         throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                     dailyTrainTicket.setEdz(countLeft);
                }
                case RW ->{
                    int countLeft= dailyTrainTicket.getRw()-1;
                     if(countLeft<0){
                         throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                     dailyTrainTicket.setRw(countLeft);
                }
                case YW ->{
                    int countLeft= dailyTrainTicket.getYw() -1;
                     if(countLeft<0){
                         throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                     dailyTrainTicket.setYw(countLeft);
                }

            }

        }
    }
}
