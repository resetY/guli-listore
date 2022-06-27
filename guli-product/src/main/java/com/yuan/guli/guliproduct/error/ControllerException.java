package com.yuan.guli.guliproduct.error;

import com.yuan.common.exception.CodeEnume;
import com.yuan.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;

/**
 *  告诉指定哪个包下需要处理异常
 * */
@Slf4j //日志
@RestControllerAdvice(basePackages = "com.yuan.guli.guliproduct.controller") //异常数据也要json返回
public class ControllerException {

    @ExceptionHandler(Exception.class)
    public R handleVaildExpection(MethodArgumentNotValidException e){
        HashMap<String,String> errorMap = new HashMap<>();
        log.error("数据校验出现问题："+e.getMessage());
       log.error("异常类型："+e.getClass());
        BindingResult result = e.getBindingResult();
        result.getFieldErrors().forEach((error -> {
            String message = error.getDefaultMessage();
            String field = error.getField();
            errorMap.put(field,message);
        }));
        return  R.error(CodeEnume.VAILD_EXCEPTION.getCode(),CodeEnume.VAILD_EXCEPTION.getMessage()).put("data",errorMap);
    }


    //公共异常：
    @ExceptionHandler(Throwable.class)
    public R handlerException(Throwable throwable){
        System.out.println(throwable);
        return R.error(CodeEnume.UNKNON_EXCEPTION.getCode(),CodeEnume.UNKNON_EXCEPTION.getMessage());
    }
        }
