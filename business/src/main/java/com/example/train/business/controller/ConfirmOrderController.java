package com.example.train.business.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.example.train.business.req.ConfirmOrderDoReq;
import com.example.train.business.service.ConfirmOrderService;
import com.example.train.common.exception.BusinessExceptionEnum;
import com.example.train.common.resp.CommonResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/confirm-order")
public class ConfirmOrderController {

    public static final Logger LOG= LoggerFactory.getLogger(ConfirmOrderController.class);

    @Resource
    private ConfirmOrderService confirmOrderService;

    @SentinelResource(value="confirmOrderDo",blockHandler = "doConfirmBlock")
    @PostMapping("/do")
    public CommonResp<Object> doConfirm(@Valid @RequestBody ConfirmOrderDoReq req) {
        confirmOrderService.doConfirm(req);
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
