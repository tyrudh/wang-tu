package com.example.wangpicture.interfaces.dto.user;

import lombok.Data;

import java.io.Serializable;

/**用户登录请求
 * @author wang
 * @version 1.0
 * @description: TODO
 * @date 2025/9/6 20:38
 */
@Data
public class UserLoginRequest implements Serializable {


    private static final long serialVersionUID = 2289295525295437575L;
    private String userAccount;
    private String userPassword;

}
