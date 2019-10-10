package team.se.acommunity.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import team.se.acommunity.entity.DiscussPost;

import java.util.List;

@Mapper // 加上注解才会让容器去扫描这个接口，然后把这个mapper装配到容器中
public interface DiscussPostMapper {
    // 根据用户id返回这个用户发表的所有帖子，而且为了以后的分页查询功能，建立了两个参数。这里还有返回全部帖子的功能，当userId传入的是0的时候
    List<DiscussPost> listDiscussPosts(int userId, int offset, int limit);

    // @Param注解用于给参数取别名
    // 如果只有一个参数，并且在<if>里使用，则必须加别名，如果向上面那个多个参数，使用if不用加别名
    // 这个方法查询某个用户发表的帖子数量,如果穿的参数是0，就显示所有的帖子有多少
    int getDiscussPostRows(@Param("userId") int userId);

    int insertDiscussPost(DiscussPost discussPost);
}
