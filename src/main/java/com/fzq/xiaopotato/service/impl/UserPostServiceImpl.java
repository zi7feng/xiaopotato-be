package com.fzq.xiaopotato.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fzq.xiaopotato.model.entity.UserPost;
import com.fzq.xiaopotato.service.UserPostService;
import com.fzq.xiaopotato.mapper.UserPostMapper;
import org.springframework.stereotype.Service;

/**
* @author zfeng
* @description 针对表【UserPost(User-Post Relationship Table)】的数据库操作Service实现
* @createDate 2024-10-23 15:20:02
*/
@Service
public class UserPostServiceImpl extends ServiceImpl<UserPostMapper, UserPost>
    implements UserPostService{

}




