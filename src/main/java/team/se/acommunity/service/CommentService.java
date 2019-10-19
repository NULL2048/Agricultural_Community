package team.se.acommunity.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import team.se.acommunity.dao.CommentMapper;
import team.se.acommunity.entity.Comment;
import team.se.acommunity.util.SensitiveFilter;

import java.util.List;

@Service
public class CommentService {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;

    public List<Comment> listCommentsByEntity(int entityType, int entityId, int offset, int limit) {
        return commentMapper.listCommentsByEntity(entityType, entityId, offset, limit);
    }

    public int countCommentsByEntity(int entityType, int entityId) {
        return commentMapper.countCommentsByEntity(entityType, entityId);
    }

    /**
     * 这个方法里面涉及到了两次DML操作（也就是对数据库中的数据的操作），所以这个方法要引入事务管理
     * @param comment
     * @return
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int saveComment(Comment comment) {
        if (comment == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }
        // 先过滤一下HTML标签，将发表内容中的HTML标签转译成普通字符，否则浏览器会把它认定为HTML标键进行渲染，会造成界面的错乱
        comment.setContent();
    }
}
