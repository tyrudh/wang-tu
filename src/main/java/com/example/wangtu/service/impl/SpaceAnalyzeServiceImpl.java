package com.example.wangtu.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.wangtu.exception.BusinessException;
import com.example.wangtu.exception.ErrorCode;
import com.example.wangtu.exception.ThrowUtils;
import com.example.wangtu.mapper.SpaceMapper;
import com.example.wangtu.model.dto.space.SpaceAddRequest;
import com.example.wangtu.model.dto.space.SpaceQueryRequest;
import com.example.wangtu.model.dto.space.analyze.*;
import com.example.wangtu.model.entity.Picture;
import com.example.wangtu.model.entity.Space;
import com.example.wangtu.model.entity.User;
import com.example.wangtu.model.enums.SpaceLevelEnum;
import com.example.wangtu.model.vo.SpaceVO;
import com.example.wangtu.model.vo.UserVO;
import com.example.wangtu.model.vo.space.analyze.*;
import com.example.wangtu.service.PictureService;
import com.example.wangtu.service.SpaceAnalyzeService;
import com.example.wangtu.service.SpaceService;
import com.example.wangtu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
* @author wang
* @createDate 2025-09-16 14:14:12
*/
@Service
public class SpaceAnalyzeServiceImpl extends ServiceImpl<SpaceMapper, Space>
    implements SpaceAnalyzeService {

    @Resource
    private UserService userService;
    @Resource
    private SpaceService spaceService;
    @Autowired
    private PictureService pictureService;


    @Override
    public SpaceUsageAnalyzeResponse getSpaceUsageAnalyze(SpaceUsageAnalyzeRequest spaceUsageAnalyzeRequest,User loginUser) {
        // 校验参数
        // 全空间或公共图库需要从Picture表中查询
        if (spaceUsageAnalyzeRequest.isQueryAll() || spaceUsageAnalyzeRequest.isQueryPublic()) {
            // 权限校验仅管理员可以使用
            checkSpaceAnalyzeAuth(spaceUsageAnalyzeRequest, loginUser);
            // 统计公共图库的使用空间
            QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("picSize");
            // 补充查询范围
            fillAnalyzeQueryWrapper(spaceUsageAnalyzeRequest, queryWrapper);
            List<Object> pictureObjList = pictureService.getBaseMapper().selectObjs(queryWrapper);
            long usedSize = pictureObjList.stream().mapToLong(obj -> (Long) obj).sum();
            long usedCount = pictureObjList.size();
            // 封装返回结果
            SpaceUsageAnalyzeResponse spaceUsageAnalyzeResponse = new SpaceUsageAnalyzeResponse();
            spaceUsageAnalyzeResponse.setUsedSize(usedSize);
            spaceUsageAnalyzeResponse.setMaxSize(null);
            spaceUsageAnalyzeResponse.setSizeUsageRatio(null);
            spaceUsageAnalyzeResponse.setUsedCount(usedCount);
            spaceUsageAnalyzeResponse.setMaxCount(null);
            spaceUsageAnalyzeResponse.setCountUsageRatio(null);
            return spaceUsageAnalyzeResponse;
        }else {
            // 分析特定空间需要从Space表中查询
            Long spaceId = spaceUsageAnalyzeRequest.getSpaceId();
            Space space = spaceService.getById(spaceId);
            ThrowUtils.throwIf(ObjUtil.isEmpty(space), ErrorCode.PARAMS_ERROR, "空间不存在");
            // 校验权限
            checkSpaceAnalyzeAuth(spaceUsageAnalyzeRequest, loginUser);
            // 封装返回结果
            SpaceUsageAnalyzeResponse spaceUsageAnalyzeResponse = new SpaceUsageAnalyzeResponse();
            spaceUsageAnalyzeResponse.setUsedSize(space.getTotalSize());
            spaceUsageAnalyzeResponse.setMaxSize(space.getMaxSize());
            spaceUsageAnalyzeResponse.setUsedCount(space.getTotalCount());
            spaceUsageAnalyzeResponse.setMaxCount(space.getMaxCount());
            // 计算空间使用率,使用hutool工具类进行一个转换保留两位小数
            double sizeUsageRatio = NumberUtil.round(space.getTotalSize() * 100.0 / space.getMaxSize(),2).doubleValue();
            // 计算数量的占比
            double countUsageRatio = NumberUtil.round(space.getTotalCount() * 100.0 / space.getMaxCount(),2).doubleValue();
            spaceUsageAnalyzeResponse.setSizeUsageRatio(sizeUsageRatio);
            spaceUsageAnalyzeResponse.setCountUsageRatio(countUsageRatio);
            return spaceUsageAnalyzeResponse;

        }
    }

    @Override
    public List<SpaceCategoryAnalyzeResponse> getSpaceCategoryAnalyze(SpaceCategoryAnalyzeRequest spaceCategoryAnalyzeRequest, User loginUser) {
        // 校验参数
        ThrowUtils.throwIf(spaceCategoryAnalyzeRequest == null, ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        // 校验权限
        checkSpaceAnalyzeAuth(spaceCategoryAnalyzeRequest, loginUser);
        // 构造查询条件
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        // 补充查询范围
        fillAnalyzeQueryWrapper(spaceCategoryAnalyzeRequest, queryWrapper);
        // 使用mybatis plus分组查询
        queryWrapper.select("category", "count(*) as count", "sum(picSize) as totalSize")
                .groupBy("category");
        // 执行查询
        // 查询并转换结果
        return pictureService.getBaseMapper().selectMaps(queryWrapper)
                .stream()
                .map(result -> {
                    String category = result.get("category") != null ? result.get("category").toString() : "未分类";
                    Long count = ((Number) result.get("count")).longValue();
                    Long totalSize = ((Number) result.get("totalSize")).longValue();
                    return new SpaceCategoryAnalyzeResponse(category, count, totalSize);
                }).collect(Collectors.toList());
    }

    @Override
    public List<SpaceTagAnalyzeResponse> getSpaceTagAnalyze(SpaceTagAnalyzeRequest spaceTagAnalyzeRequest, User loginUser) {
        ThrowUtils.throwIf(spaceTagAnalyzeRequest == null, ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        // 检查权限
        checkSpaceAnalyzeAuth(spaceTagAnalyzeRequest, loginUser);
        // 构造查询条件
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        // 补充查询范围
        fillAnalyzeQueryWrapper(spaceTagAnalyzeRequest, queryWrapper);
        // 查询所有符合条件的标签
        queryWrapper.select("tags");
        List<String> tagsJsonList = pictureService.getBaseMapper().selectObjs(queryWrapper)
                .stream()
                .filter(ObjUtil::isNotNull)
                .map(Object::toString)
                .collect(Collectors.toList());
        // 解析标签并使用

        // 合并所有标签并统计使用次数
        Map<String, Long> tagCountMap = tagsJsonList.stream()
                .flatMap(tagsJson -> JSONUtil.toList(tagsJson, String.class).stream())
                .collect(Collectors.groupingBy(tag -> tag, Collectors.counting()));

        // 转换为响应对象，按使用次数降序排序
        return tagCountMap.entrySet().stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue())) // 降序排列
                .map(entry -> new SpaceTagAnalyzeResponse(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

    }

    /**
    * @Description: 获取空间大小分析数据
    * @Param: [spaceSizeAnalyzeRequest, loginUser]
    * @return: java.util.List<com.example.wangtu.model.vo.space.analyze.SpaceSizeAnalyzeResponse>
    * @Author: trudh
    * @Date: 2025/9/21
    **/
    @Override
    public List<SpaceSizeAnalyzeResponse> getSpaceSizeAnalyze(SpaceSizeAnalyzeRequest spaceSizeAnalyzeRequest, User loginUser) {
        ThrowUtils.throwIf(spaceSizeAnalyzeRequest == null, ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        // 检查权限
        checkSpaceAnalyzeAuth(spaceSizeAnalyzeRequest, loginUser);
        // 构造查询条件
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        // 补充查询范围
        fillAnalyzeQueryWrapper(spaceSizeAnalyzeRequest, queryWrapper);
        // 查询所有符合条件的图片大小
        queryWrapper.select("picSize");
        List<Long> picSizes = pictureService.getBaseMapper().selectObjs(queryWrapper)
                .stream()
                .map(size -> ((Number) size).longValue())
                .collect(Collectors.toList());

        // 定义分段范围，注意使用有序 Map
        Map<String, Long> sizeRanges = new LinkedHashMap<>();
        sizeRanges.put("<100KB", picSizes.stream().filter(size -> size < 100 * 1024).count());
        sizeRanges.put("100KB-500KB", picSizes.stream().filter(size -> size >= 100 * 1024 && size < 500 * 1024).count());
        sizeRanges.put("500KB-1MB", picSizes.stream().filter(size -> size >= 500 * 1024 && size < 1 * 1024 * 1024).count());
        sizeRanges.put(">1MB", picSizes.stream().filter(size -> size >= 1 * 1024 * 1024).count());

        // 转换为响应对象
        return sizeRanges.entrySet().stream()
                .map(entry -> new SpaceSizeAnalyzeResponse(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    @Override
    public List<SpaceUserAnalyzeResponse> getSpaceUserAnalyze(SpaceUserAnalyzeRequest spaceUserAnalyzeRequest, User loginUser) {
        ThrowUtils.throwIf(spaceUserAnalyzeRequest == null, ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        // 检查权限
        checkSpaceAnalyzeAuth(spaceUserAnalyzeRequest, loginUser);
        // 构造查询条件
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        // 查询所有符合条件的图片
        Long userId = spaceUserAnalyzeRequest.getUserId();
        queryWrapper.eq(ObjUtil.isNotNull(userId), "userId", userId);
        fillAnalyzeQueryWrapper(spaceUserAnalyzeRequest, queryWrapper);
        // 补充分析维度每日，每周，每月
        String timeDimension = spaceUserAnalyzeRequest.getTimeDimension();
        switch (timeDimension) {
            case "day":
                queryWrapper.select("DATE_FORMAT(createTime, '%Y-%m-%d') AS period", "COUNT(*) AS count");
                break;
            case "week":
                queryWrapper.select("YEARWEEK(createTime) AS period", "COUNT(*) AS count");
                break;
            case "month":
                queryWrapper.select("DATE_FORMAT(createTime, '%Y-%m') AS period", "COUNT(*) AS count");
                break;
            default:
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的时间维度");
        }
        // 分组排序
        queryWrapper.groupBy("period").orderByAsc("period");
        // 查询结果
        List<Map<String, Object>> queryResult = pictureService.getBaseMapper().selectMaps(queryWrapper);
        return queryResult.stream()
                .map(result -> {
                    String period = result.get("period").toString();
                    Long count = ((Number) result.get("count")).longValue();
                    return new SpaceUserAnalyzeResponse(period, count);
                })
                .collect(Collectors.toList());

    }

    @Override
    public List<Space> getSpaceRankAnalyze(SpaceRankAnalyzeRequest spaceRankAnalyzeRequest, User loginUser) {
        ThrowUtils.throwIf(spaceRankAnalyzeRequest == null, ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        // 检查权限
        ThrowUtils.throwIf(!userService.isAdmin(loginUser), ErrorCode.NO_AUTH_ERROR, "没有管理员权限");
        // 构造查询条件
        QueryWrapper<Space> queryWrapper = new QueryWrapper<>();

        queryWrapper.select("id", "spaceName", "userId", "totalSize")
                .orderByDesc("totalSize")
                .last("LIMIT " + spaceRankAnalyzeRequest.getTopN()); // 取前 N 名

// 查询结果
        return spaceService.list(queryWrapper);
    }

    /**
    * @Description: 校验空间的使用权限
    * @Param:
    * @return:
    * @Author: trudh
    * @Date: 2025/9/21
    **/
    private void checkSpaceAnalyzeAuth(SpaceAnalyzeRequest spaceAnalyzeRequest, User loginUser) {

        Boolean queryPublic = spaceAnalyzeRequest.isQueryPublic();
        Boolean queryAll = spaceAnalyzeRequest.isQueryAll();
        if (queryPublic || queryAll) {
            // 判断是否具有管理员权限
            ThrowUtils.throwIf(!userService.isAdmin(loginUser), ErrorCode.NO_AUTH_ERROR, "没有管理员权限");
        }else {
            // 分析特定空间，仅本人或管理员可以访问
            Long spaceId = spaceAnalyzeRequest.getSpaceId();
            ThrowUtils.throwIf(ObjUtil.isEmpty(spaceId), ErrorCode.PARAMS_ERROR, "空间ID不能为空");
            Space space = spaceService.getById(spaceId);
            ThrowUtils.throwIf(ObjUtil.isEmpty(space), ErrorCode.PARAMS_ERROR, "空间不存在");
            spaceService.checkSpaceAuth(loginUser, space);

        }

    }

    /**
    * @Description: 根据请求对象封装查询条件 
    * @Param: [spaceAnalyzeRequest, queryWrapper]
    * @return: void
    * @Author: trudh
    * @Date: 2025/9/21
    **/
    private void fillAnalyzeQueryWrapper(SpaceAnalyzeRequest spaceAnalyzeRequest,QueryWrapper<Picture> queryWrapper) {
        boolean queryAll = spaceAnalyzeRequest.isQueryAll();
        // 全空间分析
        if (queryAll) {
            return;
        }
        // 公共图库分析
        boolean queryPublic = spaceAnalyzeRequest.isQueryPublic();
        if (queryPublic) {
            queryWrapper.isNull("spaceId");
            return;
        }
        // 特定空间分析
        Long spaceId = spaceAnalyzeRequest.getSpaceId();
        if (spaceId != null) {
            queryWrapper.eq("spaceId", spaceId);
            return;
        }
        throw new BusinessException(ErrorCode.PARAMS_ERROR, "未指定查询范围");
    }


}




