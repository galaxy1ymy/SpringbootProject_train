package com.example.train.business.service;

import cn.hutool.core.date.DateUtil;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.example.train.business.enums.RcoketMQTopicEnum;
import com.example.train.business.enums.RedisKeyPreEnum;
import com.example.train.business.req.ConfirmOrderDoReq;
import com.example.train.common.exception.BusinessException;
import com.example.train.common.exception.BusinessExceptionEnum;
import jakarta.annotation.Resource;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class BeforerConfirmOrderService {
    public static final Logger LOG= LoggerFactory.getLogger(ConfirmOrderService.class);

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private SkTokenService skTokenService;
    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @SentinelResource(value="beforeDoConfirm",blockHandler = "beforeDoConfirmBlock")
    public void beforeDoConfirm(ConfirmOrderDoReq req){

        //校验令牌余量
        boolean validSkToken = skTokenService.validSkToken(
                req.getDate(),
                req.getTrainCode(),
                req.getMemberId(),
                req.getTickets().size()

        );
        if(validSkToken){
            LOG.info("令牌校验通过");
        } else{
            LOG.info("令牌校验不通过");
            throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_SK_TOKEN_FAIL);
        }

        //购票
        String lockKey= RedisKeyPreEnum.CONFIRM_ORDER+"-"+ DateUtil.formatDate(req.getDate())+"-"+req.getTrainCode();
        Boolean setIfAbsent = redisTemplate.opsForValue().setIfAbsent(lockKey,lockKey, 5, TimeUnit.SECONDS);
        if(Boolean.TRUE.equals(setIfAbsent)){
            LOG.info("恭喜，抢到锁！lockKey:{}", lockKey);
        }else{
            //只是没抢到锁，不知道票是否卖完，抛出请稍后重试
            LOG.info("很遗憾，没抢到锁！lockKey:{}", lockKey);
            throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_LOCK_FAIL);
        }

        //发送MQ排队购票
        String reqJson= JSON.toJSONString(req);
        LOG.info("排队购票，发送mq开始，消息:{}", reqJson);
        rocketMQTemplate.convertAndSend(RcoketMQTopicEnum.CONFIRM_ORDER.getCode(), reqJson);
        LOG.info("排队购票，发送mq结束");
    }

    /**
     * 降级
     */
    public void beforeDoConfirmBlock(ConfirmOrderDoReq req, BlockException e) {
        LOG.info("购票请求被限流：{}", req);
        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_FLOW_EXCEPTION);
    }


}


