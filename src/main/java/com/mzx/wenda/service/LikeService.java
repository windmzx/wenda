package com.mzx.wenda.service;

import com.mzx.wenda.util.JedisAdapter;
import com.mzx.wenda.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by mzx on 2017/5/28.
 */
@Service
public class LikeService {
    @Autowired
    JedisAdapter jedisAdapter;

    public long like(int userid, int entityType, int entityId) {

        String likekey = RedisKeyUtil.getLikeKey(entityType, entityId);
        String dislikekey = RedisKeyUtil.getDisLikeKey(entityType, entityId);

        jedisAdapter.sadd(likekey, String.valueOf(userid));
        jedisAdapter.srem(dislikekey, String.valueOf(userid));

        return jedisAdapter.scard(likekey);
    }

    public long dislike(int userid, int entityType, int entityId) {

        String likekey = RedisKeyUtil.getLikeKey(entityType, entityId);
        String dislikekey = RedisKeyUtil.getDisLikeKey(entityType, entityId);

        jedisAdapter.srem(likekey, String.valueOf(userid));
        jedisAdapter.sadd(dislikekey, String.valueOf(userid));

        return jedisAdapter.scard(dislikekey);
    }

    public long getLikeCount(int entityType, int entityId) {
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        return jedisAdapter.scard(likeKey);
    }

    public int getLikeStatus(int userId, int entityType, int entityId) {
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        if (jedisAdapter.sismember(likeKey, String.valueOf(userId))) {
            return 1;
        }
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        return jedisAdapter.sismember(disLikeKey, String.valueOf(userId)) ? -1 : 0;
    }

}
