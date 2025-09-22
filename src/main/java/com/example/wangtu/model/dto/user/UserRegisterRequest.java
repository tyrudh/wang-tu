package com.example.wangtu.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**用户注册请求
 * @author wang
 * @version 1.0
 * @description: TODO
 * @date 2025/9/6 20:38
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 156235340815591824L;
    private String userAccount;
    private String userPassword;
    private String checkPassword;



}
