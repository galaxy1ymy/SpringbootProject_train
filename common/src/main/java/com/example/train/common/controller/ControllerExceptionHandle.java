package com.example.train.common.controller;


import com.example.train.common.resp.CommonResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class ControllerExceptionHandle {
    public static final Logger LOG=LoggerFactory.getLogger(ControllerExceptionHandle.class);
    @ExceptionHandler(value=Exception.class)
    @ResponseBody
    public CommonResp exceptionHandler(Exception e) throws Exception{
        CommonResp commonResp=new CommonResp();
        LOG.error("系统异常",e);
        commonResp.setSuccess(false);
//        commonResp.setMessage("系统出现异常，请联系管理员");
        commonResp.setMessage(e.getMessage());
        return commonResp;
    }

}
