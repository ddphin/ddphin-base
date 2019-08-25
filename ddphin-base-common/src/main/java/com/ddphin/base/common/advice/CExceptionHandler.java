package com.ddphin.base.common.advice;

import com.ddphin.base.common.entity.CResponse;
import lombok.extern.slf4j.Slf4j;
import com.ddphin.base.common.entity.CException;
import com.ddphin.base.common.entity.CMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * ClassName: ExceptionHandler
 * Function:  ExceptionHandler
 * Date:      2019/6/18 下午2:33
 * Author     ddphin
 * Version    V1.0
 */
@Slf4j
@RestControllerAdvice
public class CExceptionHandler {

    @ExceptionHandler(CException.class)
    public ResponseEntity<CResponse> handlerMException(CException me){
        log.error(me.getMessage(), me);
        CResponse response = new CResponse<>(null, new CMessage(me.getCode(), me.getMessage()));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CResponse> handlerBindException(MethodArgumentNotValidException me){
        log.error(me.getMessage(), me);
        CResponse response = new CResponse(null, CMessage.VALID_BIND_EXCEPTION, me.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CResponse> handlerException(Exception me){
        log.error(me.getMessage(), me);
        CResponse response = new CResponse(null, CMessage.UNKNOWN_EXCEPTION, me.getMessage());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
