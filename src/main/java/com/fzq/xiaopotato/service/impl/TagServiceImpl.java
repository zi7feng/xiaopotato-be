package com.fzq.xiaopotato.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fzq.xiaopotato.model.entity.Tag;
import com.fzq.xiaopotato.service.TagService;
import com.fzq.xiaopotato.mapper.TagMapper;
import org.springframework.stereotype.Service;

/**
* @author zfeng
* @description 针对表【Tag(Tag Table: stores tags or keywords associated with posts or other entities)】的数据库操作Service实现
* @createDate 2024-10-24 14:38:09
*/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService{

}




