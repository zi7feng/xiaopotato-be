package com.fzq.xiaopotato.common.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fzq.xiaopotato.mapper.PosttagMapper;
import com.fzq.xiaopotato.mapper.TagMapper;
import com.fzq.xiaopotato.mapper.UsertagMapper;
import com.fzq.xiaopotato.model.entity.Posttag;
import com.fzq.xiaopotato.model.entity.Tag;
import com.fzq.xiaopotato.model.entity.Usertag;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class TagRecommendationUtils {

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

    public List<Long> generateRecommendedPosts(Long userId, List<Long> postIds,
                                                      UsertagMapper usertagMapper,
                                                      PosttagMapper posttagMapper,
                                                      TagMapper tagMapper) {
        // 获取用户的所有 tagId
        List<Long> userTagIds = usertagMapper.selectList(
                        new QueryWrapper<Usertag>().eq("user_id", userId))
                .stream()
                .map(Usertag::getTagId)
                .collect(Collectors.toList());
        // 从 Tag 表中获取 usertagIds 对应的内容
        List<String> userTags = tagMapper.selectList(
                        new QueryWrapper<Tag>().in("id", userTagIds))
                .stream()
                .map(Tag::getContent)
                .collect(Collectors.toList());

        Map<Long, Integer> postScores = new HashMap<>();

        for (Long postId : postIds) {
            // 获取当前帖子对应的所有 tagId
            List<Long> postTagIds = posttagMapper.selectList(
                            new QueryWrapper<Posttag>().eq("post_id", postId))
                    .stream()
                    .map(Posttag::getTagId)
                    .collect(Collectors.toList());
            // 从 Tag 表中获取 posttagIds 对应的内容
            List<String> postTags = tagMapper.selectList(
                            new QueryWrapper<Tag>().in("id", postTagIds))
                    .stream()
                    .map(Tag::getContent)
                    .collect(Collectors.toList());
            // 计算当前帖子与用户标签的相似度得分
            int score = 0;
            for (String userTag : userTags) {
                for (String postTag : postTags) {
                    score += calculateEditDistance(userTag, postTag);
                }
            }

            postScores.put(postId, score);
        }

        // 按得分升序排序并取前 20 个帖子
        List<Long> recommendedPostIds =  postScores.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .limit(20)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        cacheRecommendedPosts(userId, recommendedPostIds);
        return recommendedPostIds;

    }

    public void cacheRecommendedPosts(Long userId, List<Long> recommendedPostIds) {
        String redisKey = "user_recommendation:" + userId;
        redisTemplate.opsForList().rightPushAll(redisKey, recommendedPostIds);
        redisTemplate.expire(redisKey, 24, TimeUnit.HOURS);  // 设置缓存过期时间为24小时
    }



}
