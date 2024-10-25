package com.fzq.xiaopotato.service;

import com.fzq.xiaopotato.model.dto.common.IdDTO;
import com.fzq.xiaopotato.model.entity.Saves;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

/**
* @author zfeng
* @description 针对表【Saves】的数据库操作Service
* @createDate 2024-10-25 17:08:10
*/
public interface SavesService extends IService<Saves> {
    boolean saveByPostId(IdDTO idDTO, HttpServletRequest request);

    boolean isSaved(IdDTO idDTO, HttpServletRequest request);
}
