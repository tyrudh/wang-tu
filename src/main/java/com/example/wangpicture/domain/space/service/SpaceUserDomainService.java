package com.example.wangpicture.domain.space.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.wangpicture.domain.space.entity.SpaceUser;
import com.example.wangpicture.interfaces.dto.spaceuser.SpaceUserAddRequest;
import com.example.wangpicture.interfaces.dto.spaceuser.SpaceUserQueryRequest;
import com.example.wangpicture.interfaces.vo.space.SpaceUserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author wang
* @description 针对表【space_user(空间用户关联)】的数据库操作Service
* @createDate 2025-09-22 15:51:21
*/
public interface SpaceUserDomainService extends IService<SpaceUser> {


    //将查询请求转为 QueryWrapper 对象
    QueryWrapper<SpaceUser> getQueryWrapper(SpaceUserQueryRequest spaceUserQueryRequest);




}
