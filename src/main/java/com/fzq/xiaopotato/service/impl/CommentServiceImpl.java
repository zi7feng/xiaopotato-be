package com.fzq.xiaopotato.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fzq.xiaopotato.common.ErrorCode;
import com.fzq.xiaopotato.common.utils.SocketIOUtils;
import com.fzq.xiaopotato.exception.BusinessException;
import com.fzq.xiaopotato.mapper.PostcommentMapper;
import com.fzq.xiaopotato.mapper.UserMapper;
import com.fzq.xiaopotato.mapper.UserPostMapper;
import com.fzq.xiaopotato.model.dto.comment.FirstCommentCreateDTO;
import com.fzq.xiaopotato.model.dto.comment.FirstQueryDTO;
import com.fzq.xiaopotato.model.dto.comment.SecondCommentCreateDTO;
import com.fzq.xiaopotato.model.dto.comment.SecondQueryDTO;
import com.fzq.xiaopotato.model.dto.common.IdDTO;
import com.fzq.xiaopotato.model.entity.Comment;
import com.fzq.xiaopotato.model.entity.Postcomment;
import com.fzq.xiaopotato.model.entity.User;
import com.fzq.xiaopotato.model.entity.UserPost;
import com.fzq.xiaopotato.model.vo.FirstCommentVO;
import com.fzq.xiaopotato.model.vo.NotificationVO;
import com.fzq.xiaopotato.model.vo.SecondCommentVO;
import com.fzq.xiaopotato.model.vo.UserVO;
import com.fzq.xiaopotato.service.CommentService;
import com.fzq.xiaopotato.mapper.CommentMapper;
import com.fzq.xiaopotato.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.fzq.xiaopotato.common.NotificationType.COMMENT;
import static com.fzq.xiaopotato.common.NotificationType.SAVE;

