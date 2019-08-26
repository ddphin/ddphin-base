package com.ddphin.base.common.entity;

import lombok.Data;

/**
 * ClassName: CMessage
 * Function:  CMessage
 * Date:      2019/6/18 下午2:01
 * Author     ddphin
 * Version    V1.0
 */
@Data
public class CMessage {
    public final static CMessage SUCCESS = new CMessage(0, "${COMMON_SUCCESS}");
    public final static CMessage UNKNOWN_EXCEPTION = new CMessage(100000, "${COMMON_UNKNOWN_ERROR}:{0}");
    public final static CMessage VALID_BIND_EXCEPTION = new CMessage(110000, "{0}");

    private Integer code;
    private String message;

    public CMessage(Integer code, String message) {
        this.code = code;
        this.message = message.replaceAll("\\{\\d+\\}", "%s");
    }
}
