package com.mzx.wenda.async;

import com.alibaba.fastjson.JSONObject;
import com.mzx.wenda.util.JedisAdapter;
import com.mzx.wenda.util.RedisKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by mzx on 2017/5/29.
 */
@Slf4j
@Service
public class EventProducer {

    @Autowired
    JedisAdapter jedisAdapter;

    public boolean produce(EventModel eventModel) {
        try {
            String key = RedisKeyUtil.getEventQueueKey();
            String json = JSONObject.toJSONString(eventModel);
            jedisAdapter.lpush(key, json);
            log.info("添加了一个异步任务");
            return true;
        } catch (Exception e) {
            log.error("添加异步任务失败" + e.getMessage());
            return false;
        }
    }

}
