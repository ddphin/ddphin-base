package com.ddphin.base.oss.controller;

import com.ddphin.base.oss.service.AliOssService;
import com.ddphin.base.common.entity.CMessage;
import com.ddphin.base.common.entity.CResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * ClassName: CFileController
 * Function:  CFileController
 * Date:      2019/6/17 下午8:32
 * Author     ddphin
 * Version    V1.0
 */

@RestController
@RequestMapping("/oss/upload")
public class AliOssController {

    private AliOssService service;

    public AliOssController(AliOssService service) {
        this.service = service;
    }

    @PostMapping("/image")
    public ResponseEntity<CResponse> upload(MultipartFile file) throws Exception {
        String url = null;
        if (null != file) {
            url = service.transferImg(UUID.randomUUID().toString(), file.getInputStream());
        }

        CResponse response = new CResponse(url, CMessage.SUCCESS);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
