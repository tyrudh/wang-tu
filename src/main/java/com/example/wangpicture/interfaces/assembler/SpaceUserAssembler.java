package com.example.wangpicture.interfaces.assembler;

import com.example.wangpicture.domain.space.entity.SpaceUser;
import com.example.wangpicture.interfaces.dto.spaceuser.SpaceUserAddRequest;
import com.example.wangpicture.interfaces.dto.spaceuser.SpaceUserEditRequest;
import org.springframework.beans.BeanUtils;

/**
 * @author wang
 * @version 1.0
 * @description: TODO
 * @date 2025/9/27 19:31
 */

public class SpaceUserAssembler {

    public static SpaceUser toSpaceUserEntity(SpaceUserAddRequest request) {
        SpaceUser spaceUser = new SpaceUser();
        BeanUtils.copyProperties(request, spaceUser);
        return spaceUser;
    }

    public static SpaceUser toSpaceUserEntity(SpaceUserEditRequest request) {
        SpaceUser spaceUser = new SpaceUser();
        BeanUtils.copyProperties(request, spaceUser);
        return spaceUser;
    }
}
