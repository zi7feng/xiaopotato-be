package com.fzq.xiaopotato.common.jobs;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fzq.xiaopotato.common.utils.TagRecommendationUtils;
import com.fzq.xiaopotato.mapper.PosttagMapper;
import com.fzq.xiaopotato.mapper.TagMapper;
import com.fzq.xiaopotato.mapper.UsertagMapper;
import com.fzq.xiaopotato.model.entity.Posttag;
import com.fzq.xiaopotato.model.entity.Usertag;
import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RecommendationScheduler {
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
     * 每天凌晨运行此任务来更新所有用户的推荐帖子列表
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void updateAllUserRecommendations() {
        // 获取所有用户ID
        List<Long> allUserIds = usertagMapper.selectList(new QueryWrapper<Usertag>().select("DISTINCT user_id"))
                .stream()
                .map(Usertag::getUserId)
                .collect(Collectors.toList());
        // 获取所有帖子ID（假设posttagMapper能返回所有帖子ID）
        List<Long> allPostIds = posttagMapper.selectList(new QueryWrapper<Posttag>().select("DISTINCT post_id"))
                .stream()
                .map(Posttag::getPostId)
                .collect(Collectors.toList());
        for (Long userId : allUserIds) {
            // 为每个用户生成推荐的帖子列表，并将其缓存到 Redis 中
            tagRecommendationUtils.generateRecommendedPosts(userId, allPostIds, usertagMapper, posttagMapper, tagMapper);
        }
    }
    @PostConstruct
    public void onStartup() {
        updateAllUserRecommendations();
    }
}
