package com.ddphin.base.common.entity;

import lombok.Data;

/**
 * ClassName: CException
 * Function:  CException
 * Date:      2019/6/18 下午2:03
 * Author     ddphin
 * Version    V1.0
 */
@Data
public class CException extends RuntimeException {//AuthenticationException
    private static final long serialVersionUID = -6370612186038915645L;

    private Integer code;
    private Object data;

    public CException(Object data, CMessage message, Object... args) {
        super(String.format(message.getMessage(), args));

        this.setData(data);
        this.setCode(message.getCode());
    }
}
