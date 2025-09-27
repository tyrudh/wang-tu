package com.example.wangtu.manager.websocket.disruptor;

import com.example.wangtu.manager.websocket.model.PictureEditRequestMessage;
import com.example.wangpicture.domain.user.entity.User;
import lombok.Data;
import org.springframework.web.socket.WebSocketSession;

/**
 * @author wang
 * @version 1.0
 * @description: TODO
 * @date 2025/9/26 10:02
 */

@Data
public class PictureEditEvent {

    /**
     * 消息
     */
    private PictureEditRequestMessage pictureEditRequestMessage;

    /**
     * 当前用户的 session
     */
    private WebSocketSession session;

    /**
     * 当前用户
     */
    private User user;

    /**
     * 图片 id
     */
    private Long pictureId;

}