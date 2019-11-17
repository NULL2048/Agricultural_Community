package team.se.acommunity.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;
import team.se.acommunity.dao.DiscussPostMapper;
import team.se.acommunity.entity.DiscussPost;
import team.se.acommunity.util.SensitiveFilter;

import java.util.List;

@Service
public class DiscussPostService {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    public List<DiscussPost> listDiscussPost(int userId, int offset, int limit, int orderMode) {
        return discussPostMapper.listDiscussPosts(userId, offset, limit, orderMode);
    }

    public int getDiscussPostRows(int userId) {
        return discussPostMapper.getDiscussPostRows(userId);
    }

    public int insertDiscussPost(DiscussPost post) {
        if (post == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        // 转译HTML标记，将内容中可能是HTML标签内容的给转译一下，比如<input> ，就会自动转换成转义字符,让这些标签字符串原样输出，而不会被浏览器解析成HTML样式来影响原本的页面。这个方法是spring MVC自带的
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));

        // 过滤敏感词
        post.setTitle(sensitiveFilter.filter(post.getTitle()));
        post.setContent(sensitiveFilter.filter(post.getContent()));

        return discussPostMapper.insertDiscussPost(post);
    }

    public DiscussPost getDiscussPostById(int id) {
        return discussPostMapper.getDiscussPostById(id);
    }

    public int updateCommentCount(int id, int commentCount) {
        return discussPostMapper.updateCommentCount(id, commentCount);
    }

    public int updateType(int id, int type) {
        return discussPostMapper.updateType(id, type);
    }

    public int updateStatus(int id, int type) {
        return discussPostMapper.updateStatus(id, type);
    }

    public int updateScore(int id, double score) {
        return discussPostMapper.updateScore(id, score);
    }
}
