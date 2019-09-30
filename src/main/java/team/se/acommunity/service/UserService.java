package team.se.acommunity.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import team.se.acommunity.dao.UserMapper;
import team.se.acommunity.entity.User;
import team.se.acommunity.util.MailClient;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${community.path.domain}")
    private String domain;

    @Value()

    public User getUserById(int id) {
        return userMapper.getById(id);
    }
}
