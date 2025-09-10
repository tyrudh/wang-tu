package com.example.wangtu.controller;

import com.example.wangtu.annotation.AuthCheck;
import com.example.wangtu.common.BaseResponse;
import com.example.wangtu.common.ResultUtils;
import com.example.wangtu.constant.UserConstant;
import com.example.wangtu.exception.BusinessException;
import com.example.wangtu.exception.ErrorCode;
import com.example.wangtu.manager.CosManager;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.COSObjectInputStream;
import com.qcloud.cos.utils.IOUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

import static com.baomidou.mybatisplus.extension.ddl.DdlScriptErrorHandler.PrintlnLogErrorHandler.log;

/**
 * @author wang
 * @version 1.0
 * @description: TODO
 * @date 2025/9/9 21:25
 */

@RestController
@RequestMapping("/file")
public class FileController {
    @Resource
    private CosManager cosManager;
    /**
     * 测试上传文件
     * @return
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/test/upload")
    public BaseResponse<String> testUploadFile(@RequestPart("file")MultipartFile multipartFile) {
//        获取文件名称以及存取路径
        String fileName = multipartFile.getOriginalFilename();
        String filePath = String.format("/test/%s", fileName);


        File file = null;
        try {
            //上传文件
            file = File.createTempFile(filePath, null);
            multipartFile.transferTo(file);
            cosManager.putObject(filePath, file);
            //返回可访问的地址
            return ResultUtils.success(filePath);
        } catch (IOException e) {
            log.error("上传失败", e);
            throw new RuntimeException(e);
        }finally {
            if (file != null) {
                //记得清理临时文件
                boolean delete = file.delete();
                if (!delete) {
                    log.error(String.format("upload file error, filepath = %s", filePath));
                }
            }
        }

    }

    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @GetMapping("/test/download/")
    public void testDownloadFile(String filepath, HttpServletResponse response) throws IOException{
        COSObjectInputStream cosObjectInput = null;
        try {
            COSObject cosObject = cosManager.getObject(filepath);
            cosObjectInput = cosObject.getObjectContent();
            // 处理下载到的流
            byte[] bytes = IOUtils.toByteArray(cosObjectInput);
            // 设置响应头
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + filepath);
            // 写入响应
            response.getOutputStream().write(bytes);
            response.getOutputStream().flush();
        } catch (Exception e) {
            log.error("file download error, filepath = " + filepath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "下载失败");
        } finally {
            if (cosObjectInput != null) {
                // 记得关闭输入流
                cosObjectInput.close();
            }
        }
    }

}
