package team.se.acommunity.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import team.se.acommunity.entity.DiscussPost;
import team.se.acommunity.entity.User;
import team.se.acommunity.service.DiscussPostService;
import team.se.acommunity.service.UserService;
import team.se.acommunity.util.CommunityUtil;
import team.se.acommunity.util.HostHolder;

import java.util.Date;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

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
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model) {
        // 帖子
        DiscussPost post = discussPostService.getDiscussPostById(discussPostId);
        model.addAttribute("post", post);
        //作者
        User user = userService.getUserById(post.getUserId());
        model.addAttribute("user", user);

        return "/site/discuss-detail";
    }

}
