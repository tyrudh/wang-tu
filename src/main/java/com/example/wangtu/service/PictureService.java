package com.example.wangtu.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.wangtu.model.dto.picture.PictureQueryRequest;
import com.example.wangtu.model.dto.picture.PictureReviewRequest;
import com.example.wangtu.model.dto.picture.PictureUploadByBatchRequest;
import com.example.wangtu.model.dto.picture.PictureUploadRequest;
import com.example.wangtu.model.entity.Picture;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.wangtu.model.entity.User;
import com.example.wangtu.model.vo.PictureVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
* @author wang
* @description 针对表【picture(图片)】的数据库操作Service
* @createDate 2025-09-10 11:03:21
*/
public interface PictureService extends IService<Picture> {
    // 上传图片
    PictureVO uploadPicture(Object multipartFile,
                            PictureUploadRequest pictureUploadRequest,
                            User loginUser);

    //将查询请求转为 QueryWrapper 对象
    QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest);

    // 获取单张图片信息
    PictureVO getPictureVO(Picture picture, HttpServletRequest request);

    // 获取多张图片信息(分页)
    Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request);

    // 校验图片信息
    void validPicture(Picture picture);

    /**
     * 图片审核
     *
     * @param pictureReviewRequest
     * @param loginUser
     */
    void doPictureReview(PictureReviewRequest pictureReviewRequest, User loginUser);

    /**
     * 填充图片审核信息
     *
     * @param picture
     * @param loginUser
     */
    void fillReviewParams(Picture picture, User loginUser);

    /**
     * 批量抓取和创建图片
     *
     * @param pictureUploadByBatchRequest
     * @param loginUser
     * @return 成功创建的图片数
     */
    Integer uploadPictureByBatch(
            PictureUploadByBatchRequest pictureUploadByBatchRequest,
            User loginUser
    );
}