/**
* @author zfeng
* @description 针对表【Comment】的数据库操作Service实现
* @createDate 2024-10-29 12:48:48
*/
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment>
    implements CommentService{

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private PostcommentMapper postcommentMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SocketIOUtils socketIOUtils;

    @Autowired
    private UserPostMapper userPostMapper;

    @Transactional
    @Override
    public Long createFirstLevelComment(FirstCommentCreateDTO commentCreateDTO, HttpServletRequest request) {
        UserVO user = userService.getCurrentUser(request);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }

        if (commentCreateDTO.getPostId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Post ID cannot be null.");
        }

        Comment comment = new Comment();
        comment.setContent(commentCreateDTO.getContent());
        comment.setUserId(user.getId());
        comment.setParentId(null);

        commentMapper.insert(comment);


        Postcomment postcomment = new Postcomment();
        postcomment.setCommentId(comment.getCommentId());
        postcomment.setPostId(commentCreateDTO.getPostId());
        postcommentMapper.insert(postcomment);

        QueryWrapper<UserPost> userPostQuery = new QueryWrapper<>();
        userPostQuery.eq("post_id", commentCreateDTO.getPostId());
        UserPost userPost = userPostMapper.selectOne(userPostQuery);
        if (userPost == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Post creator not found");
        }

        long creatorId = userPost.getUserId();
        sendFollowNotification(user, creatorId);

        return comment.getCommentId();
    }

    @Transactional
    @Override
    public Long createSecondLevelComment(SecondCommentCreateDTO commentCreateDTO, HttpServletRequest request) {
        UserVO user = userService.getCurrentUser(request);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }

        if (commentCreateDTO.getPostId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Post ID cannot be null.");
        }

        if (commentCreateDTO.getCommentId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Comment ID to reply cannot be null.");
        }

        Comment comment = new Comment();
        comment.setContent(commentCreateDTO.getContent());
        comment.setUserId(user.getId());
        comment.setParentId(commentCreateDTO.getCommentId()); // 设置 parentId 为要回复的 commentId

        commentMapper.insert(comment);

        Postcomment postcomment = new Postcomment();
        postcomment.setCommentId(comment.getCommentId());
        postcomment.setPostId(commentCreateDTO.getPostId());
        postcommentMapper.insert(postcomment);


        return comment.getCommentId();
    }


    @Transactional(readOnly = true)
    @Override
    public IPage<FirstCommentVO> listFirstLevelCommentsByPostId(FirstQueryDTO firstQueryDTO, HttpServletRequest request) {
        UserVO user = userService.getCurrentUser(request);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        int currentPage = firstQueryDTO.getCurrentPage();
        int pageSize = firstQueryDTO.getPageSize();
        Long postId = firstQueryDTO.getPostId();

        if (postId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Post ID cannot be null.");
        }

        Page<Comment> page = new Page<>(currentPage, pageSize);

        List<Long> firstLevelCommentIds = postcommentMapper.selectList(
                new QueryWrapper<Postcomment>().eq("post_id", postId)
        ).stream().map(Postcomment::getCommentId).collect(Collectors.toList());

        if (firstLevelCommentIds.isEmpty()) {
            return new Page<>();
        }

        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("comment_id", firstLevelCommentIds).isNull("parent_id");
        IPage<Comment> commentPage = commentMapper.selectPage(page, queryWrapper);


        List<FirstCommentVO> firstCommentVOList = commentPage.getRecords().stream().map(comment -> {
            FirstCommentVO firstCommentVO = new FirstCommentVO();
            BeanUtils.copyProperties(comment, firstCommentVO);

            User usr = userMapper.selectById(comment.getUserId());
            if (usr != null) {
                firstCommentVO.setCommentUserId(usr.getId());
                firstCommentVO.setCommentorFirstName(usr.getFirstName());
                firstCommentVO.setCommentorLastName(usr.getLastName());
                firstCommentVO.setCommentorAccount(usr.getUserAccount());
                firstCommentVO.setCommentorAvatar(usr.getUserAvatar());
            }

            int secondLevelCount = commentMapper.selectCount(
                    new QueryWrapper<Comment>().eq("parent_id", comment.getCommentId())
            ).intValue();
            firstCommentVO.setSecondLevelCount(secondLevelCount);

            return firstCommentVO;
        }).collect(Collectors.toList());


        Page<FirstCommentVO> resultPage = new Page<>(currentPage, pageSize, commentPage.getTotal());
        resultPage.setRecords(firstCommentVOList);

        return resultPage;
    }

    @Transactional(readOnly = true)
    @Override
    public IPage<SecondCommentVO> listSecondLevelCommentsByPostId(SecondQueryDTO secondQueryDTO, HttpServletRequest request) {
        UserVO user = userService.getCurrentUser(request);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        int currentPage = secondQueryDTO.getCurrentPage();
        int pageSize = secondQueryDTO.getPageSize();
        Long commentId = secondQueryDTO.getCommentId();

        if (commentId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Comment ID cannot be null.");
        }
        List<Comment> allChildComments = getAllChildComments(commentId);

        List<SecondCommentVO> secondCommentVOList = allChildComments.stream().map(comment -> {
                    SecondCommentVO secondCommentVO = new SecondCommentVO();
                    secondCommentVO.setCommentId(comment.getCommentId());
                    secondCommentVO.setContent(comment.getContent());
                    secondCommentVO.setCreateTime(comment.getCreateTime());

                    User commentUser = userMapper.selectById(comment.getUserId());
                    if (commentUser != null) {
                        secondCommentVO.setCommentUserId(commentUser.getId());
                        secondCommentVO.setCommentorFirstName(commentUser.getFirstName());
                        secondCommentVO.setCommentorLastName(commentUser.getLastName());
                        secondCommentVO.setCommentorAccount(commentUser.getUserAccount());
                        secondCommentVO.setCommentorAvatar(commentUser.getUserAvatar());
                    }

                    User replyToUser = userMapper.selectById(comment.getParentId());
                    if (replyToUser != null) {
                        secondCommentVO.setReplyToUserId(replyToUser.getId());
                        secondCommentVO.setReplyToFirstName(replyToUser.getFirstName());
                        secondCommentVO.setReplyToLastName(replyToUser.getLastName());
                        secondCommentVO.setReplyToAccount(replyToUser.getUserAccount());
                        secondCommentVO.setReplyToAvatar(replyToUser.getUserAvatar());
                    }

                    return secondCommentVO;
                }).sorted(Comparator.comparing(SecondCommentVO::getCreateTime)) // 按创建时间排序
                .collect(Collectors.toList());

        int start = Math.min((currentPage - 1) * pageSize, secondCommentVOList.size());
        int end = Math.min(start + pageSize, secondCommentVOList.size());
        List<SecondCommentVO> paginatedComments = secondCommentVOList.subList(start, end);

        Page<SecondCommentVO> resultPage = new Page<>(currentPage, pageSize, secondCommentVOList.size());
        resultPage.setRecords(paginatedComments);

        return resultPage;

    }


    @Transactional
    @Override
    public boolean deleteComment(IdDTO idDTO, HttpServletRequest request) {
        UserVO user = userService.getCurrentUser(request);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }

        Long commentId = idDTO.getId();
        // 查找要删除的评论
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "Comment not found.");
        }

        // 检查是否为评论的作者
        if (!comment.getUserId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH, "You are not authorized to delete this comment.");
        }

        int result;
        if (comment.getParentId() == null) {
            // 如果是一级评论，则删除该评论和所有子评论（级联删除）
            result = commentMapper.deleteById(commentId);
        } else {
            // 如果是二级评论或更深的评论，仅删除当前评论
            result = commentMapper.deleteById(commentId);
        }
        return result > 0;
    }

    private List<Comment> getAllChildComments(Long parentId) {
        List<Comment> childComments = commentMapper.selectList(new QueryWrapper<Comment>().eq("parent_id", parentId));
        List<Comment> allComments = new ArrayList<>(childComments);

        for (Comment childComment : childComments) {
            allComments.addAll(getAllChildComments(childComment.getCommentId()));
        }

        return allComments;
    }

    private void sendFollowNotification(UserVO user, Long destinateId) {
        NotificationVO notification = new NotificationVO();

        notification.setSourceId(user.getId());
        notification.setFirstName(user.getFirstName());
        notification.setLastName(user.getLastName());
        notification.setAccount(user.getUserAccount());
        notification.setAvatar(user.getUserAvatar());
        notification.setNotificationType(String.valueOf(COMMENT));

        // 设置时间戳为字符串格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        notification.setTimestamp(LocalDateTime.now().format(formatter));

        socketIOUtils.sendHeartbeat(destinateId);
        // 发送通知
        socketIOUtils.sendNotification(destinateId, notification);
    }
}




