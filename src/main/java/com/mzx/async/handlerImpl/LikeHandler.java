package com.mzx.async.handlerImpl;

import com.mzx.async.EventHandler;
import com.mzx.async.EventModel;
import com.mzx.async.EventType;
import com.mzx.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.Arrays;
import java.util.List;

/**
 * Created by mzx on 2017/5/29.
 */
@Component
public class LikeHandler implements EventHandler {
    @Autowired
    LikeService likeService;
    @Override
    public void dohandle(EventModel eventModel) {
        int actorId = eventModel.getActorId();
        int entityId = eventModel.getEntityId();
        int ownerId = eventModel.getEntityOwnerId();
        EventType eventType = EventType.valueOf(String.valueOf(eventModel.getType()));

    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKE);
    }
}
