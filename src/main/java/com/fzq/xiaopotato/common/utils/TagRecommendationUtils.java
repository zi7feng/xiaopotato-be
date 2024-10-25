package com.fzq.xiaopotato.common.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fzq.xiaopotato.mapper.PosttagMapper;
import com.fzq.xiaopotato.mapper.TagMapper;
import com.fzq.xiaopotato.mapper.UsertagMapper;
import com.fzq.xiaopotato.model.entity.Posttag;
import com.fzq.xiaopotato.model.entity.Tag;
import com.fzq.xiaopotato.model.entity.Usertag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class TagRecommendationUtils {

    private static final Logger logger = LoggerFactory.getLogger(TagRecommendationUtils.class);


    private final RedisTemplate<String, Object> redisTemplate;

    public TagRecommendationUtils(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public int calculateEditDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= s2.length(); j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = Math.min(dp[i - 1][j - 1], Math.min(dp[i - 1][j], dp[i][j - 1])) + 1;
                }
            }
        }
        return dp[s1.length()][s2.length()];
    }

    @Async
    public CompletableFuture<List<Long>> generateRecommendedPosts(Long userId, List<Long> postIds,
                                                                  UsertagMapper usertagMapper,
                                                                  PosttagMapper posttagMapper,
                                                                  TagMapper tagMapper) {
        logger.info("Generating recommended posts on thread: {}", Thread.currentThread().getId());

        // get user's all tagIds
        List<Long> userTagIds = usertagMapper.selectList(
                        new QueryWrapper<Usertag>().eq("user_id", userId))
                .stream()
                .map(Usertag::getTagId)
                .collect(Collectors.toList());
        // get content from the tag table by tagId
        List<String> userTags = tagMapper.selectList(
                        new QueryWrapper<Tag>().in("id", userTagIds))
                .stream()
                .map(Tag::getContent)
                .collect(Collectors.toList());

        Map<Long, Integer> postScores = new HashMap<>();

        for (Long postId : postIds) {
            // get post's all tagId
            List<Long> postTagIds = posttagMapper.selectList(
                            new QueryWrapper<Posttag>().eq("post_id", postId))
                    .stream()
                    .map(Posttag::getTagId)
                    .collect(Collectors.toList());
            // get content from the tag table by tagId
            List<String> postTags = tagMapper.selectList(
                            new QueryWrapper<Tag>().in("id", postTagIds))
                    .stream()
                    .map(Tag::getContent)
                    .collect(Collectors.toList());
            // calculate the similar rate score for each user
            int score = 0;
            for (String userTag : userTags) {
                for (String postTag : postTags) {
                    score += calculateEditDistance(userTag, postTag);
                }
            }

            postScores.put(postId, score);
        }

        // get the top 20 with the highest score
        List<Long> recommendedPostIds =  postScores.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .limit(20)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        cacheRecommendedPosts(userId, recommendedPostIds);
        return CompletableFuture.completedFuture(recommendedPostIds);

    }

    @Async
    public CompletableFuture<Void> cacheRecommendedPosts(Long userId, List<Long> recommendedPostIds) {
        logger.info("Caching recommended posts on thread: {}", Thread.currentThread().getId());
        String redisKey = "user_recommendation:" + userId;
        redisTemplate.opsForList().rightPushAll(redisKey, recommendedPostIds);
        redisTemplate.expire(redisKey, 24, TimeUnit.HOURS);  // expire 24 hours (schedule a task runs every 24 hours)
        return CompletableFuture.completedFuture(null);
    }



}
