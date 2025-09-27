package com.example.wangpicture.infrastructure.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.wangpicture.domain.picture.entity.Picture;
import com.example.wangpicture.domain.picture.repository.PictureRepository;
import com.example.wangpicture.domain.user.entity.User;
import com.example.wangpicture.domain.user.repository.UserRepository;
import com.example.wangpicture.infrastructure.mapper.PictureMapper;
import com.example.wangpicture.infrastructure.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
 * @author wang
 * @version 1.0
 * @description: 用户仓储实现
 * @date 2025/9/27 14:26
 */

@Service
public class PictureRepositoryImpl extends ServiceImpl<PictureMapper, Picture> implements PictureRepository {
}

