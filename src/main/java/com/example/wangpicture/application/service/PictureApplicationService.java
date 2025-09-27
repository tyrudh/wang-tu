package com.example.wangpicture.application.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.wangpicture.infrastructure.api.aliyunai.model.CreateOutPaintingTaskResponse;
import com.example.wangpicture.interfaces.dto.picture.*;
import com.example.wangpicture.domain.picture.entity.Picture;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.wangpicture.domain.user.entity.User;
import com.example.wangpicture.interfaces.vo.picture.PictureVO;
import org.springframework.scheduling.annotation.Async;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author wang
* @description 针对表【picture(图片)】的数据库操作Service
* @createDate 2025-09-10 11:03:21
*/
public interface PictureApplicationService extends IService<Picture> {
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


    public void validPicture(Picture picture);

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


    void checkPicture(User loginUser, Picture picture);

    /**
    * @Description: 图片的删除
    * @Param: [pictureId, loginUser]
    * @return: void
    * @Author: trudh
    * @Date: 2025/9/17
    **/
    void deletePicture(long pictureId, User loginUser);

    /**
    * @Description: 更新图片 
    * @Param: [pictureEditRequest, loginUser]
    * @return: void
    * @Author: trudh
    * @Date: 2025/9/17
    **/
    void editPicture(PictureEditRequest pictureEditRequest, User loginUser);

    @Async
    void clearPictureFile(Picture oldPicture);

    /**
    * @Description: 按照图片的主色调进行搜索
    * @Param: [spaceId, picColor, loginUser]
    * @return: java.util.List<com.example.wangpicture.interfaces.vo.picture.PictureVO>
    * @Author: trudh
    * @Date: 2025/9/19
    **/
    public List<PictureVO> searchPictureByColor(Long spaceId, String picColor, User loginUser);

    //批量编辑图片
    void editPictureByBatch(PictureEditByBatchRequest pictureEditByBatchRequest, User loginUser);

    // 扩图
    public CreateOutPaintingTaskResponse createPictureOutPaintingTask(CreatePictureOutPaintingTaskRequest createPictureOutPaintingTaskRequest, User loginUser);
}
