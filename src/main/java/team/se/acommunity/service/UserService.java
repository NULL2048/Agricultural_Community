package team.se.acommunity.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import team.se.acommunity.dao.LoginTicketMapper;
import team.se.acommunity.dao.UserMapper;
import team.se.acommunity.entity.LoginTicket;
import team.se.acommunity.entity.User;
import team.se.acommunity.util.CommunityConstant;
import team.se.acommunity.util.CommunityUtil;
import team.se.acommunity.util.MailClient;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements CommunityConstant {
    @Autowired
    private UserMapper userMapper;
    // 注入邮件客户端
    @Autowired
    private MailClient mailClient;
    // 注入模板引擎，用来给邮箱发送HTML网页
    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    // 将域名给注入
    @Value("${community.path.domain}")
    private String domain;
    // 注入项目名,这个项目名已经在application.properties里面设置过了，已经存在了键值对里，注入进了spring容器，直接通过key来取得值就可以了
    @Value("${server.servlet.context-path}")
    private String contextPath;

    public User getUserById(int id) {
        return userMapper.getById(id);
    }

    public User getUserByEmail(String email) {
        return userMapper.getByEmail(email);
    }

    public int updatePassword(int id, String password) {
        return userMapper.updatePassword(id, password);
    }

    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();

        // 控制判断
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }

        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "账号不能为空！");
            return map;
        }

        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空！");
            return map;
        }

        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空！");
            return map;
        }

        // 验证账号是否已存在
        User u = userMapper.getByName(user.getUsername());
        if (u != null) {
            map.put("usernameMsg", "该账号已存在");
            return map;
        }
        // 验证邮箱是否已注册
        u = userMapper.getByEmail(user.getEmail());
        if (u != null) {
            map.put("emailMsg", "该邮箱已被注册");
            return map;
        }

        // 注册用户
        // 产生随机数将其存入数据库的salt字段中来和密码拼接，取得是5位随机数
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        // 设置用户类型，0是普通用户
        user.setType(0);
        // 设置帐号状态，0是未激活
        user.setStatus(0);
        // 设置这个账号的激活码
        user.setActivationCode(CommunityUtil.generateUUID());
        // 设置这个账号的默认随即头像，String.format这个方法就可以用占位符%d在字符串中占一个位置，然后再通过参数去设置这个字符串的最终内容，这里通过随机数方法来获取0-10000的随机数
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        // 发送激活邮件
        // 设置要传送的模板内容,这个Context对象是用来存储键值对的
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        // http://localhost:8080/ac/activation/101/code
        // 将要访问的url也动态写出来
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        // content才是真正要铜鼓哦邮件发送出去的内容  铜鼓哦模板引擎对象将context键值对传送给HTML静态模板，并且解析出来给context字符串
        String content = templateEngine.process("/mail/activation", context);
        // 将content字符串通过邮箱发送
        mailClient.sendMail(user.getEmail(), "惠农网激活账号", content);
        // 返回的map是空的就表示注册成功
        return map;
    }

    /**
     * 校验验证码激活账号
     * @param userId
     * @param code
     * @return
     */
    public int activation(int userId, String code) {
        User user = userMapper.getById(userId);
        if (user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(code)) {
            userMapper.updateStatus(userId, 1);
            return ACTIVATION_SUCCESS;
        } else {
            return ACTIVATION_FAILURE;
        }
    }

    /**
     * 登录业务层
     * @param username 登录账号
     * @param password 登陆密码
     * @param expiredSeconds 生成登陆凭证有效时间
     * @return
     */
    public Map<String, Object> login(String username, String password, int expiredSeconds) {
        Map<String, Object> map = new HashMap<>();

        // 空值处理
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "账号不能为空！");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空！");
            return map;
        }

        // 验证账号
        User user = userMapper.getByName(username);
        if (user == null) {
            map.put("usernameMsg", "该账号不存在！");
            return map;
        }
        // 验证状态
        if (user.getStatus() == 0) {
            map.put("usernameMsg", "该账号未激活！");
            return map;
        }
        // 验证密码
        password = CommunityUtil.md5(password + user.getSalt());
        if (!user.getPassword().equals(password)) {
            map.put("passwordMsg", "密码不正确！");
            return map;
        }

        // 生成登陆凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        // 添加登录凭证，通过随机生成字符串方法生成登录凭证
        loginTicket.setTicket(CommunityUtil.generateUUID());
        // 设置为有效凭证
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
        loginTicketMapper.insertLoginTicket(loginTicket);
        // 将登陆凭证传给controller
        map.put("ticket", loginTicket.getTicket());
        return map;
    }
    // 账号退出
    public void logout(String ticket) {
        // 将登陆凭证修改为无效
        loginTicketMapper.updateStatus(ticket, 1);
    }

    public LoginTicket getLoginTicket(String ticket) {
        return loginTicketMapper.getByTicket(ticket);
    }

    public int updateHeader(int userId, String headerUrl) {
        return userMapper.updateHeader(userId, headerUrl);
    }

    public User getUserByName(String username) {
        return userMapper.getByName(username);
    }
}
