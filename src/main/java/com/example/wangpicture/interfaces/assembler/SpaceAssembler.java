package com.example.wangpicture.interfaces.assembler;

import com.example.wangpicture.domain.space.entity.Space;
import com.example.wangpicture.interfaces.dto.space.SpaceAddRequest;
import com.example.wangpicture.interfaces.dto.space.SpaceEditRequest;
import com.example.wangpicture.interfaces.dto.space.SpaceUpdateRequest;
import org.springframework.beans.BeanUtils;

/**
 * @author wang
 * @version 1.0
 * @description: TODO
 * @date 2025/9/27 19:31
 */

public class SpaceAssembler {

    public static Space toSpaceEntity(SpaceAddRequest request) {
        Space space = new Space();
        BeanUtils.copyProperties(request, space);
        return space;
    }

    public static Space toSpaceEntity(SpaceUpdateRequest request) {
        Space space = new Space();
        BeanUtils.copyProperties(request, space);
        return space;
    }

    public static Space toSpaceEntity(SpaceEditRequest request) {
        Space space = new Space();
        BeanUtils.copyProperties(request, space);
        return space;
    }
}
