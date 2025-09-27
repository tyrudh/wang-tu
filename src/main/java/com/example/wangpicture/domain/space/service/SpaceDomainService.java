package com.example.wangpicture.domain.space.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.wangpicture.domain.space.entity.Space;
import com.example.wangpicture.domain.user.entity.User;
import com.example.wangpicture.interfaces.dto.space.SpaceAddRequest;
import com.example.wangpicture.interfaces.dto.space.SpaceQueryRequest;
import com.example.wangpicture.interfaces.vo.space.SpaceVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author wang
* @description 针对表【space(空间)】的数据库操作Service
* @createDate 2025-09-16 14:14:12
*/
public interface SpaceDomainService {


    //将查询请求转为 QueryWrapper 对象
    QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest);


    public void fillSpaceBySpaceLevel(Space space);

    // 检查权限仅本人或管理员
    void checkSpaceAuth(User loginUser,Space space);



}
