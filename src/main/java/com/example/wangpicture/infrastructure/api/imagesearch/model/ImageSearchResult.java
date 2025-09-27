package com.example.wangpicture.infrastructure.api.imagesearch.model;

import lombok.Data;

/**
 * @author wang
 * @version 1.0
 * @description: TODO
 * @date 2025/9/19 12:55
 */
@Data
public class ImageSearchResult {

    /**
     * 缩略图地址
     */
    private String thumbUrl;

    /**
     * 来源地址
     */
    private String fromUrl;
}
