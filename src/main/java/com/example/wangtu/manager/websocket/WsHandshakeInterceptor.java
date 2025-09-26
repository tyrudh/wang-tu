package com.example.wangtu.manager.websocket;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.example.wangtu.manager.auth.SpaceUserAuthManager;
import com.example.wangtu.manager.auth.model.SpaceUserPermissionConstant;
import com.example.wangtu.model.entity.Picture;
import com.example.wangtu.model.entity.Space;
import com.example.wangtu.model.entity.User;
import com.example.wangtu.model.enums.SpaceTypeEnum;
import com.example.wangtu.service.PictureService;
import com.example.wangtu.service.SpaceService;
import com.example.wangtu.service.SpaceUserService;
import com.example.wangtu.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author wang
 * @version 1.0
 * @description: webSocket拦截器
 * @date 2025/9/24 19:12
 */
@Slf4j
@Component
public class WsHandshakeInterceptor implements HandshakeInterceptor {

    @Resource
    private UserService userService;
    @Resource
    private SpaceService spaceService;
    @Resource
    private PictureService pictureService;
    @Resource
    private SpaceUserAuthManager spaceUserAuthManager;
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // 获取到当前用户
        if (request instanceof ServletServerHttpRequest){
            HttpServletRequest httpServletRequest = ((ServletServerHttpRequest) request).getServletRequest();
            // 从请求中获取参数
            String pictureId = httpServletRequest.getParameter("pictureId");
            if (StrUtil.isBlank(pictureId)){
                log.error("缺少图片参数拒绝握手");
                return false;
            }
            User loginUser = userService.getLoginUser(httpServletRequest);
            if (ObjUtil.isEmpty(loginUser)){
                log.error("用户未登录拒绝握手");
                return false;
            }
            // 判断当前用户是否有编辑图片的权限
            Picture picture = pictureService.getById(pictureId);
            if (ObjUtil.isEmpty(picture)){
                log.error("图片不存在拒绝握手");
                return false;
            }
            Long spaceId = picture.getSpaceId();
            Space space = null;
            if (spaceId != null){
                space = spaceService.getById(spaceId);
                if (ObjUtil.isEmpty(space)){
                    log.error("图片所在空间不存在拒绝握手");
                    return false;
                }
                if(space.getSpaceType() != SpaceTypeEnum.TEAM.getValue()){
                    log.error("图片所在空间不是团队空间拒绝握手");
                    return false;
                }
            }
            List<String> permissionList = spaceUserAuthManager.getPermissionList(space, loginUser);
            if (!permissionList.contains(SpaceUserPermissionConstant.PICTURE_EDIT)){
                log.error("用户没有编辑图片的权限拒绝握手");
                return false;
            }

            // 设置 attributes
            attributes.put("user", loginUser);
            attributes.put("userId", loginUser.getId());
            attributes.put("pictureId", Long.valueOf(pictureId)); // 记得转换为 Long 类型
        }
        return true;

    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}









