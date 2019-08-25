package com.ddphin.base.oss.service;

import java.io.InputStream;

/**
 * ClassName: AliOssService
 * Function:  AliOssService
 * Date:      2019/6/29 下午4:43
 * Author     ddphin
 * Version    V1.0
 */
public interface AliOssService {
    String transferImg(String fileName, InputStream is) throws Exception;
    String transferImg(String fileName, String filePath) throws Exception;

    String transferOnlineImg(String fileName, String url) throws Exception;
}
