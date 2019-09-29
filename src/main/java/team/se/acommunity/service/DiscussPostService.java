package team.se.acommunity.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team.se.acommunity.dao.DiscussPostMapper;
import team.se.acommunity.entity.DiscussPost;

import java.util.List;

@Service
public class DiscussPostService {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    public List<DiscussPost> listDiscussPost(int userId, int offset, int limit) {
        return discussPostMapper.listDiscussPosts(userId, offset, limit);
    }

    public int getDiscussPostRows(int userId) {
        return discussPostMapper.getDiscussPostRows(userId);
    }
}
