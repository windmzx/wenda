package com.mzx.wenda.service;

import com.mzx.wenda.dao.MessageDAO;
import com.mzx.wenda.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by mzx on 2017/5/26.
 */
@Service
public class MessageService {

    @Autowired
    MessageDAO messageDAO;

    public int addMessage(Message message) {
        return messageDAO.addMessage(message);
    }

    //根据conversationId获取一个对话中所有的消息
    public List<Message> getConversationDetail(String conversationId, int offset, int limit) {
        return messageDAO.getConversationDetail(conversationId, offset, limit);
    }


    //根据用户的id获取一个用户有关的所有消息，并根据conversationId进行分组,并把id设置为数量
    public List<Message> getConversationList(int userId, int offset, int limit) {
        return messageDAO.getConversationList(userId, offset, limit);
    }

    //获取一个conversation中没有被阅读的消息的数量
    public int getConvesationUnreadCount(int userId, String conversationId) {
        return messageDAO.getConvesationUnreadCount(userId, conversationId);
    }

    public int upDateUnreadStatus(int id){
        return messageDAO.updateReadStatus(id);
    }
}
