package com.example.wangtu.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.wangtu.model.dto.space.SpaceAddRequest;
import com.example.wangtu.model.dto.space.SpaceQueryRequest;
import com.example.wangtu.model.entity.Space;
import com.example.wangtu.model.entity.User;
import com.example.wangtu.model.vo.SpaceVO;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
* @author wang
* @description 针对表【space(空间)】的数据库操作Service
* @createDate 2025-09-16 14:14:12
*/
public interface SpaceService extends IService<Space> {

    // 创建空间
    long addSpace(SpaceAddRequest spaceAddRequest, User loginUser);
    //将查询请求转为 QueryWrapper 对象
    QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest);

    // 获取单张空间信息
    SpaceVO getSpaceVO(Space space, HttpServletRequest request);

    // 获取多张空间信息(分页)
    Page<SpaceVO> getSpaceVOPage(Page<Space> spacePage, HttpServletRequest request);

    // 校验空间信息,add判断是否处于修改的状态
    void validSpace(Space space,boolean add);

    public void fillSpaceBySpaceLevel(Space space);

    // 检查权限仅本人或管理员
    void checkSpaceAuth(User loginUser,Space space);



}
