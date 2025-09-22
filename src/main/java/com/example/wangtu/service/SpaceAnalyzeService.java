package com.example.wangtu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.wangtu.model.dto.space.analyze.*;
import com.example.wangtu.model.entity.Space;
import com.example.wangtu.model.entity.User;
import com.example.wangtu.model.vo.space.analyze.*;

import java.util.List;

public interface SpaceAnalyzeService extends IService<Space> {

    /**
     * 获取空间使用分析
     *
     * @param
     * @return 空间使用分析结果
     */
    SpaceUsageAnalyzeResponse getSpaceUsageAnalyze(SpaceUsageAnalyzeRequest spaceUsageAnalyzeRequest, User loginUser);

    /**
     * 获取空间分类分析
     *
     * @param
     * @return 空间使用分类结果
     */
    List<SpaceCategoryAnalyzeResponse> getSpaceCategoryAnalyze(SpaceCategoryAnalyzeRequest spaceCategoryAnalyzeRequest, User loginUser);

    // 获取空间里面图片标签使用分析
    List<SpaceTagAnalyzeResponse> getSpaceTagAnalyze(SpaceTagAnalyzeRequest spaceTagAnalyzeRequest, User loginUser);

    // 分段统计图片大小：
    List<SpaceSizeAnalyzeResponse> getSpaceSizeAnalyze(SpaceSizeAnalyzeRequest spaceSizeAnalyzeRequest, User loginUser);

    // 获取空间用户分析
    List<SpaceUserAnalyzeResponse> getSpaceUserAnalyze(SpaceUserAnalyzeRequest spaceUserAnalyzeRequest, User loginUser);

    // 获取空间排名分析
    List<Space> getSpaceRankAnalyze(SpaceRankAnalyzeRequest spaceRankAnalyzeRequest, User loginUser);
}
