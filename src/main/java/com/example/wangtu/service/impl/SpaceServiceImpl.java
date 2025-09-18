package com.example.wangtu.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.wangtu.exception.BusinessException;
import com.example.wangtu.exception.ErrorCode;
import com.example.wangtu.exception.ThrowUtils;
import com.example.wangtu.model.dto.space.SpaceAddRequest;
import com.example.wangtu.model.dto.space.SpaceQueryRequest;
import com.example.wangtu.model.entity.Picture;
import com.example.wangtu.model.entity.Space;
import com.example.wangtu.model.entity.User;
import com.example.wangtu.model.enums.SpaceLevelEnum;
import com.example.wangtu.model.vo.PictureVO;
import com.example.wangtu.model.vo.SpaceVO;
import com.example.wangtu.model.vo.UserVO;
import com.example.wangtu.service.SpaceService;
import com.example.wangtu.mapper.SpaceMapper;
import com.example.wangtu.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author wang
* @description 针对表【space(空间)】的数据库操作Service实现
* @createDate 2025-09-16 14:14:12
*/
@Service
public class SpaceServiceImpl extends ServiceImpl<SpaceMapper, Space>
    implements SpaceService{

    @Resource
    private UserService userService;

    // 使用编程式事务
    @Resource
    private TransactionTemplate transactionTemplate;

    /**
    * @Description: 创建空间 
    * @Param: [spaceAddRequest, loginUser]
    * @return: long
    * @Author: trudh
    * @Date: 2025/9/17
    **/
    @Override
    public long addSpace(SpaceAddRequest spaceAddRequest, User loginUser) {

        // 1.填充参数默认值
        // 转换实体类和dto
        Space space = new Space();
        BeanUtil.copyProperties(spaceAddRequest,space);
        if(StrUtil.isBlank(space.getSpaceName())){
            space.setSpaceName("默认空间");
        }
        if(space.getSpaceLevel() == null){
            space.setSpaceLevel(SpaceLevelEnum.COMMON.getValue());
        }
        // 填充容量和大小
        this.fillSpaceBySpaceLevel(space);
        // 2.校验参数，此时校验处于创建状态所以填入true
        this.validSpace(space,true);
        // 3.校验权限非管理员只能创建普通级空间
        Long userId = loginUser.getId();
        space.setUserId(userId);
        if(space.getSpaceLevel() != SpaceLevelEnum.COMMON.getValue() && !userService.isAdmin(loginUser)){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR,"没有权限创建指定的空间");
        }
        // 4.控制同一用户只能创建一个私有空间
        String lock = String.valueOf(userId).intern();
        synchronized (lock){
            // 此处使用编程式锁
            Long newSpaceId = transactionTemplate.execute(status -> {
                // 判断是否已有空间
                boolean exists = this.lambdaQuery().eq(Space::getUserId, userId).exists();
                // 如果已有空间则不能创建
                if(exists){
                    throw new BusinessException(ErrorCode.OPERATION_ERROR,"每个用户只能有一个私有空间");
                }
                // 创建
                boolean result = this.save(space);
                ThrowUtils.throwIf(!result,ErrorCode.OPERATION_ERROR,"存储数据库失败");
                // 返回结果
                return space.getId();
            });
            return Optional.ofNullable(newSpaceId).orElse(-1L);
        }


    }

    @Override
    public QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest) {

        QueryWrapper<Space> queryWrapper = new QueryWrapper<>();
        if (spaceQueryRequest == null) {
            return queryWrapper;
        }
        // 从对象中取值
        Long id = spaceQueryRequest.getId();
        Long userId = spaceQueryRequest.getUserId();

        String spaceName = spaceQueryRequest.getSpaceName();
        Integer spaceLevel = spaceQueryRequest.getSpaceLevel();
        String sortField = spaceQueryRequest.getSortField();
        String sortOrder = spaceQueryRequest.getSortOrder();



        queryWrapper.eq(ObjUtil.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjUtil.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjUtil.isNotEmpty(spaceLevel), "spaceLevel", spaceLevel);



        queryWrapper.like(StrUtil.isNotBlank(spaceName), "spaceName", spaceName);



        // 排序
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }

    @Override
    public SpaceVO getSpaceVO(Space space, HttpServletRequest request) {
        // 对象转封装类
        SpaceVO spaceVO = SpaceVO.objToVo(space);
        // 关联查询用户信息
        Long userId = space.getUserId();
        if (userId != null && userId > 0) {
            User user = userService.getById(userId);
            UserVO userVO = userService.getUserVO(user);
            spaceVO.setUser(userVO);
        }
        return spaceVO;
    }

    @Override
    public Page<SpaceVO> getSpaceVOPage(Page<Space> spacePage, HttpServletRequest request) {
        List<Space> spaceList = spacePage.getRecords();
        Page<SpaceVO> spaceVOPage = new Page<>(spacePage.getCurrent(), spacePage.getSize(), spacePage.getTotal());
        if (CollUtil.isEmpty(spaceList)) {
            return spaceVOPage;
        }
        // 对象列表 => 封装对象列表
        List<SpaceVO> spaceVOList = spaceList.stream().map(SpaceVO::objToVo).collect(Collectors.toList());
        // 1. 关联查询用户信息
        Set<Long> userIdSet = spaceList.stream().map(Space::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 2. 填充信息
        spaceVOList.forEach(spaceVO -> {
            Long userId = spaceVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            spaceVO.setUser(userService.getUserVO(user));
        });
        spaceVOPage.setRecords(spaceVOList);
        return spaceVOPage;
    }

    @Override
    public void validSpace(Space space,boolean add) {
        ThrowUtils.throwIf(space == null, ErrorCode.PARAMS_ERROR);
        // 从对象中取值
        String spaceName = space.getSpaceName();
        Integer spaceLevel = space.getSpaceLevel();
        SpaceLevelEnum spaceLevelEnum = SpaceLevelEnum.getEnumByValue(spaceLevel);
        // 要创建
        if (add) {
            if (StrUtil.isBlank(spaceName)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间名称不能为空");
            }
            if (spaceLevel == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间级别不能为空");
            }
        }
        // 修改数据时，如果要改空间级别
        if (spaceLevel != null && spaceLevelEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间级别不存在");
        }
        if (StrUtil.isNotBlank(spaceName) && spaceName.length() > 30) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间名称过长");
        }
    }

    @Override
    public void fillSpaceBySpaceLevel(Space space) {
        // 根据空间级别，自动填充限额
        SpaceLevelEnum spaceLevelEnum = SpaceLevelEnum.getEnumByValue(space.getSpaceLevel());
        if (spaceLevelEnum != null) {
            long maxSize = spaceLevelEnum.getMaxSize();
            if (space.getMaxSize() == null) {
                space.setMaxSize(maxSize);
            }
            long maxCount = spaceLevelEnum.getMaxCount();
            if (space.getMaxCount() == null) {
                space.setMaxCount(maxCount);
            }
        }
    }
}




