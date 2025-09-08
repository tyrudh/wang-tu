package com.example.wangtu.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wang
 * @version 1.0
 * @description: TODO
 * @date 2025/9/7 22:14
 */

@Data
public class UserAddRequest implements Serializable {

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色: user, admin
     */
    private String userRole;

    private static final long serialVersionUID = 1L;
}