package com.example.train.common.controller;


import cn.hutool.core.util.StrUtil;
import com.example.train.common.exception.BusinessException;
import com.example.train.common.resp.CommonResp;
import io.seata.core.context.RootContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


@ControllerAdvice
public class ControllerExceptionHandle {
    public static final Logger LOG=LoggerFactory.getLogger(ControllerExceptionHandle.class);
    @ExceptionHandler(value= Exception.class)
    @ResponseBody
    public CommonResp exceptionHandler(Exception e) throws  Exception{
        /*LOG.info("seata全局事务ID：{}", RootContext.getXID());
        //如果是在一次全局事务里出了异常，就不要包装返回值，将异常抛给调用方，让调用方回滚事务
        if(StrUtil.isNotBlank(RootContext.getXID())){
            throw e;
        }*/
        CommonResp commonResp=new CommonResp();
        LOG.error("系统异常",e);
        commonResp.setSuccess(false);
        commonResp.setMessage("系统出现异常，请联系管理员");
        return commonResp;
    }

//    业务异常
    @ExceptionHandler(value= BusinessException.class)
    @ResponseBody
    public CommonResp exceptionHandler(BusinessException e){
        CommonResp commonResp=new CommonResp();
        LOG.error("业务异常：{}",e.getE().getDesc());
        commonResp.setSuccess(false);
        commonResp.setMessage(e.getE().getDesc());
    //        commonResp.setMessage(e.getMessage());
        return commonResp;
    }
    //校验异常
    @ExceptionHandler(value= BindException.class)
    @ResponseBody
    public CommonResp exceptionHandler(BindException e){
        CommonResp commonResp=new CommonResp();
        LOG.error("校验异常：{}",e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        commonResp.setSuccess(false);
        commonResp.setMessage(e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        return commonResp;
    }

}
