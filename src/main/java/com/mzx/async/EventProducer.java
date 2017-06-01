package com.mzx.async;

import com.alibaba.fastjson.JSONObject;
import com.mzx.util.JedisAdapter;
import com.mzx.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by mzx on 2017/5/29.
 */
@Service
public class EventProducer {

    public static final Logger logger = LoggerFactory.getLogger(EventProducer.class);
    @Autowired
    JedisAdapter jedisAdapter;

    public boolean produce(EventModel eventModel) {
        try {
            String key = RedisKeyUtil.getEventQueueKey();
            String json = JSONObject.toJSONString(eventModel);
            jedisAdapter.lpush(key, json);
            return true;
        } catch (Exception e) {
            logger.error("添加异步任务失败" + e.getMessage());
            return false;
        }
    }

}
