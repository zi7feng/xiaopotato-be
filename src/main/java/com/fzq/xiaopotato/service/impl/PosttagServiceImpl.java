package com.fzq.xiaopotato.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fzq.xiaopotato.model.entity.Posttag;
import com.fzq.xiaopotato.service.PosttagService;
import com.fzq.xiaopotato.mapper.PosttagMapper;
import org.springframework.stereotype.Service;

/**
* @author zfeng
* @description 针对表【Posttag(Post-Tag Relationship Table: stores the many-to-many relationship between posts and tags)】的数据库操作Service实现
* @createDate 2024-10-24 14:39:03
*/
@Service
public class PosttagServiceImpl extends ServiceImpl<PosttagMapper, Posttag>
    implements PosttagService{

}




