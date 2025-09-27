package com.example.wangpicture.infrastructure.api.imagesearch;

import com.example.wangpicture.infrastructure.api.imagesearch.model.ImageSearchResult;
import com.example.wangpicture.infrastructure.api.imagesearch.sub.GetImageFirstUrlApi;
import com.example.wangpicture.infrastructure.api.imagesearch.sub.GetImageListApi;
import com.example.wangpicture.infrastructure.api.imagesearch.sub.GetImagePageUrlApi;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author wang
 * @version 1.0
 * @description: TODO
 * @date 2025/9/19 13:40
 */

@Slf4j
public class ImageSearchApiFacade {

    /**
     * 搜索图片
     *
     * @param imageUrl
     * @return
     */
    public static List<ImageSearchResult> searchImage(String imageUrl) {
        String imagePageUrl = GetImagePageUrlApi.getImagePageUrl(imageUrl);
        String imageFirstUrl = GetImageFirstUrlApi.getImageFirstUrl(imagePageUrl);
        List<ImageSearchResult> imageList = GetImageListApi.getImageList(imageFirstUrl);
        return imageList;
    }

    public static void main(String[] args) {
        // 测试以图搜图功能
        String imageUrl = "https://img-baofun.zhhainiao.com/pcwallpaper_ugc_mobile/preview_jpg/82e40c5fb47fa98aacb47c305e4ee27c.jpg";
        List<ImageSearchResult> resultList = searchImage(imageUrl);
        System.out.println("结果列表" + resultList);
    }
}

