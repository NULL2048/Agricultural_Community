package team.se.acommunity.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team.se.acommunity.dao.CommentMapper;
import team.se.acommunity.entity.Comment;

import java.util.List;

@Service
public class CommentService {
    @Autowired
    private CommentMapper commentMapper;

    public List<Comment> listCommentsByEntity(int entityType, int entityId, int offset, int limit) {
        return commentMapper.listCommentsByEntity(entityType, entityId, offset, limit);
    }

    public int countCommentsByEntity(int entityType, int entityId) {
        return commentMapper.countCommentsByEntity(entityType, entityId);
    }
}
