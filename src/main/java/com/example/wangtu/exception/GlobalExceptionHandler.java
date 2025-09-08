package com.example.wangtu.exception;


import com.example.wangtu.common.BaseResponse;
import com.example.wangtu.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/*全局异常处理器
* */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    //自定义的异常
    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException businessException) {
        log.error("BusinessException: ", businessException);
        return ResultUtils.error(businessException.getCode(), businessException.getMessage());
    }
    /** 
     * @description: 测试返回值 
     * @param: e 
     * @return: com.example.wangtu.common.BaseResponse<?> 
     * @author wang
     * @date:  21:32
     */  
    //更常见的异常
    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> businessExceptionHandler(RuntimeException e) {
        log.error("BusinessException: ", e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "系统错误");
    }
}
