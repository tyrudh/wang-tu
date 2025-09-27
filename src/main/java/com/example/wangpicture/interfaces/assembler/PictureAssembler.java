package com.example.wangpicture.interfaces.assembler;

import cn.hutool.json.JSONUtil;
import com.example.wangpicture.domain.picture.entity.Picture;
import com.example.wangpicture.interfaces.dto.picture.PictureEditRequest;
import com.example.wangpicture.interfaces.dto.picture.PictureUpdateRequest;
import org.springframework.beans.BeanUtils;

/**
 * @author wang
 * @version 1.0
 * @description: 转化方法
 * @date 2025/9/27 18:35
 */

public class PictureAssembler {

    public static Picture toPictureEntity(PictureEditRequest request) {
        Picture picture = new Picture();
        BeanUtils.copyProperties(request, picture);
        // 注意将 list 转为 string
        picture.setTags(JSONUtil.toJsonStr(request.getTags()));
        return picture;
    }

    public static Picture toPictureEntity(PictureUpdateRequest request) {
        Picture picture = new Picture();
        BeanUtils.copyProperties(request, picture);
        // 注意将 list 转为 string
        picture.setTags(JSONUtil.toJsonStr(request.getTags()));
        return picture;
    }
}