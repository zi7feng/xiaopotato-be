package com.fzq.xiaopotato.common.jobs;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fzq.xiaopotato.common.utils.TagRecommendationUtils;
import com.fzq.xiaopotato.mapper.PosttagMapper;
import com.fzq.xiaopotato.mapper.TagMapper;
import com.fzq.xiaopotato.mapper.UsertagMapper;
import com.fzq.xiaopotato.model.entity.Posttag;
import com.fzq.xiaopotato.model.entity.Usertag;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
public class RecommendationScheduler {
    private static final Logger logger = LoggerFactory.getLogger(RecommendationScheduler.class);


    private final TagRecommendationUtils tagRecommendationUtils;
    private final UsertagMapper usertagMapper;
    private final PosttagMapper posttagMapper;
    private final TagMapper tagMapper;

    public RecommendationScheduler(TagRecommendationUtils tagRecommendationUtils,
                                   UsertagMapper usertagMapper,
                                   PosttagMapper posttagMapper, TagMapper tagMapper) {
        this.tagRecommendationUtils = tagRecommendationUtils;
        this.usertagMapper = usertagMapper;
        this.posttagMapper = posttagMapper;
        this.tagMapper = tagMapper;
    }

    /**
     * execute the job at 00:00 to update and cache recommendation list
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void updateAllUserRecommendations() {
        // Get all users' ids
        List<Long> allUserIds = usertagMapper.selectList(new QueryWrapper<Usertag>().select("DISTINCT user_id"))
                .stream()
                .map(Usertag::getUserId)
                .collect(Collectors.toList());
        // get all posts' ids
        List<Long> allPostIds = posttagMapper.selectList(new QueryWrapper<Posttag>().select("DISTINCT post_id"))
                .stream()
                .map(Posttag::getPostId)
                .collect(Collectors.toList());
        for (Long userId : allUserIds) {
            // generate recommendation list for each user, and cache into redis
            CompletableFuture<List<Long>> recommendedPostsFuture = tagRecommendationUtils.generateRecommendedPosts(userId, allPostIds, usertagMapper, posttagMapper, tagMapper);

            recommendedPostsFuture.thenAccept(recommendedPosts -> {
                tagRecommendationUtils.cacheRecommendedPosts(userId, recommendedPosts);
                logger.info("Cached recommendations for userId {} on thread: {}", userId, Thread.currentThread().getId());
            });
        }
    }
    @PostConstruct
    public void onStartup() {
        updateAllUserRecommendations();
    }
}
