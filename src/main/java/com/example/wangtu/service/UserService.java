package com.example.wangtu.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.wangtu.model.dto.user.UserQueryRequest;
import com.example.wangtu.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.wangtu.model.vo.LoginUserVo;
import com.example.wangtu.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author wang
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2025-09-06 19:25:27
*/
public interface UserService extends IService<User> {


    long userRegister(String userAccount,String userPassword,String checkPassword);

    /**
    * @Description: 获取加密后的密码 
    * @Param: [userPassword]
    * @return: java.lang.String
    * @Author: trudh
    * @Date: 2025/9/7
    **/
    String getEncryptPassword(String userPassword);

    /**
    * @Description: 用户登录方法
    * @Param:
    * @return: 脱敏之后的用户信息
    * @Author: trudh
    * @Date: 2025/9/7
    **/
    LoginUserVo userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
    * @Description: 获取当前用户
    * @Param: [user]
    * @return: com.example.wangtu.model.vo.LoginUserVo
    * @Author: trudh
    * @Date: 2025/9/7
    **/
    User getLoginUser(HttpServletRequest request);

    LoginUserVo getLoginUserVo(User user);

    /**
     * @Description: 用户注销登录
     * @Param: [request]
     * @return: com.example.wangtu.model.entity.User
     * @Author: trudh
     * @Date: 2025/9/7
     **/
    Boolean userLoginOut(HttpServletRequest request);

    UserVO getUserVO(User user);

    List<UserVO> getUserVOList(List<User> userList);

    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);
}
