package team.se.acommunity.controller;

import com.sun.org.apache.xpath.internal.operations.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import team.se.acommunity.dao.MessageMapper;
import team.se.acommunity.entity.Message;
import team.se.acommunity.entity.Page;
import team.se.acommunity.entity.User;
import team.se.acommunity.service.MessageService;
import team.se.acommunity.service.UserService;
import team.se.acommunity.util.HostHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MessageController {
    @Autowired
    private MessageService messageService;
    // 因为处理的是当前用户的操作，所以要注入HostHolder
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;

    // 私信列表
    @RequestMapping(path = "/letter/list", method = RequestMethod.GET)
    public String getLetterList(Model model, Page page) {
        User user = hostHolder.getUser();

        // 设置分页信息
        // 每一页显示五条数据
        page.setLimit(5);
        // 设置访问地址
        page.setPath("/letter/list");
        // 设置数据总条数
        page.setRows(messageService.countConversation(user.getId()));

        // 会话列表
        List<Message> conversationList = messageService.listConversations(user.getId(), page.getOffset(), page.getLimit());

        // 存储页面相关信息，如各个未读消息数等等
        List<Map<String, Object>> conversations = new ArrayList<>();
        if (conversationList != null) {
            for (Message message : conversationList) {
                Map<String, Object> map = new HashMap<>();
                // 存储每一个会话最新私信内容
                map.put("conversation", message);
                // 存储每一条会话中私信的数量
                map.put("letterCount", messageService.countLetter(message.getConversationId()));
                // 存储未读会话数
                map.put("unreadCount", messageService.countUnreadLetter(user.getId(), message.getConversationId()));

                // 取得会话的对方身份，要先判断一下相对于当前用户是fromID是对方用户还是toID是对方用户
                int targetId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
                map.put("target", userService.getUserById(targetId));

                conversations.add(map);
            }
        }

        model.addAttribute("conversations", conversations);

        // 查询用户未读消息总数
        int letterUnreadCount = messageService.countUnreadLetter(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);

        return "/site/letter";
    }

    @RequestMapping(path = "/letter/detail/{conversationId}", method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Page page, Model model) {
        // 设置分页信息
        page.setLimit(5);
        page.setPath("/letter/detail/" + conversationId);
        page.setRows(messageService.countLetter(conversationId));
    }
}
