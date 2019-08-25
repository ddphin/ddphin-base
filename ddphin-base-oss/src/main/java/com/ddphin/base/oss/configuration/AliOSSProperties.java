package com.ddphin.base.oss.configuration;

import lombok.Data;

/**
 * ClassName: AliOSSProperties
 * Function:  AliOSSProperties
 * Date:      2019/6/21 下午2:37
 * Author     ddphin
 * Version    V1.0
 */
@Data
public class AliOSSProperties {
    private String endpoint;
    private String appKeyId;
    private String appKeySecret;
    private String imgBucket;
    private String imgHome;
    private String imgCdn;
}
