package com.example.wangtu.manager.websocket.disruptor;

import com.example.wangtu.manager.websocket.model.PictureEditRequestMessage;
import com.example.wangtu.model.entity.User;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;

/**
 * @author wang
 * @version 1.0
 * @description: TODO
 * @date 2025/9/26 10:14
 */

@Component
@Slf4j
public class PictureEditEventProducer {
    @Resource
    private Disruptor<PictureEditEvent> pictureEditEventDisruptor;

    /**
    * @Description: 发布事件
    * @Param: [pictureEditRequestMessage, session, user, pictureId]
    * @return: void
    * @Author: trudh
    * @Date: 2025/9/26
    **/
    public void publishEvent(PictureEditRequestMessage pictureEditRequestMessage, WebSocketSession session, User user, Long pictureId)  {
        //获取到可以放置事件的位置
        RingBuffer<PictureEditEvent> ringBuffer = pictureEditEventDisruptor.getRingBuffer();
        long next = ringBuffer.next();
        PictureEditEvent pictureEditEvent = ringBuffer.get(next);
        pictureEditEvent.setPictureEditRequestMessage(pictureEditRequestMessage);
        pictureEditEvent.setSession(session);
        pictureEditEvent.setUser(user);
        pictureEditEvent.setPictureId(pictureId);
        // 发布事件
        ringBuffer.publish(next);
    }

    @PreDestroy
    public void destroy() {
        pictureEditEventDisruptor.shutdown();

    }










}
