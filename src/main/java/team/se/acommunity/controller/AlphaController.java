package team.se.acommunity.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import team.se.acommunity.service.AlphaService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

// controller类都要用这个标签标注
@Controller
// 下面这个注解设置这个类的的访问地址
@RequestMapping("/alpha")
public class AlphaController {
    // 将service的bean依赖注入给controller
    @Autowired
    private AlphaService alphaService;
    // 下面这个注解设置的是这个方法的访问地址
    @RequestMapping("/hello")
    // 下面这个注解的作用是将controller的方法返回的对象通过适当的转换器转换为指定的格式之后，写入到response对象的body区，通常用来返回JSON数据或者是XML
    // 每一个Controller中想要返回给浏览器一些内容的方法都要用这个注解注释，没有要返回的内容的方法可以不用写,返回的数据会直接传回给浏览器
    @ResponseBody // controller类中的方法都要用这个标签标注
    public String sayHello() {
        return "Hello Spring Boot";
    }

    @RequestMapping("/data")
    @ResponseBody
    public String getData() {
        return alphaService.find();
    }

    @RequestMapping("/http")
    public void http(HttpServletRequest req, HttpServletResponse resp) {
        System.out.println(req.getMethod());
        System.out.println(req.getServletPath());
        Enumeration<String> enumeration = req.getHeaderNames();

        while (enumeration.hasMoreElements()) {
            String name = enumeration.nextElement();
            String value = req.getHeader(name);
            System.out.println(name + ":" + value);
        }
        System.out.println(req.getParameter("code"));

        resp.setContentType("text/html;charset=utf-8");

        try {
            PrintWriter writer = resp.getWriter();
            writer.write("<h1>河北农业大学</h1>");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // get请求

    // /students?current=1&limit=20
    // 下面这个注解里面也是有参数的，path就是表示这个方法的访问地址，method就是设置这个方法动请求方法
    @RequestMapping(path = "/students", method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(
            // 下面这个注释是给参数设置时用的，如果不写这个注释，那么这个方法通过浏览器传过来的参数名默认就是这个参数里的参数名，在浏览器端就要通过这个参数名来向服务器发送数据
            // 下面这个注解中name=current表示在浏览器中传过来名为current的键值对赋值给这个方法的current这个参数，required=false表示也可以不传参进来，defailtValue表示如果不传参进来的话，这个参数的默认值是多少
            @RequestParam(name = "current", required = false, defaultValue = "1") int current,
            @RequestParam(name = "limit", required = false, defaultValue = "10") int limit) {
        System.out.println(current);
        System.out.println(limit);
        return "some students";
    }

    // /student/123    将路径里面的信息直接当作数据传递进来
    @RequestMapping(path = "/student/{id}", method = RequestMethod.GET)
    @ResponseBody
    // 下面这个注解表示从路径中找到id这个数据，赋值给参数int id
    public String getStudent(@PathVariable("id") int id) {
        System.out.println(id);
        return "a student";
    }

    // post请求   一般浏览器向服务器提交数据就用post
    @RequestMapping(path = "/student", method = RequestMethod.POST)
    @ResponseBody
    public String saveStudent(String name, String age) {
        System.out.println(name);
        System.out.println(age);
        return "success";
    }

    // 响应HTML数据

    @RequestMapping(path = "/teacher", method = RequestMethod.GET)
    public ModelAndView getTeacher() {
        // 首先创建模板视图对象，这个对象里面存放着model数据模板，还有视图相关信息,比如页面路径
        ModelAndView mav = new ModelAndView();
        // 向对象中存入要响应的键值对信息
        mav.addObject("name", "张三");
        mav.addObject("age", "20");
        // 设置要把这个模板视图对象中的信息响应给哪个界面
        mav.setViewName("/demo/view");
        return mav;
    }

    // 这个是直接接收model模板对象，这个相较于上面那个方法更方便一点
    @RequestMapping(path = "/school", method = RequestMethod.GET)
    public String getSchool(Model model) {
        // 给模板对象添加数据
        model.addAttribute("name", "北京大学");
        model.addAttribute("age", "120");
        // 直接将模板对象返回给下面这个路径的动态页面
        return "/demo/view";
    }

    // 响应json数据，通常是在异步请求中使用
    // 就是当前页面不被刷新，但是悄悄的去执行了别的操作，这就叫做异步

    //Java对象 -> JSON字符串 -> JS对象  JSON能将java对象转成js对象

    @RequestMapping(path = "/emp", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getEmp() {
        // 把value设置成Object，这样值就可以存入任何类型了
        Map<String, Object> emp = new HashMap<>();
        emp.put("name", "张三");
        emp.put("age", 23);
        emp.put("salary", 8000.00);
        // 返回给服务器后，他就会把Java对象自动转换成JSON对象
        return emp;
    }

    @RequestMapping(path = "/emps", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>> getEmps() {
        List<Map<String, Object>> list = new ArrayList<>();
        // 把value设置成Object，这样值就可以存入任何类型了
        Map<String, Object> emp = new HashMap<>();
        emp.put("name", "张三");
        emp.put("age", 23);
        emp.put("salary", 8000.00);

        Map<String, Object> empq = new HashMap<>();
        empq.put("name", "李四");
        empq.put("age", 24);
        empq.put("salary", 9000.00);

        list.add(emp);
        list.add(empq);
        // 返回给服务器后，他就会把Java对象自动转换成JSON对象
        return list;
    }
}
