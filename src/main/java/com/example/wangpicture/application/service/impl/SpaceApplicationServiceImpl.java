package com.example.wangpicture.application.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.wangpicture.application.service.SpaceApplicationService;
import com.example.wangpicture.domain.space.service.SpaceDomainService;
import com.example.wangpicture.infrastructure.exception.BusinessException;
import com.example.wangpicture.infrastructure.exception.ErrorCode;
import com.example.wangpicture.infrastructure.exception.ThrowUtils;
import com.example.wangpicture.interfaces.dto.space.SpaceAddRequest;
import com.example.wangpicture.interfaces.dto.space.SpaceQueryRequest;
import com.example.wangpicture.domain.space.entity.Space;
import com.example.wangpicture.domain.space.entity.SpaceUser;
import com.example.wangpicture.domain.user.entity.User;
import com.example.wangpicture.domain.space.valueobject.SpaceLevelEnum;
import com.example.wangpicture.domain.space.valueobject.SpaceRoleEnum;
import com.example.wangpicture.domain.space.valueobject.SpaceTypeEnum;
import com.example.wangpicture.interfaces.vo.space.SpaceVO;
import com.example.wangpicture.interfaces.vo.user.UserVO;
import com.example.wangpicture.infrastructure.mapper.SpaceMapper;
import com.example.wangpicture.application.service.SpaceUserApplicationService;
import com.example.wangpicture.application.service.UserApplicationService;
import org.springframework.stereotype.Service;
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
public class SpaceApplicationServiceImpl extends ServiceImpl<SpaceMapper, Space>
    implements SpaceApplicationService {

    @Resource
    private SpaceDomainService spaceDomainService;

    @Resource
    private UserApplicationService userApplicationService;

    @Resource
    private SpaceUserApplicationService spaceUserApplicationService;

    // 使用编程式事务
    @Resource
    private TransactionTemplate transactionTemplate;

    // 为方便开发先注释点分库分表
//    @Resource
//    @Lazy
//    private DynamicShardingManager dynamicShardingManager;

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
        if(space.getSpaceType() == null){
            space.setSpaceType(SpaceTypeEnum.PRIVATE.getValue());
        }
        // 填充容量和大小
        this.fillSpaceBySpaceLevel(space);
        // 2.校验参数，此时校验处于创建状态所以填入true
        space.validSpace(true);
        // 3.校验权限非管理员只能创建普通级空间
        Long userId = loginUser.getId();
        space.setUserId(userId);
        if(space.getSpaceLevel() != SpaceLevelEnum.COMMON.getValue() && !loginUser.isAdmin()){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR,"没有权限创建指定的空间");
        }
        // 4.控制同一用户只能创建一个私有空间
        String lock = String.valueOf(userId).intern();
        synchronized (lock){
            // 此处使用编程式锁
            Long newSpaceId = transactionTemplate.execute(status -> {
                if (!loginUser.isAdmin()) {
                    boolean exists = this.lambdaQuery()
                            .eq(Space::getUserId, userId)
                            .eq(Space::getSpaceType, space.getSpaceType())
                            .exists();
                    ThrowUtils.throwIf(exists, ErrorCode.OPERATION_ERROR, "每个用户每类空间仅能创建一个");
                }
                // 写入数据库
                boolean result = this.save(space);
                ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
                // 如果是团队空间，关联新增团队成员记录
                if (space.getSpaceType() == SpaceTypeEnum.TEAM.getValue()) {
                    SpaceUser spaceUser = new SpaceUser();

                    spaceUser.setSpaceId(space.getId());
                    spaceUser.setUserId(userId);
                    spaceUser.setSpaceRole(SpaceRoleEnum.ADMIN.getValue());
                    result = spaceUserApplicationService.save(spaceUser);
                    ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR,"创建团队成员记录失败");
                }
                // 为方便开发先祝注释点分库分表
//                // 动态建表(仅对团队空间生效)
//                dynamicShardingManager.createSpacePictureTable(space);

                // 返回新写入的数据 id
                return space.getId();
            });
            return Optional.ofNullable(newSpaceId).orElse(-1L);
        }


    }

    @Override
    public QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest) {

        return spaceDomainService.getQueryWrapper(spaceQueryRequest);
    }

    @Override
    public SpaceVO getSpaceVO(Space space, HttpServletRequest request) {
        // 对象转封装类
        SpaceVO spaceVO = SpaceVO.objToVo(space);
        // 关联查询用户信息
        Long userId = space.getUserId();
        if (userId != null && userId > 0) {
            User user = userApplicationService.getUserById(userId);
            UserVO userVO = userApplicationService.getUserVO(user);
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
        Map<Long, List<User>> userIdUserListMap = userApplicationService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 2. 填充信息
        spaceVOList.forEach(spaceVO -> {
            Long userId = spaceVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            spaceVO.setUser(userApplicationService.getUserVO(user));
        });
        spaceVOPage.setRecords(spaceVOList);
        return spaceVOPage;
    }



    @Override
    public void fillSpaceBySpaceLevel(Space space) {
        spaceDomainService.fillSpaceBySpaceLevel(space);
    }

    @Override
    public void checkSpaceAuth(User loginUser, Space space) {
        spaceDomainService.checkSpaceAuth(loginUser,space);
    }
}




