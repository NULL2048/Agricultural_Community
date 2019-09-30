package team.se.acommunity.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LoginController {
    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public String getRegisterPage() {
        // 这个就是要返回这个controller操作要取得哪一个html页面，下面就写这个页面的路径就行，也是绝对路径，/表示templates文件夹，不用写.html后缀
        return "/site/register";
    }
}
