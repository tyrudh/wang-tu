package com.example.wangtu.model.dto.spaceuser;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wang
 * @version 1.0
 * @description: TODO
 * @date 2025/9/22 16:08
 */

@Data
public class SpaceUserEditRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 空间角色：viewer/editor/admin
     */
    private String spaceRole;

    private static final long serialVersionUID = 1L;
}
