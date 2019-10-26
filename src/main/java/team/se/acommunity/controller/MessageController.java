package team.se.acommunity.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import team.se.acommunity.entity.Message;
import team.se.acommunity.entity.Page;
import team.se.acommunity.entity.User;
import team.se.acommunity.service.MessageService;
import team.se.acommunity.service.UserService;
import team.se.acommunity.util.CommunityUtil;
import team.se.acommunity.util.HostHolder;

import java.util.*;

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

        return "/test/letter";
    }

    /**
     * 获得私信详细信息
     * @param conversationId
     * @param page
     * @param model
     * @return
     */
    @RequestMapping(path = "/letter/detail/{conversationId}", method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Page page, Model model) {
        // 设置分页信息
        page.setLimit(5);
        page.setPath("/letter/detail/" + conversationId);
        page.setRows(messageService.countLetter(conversationId));

        // 得到得到一页私信的集合，以后点下一页就会重新进到这个cottoller中，因为page的path就是这个路径，并且点击下一页之后page中的offset也会改变，所以就会取到下一页数据
        List<Message> letterList = messageService.listLetters(conversationId, page.getOffset(), page.getLimit());

        List<Map<String, Object>> letters = new ArrayList<>();
        if (letterList != null) {
            for (Message message : letterList) {
                Map<String, Object> map = new HashMap<>();
                map.put("letter", message);
                // 将每一条私信的发从者取出来，因为会在页面中显示
                map.put("fromUser", userService.getUserById(message.getFromId()));

                letters.add(map);
            }
        }

        model.addAttribute("letters", letters);
        // 查询私信目标
        model.addAttribute("target", getLetterTarget(conversationId));

        // 设置已读
        List<Integer> ids = getUnreadLetterIds(letterList);
        // 这里要先判断ids是否为空，如果为空就说明没有未读消息，这里需要注意需要用isEmpty这个方法，因为ids永远不可能为null，因为在方法里面已经创建好了实例
        if (!ids.isEmpty()) {
            messageService.updateReadMessage(ids);
        }

        return "/site/letter-detail";
    }

    /**
     * 用来取得当前登陆账号中会话的对方的用户信息，这里构建一个私有方法，方便以后复用
     * @param conversationId 会话ID
     * @return
     */
    private User getLetterTarget(String conversationId) {
        // 因为会话ID就是会话双方的ID拼接而成的，所以直接用方法把String拆分成两个字符串
        String[] ids = conversationId.split("_");
        // 将字符串ID转换成数值ID
        int id1 = Integer.valueOf(ids[0]);
        int id2 = Integer.valueOf(ids[1]);

        // 取出当前登录用户，并且和上面的两个ID比较，不一样的就是会话对方用户
        if (hostHolder.getUser().getId() != id1) {
            return userService.getUserById(id1);
        } else {
            return userService.getUserById(id2);
        }
    }

    /**
     * 写一个私有方法，用来取得消息集合中所有未读消息的id
     * @param letterList
     * @return
     */
    private List<Integer> getUnreadLetterIds(List<Message> letterList) {
        List<Integer> ids = new ArrayList<>();

        if (letterList != null) {
            for (Message message : letterList) {
                // 要保证当前用户是私信的接收方才行，这样它对这条消息才有已读未读的区别
                if (hostHolder.getUser().getId() == message.getToId() && message.getStatus() == 0) {
                    ids.add(message.getId());
                }
            }
        }

        return ids;
    }
    // 只要是要提交数据的请求，请求方法都需要用post
    @RequestMapping(path = "/letter/send", method = RequestMethod.POST)
    @ResponseBody // 这个方法是异步的，所以这里要加ResponseBody这个标签
    public String sendMessage(String toName, String content) {
        Integer.valueOf("abc");
        User target = userService.getUserByName(toName);

        if (target == null) {
            return CommunityUtil.getJSONString(1, "目标用户不存在！");
        }

        Message message = new Message();
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(target.getId());
        message.setContent(content);
        // 会话ID要小的拼接在前面
        if (message.getToId() < message.getFromId()) {
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        } else {
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        }
        message.setCreateTime(new Date());

        messageService.saveMessage(message);
        // 如果没有问题直接给页面返回一个0状态就可以了
        return CommunityUtil.getJSONString(0);
    }
}
