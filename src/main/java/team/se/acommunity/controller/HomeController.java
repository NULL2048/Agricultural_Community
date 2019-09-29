package team.se.acommunity.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import team.se.acommunity.service.DiscussPostService;
import team.se.acommunity.service.UserService;

// controller也是可以不用写访问路径的，如果不写controller的访问路径的话，到时候可以直接跳过controller路径，直接访问里面的方法的路径就行
@Controller
public class HomeController {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;

    @RequestMapping(path = "/index", method = RequestMethod.GET)
    public String get
}
