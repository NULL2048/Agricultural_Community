package team.se.acommunity.event;

import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.omg.SendingContext.RunTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import team.se.acommunity.entity.DiscussPost;
import team.se.acommunity.entity.Event;
import team.se.acommunity.entity.Message;
import team.se.acommunity.service.DiscussPostService;
import team.se.acommunity.service.ElasticsearchService;
import team.se.acommunity.service.MessageService;
import team.se.acommunity.util.CommunityConstant;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class EventConsumer implements CommunityConstant {
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    private MessageService messageService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private ElasticsearchService elasticsearchService;

    // 注入要执行命令的所在目录
    @Value("${wk.image.command}")
    private String wkImageCommand;
    // 注入图片保存路径
    @Value("${wk.image.storage}")
    private String wkImageStorage;

    // 一个方法可以消费多个主题，一个主题也可以被多个方法消费
    // 下面这个注解就表示这个方法要消费这三个主题  参数中的ConsumerRecord固定写法，被动接受的消息都在这个对象中的value里
    @KafkaListener(topics = {TOPIC_COMMENT, TOPIC_LIKE, TOPIC_FOLLOW})
    public void handleCommentMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            logger.error("消息的内容为空！");
            return;
        }

        // 将json字符串转换为原有的对象，因为json字符串只是为了方便传输，提高传输效率用的
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);

        if (event == null) {
            logger.error("消息格式错误");
            return;
        }

        // 发送站内通知  消费者需要将envent事件中的消息数据取出来，封装成message对象然后再进行消费
        Message message = new Message();
        // 系统通知，所以发送者都是这个
        message.setFromId(SYSTEM_USER_ID);
        // 接收者者就是事件对象中的实体用户id
        message.setToId(event.getEntityUserId());
        // conversationId在系统消息中直接就填这中类型的消息所在主题就可以
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());

        // 将event相关的其他数据存入到message的content字段中
        Map<String, Object> content = new HashMap<>();
        // 将这个事件是谁触发的存入map
        content.put("userId", event.getUserId());
        content.put("entityType", event.getEntityType());
        content.put("entityId", event.getEntityId());

        // 判断一下事件有没有额外的数据，如果有就添加进来
        if (!event.getData().isEmpty()) {
            for (Map.Entry<String, Object> entry : event.getData().entrySet()) {
                content.put(entry.getKey(), entry.getValue());
            }
        }
        // 将content的内容转换成json字符串放到message的content字段中
        message.setContent(JSONObject.toJSONString(content));

        // 将消息保存到mysql数据库中
        messageService.saveMessage(message);
    }

    // 消费发帖事件
    @KafkaListener(topics = {TOPIC_PUBLISH})
    public void handlePublishMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            logger.error("消息的内容为空！");
            return;
        }

        // 将json字符串转换为原有的对象，因为json字符串只是为了方便传输，提高传输效率用的
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);

        if (event == null) {
            logger.error("消息格式错误");
            return;
        }

        // 将这个帖子找到
        DiscussPost post = discussPostService.getDiscussPostById(event.getEntityId());
        // 存入es服务器当中
        elasticsearchService.saveDiscussPost(post);
    }

    // 消费删帖事件
    @KafkaListener(topics = {TOPIC_DELETE})
    public void handleDeleteMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            logger.error("消息的内容为空！");
            return;
        }

        // 将json字符串转换为原有的对象，因为json字符串只是为了方便传输，提高传输效率用的
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);

        if (event == null) {
            logger.error("消息格式错误");
            return;
        }

        // 从es中删除
        elasticsearchService.removeDiscussPost(event.getEntityId());
    }

    // 消费分享事件
    @KafkaListener(topics = TOPIC_SHARE)
    public void handleShareMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            logger.error("消息的内容为空！");
            return;
        }

        // 将json字符串转换为原有的对象，因为json字符串只是为了方便传输，提高传输效率用的
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);

        if (event == null) {
            logger.error("消息格式错误");
            return;
        }

        String htmlUrl = (String) event.getData().get("htmlUrl");
        String fileName = (String) event.getData().get("fileName");
        String suffix = (String) event.getData().get("suffix");

        // quality加了这个参数就是对生成的图片压缩，压缩率75是一个比较合适的值，既能保证质量还不错，又能大大压缩图片大小，减轻服务器压力
        String cmd = wkImageCommand + " --quality 75 "
                + htmlUrl + " " + wkImageStorage + "/" + fileName + suffix;

        try {
            Runtime.getRuntime().exec(cmd);
            logger.info("生成长图成功：" + cmd);
        } catch (IOException e) {
            logger.error("生成长图失败：" + e.getMessage());
        }

    }
}
