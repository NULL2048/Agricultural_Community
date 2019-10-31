package team.se.acommunity.util;

public class RedisKeyUtil {
    // 分隔符
    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";
    // 若A关注了B，则A是B的Follower（粉丝），B是A的Followee（目标）
    private static final String PREFIX_FOLLOWEE = "followee";
    private static final String PREFIX_FOLLOWER = "follower";

    // 某个实体的赞
    // like:entity:entityType:entityId -> set(userId)      这里的value最好是存入set集合，因为这样的话这个功能不光能统计赞的数量，还能统计哪些用户给了赞，这样系统能更方便地进行功能扩充
    /**
     * 获得redis中的key
     * @param entityType
     * @param entityId
     * @return
     */
     public static String getEntityLikeKey(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    /**
     * 某个用户的赞
     * like:user:userId -> int
     * @param userId
     * @return
     */
    public static String getUserLikeKey(int userId) {
         return PREFIX_USER_LIKE + SPLIT + userId;
    }

    /**
     * 某个用户关注的实体
     * followee:userId:entityType -> zset(entityId, now)
     * 这里就要体现出要关注的用户和被关注的实体之间的关系，值是一个sortedset，里面存储的是userId这个用户关注实体的id，now是关注事件，如果有显示关注用户的需求，就可以根据关注的事件进行排序显示
     * @param userId 要关注的用户id
     * @param entityType 被关注的实体类型
     * @return
     */
    public static String getFolloweeKey(int userId, int entityType) {
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    /**
     * 某个实体拥有的粉丝
     * follower:entityType:entityId -> zset(userId, now)
     * 这里就用entityType:entityId可以唯一标识出一个用户实体 值中存的就是粉丝用户的id，依旧使用now来进行排序
     * @param entityType 实体类型
     * @param entityId 实体id
     * @return
     */
    public static String getFollowerKey(int entityType, int entityId) {
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }
}
