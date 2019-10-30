package team.se.acommunity.util;

public class RedisKeyUtil {
    // 分隔符
    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";

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
     * @param userId
     * @return
     */
    public static String getUserLikeKey(int userId) {
         return PREFIX_USER_LIKE + SPLIT + userId;
    }
}
