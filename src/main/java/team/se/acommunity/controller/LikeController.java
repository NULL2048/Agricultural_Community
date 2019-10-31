package team.se.acommunity.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import team.se.acommunity.annotation.LoginRequired;
import team.se.acommunity.entity.User;
import team.se.acommunity.service.LikeService;
import team.se.acommunity.util.CommunityUtil;
import team.se.acommunity.util.HostHolder;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController {
    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    // 这个注释表明这个请求需要有登陆凭证，拦截器会拦截它
    @LoginRequired
    @RequestMapping(path = "/like", method = RequestMethod.POST)
    // 因为这里的点赞操作时异步请求，所以必须加上ResponseBody这个标签，通过这个路径发送请求的时候都是异步请求，不会刷新原有界面
    @ResponseBody // 加上他就表示是异步请求，从浏览器传过来的是json格式数据，他会自动根据key将json中的值取出来，赋值给这个方法的参数
    public String like(int entityType, int entityId, int entityUserId) {
        User user = hostHolder.getUser();

        // 点赞
        likeService.saveLike(user.getId(), entityType, entityId, entityUserId);

        // 获得点赞数量
        long likeCount = likeService.countEntityLike(entityType, entityId);
        // 获得状态
        int likeStatus = likeService.getEntityLikeStatus(user.getId(), entityType, entityId);

        // 将上面的两个值要发回给页面
        // 将这两个值封装一下，方便传输
        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);

        // 因为这里是异步请求，所以就不能跳转页面了，要返回JSON数据，就跟ajax一样，在浏览器端它会将这段ajax数据给解析出来，状态码0表示相应成功
        return CommunityUtil.getJSONString(0, null, map);
    }
}
