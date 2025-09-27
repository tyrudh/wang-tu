package com.example.wangpicture.infrastructure.utils;

/**
 * @author wang
 * @version 1.0
 * @description: TODO
 * @date 2025/9/19 15:00
 */

import java.awt.*;

/**
 * 工具类：计算颜色相似度
 */
public class ColorTransformUtils {

    private ColorTransformUtils() {
        // 工具类不需要实例化
    }

    /**
    * @Description: 获取标准颜色，（将数据万象的5位色值改为6位） 
    * @Param: [color1, color2]
    * @return: double
    * @Author: trudh
    * @Date: 2025/9/19
    **/
    public static double getStandardColor(Color color){
        // 0x080e0 转为 0x080e00
        return (double) (color.getRGB() & 0xffffff) / 0x1000000;
    }


    // 示例代码

}
