package com.example.wangtu.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.wangtu.model.dto.space.SpaceAddRequest;
import com.example.wangtu.model.dto.space.SpaceQueryRequest;
import com.example.wangtu.model.dto.spaceuser.SpaceUserAddRequest;
import com.example.wangtu.model.dto.spaceuser.SpaceUserQueryRequest;
import com.example.wangtu.model.entity.Space;
import com.example.wangtu.model.entity.SpaceUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.wangtu.model.entity.User;
import com.example.wangtu.model.vo.SpaceUserVO;
import com.example.wangtu.model.vo.SpaceVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author wang
* @description 针对表【space_user(空间用户关联)】的数据库操作Service
* @createDate 2025-09-22 15:51:21
*/
public interface SpaceUserService extends IService<SpaceUser> {

    // 创建空间成员
    long addSpaceUser(SpaceUserAddRequest spaceUserAddRequest);
    //将查询请求转为 QueryWrapper 对象
    QueryWrapper<SpaceUser> getQueryWrapper(SpaceUserQueryRequest spaceUserQueryRequest);

    // 获取单张空间成员包装类
    SpaceUserVO getSpaceUserVO(SpaceUser spaceUser, HttpServletRequest request);

    // 获取多张空间信息(分页)
    List<SpaceUserVO> getSpaceUserVOList(List<SpaceUser> spaceUserList);

    // 校验空间成员
    void validSpaceUser(SpaceUser spaceUser,boolean add);


}
