package com.example.wangpicture.interfaces.vo.user;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author wang
 * @version 1.0
 * @description: TODO
 * @date 2025/9/7 22:25
 */
@Data
public class UserVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色：user/admin
     */
    private String userRole;

    /**
     * 创建时间
     */
    private Date createTime;

    private static final long serialVersionUID = 1L;
}
