package team.se.acommunity.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import team.se.acommunity.entity.Comment;
import team.se.acommunity.entity.DiscussPost;
import team.se.acommunity.entity.Page;
import team.se.acommunity.entity.User;
import team.se.acommunity.service.CommentService;
import team.se.acommunity.service.DiscussPostService;
import team.se.acommunity.service.UserService;
import team.se.acommunity.util.CommunityConstant;
import team.se.acommunity.util.CommunityUtil;
import team.se.acommunity.util.HostHolder;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private CommentService commentService;

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody // 不是返回网页，所以加上ResponseBody标签

    public String addDiscussPost(String title, String content) {
        User user = hostHolder.getUser();
        // 403表示没有权限
        if (user == null) {
            return CommunityUtil.getJSONString(403, "你还没有登录！");
        }

        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.insertDiscussPost(post);

        // 报错的情况将来统一处理
        return CommunityUtil.getJSONString(0, "发布成功！");
    }
    // 返回文章的详细页，因为不同的文章有不同的文章id，所以这里的访问路径是动态的根据不同文章id来的访问的
    @RequestMapping(path = "/detail/{discussPostId}", method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page) {
        // 帖子
        DiscussPost post = discussPostService.getDiscussPostById(discussPostId);
        model.addAttribute("post", post);
        //作者
        User user = userService.getUserById(post.getUserId());
        model.addAttribute("user", user);

        // 评论分页信息
        page.setLimit(5);
        page.setPath("/discuss/detail/" + discussPostId);
        page.setRows(post.getCommentCount());
        // 评论：给帖子的评论
        // 回复：给评论的评论
        // 评论列表
        List<Comment> commentList = commentService.listCommentsByEntity(ENTITY_TYPE_POST, post.getId(), page.getOffset(),
                page.getLimit());
        // 评论VO列表   VO表示就是要显示在页面上的列表 VO表示展示对象，在笔记中的Java命名规范领域模型命名规约中
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if (commentList != null) {
            for (Comment comment : commentList) {
                // 评论VO
                Map<String, Object> commentVO = new HashMap<>();
                // 评论
                commentVO.put("comment", comment);
                // 评论作者
                commentVO.put("user", userService.getUserById(comment.getUserId()));

                // 回复列表                                                                         // 这个标识最大值，也就是有多少条查多少条，不做分页了,然后在界面通过循环实现分页显示
                List<Comment> replyList = commentService.listCommentsByEntity(ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                // 回复VO列表
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if (replyList != null) {
                    for (Comment reply : replyList) {
                        Map<String, Object> replyVO = new HashMap<>();
                        replyVO.put("reply", reply);
                        replyVO.put("user", userService.getUserById(reply.getUserId()));
                        // 回复的目标
                        User target = reply.getTargetId() == 0 ? null : userService.getUserById(reply.getTargetId());
                        replyVO.put("target", target);

                        replyVoList.add(replyVO);
                    }
                }
                commentVO.put("replys", replyVoList);

                // 回复数量
                int replyCount = commentService.countCommentsByEntity(ENTITY_TYPE_COMMENT, comment.getId());
                commentVO.put("replyCount", replyCount);

                commentVoList.add(commentVO);
            }
        }

        model.addAttribute("comments", commentVoList);
        return "/test/discuss-detail";
    }
}
