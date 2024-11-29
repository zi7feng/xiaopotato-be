package com.fzq.xiaopotato.common.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fzq.xiaopotato.mapper.PosttagMapper;
import com.fzq.xiaopotato.mapper.TagMapper;
import com.fzq.xiaopotato.mapper.UsertagMapper;
import com.fzq.xiaopotato.model.entity.Posttag;
import com.fzq.xiaopotato.model.entity.Tag;
import com.fzq.xiaopotato.model.entity.Usertag;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Collections;
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
        for (int i = 0; i <= s1.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= s2.length(); j++) dp[0][j] = j;
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



    private double calculateJaccardSimilarity(List<String> userTags, List<String> postTags) {
        int intersectionSize = (int) userTags.stream().filter(postTags::contains).count();
        int unionSize = userTags.size() + postTags.size() - intersectionSize;
        return unionSize == 0 ? 0 : (double) intersectionSize / unionSize;
    }

    private int calculateEditDistanceScore(List<String> userTags, List<String> postTags) {
        int score = 0;

        for (String userTag : userTags) {
            userTag = StringUtils.upperCase(userTag);
            int minScoreForTag = Integer.MAX_VALUE; // 用于存储该 userTag 与 postTags 的最小得分

            for (String postTag : postTags) {
                postTag = StringUtils.upperCase(postTag);
                int distance = calculateEditDistance(userTag, postTag);

                // 根据最小编辑距离计算得分，但不累加，只取最小分数
                if (distance <= 7) {
                    int tempScore = 15 - distance * 2; // Partial match score
                    minScoreForTag = Math.min(minScoreForTag, tempScore); // 更新最小得分
                }
            }

            // 如果没有小于或等于 7 的匹配距离，则得分为 0
            score += minScoreForTag == Integer.MAX_VALUE ? 0 : minScoreForTag;
        }

        return score;
    }


    @Async
    public CompletableFuture<List<Long>> generateRecommendedPosts(Long userId, List<Long> postIds,
                                                                  UsertagMapper usertagMapper,
                                                                  PosttagMapper posttagMapper,
                                                                  TagMapper tagMapper) {
        logger.info("Generating recommended posts on thread: {}", Thread.currentThread().getId());

        // 获取用户的所有标签ID
        List<Long> userTagIds = usertagMapper.selectList(
                        new QueryWrapper<Usertag>().eq("user_id", userId))
                .stream()
                .map(Usertag::getTagId)
                .collect(Collectors.toList());

        if (userTagIds.isEmpty()) {
            logger.info("User with ID {} has no tags, skipping recommendation generation.", userId);
            cacheRecommendedPosts(userId, Collections.emptyList());  // 缓存空列表
            return CompletableFuture.completedFuture(Collections.emptyList());
        }

        // 获取用户的标签内容
        List<String> userTags = tagMapper.selectList(
                        new QueryWrapper<Tag>().in("id", userTagIds))
                .stream()
                .map(tag -> tag.getContent().toUpperCase())
                .collect(Collectors.toList());

        Map<Long, Double> postScores = new HashMap<>();

        for (Long postId : postIds) {
            logger.info("Starting calculation on Post: {}", postId);

            // 获取帖子的标签ID和内容
            List<Long> postTagIds = posttagMapper.selectList(
                            new QueryWrapper<Posttag>().eq("post_id", postId))
                    .stream()
                    .map(Posttag::getTagId)
                    .collect(Collectors.toList());

            List<String> postTags = tagMapper.selectList(
                            new QueryWrapper<Tag>().in("id", postTagIds))
                    .stream()
                    .map(tag -> tag.getContent().toUpperCase())
                    .collect(Collectors.toList());

            // 计算相似度得分
            double jaccardSimilarity = calculateJaccardSimilarity(userTags, postTags);
            int editDistanceScore = calculateEditDistanceScore(userTags, postTags);

            logger.info("jaccard similarity: {}", jaccardSimilarity);
            logger.info("edit distance score: {}", editDistanceScore);

            // 计算总分并加上波动系数
            double fluctuationFactor = 0.9 + (Math.random() * 0.2);
            double totalScore = (  (-0.85) * jaccardSimilarity - 0.15 * editDistanceScore) * fluctuationFactor;
            logger.info("total: {}", totalScore);

            postScores.put(postId, totalScore);
        }

        // 按分数降序排序并限制到前20名
        List<Long> recommendedPostIds = postScores.entrySet().stream()
                .sorted(Map.Entry.comparingByValue()) // 使用升序排序
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
        redisTemplate.delete(redisKey);
        redisTemplate.opsForList().rightPushAll(redisKey, recommendedPostIds);
        redisTemplate.expire(redisKey, 24, TimeUnit.HOURS);  // expire in 24 hours
        return CompletableFuture.completedFuture(null);
    }
}
