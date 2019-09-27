package team.se.acommunity.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import team.se.acommunity.service.AlphaService;

// controller类都要用这个标签标注
@Controller
@RequestMapping("/alpha")
public class AlphaController {
    // 将service的bean依赖注入给controller
    @Autowired
    private AlphaService alphaService;

    @RequestMapping("/hello")
    // 下面这个注解的作用是将controller的方法返回的对象通过适当的转换器转换为指定的格式之后，写入到response对象的body区，通常用来返回JSON数据或者是XML
    @ResponseBody // controller类中的方法都要用这个标签标注
    public String sayHello() {
        return "Hello Spring Boot";
    }

    @RequestMapping("/data")
    @ResponseBody
    public String getData() {
        return alphaService.find();
    }
}
