package team.se.acommunity.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import team.se.acommunity.entity.Comment;
import team.se.acommunity.service.CommentService;
import team.se.acommunity.util.HostHolder;

import java.util.Date;

@Controller
@RequestMapping(path = "/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;
    // 添加评论需要知道是谁添加的，所以需要通过这个hostHolder方法得到当前登录的用户信息
    @Autowired
    private HostHolder hostHolder;

    // 因为添加评论之后希望重定向到当前的文章页面，但是文章页面的访问路径中有动态变量帖子id，所以添加评论的访问地址也需要将文章id传进来
    @RequestMapping(path = "add/{discussPostId}", method = RequestMethod.POST)
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment) {
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());

        commentService.saveComment(comment);
        // 结束本次请求，重定向到评论的帖子界面。因为这个方法不需要想别的页面返回什么信息了，因为在这里重定向本次请求就结束了，所以这个方法就没有model这个对象
        return "redirect:/discuss/detail/" + discussPostId;
    }
}
