package com.example.wangtu.model.vo.space.analyze;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author wang
 * @version 1.0
 * @description: TODO
 * @date 2025/9/21 16:33
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpaceUserAnalyzeResponse implements Serializable {

    /**
     * 时间区间
     */
    private String period;

    /**
     * 上传数量
     */
    private Long count;

    private static final long serialVersionUID = 1L;
}