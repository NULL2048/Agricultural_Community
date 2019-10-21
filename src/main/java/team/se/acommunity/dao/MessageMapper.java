package team.se.acommunity.dao;

import org.apache.ibatis.annotations.Mapper;
import team.se.acommunity.entity.Message;

import java.util.List;

@Mapper
public interface MessageMapper {
    // 查询当前用户的会话列表，针对每个会话返回一条最新的私信
    List<Message> listConversations(int userId, int offset, int limit);
    // 查询当前用户的会话数量
    int countConversation(int userId);
    // 查询某个会话的全部私信信息
    List<Message> listLetters(String conversationId, int offset, int limit);
    // 查询某个会话所包含的私信数量
    int countLetter(String conversationId);
    // 查询未读私信数量  这个方法要负责实现两种业务，一种是统计用户所有未读私信总数，一个是统计某一个会话的未读私信数，所以这里用一个动态参数conversationId，如果查询某个会话的未读消息数才添加这个参数
    int countUnreadLetter(int userId, String conversationId);

}
