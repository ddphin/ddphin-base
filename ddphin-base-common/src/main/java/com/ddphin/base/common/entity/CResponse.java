package com.ddphin.base.common.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * ClassName: CResponse
 * Function:  CResponse
 * Date:      2019/6/18 下午1:56
 * Author     ddphin
 * Version    V1.0
 */
@Data
public class CResponse<T> implements Serializable {
    private static final long serialVersionUID = -4505655308965878999L;
    //返回数据
    private T data;
    //返回码
    private Integer code;
    //返回描述
    private String message;

    public CResponse(T data, CMessage message, Object... args){
        this.code = message.getCode();
        this.message = String.format(message.getMessage(), args);
        this.data = data;
    }
}
