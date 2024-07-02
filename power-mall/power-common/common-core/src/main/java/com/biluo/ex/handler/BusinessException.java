package com.biluo.ex.handler;

import com.biluo.constant.BusinessEnum;
import lombok.Getter;

/**
 * 自定义业务异常类
 */
@Getter
public class BusinessException extends RuntimeException{
    private final BusinessEnum businessEnum;

    public BusinessException(BusinessEnum businessEnum) {
        super(businessEnum.getMsg());
        this.businessEnum = businessEnum;
    }

    public BusinessException(BusinessEnum businessEnum, Throwable cause) {
        super(businessEnum.getMsg(), cause);
        this.businessEnum = businessEnum;
    }
}
