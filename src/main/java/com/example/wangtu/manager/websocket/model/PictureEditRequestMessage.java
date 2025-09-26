package com.example.wangtu.manager.websocket.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wang
 * @version 1.0
 * @description: TODO
 * @date 2025/9/24 18:36
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PictureEditRequestMessage {

    /**
     * 消息类型，例如 "ENTER_EDIT", "EXIT_EDIT", "EDIT_ACTION"
     */
    private String type;

    /**
     * 执行的编辑动作(比如说放大或者缩小等等)
     */
    private String editAction;
}
