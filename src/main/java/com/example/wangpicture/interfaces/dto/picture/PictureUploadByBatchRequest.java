package com.example.wangpicture.interfaces.dto.picture;

import lombok.Data;

/**
 * @author wang
 * @version 1.0
 * @description: TODO
 * @date 2025/9/13 20:16
 */
@Data
public class PictureUploadByBatchRequest {

    /**
     * 搜索词
     */
    private String searchText;
    /**
     * 名称前缀
     */
    private String namePrefix;

    /**
     * 抓取数量
     */
    private Integer count = 10;
}
