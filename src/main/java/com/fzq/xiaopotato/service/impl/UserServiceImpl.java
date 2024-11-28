package com.fzq.xiaopotato.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.esotericsoftware.minlog.Log;
import com.fzq.xiaopotato.common.*;
import com.fzq.xiaopotato.common.utils.*;
import com.fzq.xiaopotato.exception.BusinessException;
import com.fzq.xiaopotato.mapper.*;
import com.fzq.xiaopotato.model.dto.common.IdDTO;
import com.fzq.xiaopotato.model.dto.common.PageDTO;
import com.fzq.xiaopotato.model.dto.user.UserLoginDTO;
import com.fzq.xiaopotato.model.dto.user.UserQueryDTO;
import com.fzq.xiaopotato.model.dto.user.UserRegisterDTO;
import com.fzq.xiaopotato.model.dto.user.UserUpdateDTO;
import com.fzq.xiaopotato.model.entity.*;
import com.fzq.xiaopotato.model.vo.NotificationVO;
import com.fzq.xiaopotato.model.vo.PostVO;
import com.fzq.xiaopotato.model.vo.UserVO;
import com.fzq.xiaopotato.service.*;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.fzq.xiaopotato.constant.UserConstant.ADMIN_ROLE;

/**
* @author zfeng
* @description 针对表【User(User table)】的数据库操作Service实现
* @createDate 2024-10-03 18:26:44
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{


    private static final String SALT = "potato";

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UsertagMapper usertagMapper;

    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private PosttagMapper posttagMapper;
    @Autowired
    private TagRecommendationUtils tagRecommendationUtils;

    @Autowired
    private UserfollowMapper userfollowMapper;

    @Autowired
    @Lazy
    private UserfollowService userfollowService;

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private RedissonClient redissonClient;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public Long userRegister(UserRegisterDTO userRegisterDTO) {

        String firstName = userRegisterDTO.getFirstName();
        String lastName = userRegisterDTO.getLastName();
        String userAccount = userRegisterDTO.getUserAccount();
        String userPassword = userRegisterDTO.getUserPassword();
        String email = userRegisterDTO.getEmail();
        String gender= userRegisterDTO.getGender();
        String phone = userRegisterDTO.getPhone();

        String checkPassword = userRegisterDTO.getCheckPassword();
        if (StringUtils.isAnyBlank(firstName, lastName, userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }

        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "The length of the user account cannot be less than 4");
        }

        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "The length of the password cannot be less than 8");
        }

        if (RegexValidator.isNotValidAccount(userAccount)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "The user account cannot contain special characters");
        }
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "The two passwords are not matched");
        }

        String lockKey = "user:register:" + userAccount;
        RLock lock = redissonClient.getLock(lockKey);
        try {
            while (true) {
                if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                    Log.info("getLock: " + Thread.currentThread().getId());
                    // duplicate account
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("user_account", userAccount);
                    queryWrapper.eq("is_delete", 0);
                    long count = userMapper.selectCount(queryWrapper);

                    if (count > 0) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "Account already exists.");
                    }

                    // encrypt
                    String encryptPassword = PasswordUtils.encryptPassword(userPassword, SALT);        // is account exist
                    User user = new User();
                    user.setUserAccount(userAccount);
                    user.setFirstName(firstName);
                    user.setLastName(lastName);
                    user.setUserPassword(encryptPassword);
                    user.setEmail(email);
                    user.setGender(gender);
                    user.setPhone(phone);
                    boolean saveResult = this.save(user);
                    if (saveResult) {
                        return user.getId();
                    } else {
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Register failed.");
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error(Thread.currentThread().getId() + " get lock failed");
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "get lock failed");
        } finally {
            // can only release itself lock
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                Log.info("release lock: {}", String.valueOf(Thread.currentThread().getId()));
            }

        }





    }

    @Override
    public UserVO userLogin(UserLoginDTO userLoginDTO) {
        String userAccount = userLoginDTO.getUserAccount();
        String userPassword = userLoginDTO.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // encrypt
        String encryptPassword = PasswordUtils.encryptPassword(userPassword, SALT);        // is account exist
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        queryWrapper.eq("user_password", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        // doesn't exist
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Wrong account or password.");
        }

        return getSafeUser(user);

    }

    @Override
    public Boolean userLogout(HttpServletRequest request) {
        UserVO currentUser = getCurrentUser(request);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        // get old token, put it into the blacklist
        String oldToken = request.getHeader("Authorization").substring(7);
        jwtUtils.addToBlacklist(oldToken);
        return true;
    }

    @Override
    public UserVO getCurrentUser(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // remove Bearer prefix
            if (jwtUtils.isTokenBlacklisted(token)) {
                // Token in blacklist
                throw new BusinessException(ErrorCode.NO_AUTH, "Token has been invalidated.");
            }
            Claims claims = jwtUtils.getClaimsFromToken(token);
            Long userId = claims.get("id", Long.class);
            User user = userMapper.selectById(userId);
            if (user != null) {
                return getSafeUser(user);
            }
        }
        return null;
    }

    @Override
    public int updateUser(UserUpdateDTO userUpdateDTO, UserVO currentUser, HttpServletRequest request) {
        UserVO user = getCurrentUser(request);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        Long id = user.getId();
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Invalid ID.");
        }

        // check authority
        // 2.1 admin can update any info, user can only update their info
        if (!isAdmin(currentUser) && !id.equals(currentUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        User oldUser = this.getById(user.getId());
        if (oldUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Cannot find user with id: " + user.getId());
        }

        if (RegexValidator.isValidEmail(userUpdateDTO.getEmail())) {
            oldUser.setEmail(userUpdateDTO.getEmail());
        } else {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Invalid Email.");
        }

        if (RegexValidator.isValidPhoneNumber(userUpdateDTO.getPhone())) {
            oldUser.setPhone(userUpdateDTO.getPhone());
        } else {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Invalid Phone.");
        }

        if (oldUser.getUserAvatar() != null) {
            String oldImageUrl = oldUser.getUserAvatar();
            try {
                UploadUtils.deleteImage(oldImageUrl);
            } catch (Exception e) {
                throw new BusinessException(ErrorCode.NULL_ERROR, "No old Image found");
            }

        }
        oldUser.setGender(userUpdateDTO.getGender());
        oldUser.setUserAvatar(userUpdateDTO.getUserAvatar());
        oldUser.setDescription(userUpdateDTO.getDescription());

        usertagMapper.delete(new QueryWrapper<Usertag>().eq("user_id", oldUser.getId()));
        List<String> newTags = TagUtils.extractTags(userUpdateDTO.getDescription());
        for (String tagContent : newTags) {
            Tag tag = tagMapper.selectOne(new QueryWrapper<Tag>().eq("content", tagContent));
            Long tagId;
            if (tag == null) {
                Tag newTag = new Tag();
                newTag.setContent(tagContent);
                tagMapper.insert(newTag);
                tagId = newTag.getId();
            } else {
                tagId = tag.getId();
            }
            Usertag usertag = new Usertag();
            usertag.setUserId(oldUser.getId());
            usertag.setTagId(tagId);
            usertagMapper.insert(usertag);
        }

        int updateResult =  this.baseMapper.updateById(oldUser);

        if (updateResult > 0) {
            logger.info("Starting recommendation generation on main thread: {}", Thread.currentThread().getId());

            List<Long> allPostIds = posttagMapper.selectList(new QueryWrapper<Posttag>().select("DISTINCT post_id"))
                    .stream()
                    .map(Posttag::getPostId)
                    .collect(Collectors.toList());

            // async generate and cache recommendation list
//            CompletableFuture<List<Long>> recommendedPostsFuture = tagRecommendationUtils.generateRecommendedPosts(id, allPostIds, usertagMapper, posttagMapper, tagMapper);
//            recommendedPostsFuture.thenAccept(recommendedPosts ->
//                    tagRecommendationUtils.cacheRecommendedPosts(id, recommendedPosts)
//            );
            CompletableFuture.runAsync(() -> {
                tagRecommendationUtils.generateRecommendedPosts(id, allPostIds, usertagMapper, posttagMapper, tagMapper)
                        .thenAccept(recommendedPosts -> tagRecommendationUtils.cacheRecommendedPosts(id, recommendedPosts));
            });
        }

        return updateResult;
    }

    @Override
    @Transactional
    public int deleteUserById(IdDTO idDTO, HttpServletRequest request) {
        UserVO user = getCurrentUser(request);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "user not logged in");
        }
        if (!ADMIN_ROLE.equals(user.getUserRole())) {
            throw new BusinessException(ErrorCode.NO_AUTH, "Only Admin can delete user.");
        }

        List<Post> userPosts = postMapper.selectList(
                new QueryWrapper<Post>()
                        .inSql("id", "SELECT post_id FROM UserPost WHERE user_id = " + idDTO.getId())
        );
        for (Post post : userPosts) {
            // delete file in oss
            if (post.getPostImage() != null) {
                String imageUrl = post.getPostImage();
                UploadUtils.deleteImage(imageUrl);
            }
            postMapper.deleteById(post);
        }

        return userMapper.deleteById(idDTO.getId());
    }

    @Override
    public Long getNotificationCount(HttpServletRequest request) {
        UserVO user = getCurrentUser(request);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "user not logged in");
        }
        Long userId = user.getId();


        QueryWrapper<Notification> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .eq("is_read", 0);

        Long result = notificationMapper.selectCount(queryWrapper);
        log.error("result of count notification is " + result);
        return result;


    }

    @Override
    public boolean isAdmin(UserVO user) {
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return user.getUserRole().equals(ADMIN_ROLE);
    }


    @Override
    public UserVO selectUserById(IdDTO idDTO, HttpServletRequest request) {
        if (getCurrentUser(request) == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        Long userId = idDTO.getId();
        User user = userMapper.selectById(userId);
        if (user == null || user.getIsDelete() == 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "User not found or deleted.");
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        userVO.setFansCount(getFollowerCount(idDTO));
        userVO.setFollowCount(getFollowedCount(idDTO));
        userVO.setFollowed(userfollowService.isFollowedByUser(idDTO, request));
        return userVO;
    }

    @Override
    public IPage<UserVO> listUserByPage(UserQueryDTO userQueryDTO, HttpServletRequest request) {
        UserVO user = getCurrentUser(request);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        Page<User> page = new Page<>(userQueryDTO.getCurrentPage(), userQueryDTO.getPageSize());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        Long userId = userQueryDTO.getUserId();
        String userName = userQueryDTO.getSearchName();
        if (userId > 0) {
            queryWrapper.eq("id", userId);
        }
        if (!StringUtils.isEmpty(userName)) {
            String lowerUserName = userName.toLowerCase();
            queryWrapper.nested(wrapper ->
                    wrapper.like("LOWER(first_name)", lowerUserName)
                            .or()
                            .like("LOWER(last_name)", lowerUserName)
                            .or()
                            .apply("LOWER(CONCAT(first_name, ' ', last_name)) LIKE {0}", "%" + lowerUserName + "%")
            );
        }
        IPage<User> pageResult = this.page(page, queryWrapper);
        List<UserVO> userVOList = pageResult.getRecords().stream().map(
                usr -> {
                    UserVO userVO = new UserVO();
                    BeanUtils.copyProperties(usr, userVO);
                    IdDTO idDTO = new IdDTO();
                    idDTO.setId(usr.getId());
                    userVO.setFansCount(getFollowerCount(idDTO));
                    userVO.setFollowCount(getFollowedCount(idDTO));
                    userVO.setFollowed(userfollowService.isFollowedByUser(idDTO, request));
                    return userVO;
                }
        ).collect(Collectors.toList());
        Page<UserVO> userVOPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        userVOPage.setRecords(userVOList);

        return userVOPage;

    }

    @Override
    public IPage<UserVO> listFansByPage(PageDTO pageDTO, HttpServletRequest request) {
        UserVO user = getCurrentUser(request);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        Long userId = user.getId();
        List<Long> fansIds = userfollowMapper.selectList(new QueryWrapper<Userfollow>().eq("followed_id", userId))
                .stream()
                .map(Userfollow::getFollowerId)
                .collect(Collectors.toList());
        if (fansIds.isEmpty()) {
            // If there are no saved posts, return an empty page
            return new Page<>();
        }
        Page<User> page = new Page<>(pageDTO.getCurrentPage(), pageDTO.getPageSize());

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", fansIds);
        IPage<User> pageResult = this.page(page, queryWrapper);
        List<UserVO> userVOList = pageResult.getRecords().stream().map(
                usr -> {
                    UserVO userVO = new UserVO();
                    BeanUtils.copyProperties(usr, userVO);
                    IdDTO idDTO = new IdDTO();
                    idDTO.setId(usr.getId());
                    userVO.setFansCount(getFollowerCount(idDTO));
                    userVO.setFollowCount(getFollowedCount(idDTO));
                    userVO.setFollowed(userfollowService.isFollowedByUser(idDTO, request));
                    return userVO;
                }
        ).collect(Collectors.toList());
        Page<UserVO> userVOPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        userVOPage.setRecords(userVOList);

        return userVOPage;

    }

    @Override
    public IPage<UserVO> listFollowsByPage(PageDTO pageDTO, HttpServletRequest request) {
        UserVO user = getCurrentUser(request);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        Long userId = user.getId();
        List<Long> followsIds = userfollowMapper.selectList(new QueryWrapper<Userfollow>().eq("follower_id", userId))
                .stream()
                .map(Userfollow::getFollowedId)
                .collect(Collectors.toList());
        if (followsIds.isEmpty()) {
            // If there are no saved posts, return an empty page
            return new Page<>();
        }
        Page<User> page = new Page<>(pageDTO.getCurrentPage(), pageDTO.getPageSize());

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", followsIds);
        IPage<User> pageResult = this.page(page, queryWrapper);
        List<UserVO> userVOList = pageResult.getRecords().stream().map(
                usr -> {
                    UserVO userVO = new UserVO();
                    BeanUtils.copyProperties(usr, userVO);
                    IdDTO idDTO = new IdDTO();
                    idDTO.setId(usr.getId());
                    userVO.setFansCount(getFollowerCount(idDTO));
                    userVO.setFollowCount(getFollowedCount(idDTO));
                    userVO.setFollowed(userfollowService.isFollowedByUser(idDTO, request));
                    return userVO;
                }
        ).collect(Collectors.toList());
        Page<UserVO> userVOPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        userVOPage.setRecords(userVOList);

        return userVOPage;

    }

    @Override
    public IPage<NotificationVO> listNotificationByPage(PageDTO pageDTO, HttpServletRequest request) {
        UserVO user = getCurrentUser(request);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        Long userId = user.getId();

        // 设置分页参数
        Page<Notification> page = new Page<>(pageDTO.getCurrentPage(), pageDTO.getPageSize());

        // 创建查询条件
        QueryWrapper<Notification> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .orderByDesc("create_time"); // 按创建时间降序排列

        // 分页查询
        IPage<Notification> notificationPage = notificationMapper.selectPage(page, queryWrapper);

        // 转换成 NotificationVO
        List<NotificationVO> notificationVOList = notificationPage.getRecords().stream().map(notification -> {
            NotificationVO notificationVO = new NotificationVO();
            BeanUtils.copyProperties(notification, notificationVO);
            notificationVO.setTimestamp(
                    new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(notification.getCreateTime())
            );
            return notificationVO;
        }).collect(Collectors.toList());

        // 设置返回的分页对象
        Page<NotificationVO> resultPage = new Page<>(pageDTO.getCurrentPage(), pageDTO.getPageSize(), notificationPage.getTotal());
        resultPage.setRecords(notificationVOList);
        notificationService.markNotificationsAsRead(userId);

        return resultPage;

    }


    private Integer getFollowerCount(IdDTO idDTO) {
        Long userId = idDTO.getId();
        Long count = userfollowMapper.selectCount(new QueryWrapper<Userfollow>().eq("followed_id", userId)); // get fans count: count in db in column "being followed"
        return count != null ? count.intValue() : 0;
    }

    private Integer getFollowedCount(IdDTO idDTO) {
        Long userId = idDTO.getId();
        Long count = userfollowMapper.selectCount(new QueryWrapper<Userfollow>().eq("follower_id", userId)); // get followed user count, count in db in column "be a follower"
        return count != null ? count.intValue() : 0;
    }


    private UserVO getSafeUser(User user) {
        UserVO safeUser = new UserVO();
        safeUser.setFirstName(user.getFirstName());
        safeUser.setLastName(user.getLastName());
        safeUser.setUserAccount(user.getUserAccount());
        safeUser.setEmail(user.getEmail());
        safeUser.setId(user.getId());
        safeUser.setUserAvatar(user.getUserAvatar());
        safeUser.setUserRole(user.getUserRole());
        safeUser.setGender(user.getGender());
        safeUser.setDescription(user.getDescription());
        safeUser.setPhone(user.getPhone());
        safeUser.setStatus(user.getStatus());
        IdDTO idDTO = new IdDTO();
        idDTO.setId(user.getId());
        safeUser.setFollowCount(getFollowedCount(idDTO));
        safeUser.setFansCount(getFollowerCount(idDTO));

        return safeUser;
    }
}




