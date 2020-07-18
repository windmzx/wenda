package com.mzx.wenda.async.handlerImpl;

import com.mzx.wenda.async.EventHandler;
import com.mzx.wenda.async.EventModel;
import com.mzx.wenda.async.EventType;
import com.mzx.wenda.model.EntityType;
import com.mzx.wenda.model.Message;
import com.mzx.wenda.service.MessageService;
import com.mzx.wenda.service.QuestionService;
import com.mzx.wenda.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by mzx on 2017/6/2.
 */
@Slf4j
@Component
public class FollowHandler implements EventHandler {

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
        log.info("发送了消息");
        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.FOLLOW);
    }
}
