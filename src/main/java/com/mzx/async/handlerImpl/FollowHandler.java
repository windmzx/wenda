package com.mzx.async.handlerImpl;

import com.mzx.async.EventHandler;
import com.mzx.async.EventModel;
import com.mzx.async.EventType;
import com.mzx.model.EntityType;
import com.mzx.model.Message;
import com.mzx.service.MessageService;
import com.mzx.service.QuestionService;
import com.mzx.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by mzx on 2017/6/2.
 */
@Component
public class FollowHandler implements EventHandler {

    public static final Logger logger = LoggerFactory.getLogger(FollowHandler.class);
    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Autowired
    QuestionService questionService;

    @Override
    public void dohandle(EventModel eventModel) {
        Message message = new Message();
        message.setCreatedDate(new Date());
        message.setHasRead(0);
        message.setToId(eventModel.getEntityOwnerId());
        message.setFromId(Integer.MAX_VALUE);
        if (eventModel.getEntityType() == EntityType.USER) {
            message.setContent("用户" + eventModel.getActorId() + "关注了您");
        } else {
            message.setContent("用户" + eventModel.getActorId() + "关注了您的问题" + questionService.getQuestion(eventModel.getEntityId()).getTitle());
        }
        logger.info("发送了消息");
        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.FOLLOW);
    }
}
