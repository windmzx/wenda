package com.mzx.async;

import com.alibaba.fastjson.JSONObject;
import com.mzx.util.JedisAdapter;
import com.mzx.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mzx on 2017/5/29.
 */
@Service
public class EventConsumer implements InitializingBean, ApplicationContextAware {
    public static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    private ApplicationContext applicationContext;
    private Map<EventType, List<EventHandler>> mapping = new HashMap<>();

    @Autowired
    JedisAdapter jedisAdapter;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        //得到所有的EventHandler的实例
        Map<String, EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);
        if (beans != null) {
            //进行遍历
            for (Map.Entry<String, EventHandler> entry : beans.entrySet()) {
                List<EventType> eventTypes = entry.getValue().getSupportEventTypes();

                for (EventType eventType : eventTypes) {
                    if (!mapping.containsKey(eventType)) {
                        mapping.put(eventType, new ArrayList<EventHandler>());
                    }
                    mapping.get(eventType).add(entry.getValue());
                }
            }
        }
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    String key = RedisKeyUtil.getEventQueueKey();
                    List<String> messages = jedisAdapter.brpop(0, key);

                    for (String message : messages) {
                        if (message.equals(key)) {
                            continue;
                        }
                        EventModel eventModel = JSONObject.parseObject(message, EventModel.class);
                        if (!mapping.containsKey(eventModel.getType())) {
                            logger.error("未注册的事件");
                            continue;
                        }
                        for (EventHandler eventHandler : mapping.get(eventModel.getType())) {
                            eventHandler.dohandle(eventModel);
                        }
                    }


                }
            }
        });
        thread.start();
    }

}
