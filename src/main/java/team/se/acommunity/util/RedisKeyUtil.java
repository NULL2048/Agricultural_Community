package team.se.acommunity.util;

public class RedisKeyUtil {
    // 分隔符
    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";
    // 若A关注了B，则A是B的Follower（粉丝），B是A的Followee（目标）
    private static final String PREFIX_FOLLOWEE = "followee";
    private static final String PREFIX_FOLLOWER = "follower";
    // 验证码的Key
    private static final String PREFIX_KAPTCHA = "kaptcha";
    // 登陆凭证的key
    private static final String PREFIX_TICKET = "ticket";
    // 用户信息的Key
    private static final String PREFIX_USER = "user";
    // 独立访客
    private static final String PREFIX_UV = "uv";
    // 日活跃用户
    private static final String PREFIX_DAU = "dau";

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

    /**
     * 登录验证码
     * @param owner 用户的临时凭证，用来将验证码与用户绑定在一起,生成一个Key，这样能方便去比对输入的验证码一不一致，owner就是一个临时的随机字符串
     * @return
     */
    public static String getKaptchaKay(String owner) {
        return PREFIX_KAPTCHA + SPLIT + owner;
    }

    /**
     * 登录凭证
     * @param ticket
     * @return
     */
    public static String getTicketKey(String ticket) {
        return PREFIX_TICKET + SPLIT + ticket;
    }

    /**
     * 用户信息
     * @param userId
     * @return
     */
    public static String getUserKey(int userId) {
        return PREFIX_USER + SPLIT +userId;
    }

    /**
     * 单日UV的key
     * @param date
     * @return
     */
    public static String getUVKey(String date) {
        return PREFIX_UV + SPLIT + date;
    }

    /**
     * 某一个区间UV的key
     * @param startDate
     * @param endDate
     * @return
     */
    public static String getUVKey(String startDate, String endDate) {
        return PREFIX_UV + SPLIT + startDate + SPLIT +endDate;
    }

    /**
     * 单日活跃用户
     * @param date
     * @return
     */
    public static String getDAUKey(String date) {
        return PREFIX_DAU + SPLIT + date;
    }

    /**
     * 区间活跃用户
     * @param startDate
     * @param endDate
     * @return
     */
    public static String getDAUKey(String startDate, String endDate) {
        return PREFIX_DAU + SPLIT + startDate + SPLIT + endDate;
    }
}
