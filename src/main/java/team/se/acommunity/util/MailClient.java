package team.se.acommunity.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

// 下面这个注解也是表示将这个bean加入到spring容器中，这个注解是一个通用的bean，在哪一个层次都可以用
@Component
public class MailClient {
    private static final Logger logger = LoggerFactory.getLogger(MailClient.class);
    // 这个JavaMailSender类也是由spring管理的，所以直接从spring容器中拿就可以，里面的内容就是在配置文件里配置的那些东西
    @Autowired
    private JavaMailSender mailSender;

    // 这个标注表示要将括号里面的key所对应的值注入到下面from对象中，这个key就是在配置文件中配置的，也已经被注入到spring容器中了，可以直接取出来用
    @Value("${spring.mail.username}")
    private String from;

    public void sendMail(String to, String subject, String content){
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            // 设置邮件相关内容
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            // 第二个参数是true，表示可以发送HTML，他会将HTML解析成网页发送
            helper.setText(content, true);
            // 发送邮件
            mailSender.send(helper.getMimeMessage());
        } catch (MessagingException e) {
            logger.error("发送邮件失败" + e.getMessage());
        }
    }
}
