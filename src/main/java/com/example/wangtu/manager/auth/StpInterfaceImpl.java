package com.example.wangtu.manager.auth;

import cn.dev33.satoken.stp.StpInterface;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wang
 * @version 1.0
 * @description: 自定义权限加载接口实现类
 * @date 2025/9/22 21:21
 */
@Component
public class StpInterfaceImpl implements StpInterface {

    // 默认是/api
    @Value("${server.servlet.context-path}")
    private String contextPath;

    /**
    * @Description: 返回一个账号所拥有的权限集合
    * @Param: [o, s]
    * @return: java.util.List<java.lang.String>
    * @Author: trudh
    * @Date: 2025/9/22
    **/
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return new ArrayList<>();
    }

    /**
    * @Description: 本项目中不使用，返回一个账号所拥有的角色集合
    * @Param: [o, s]
    * @return: java.util.List<java.lang.String>
    * @Author: trudh
    * @Date: 2025/9/22
    **/
    @Override
    public List<String> getRoleList(Object o, String s) {
        return List.of();
    }
    /**
    * @Description: 从请求中获取上下文对象todo
    * @Param: 
    * @return: 
    * @Author: trudh
    * @Date: 2025/9/22
    **/
    private SpaceUserAuthContext getAuthContextByRequest(){


        return null;
    }
    
    
}
