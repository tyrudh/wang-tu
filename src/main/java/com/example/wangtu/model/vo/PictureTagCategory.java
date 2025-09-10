package com.example.wangtu.model.vo;

import lombok.Data;


import java.util.List;

/**
 * @author wang
 * @version 1.0
 * @description: 图片标签分类列表视图
 * @date 2025/9/10 20:05
 */
@Data
public class PictureTagCategory {
    /**
    * @Description:分类列表
    * @Param:
    * @return:
    * @Author: trudh
    * @Date: 2025/9/10
    **/
    private List<String> tagList;
    /**
    * @Description:标签列表
    * @Param:
    * @return:
    * @Author: trudh
    * @Date: 2025/9/10
    **/
    private List<String> categoryList;
}

