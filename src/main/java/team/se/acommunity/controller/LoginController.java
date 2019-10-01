package team.se.acommunity.controller;

import com.google.code.kaptcha.Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import team.se.acommunity.entity.User;
import team.se.acommunity.service.UserService;
import team.se.acommunity.util.CommunityConstant;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaProducer;

    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public String getRegisterPage() {
        // 这个就是要返回这个controller操作要取得哪一个html页面，下面就写这个页面的路径就行，也是绝对路径，/表示templates文件夹，不用写.html后缀
        return "/site/register";
    }

    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String getLoginPage() {
        // 下面就是返回一个字符串给浏览器，浏览器就会根据这个字符串的路径去访问这个页面
        return "/site/login";
    }

    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public String register(Model model, User user) {
        // user对象被自动放进了model中
        Map<String, Object> map = userService.register(user);
        if (map == null || map.isEmpty()) {
            model.addAttribute("msg", "恭喜您，注册成功，我们已经向您的邮箱发送了一封激活邮件，请尽快激活");
            model.addAttribute("target", "/index");
            // 将model传给下面这个return的界面，然后跳转到这个界面
            return "/site/operate-result";
        } else { // 注册失败
            // 取到service传过来的数据，存入到model中，用来将这些数据传送给/site/register这个界面
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));

            // 注册失败就跳转回/site/register这个界面
            return "/site/register";
        }
    }

    // http://localhost:8080/ac/activation/101/code
    // 下面这个路径就是按照这个格式的任意地址访问都会进入到这个方法，然后这个方法再去判断传进来的激活码对不对
    @RequestMapping(path = "/activation/{userId}/{code}", method = RequestMethod.GET)
                                          // 下面userId,code参数是从上面path中{}取出来的
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("code") String code) {
        int result = userService.activation(userId, code);
        if (result == ACTIVATION_SUCCESS) {
            model.addAttribute("msg", "恭喜您，您的账号已经激活成功！");
            model.addAttribute("target", "/login");
        } else if (result == ACTIVATION_REPEAT) {
            model.addAttribute("msg", "无效操作，该账号已经激活过了");
            model.addAttribute("target", "/index");
        } else {
            model.addAttribute("msg", "激活失败，您的激活链接已失效");
            model.addAttribute("target", "/index");
        }
        return "/site/operate-result";
    }

    @RequestMapping(path = "/kaptcha", method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse resp, HttpSession session) {
        // 生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        // 将验证码存入session
        session.setAttribute("kaptcha", text);

        // 将图片输出给浏览器
        resp.setContentType("image/png");
        try {
            OutputStream os = resp.getOutputStream();
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            logger.error("相应验证码失败：" + e.getMessage());
        }
    }
}
