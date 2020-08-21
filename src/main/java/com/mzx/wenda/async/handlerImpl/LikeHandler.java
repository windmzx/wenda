package com.mzx.wenda.async.handlerImpl;

import com.mzx.wenda.async.EventHandler;
import com.mzx.wenda.async.EventModel;
import com.mzx.wenda.async.EventType;
import org.springframework.stereotype.Component;


import java.util.Arrays;
import java.util.List;

/**
 * Created by mzx on 2017/5/29.
 */
@Component
public class LikeHandler implements EventHandler {
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
