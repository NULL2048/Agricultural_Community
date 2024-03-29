package team.se.acommunity.dao;

import org.apache.ibatis.annotations.Mapper;
import team.se.acommunity.entity.Comment;

import java.util.List;

@Mapper
public interface CommentMapper {
    /**
     * 根据实体来查询评论
     * @param entityType
     * @param entityId
     * @param offset
     * @param limit
     * @return
     */
    List<Comment> listCommentsByEntity(int entityType, int entityId, int offset, int limit);

    /**
     * 统计评论数
     * @param entityType
     * @param entityId
     * @return
     */
    int countCommentsByEntity(int entityType, int entityId);

    int insertComment(Comment comment);

    Comment getCommentById(int id);
}
