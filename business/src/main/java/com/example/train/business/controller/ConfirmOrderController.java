package com.example.train.business.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.example.train.business.req.ConfirmOrderDoReq;
import com.example.train.business.service.BeforerConfirmOrderService;
import com.example.train.common.exception.BusinessExceptionEnum;
import com.example.train.common.resp.CommonResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import com.example.train.common.context.LoginMemberContext;



@RestController
@RequestMapping("/confirm-order")
public class ConfirmOrderController {

    public static final Logger LOG= LoggerFactory.getLogger(ConfirmOrderController.class);

    @Resource
    private BeforerConfirmOrderService beforerConfirmOrderService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @SentinelResource(value="confirmOrderDo",blockHandler = "doConfirmBlock")
    @PostMapping("/do")
    public CommonResp<Object> doConfirm(@Valid @RequestBody ConfirmOrderDoReq req) {

        //图形验证码校验
        String imageCode = req.getImageCode();
        String imageCodeToken = req.getImageCodeToken();
        String imageCodeRedis = redisTemplate.opsForValue().get(imageCodeToken);
        LOG.info("从redis中获取到的验证码：{}",imageCodeRedis);
        if(ObjectUtils.isEmpty(imageCodeRedis)){
            return new CommonResp<>(false,"验证码已过期",null);
        }
        //校验验证码码，大小写忽略
        if(!imageCodeRedis.equalsIgnoreCase(imageCode)){
            return new CommonResp<>(false,"验证码错误",null);
        }else{
            redisTemplate.delete(imageCodeToken);
        }

        Long memberId = LoginMemberContext.getId(); // 直接拿 ID，不用 LoginMember 对象

        req.setMemberId(memberId); // 设置进请求对象中

        beforerConfirmOrderService.beforeDoConfirm(req);
        return new CommonResp<>();
    }

    /**
     * 购票限流处理
     * */
    public CommonResp<Object> doConfirmBlock(ConfirmOrderDoReq req, BlockException e){
        LOG.info("购票请求被限流：{}", req);
        /*throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_FLOW_EXCEPTION);*/
        CommonResp<Object> commonResp = new CommonResp<>();
        commonResp.setSuccess( false);
        commonResp.setMessage(BusinessExceptionEnum.CONFIRM_ORDER_FLOW_EXCEPTION.getDesc());
        return commonResp;
    }


}
