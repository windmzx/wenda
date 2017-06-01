package com.mzx.async;

import java.util.List;

/**
 * Created by mzx on 2017/5/29.
 */
public interface EventHandler {
    void dohandle(EventModel eventModel);

    List<EventType> getSupportEventTypes();

}
