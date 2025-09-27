package com.example.wangpicture.infrastructure.exception;
/*
* 异常处理工具类
* */
public class ThrowUtils {

    //条件成立则抛异常
    public static void throwIf(boolean condition,RuntimeException runtimeException){
        if(condition){
            throw runtimeException;
        }
    }
    //方法重载
    public static void throwIf(boolean condition,ErrorCode errorCode){
        throwIf(condition,new BusinessException(errorCode));
    }

    //方法重载
    public static void throwIf(boolean condition,ErrorCode errorCode,String message){
        throwIf(condition,new BusinessException(errorCode,message));
    }
}
