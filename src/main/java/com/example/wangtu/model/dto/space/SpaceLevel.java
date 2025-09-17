package com.example.wangtu.model.dto.space;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author wang
 * @version 1.0
 * @description: 空间级别
 * @date 2025/9/17 17:14
 */

@Data
@AllArgsConstructor
public class SpaceLevel {

    /**
    * @Description: 值
    * @Param:
    * @return:
    * @Author: trudh
    * @Date: 2025/9/17
    **/
    private int value;

    /**
     * @description: 中文
     * @author
     * @date  17:15
     * @version 1.0
     */
    private String text;

    /**
     * @description: 最大数量
     * @author
     * @date  17:15
     * @version 1.0
     */
    private long maxCount;

    /**
     * @description: 最大空间
     * @author
     * @date  17:15
     * @version 1.0
     */
    private long maxSize;
}