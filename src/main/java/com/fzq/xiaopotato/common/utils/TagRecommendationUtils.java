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

        if (userTagIds.isEmpty()) {
            logger.info("User with ID {} has no tags, skipping recommendation generation.", userId);
            cacheRecommendedPosts(userId, Collections.emptyList());  // cache empty list
            return CompletableFuture.completedFuture(Collections.emptyList());
        }

        // get user tag contents
        List<String> userTags = tagMapper.selectList(
                        new QueryWrapper<Tag>().in("id", userTagIds))
                .stream()
                .map(Tag::getContent)
                .collect(Collectors.toList());

        Map<Long, Double> postScores = new HashMap<>();

        for (Long postId : postIds) {
            logger.info("Starting calculation on Post: {}", postId);

            // get post's tag IDs and content
            List<Long> postTagIds = posttagMapper.selectList(
                            new QueryWrapper<Posttag>().eq("post_id", postId))
                    .stream()
                    .map(Posttag::getTagId)
                    .collect(Collectors.toList());

            List<String> postTags = tagMapper.selectList(
                            new QueryWrapper<Tag>().in("id", postTagIds))
                    .stream()
                    .map(Tag::getContent)
                    .collect(Collectors.toList());


            // Jaccard 相似度分数
            int intersectionSize = (int) userTags.stream().filter(postTags::contains).count();
            int unionSize = userTags.size() + postTags.size() - intersectionSize;
            double jaccardSimilarity = unionSize == 0 ? 0 : (double) intersectionSize / unionSize;
            int jaccardBonus = (int) (jaccardSimilarity * 20); // 将 Jaccard 相似度比例转为加分项

            int exactMatchBonus = 0; // Bonus score for exact matches
            int totalEditDistance = 0; // Cumulative edit distance score

            // Calculate score based on tag similarity
            for (String userTag : userTags) {
                boolean exactMatch = false;
                int minDistance = Integer.MAX_VALUE;

                for (String postTag : postTags) {
                    if (userTag.equals(postTag)) {
                        exactMatch = true;
                        break; // prioritize exact match
                    } else {
                        int distance = calculateEditDistance(userTag, postTag);
                        minDistance = Math.min(minDistance, distance);
                    }
                }
                // Apply exact match bonus or edit distance
                if (exactMatch) {
                    exactMatchBonus += 5; // Give a high bonus for exact matches
                } else {
                    totalEditDistance += minDistance; // add minimum edit distance for non-exact matches
                }
            }

            // Adjust score by considering tag matching ratio
            int matchingRatioBonus = 10 * exactMatchBonus / postTags.size();

            logger.info("totalEditDistance: {}", totalEditDistance);
            logger.info("exact match bonus: {}", exactMatchBonus);
            logger.info("matchingRatioBonus: {}", matchingRatioBonus);
            logger.info("jaccardBonus: {}", jaccardBonus);

            // Total score (lower is better)
            double totalScore = 0.5 * exactMatchBonus - 0.3 * jaccardBonus - 0.1 * totalEditDistance - 0.1 * matchingRatioBonus;
            logger.info("total Score: {}", totalScore);

            postScores.put(postId, totalScore);
        }

        // Sort and return the top 20 posts
        List<Long> recommendedPostIds = postScores.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .limit(20)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

//        cacheRecommendedPosts(userId, recommendedPostIds);
        return CompletableFuture.completedFuture(recommendedPostIds);
    }


    @Async
    public CompletableFuture<Void> cacheRecommendedPosts(Long userId, List<Long> recommendedPostIds) {
        logger.info("Caching recommended posts on thread: {}", Thread.currentThread().getId());
        String redisKey = "user_recommendation:" + userId;
        if (recommendedPostIds.isEmpty()) {
            redisTemplate.delete(redisKey);
        } else {
            redisTemplate.opsForList().rightPushAll(redisKey, recommendedPostIds);
            redisTemplate.expire(redisKey, 24, TimeUnit.HOURS);  // expire 24 hours (schedule a task runs every 24 hours)
        }

        return CompletableFuture.completedFuture(null);
    }



}
