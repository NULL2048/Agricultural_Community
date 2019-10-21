package team.se.acommunity.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team.se.acommunity.dao.MessageMapper;
import team.se.acommunity.entity.Message;

import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageMapper messageMapper;

    public List<Message> listConversations(int userId, int offset, int limit) {
        return messageMapper.listConversations(userId, offset, limit);
    }

    public int countConversation(int userId) {
        return messageMapper.countConversation(userId);
    }

    public List<Message> listLetters(String conversationId, int offset, int limit) {
        return messageMapper.listLetters(conversationId, offset, limit);
    }

    public int countLetter(String conversationId) {
        return messageMapper.countLetter(conversationId);
    }

    public int countUnreadLetter(int userId, String conversationId) {
        return messageMapper.countUnreadLetter(userId, conversationId);
    }
}
