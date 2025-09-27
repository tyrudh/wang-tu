package com.example.wangpicture.infrastructure.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.wangpicture.domain.space.entity.Space;
import com.example.wangpicture.domain.space.entity.SpaceUser;
import com.example.wangpicture.domain.space.repository.SpaceRepository;
import com.example.wangpicture.domain.space.repository.SpaceUserRepository;
import com.example.wangpicture.infrastructure.mapper.SpaceMapper;
import com.example.wangpicture.infrastructure.mapper.SpaceUserMapper;
import org.springframework.stereotype.Service;

/**
 * @author wang
 * @version 1.0
 * @description: 用户仓储实现
 * @date 2025/9/27 14:26
 */

@Service
public class SpaceUserRepositoryImpl extends ServiceImpl<SpaceUserMapper, SpaceUser> implements SpaceUserRepository {
}

