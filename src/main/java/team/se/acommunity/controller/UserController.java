package team.se.acommunity.controller;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import team.se.acommunity.annotation.LoginRequired;
import team.se.acommunity.entity.User;
import team.se.acommunity.service.UserService;
import team.se.acommunity.util.CommunityUtil;
import team.se.acommunity.util.HostHolder;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@Controller
@RequestMapping("/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage() {
        return "/test/setting";
    }

    @LoginRequired
    @RequestMapping(path = "/setting/basic", method = RequestMethod.GET)
    public String getSettingBasicPage() {
        return "/test/settings/basic";
    }

    // 上传的时候表单方法必须为post
    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", "您还没有选择图片");
            return "/test/setting";
        }

        String fileName = headerImage.getOriginalFilename();
        // 学习string的这些方法
        String suffix = fileName.substring(fileName.lastIndexOf('.'));
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "文件的格式不正确");
            return "/test/setting";
        }

        // 生成随机文件名
        fileName = CommunityUtil.generateUUID() + suffix;
        // 确定文件存放的路径
        File dest = new File(uploadPath + "/" + fileName);
        try {
            // 将文件内容写入file
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败：" + e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发生异常");
        }

        // 更新当前用户的头像路径（web访问路径）
        // http://locahost:8080/ac/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getId(), headerUrl);

        return "redirect:/index";
    }
    /*
    * return “/site/index”是返回一个模板路径，本次请求没有处理完，DispatcherServlet会将Model中的数据和对应的模板提交给模板引擎，让它继续处理完这次请求。
    * return "redirect:/index"是重定向，表示本次请求已经处理完毕，但是没有什么合适的数据展现给客户端，建议客户端再发一次请求，访问"/index"以获得合适的数据。
    * */

    // 获取图片的方法，路径要按照上面那个方法设置的那样，在这里用{}引入一个变量，这个方法用get就行
    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    // 这个方法不是向浏览器放回一个字符串也不是像浏览器返回一个页面，而是返回一个二进制数据，所以这里的返回值类型用void就可以，利用responese就可以把数据传过去
                           // 取出访问路径中的{fileName}变量，将其赋值给参数String fileName
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        // 服务器存放路径
        fileName = uploadPath + "/" + fileName;
        // 文件的后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        // 相应图片
        response.setContentType("image/" + suffix);
        try { // 这个是1.7的语法，单独写在这个括号里编译的时候会自动加上finally，然后将这些流都关闭
            // 这个输出流是response的输出流，response是由spring mvc管理的，它会自动将这个输出流关闭
            OutputStream os = response.getOutputStream();
            // 但是这个文件输入流不是spring mvc管理的，需要手动关闭
            // 将路径中的文件写入到这个file输入流中
            FileInputStream fis = new FileInputStream(fileName);
            {
                // 使用一个字节流对象作为缓冲区，来分批将图片传入到response输出流中
                byte[] buffer = new byte[1024];
                int b = 0;
                // 如果返回-1 说明取不出来东西了
                // read是从fis中读取出内容
                while ((b = fis.read(buffer)) != -1) {
                    // write是从buffer中向os写入内容
                    os.write(buffer, 0, b);
                }
            }
        } catch (IOException e) {
            logger.error("读取头像失败：" + e.getMessage());
        }
    }
}