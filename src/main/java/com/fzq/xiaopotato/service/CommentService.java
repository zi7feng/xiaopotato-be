package com.fzq.xiaopotato.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fzq.xiaopotato.model.dto.comment.FirstCommentCreateDTO;
import com.fzq.xiaopotato.model.dto.comment.FirstQueryDTO;
import com.fzq.xiaopotato.model.dto.comment.SecondCommentCreateDTO;
import com.fzq.xiaopotato.model.dto.comment.SecondQueryDTO;
import com.fzq.xiaopotato.model.dto.common.IdDTO;
import com.fzq.xiaopotato.model.entity.Comment;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fzq.xiaopotato.model.vo.FirstCommentVO;
import com.fzq.xiaopotato.model.vo.SecondCommentVO;
import jakarta.servlet.http.HttpServletRequest;

/**
* @author zfeng
* @description 针对表【Comment】的数据库操作Service
* @createDate 2024-10-29 12:48:48
*/
public interface CommentService extends IService<Comment> {

    Long createFirstLevelComment(FirstCommentCreateDTO commentCreateDTO, HttpServletRequest request);

    Long createSecondLevelComment(SecondCommentCreateDTO commentCreateDTO, HttpServletRequest request);

    IPage<FirstCommentVO> listFirstLevelCommentsByPostId(FirstQueryDTO firstQueryDTO, HttpServletRequest request);

    IPage<SecondCommentVO> listSecondLevelCommentsByPostId(SecondQueryDTO secondQueryDTO, HttpServletRequest request);


    boolean deleteComment(IdDTO idDTO, HttpServletRequest request);
}
