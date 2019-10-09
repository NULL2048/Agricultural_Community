package team.se.acommunity.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import team.se.acommunity.entity.DiscussPost;
import team.se.acommunity.entity.Page;
import team.se.acommunity.entity.User;
import team.se.acommunity.service.DiscussPostService;
import team.se.acommunity.service.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// controller也是可以不用写访问路径的，如果不写controller的访问路径的话，到时候可以直接跳过controller路径，直接访问里面的方法的路径就行
@Controller
public class HomeController {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;

    @RequestMapping(path = "/index", method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page) {
        // 方法调用前，spring MVC会会自动实例化model和page，并将page注入model，因为这两个都是参数
        // 所以，在Thymeleaf中可以直接访问page对象中的数据，不需要手动将page添加到model了
        page.setRows(discussPostService.getDiscussPostRows(0));
        page.setPath("/test/index"); // 页面的访问路径

        List<DiscussPost> list = discussPostService.listDiscussPost(0, page.getOffset(), page.getLimit());
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (list != null) {
            for (DiscussPost post : list) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);

                User user = userService.getUserById(post.getUserId());
                map.put("user", user);
                discussPosts.add(map);
            }
        }
        // 这个就是将discussPosts这个List对象作为值存入到model中，然后将他命名成discussPosts，以后从model中取discussPosts这个对象就要通过键名discussPosts来取得
        // 这个model就对被传送到界面，实现不同层级的数据传送
        model.addAttribute("discussPosts", discussPosts);
        // 返回的是模板的路径，也就是主页的路径,就是想要把model对象中存储的数据传给哪一个界面
        // 注意区分下面这个/index和上面那个/index。上面那个是在浏览器访问的时候写index就访问了这个controller，然后这个controller处理了数据后，return给了index.html这个模板，界面就给跳转到了indext.html了，下面这个return写的是html模板的名字
        return "/test/index";
    }
}
