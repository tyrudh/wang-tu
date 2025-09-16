package com.example.wangtu.manager.upload;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.example.wangtu.config.CosClientConfig;
import com.example.wangtu.exception.BusinessException;
import com.example.wangtu.exception.ErrorCode;
import com.example.wangtu.exception.ThrowUtils;
import com.example.wangtu.manager.CosManager;
import com.example.wangtu.model.dto.file.UploadPictureResult;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.CIObject;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import com.qcloud.cos.model.ciModel.persistence.ProcessResults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author wang
 * @version 1.0
 * @description: TODO
 * @date 2025/9/10 13:38
 */
@Service
@Slf4j
public abstract class PictureUploadTemplate{

    @Resource
    private CosClientConfig cosClientConfig;

    @Resource
    private CosManager cosManager;

    public UploadPictureResult uploadPicture(Object inputSource,String uploadPathPrefix) {
        //校验图片
        validPicture(inputSource);

        //图片上传地址
        String uuid = RandomUtil.randomString(16);
        // todo
        String originFilename = getOriginalFilename(inputSource);
        String uploadFilename = String.format("%s_%s.%s", DateUtil.formatDate(new Date()), uuid,
                FileUtil.getSuffix(originFilename));
        String uploadPath = String.format("/%s/%s", uploadPathPrefix, uploadFilename);
        //解析结果并返回

        File file = null;
        try {
            // 创建临时文件
            file = File.createTempFile(uploadPath, null);
            // todo处理文件来源
            processFile(inputSource,file);
            // 上传图片
            PutObjectResult putObjectResult = cosManager.putPictureObject(uploadPath, file);
            // 获取到图片信息对象
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();
            // 获取图片处理结果
            ProcessResults processResults = putObjectResult.getCiUploadResult().getProcessResults();
            List<CIObject> objectList = processResults.getObjectList();
            if (CollUtil.isNotEmpty(objectList)) {
                // 获取压缩之后的图片信息，以及缩略图信息
                CIObject compressedCiobject = objectList.get(0);
                // 缩略图默认等于原图
                CIObject thumbnailCiObject = compressedCiobject;

                if (objectList.size() > 1) {
                    thumbnailCiObject = objectList.get(1);
                }
                // 封装压缩图的返回结果
                return buildResult(originFilename,compressedCiobject,thumbnailCiObject);
            }
            return buildResult(imageInfo, originFilename, file, uploadPath);
        } catch (Exception e) {
            log.error("图片上传到对象存储失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            //文件清理
            this.deleteTempFile(file);
        }
    }
    /**
    * @Description: 封装返回结果
    * @Param: [originFilename, compressedCiObject]
    * @return: com.example.wangtu.model.dto.file.UploadPictureResult
    * @Author: trudh
    * @Date: 2025/9/15
    **/

    private UploadPictureResult buildResult(String originFilename, CIObject compressedCiObject,CIObject thumbnailCiObject) {
        UploadPictureResult uploadPictureResult = new UploadPictureResult();
        int picWidth = compressedCiObject.getWidth();
        int picHeight = compressedCiObject.getHeight();
        double picScale = NumberUtil.round(picWidth * 1.0 / picHeight, 2).doubleValue();
        uploadPictureResult.setPicName(FileUtil.mainName(originFilename));
        uploadPictureResult.setPicWidth(picWidth);
        uploadPictureResult.setPicHeight(picHeight);
        uploadPictureResult.setPicScale(picScale);
        uploadPictureResult.setPicFormat(compressedCiObject.getFormat());
        uploadPictureResult.setPicSize(compressedCiObject.getSize().longValue());
        // 设置图片为压缩后的地址
        uploadPictureResult.setUrl(cosClientConfig.getHost() + "/" + compressedCiObject.getKey());
        // 设置缩略图
        uploadPictureResult.setThumbnailUrl(cosClientConfig.getHost() + "/" + thumbnailCiObject.getKey());

        return uploadPictureResult;
    }
    /**
     * 处理文件来源
     */
    protected abstract void processFile(Object inputSource,File file) throws IOException;

    /**
     * 获取原始文件名
     */
    protected abstract String getOriginalFilename(Object inputSource) ;

    /**
     * 校验图片
     */
    protected abstract void validPicture(Object inputSource);


    /**
     * 删除临时文件
     */
    public void deleteTempFile(File file) {
        if (file == null) {
            return;
        }
        // 删除临时文件
        boolean deleteResult = file.delete();
        if (!deleteResult) {
            log.error("file delete error, filepath = {}", file.getAbsolutePath());
        }
    }
    // ...

    public UploadPictureResult uploadPictureByUrl(String fileUrl,String uploadPathPrefix) {
        //校验图片

        validPicture(fileUrl);

        //图片上传地址
        String uuid = RandomUtil.randomString(16);
        String originFilename = FileUtil.getName(fileUrl);
        //拼接上传路径
        String uploadFilename = String.format("%s_%s.%s", DateUtil.formatDate(new Date()), uuid,
                FileUtil.getSuffix(originFilename));
        String uploadPath = String.format("/%s/%s", uploadPathPrefix, uploadFilename);
        //解析结果并返回

        File file = null;
        try {
            // 创建临时文件
            file = File.createTempFile(uploadPath, null);
            //先下载文件,这是从网络上下载
            HttpUtil.downloadFile(fileUrl, file);
            // 上传图片
            PutObjectResult putObjectResult = cosManager.putPictureObject(uploadPath, file);
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();
            // 封装返回结果
            return buildResult(imageInfo, originFilename, file, uploadPath);
        } catch (Exception e) {
            log.error("图片上传到对象存储失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            //文件清理
            this.deleteTempFile(file);
        }
    }

    /**
    * @Description: 校验通过url下载的图片
    * @Param: [fileUrl]
    * @return: void
    * @Author: trudh
    * @Date: 2025/9/13
    **/
    private void validPicture(String fileUrl){
        // 校验非空
        ThrowUtils.throwIf(StrUtil.isBlank(fileUrl), ErrorCode.PARAMS_ERROR,"文件地址不能为空");
        // 校验url格式，可以通过查看能否自动的解析对应的url来查看是否url格式正确
        try {
            new URL(fileUrl);
        } catch (MalformedURLException e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"url格式不对");
        }
        // 校验url协议
        ThrowUtils.throwIf(
                !fileUrl.startsWith("http://") && !fileUrl.startsWith("https://"),
                ErrorCode.PARAMS_ERROR,"仅支持HTTP请求或HTTPS请求");
        // 发送head请求验证文件是否存在
        HttpResponse httpResponse = null;

        try {
            httpResponse = HttpUtil.createRequest(Method.HEAD, fileUrl)
                    .execute();
            // 未正常返回无需执行其他校验
            if(httpResponse.getStatus() != HttpStatus.HTTP_OK ){

                return;
            }
            // 4. 校验文件类型
            String contentType = httpResponse.header("Content-Type");
            if (StrUtil.isNotBlank(contentType)) {
                // 允许的图片类型
                final List<String> ALLOW_CONTENT_TYPES = Arrays.asList("image/jpeg", "image/jpg", "image/png", "image/webp");
                ThrowUtils.throwIf(!ALLOW_CONTENT_TYPES.contains(contentType.toLowerCase()),
                        ErrorCode.PARAMS_ERROR, "文件类型错误");
            }
            // 5. 校验文件大小
            String contentLengthStr = httpResponse.header("Content-Length");
            if (StrUtil.isNotBlank(contentLengthStr)) {
                try {
                    long contentLength = Long.parseLong(contentLengthStr);
                    final long TWO_MB = 5 * 1024 * 1024L; // 限制文件大小为 2MB
                    ThrowUtils.throwIf(contentLength > TWO_MB, ErrorCode.PARAMS_ERROR, "文件大小不能超过 5M");
                } catch (NumberFormatException e) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小格式错误");
                }
            }
        }finally {
            //  关闭http响应
            if(httpResponse != null){
                httpResponse.close();
            }
        }



    }

    /**
     * @Description: 封装返回对象
     * @Param: [imageInfo, originFilename, file, uploadPath]
     * @return: com.example.wangtu.model.dto.file.UploadPictureResult
     * @Author: trudh
     * @Date: 2025/9/13
     **/
    private UploadPictureResult buildResult(ImageInfo imageInfo, String originFilename, File file, String uploadPath) {
        // 封装返回结果
        UploadPictureResult uploadPictureResult = new UploadPictureResult();
        int picWidth = imageInfo.getWidth();
        int picHeight = imageInfo.getHeight();
        double picScale = NumberUtil.round(picWidth * 1.0 / picHeight, 2).doubleValue();
        uploadPictureResult.setPicName(FileUtil.mainName(originFilename));
        uploadPictureResult.setPicWidth(picWidth);
        uploadPictureResult.setPicHeight(picHeight);
        uploadPictureResult.setPicScale(picScale);
        uploadPictureResult.setPicFormat(imageInfo.getFormat());
        uploadPictureResult.setPicSize(FileUtil.size(file));
        uploadPictureResult.setUrl(cosClientConfig.getHost() + "/" + uploadPath);
        return uploadPictureResult;
    }


}


















