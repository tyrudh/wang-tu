package com.example.wangpicture.infrastructure.aop;

import com.example.wangpicture.application.service.UserApplicationService;
import com.example.wangpicture.infrastructure.annotation.AuthCheck;
import com.example.wangpicture.infrastructure.exception.BusinessException;
import com.example.wangpicture.infrastructure.exception.ErrorCode;
import com.example.wangpicture.domain.user.entity.User;
import com.example.wangpicture.domain.user.valueobject.UserRoleEnum;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author wang
 * @version 1.0
 * @description: TODO
 * @date 2025/9/7 16:10
 */
@Aspect
@Component
public class AuthInterceptor {
    @Resource
    private UserApplicationService userApplicationService;

    /**
    * @Description: 权限注解 
    * @Param: [joinPoint, authCheck]
    * @return: java.lang.Object
    * @Author: trudh
    * @Date: 2025/9/7
    **/
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        String mustRole = authCheck.mustRole();
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        // 获取当前登录用户
        User loginUser = userApplicationService.getLoginUser(request);
        UserRoleEnum mustUserRoleEnum = UserRoleEnum.getEnumByValue(mustRole);
        //如果不需要权限
        if (mustUserRoleEnum == null) {
            return joinPoint.proceed();
        }
        //以下的代码必须有权限才能通过
        UserRoleEnum loginUserRoleEnum = UserRoleEnum.getEnumByValue(loginUser.getUserRole());
        if (loginUserRoleEnum == null) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        //要求必须具有管理员权限，但是用户没有管理员权限，拒绝请求
        if (UserRoleEnum.ADMIN.equals(mustUserRoleEnum) && !UserRoleEnum.ADMIN.equals(loginUserRoleEnum)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return joinPoint.proceed();

    }
}
