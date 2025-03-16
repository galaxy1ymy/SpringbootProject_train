package com.example.train.common.exception;

public class BusinessException extends RuntimeException{
    private BusinessExceptionEnum e;

    public BusinessException(BusinessExceptionEnum e) {
        this.e = e;
    }

    public BusinessExceptionEnum getE() {
        return e;
    }

    public void setAnEnum(BusinessExceptionEnum e) {
        this.e = e;
    }


}
