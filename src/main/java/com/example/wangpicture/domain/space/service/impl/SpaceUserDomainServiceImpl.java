package com.example.wangpicture.domain.space.service.impl;

import cn.hutool.core.util.ObjUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.wangpicture.application.service.SpaceApplicationService;
import com.example.wangpicture.application.service.UserApplicationService;
import com.example.wangpicture.domain.space.entity.SpaceUser;
import com.example.wangpicture.domain.space.service.SpaceUserDomainService;
import com.example.wangpicture.infrastructure.mapper.SpaceUserMapper;
import com.example.wangpicture.interfaces.dto.spaceuser.SpaceUserQueryRequest;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
* @author wang
* @description 针对表【space_user(空间用户关联)】的数据库操作Service实现
* @createDate 2025-09-22 15:51:21
*/
@Service
public class SpaceUserDomainServiceImpl extends ServiceImpl<SpaceUserMapper, SpaceUser>
    implements SpaceUserDomainService {


    @Resource
    @Lazy
    private SpaceApplicationService spaceApplicationService;

    @Resource
    private UserApplicationService userApplicationService;



    @Override
    public QueryWrapper<SpaceUser> getQueryWrapper(SpaceUserQueryRequest spaceUserQueryRequest) {
        QueryWrapper<SpaceUser> queryWrapper = new QueryWrapper<>();
        if (spaceUserQueryRequest == null) {
            return queryWrapper;
        }
        // 从对象中取值
        Long id = spaceUserQueryRequest.getId();
        Long spaceId = spaceUserQueryRequest.getSpaceId();
        Long userId = spaceUserQueryRequest.getUserId();
        String spaceRole = spaceUserQueryRequest.getSpaceRole();
        queryWrapper.eq(ObjUtil.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjUtil.isNotEmpty(spaceId), "spaceId", spaceId);
        queryWrapper.eq(ObjUtil.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjUtil.isNotEmpty(spaceRole), "spaceRole", spaceRole);
        return queryWrapper;
    }

}




