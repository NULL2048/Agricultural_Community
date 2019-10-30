package team.se.acommunity.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import team.se.acommunity.util.RedisKeyUtil;

@Service
public class LikeService {
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 点赞
     * @param userId 当前要点赞的用户
     * @param entityType 点赞对象的实体类型
     * @param entityId 点赞对象的id
     */
    public void saveLike(int userId, int entityType, int entityId) {
        // 因为点赞value是set类型，所以先判断userId是不是已经点过赞了，即已经存在在set当中了
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        // 检查当前用户是否已经点赞
        boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey, userId);

        // 已点赞就取消点赞，未点赞就添加点赞
        if (isMember) {
            redisTemplate.opsForSet().remove(entityLikeKey, userId);
        } else {
            redisTemplate.opsForSet().add(entityLikeKey, userId);
        }
    }

    /**
     * 统计某实体点赞数量
     * @param entityType
     * @param entityId
     * @return
     */
    public long countEntityLike(int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        // 直接统计这个key的value值个数就可以
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    /**
     * 查询某个人对某实体的点赞状态，这里选用int为返回值是为了为以后业务功能扩充做准备，比如以后如果想添加一个踩的功能，这样这个int就可以表示多种状态了
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    public int getEntityLikeStatus(int userId, int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);

        return redisTemplate.opsForSet().isMember(entityLikeKey, userId) ? 1 : 0;
    }
}
