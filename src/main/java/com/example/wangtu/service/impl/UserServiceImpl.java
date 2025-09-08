package com.example.wangtu.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.wangtu.constant.UserConstant;
import com.example.wangtu.exception.BusinessException;
import com.example.wangtu.exception.ErrorCode;
import com.example.wangtu.model.dto.user.UserQueryRequest;
import com.example.wangtu.model.entity.User;
import com.example.wangtu.model.enums.UserRoleEnum;
import com.example.wangtu.model.vo.LoginUserVo;
import com.example.wangtu.model.vo.UserVO;
import com.example.wangtu.service.UserService;
import com.example.wangtu.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author wang
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2025-09-06 19:25:27
*/

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    /**
    * @Description: 用户登录逻辑 
    * @Param: [userAccount, userPassword, checkPassword]
    * @return: long
    * @Author: trudh
    * @Date: 2025/9/6
    **/
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        //1检验参数

        if (userAccount == null || userPassword == null || checkPassword == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户密码过短");
        }
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"两次输入的密码不一致");
        }
        //2检测用户账号是否与数据库中的重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        Long accountNum  = this.baseMapper.selectCount(queryWrapper);
        if (accountNum > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号重复");
        }
        //3加密密码
        String encryptPassword = getEncryptPassword(userPassword);
        //4插入数据到数据库中
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUserName("无名");
        user.setUserRole(UserRoleEnum.USER.getValue());
        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"注册失败,数据库保存错误");
        }
        return user.getId();
    }

    /**
    * @Description: 获取加密密码
    * @Param: [userPassword]
    * @return: java.lang.String
    * @Author: trudh
    * @Date: 2025/9/6
    **/
    @Override
    public String getEncryptPassword(String userPassword) {
        //加盐操作
        final String salt = "tu";

        return DigestUtils.md5DigestAsHex((userPassword+salt).getBytes());
    }

    @Override
    public LoginUserVo userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //1检验参数
        if (StrUtil.hasBlank(userAccount, userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户账号错误");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户密码错误");
        }
        //2对密码进行加密
        String encryptPassword = getEncryptPassword(userPassword);
        //3查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = this.baseMapper.selectOne(queryWrapper);
        //4用户不存在则返回错误
        if (user == null) {
            log.info("用户不存在");
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户不存在");
        }
        //5返回用户信息，并且保存用户的登录态
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, user);

        return this.getLoginUserVo(user);
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        Object userOj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User currentUser = (User) userOj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        //从数据库中获取（追求性能就可以忽略）
        currentUser = this.getById(currentUser.getId());
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    /**
    * @Description: 获取脱敏后的用户信息
    * @Param: [user]
    * @return: com.example.wangtu.model.vo.LoginUserVo
    * @Author: trudh
    * @Date: 2025/9/7
    **/
    @Override
    public LoginUserVo getLoginUserVo(User user) {
        LoginUserVo loginUserVo = new LoginUserVo();
        BeanUtils.copyProperties(user, loginUserVo);
        return loginUserVo;
    }

    @Override
    public Boolean userLoginOut(HttpServletRequest request) {
        Object userOj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (userOj == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"未登录");
        }
        //移除登录态
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
        return true;
    }


    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVOList(List<User> userList) {
        if (CollUtil.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String userAccount = userQueryRequest.getUserAccount();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjUtil.isNotNull(id), "id", id);
        queryWrapper.eq(StrUtil.isNotBlank(userRole), "userRole", userRole);
        queryWrapper.like(StrUtil.isNotBlank(userAccount), "userAccount", userAccount);
        queryWrapper.like(StrUtil.isNotBlank(userName), "userName", userName);
        queryWrapper.like(StrUtil.isNotBlank(userProfile), "userProfile", userProfile);
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }

}




