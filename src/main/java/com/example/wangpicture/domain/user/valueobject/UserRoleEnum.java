package com.example.wangpicture.domain.user.valueobject;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

/*用户角色枚举
* */

/**
 * @description: TODO
 * @author wang
 * @date  21:32
 * @version 1.0
 */
@Getter
public enum UserRoleEnum {
    USER("用户","user"),
    ADMIN("管理员","admin");

    private final String text;
    private final String value;


    UserRoleEnum(String text,String value){
        this.text = text;
        this.value = value;
    }

    
    public static UserRoleEnum getEnumByValue(String value){
        if(ObjUtil.isEmpty(value)){
            return null;
        }
        for (UserRoleEnum anEnum : UserRoleEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }
}
