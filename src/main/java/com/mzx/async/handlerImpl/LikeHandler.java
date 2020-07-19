package com.mzx.async.handlerImpl;

import com.mzx.async.EventHandler;
import com.mzx.async.EventModel;
import com.mzx.async.EventType;
import com.mzx.model.EntityType;
import com.mzx.model.Message;
import com.mzx.service.LikeService;
import com.mzx.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by mzx on 2017/5/29.
 */
@Component
public class LikeHandler implements EventHandler {
    @Autowired
    LikeService likeService;

    @Autowired
    MessageService messageService;

    @Override
    public void dohandle(EventModel eventModel) {
        int actorId = eventModel.getActorId();
        int entityId = eventModel.getEntityId();
        int ownerId = eventModel.getEntityOwnerId();
        EventType eventType = EventType.valueOf(String.valueOf(eventModel.getType()));


        Message message = new Message();
        message.setHasRead(0);
        message.setToId(actorId);
        message.setFromId(actorId);
        message.setContent("用户赞了你的评论");
        message.setCreatedDate(new Date());

        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKE);
    }
}
