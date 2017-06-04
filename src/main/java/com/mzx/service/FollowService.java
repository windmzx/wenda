package com.mzx.service;

import com.mzx.util.JedisAdapter;
import com.mzx.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by mzx on 2017/5/31.
 */
@Service
public class FollowService {

    public static final Logger logger = LoggerFactory.getLogger(FollowService.class);
    @Autowired
    JedisAdapter jedisAdapter;

    public boolean follow(int userid, int entityType, int entityId) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(entityType, userid);
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        Date date = new Date();
        Jedis jedis = jedisAdapter.getJedis();
        Transaction tx = jedis.multi();
        tx.zadd(followeeKey, date.getTime(), String.valueOf(entityId));
        tx.zadd(followerKey, date.getTime(), String.valueOf(userid));
        List<Object> res = jedisAdapter.exec(tx, jedis);
        return (res.size() == 2) && ((Long) res.get(0) > 0) && ((Long) res.get(1) > 0);
    }

    public boolean unfollow(int userid, int entityType, int entityId) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(entityType, userid);
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        Date date = new Date();
        Jedis jedis = jedisAdapter.getJedis();
        Transaction tx = jedis.multi();
        tx.zrem(followeeKey, String.valueOf(entityId));
        tx.zrem(followerKey, String.valueOf(userid));
        List<Object> res = jedisAdapter.exec(tx, jedis);
        return (res.size() == 2) && ((Long) res.get(0) > 0) && ((Long) res.get(1) > 0);
    }

    public List<Integer> getFollower(int entityType, int entityId, int count) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return getIdsFromSet(jedisAdapter.zrevrange(followerKey, 0, count));
    }

    public List<Integer> getFollower(int entityType, int entityId, int offset, int count) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return getIdsFromSet(jedisAdapter.zrevrange(followerKey, offset, offset + count));
    }

    public List<Integer> getFollowee(int entityType, int entityId, int count) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(entityType, entityId);
        return getIdsFromSet(jedisAdapter.zrevrange(followeeKey, 0, count));
    }

    public List<Integer> getFollowee(int entityType, int entityId, int offset, int count) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(entityType, entityId);
        return getIdsFromSet(jedisAdapter.zrevrange(followeeKey, offset, offset + count));
    }


    public long getFollowerCount(int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return jedisAdapter.zcard(followerKey);
    }

    public long getFolloweeCount(int userId, int entityType) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(entityType,userId);
        return jedisAdapter.zcard(followeeKey);
    }

    private List<Integer> getIdsFromSet(Set<String> idSet) {
        List<Integer> re = new ArrayList<>();
        for (String s : idSet) {
            re.add(Integer.parseInt(s));
        }
        return re;
    }

    public boolean isFollower(int userId, int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        logger.info("key " + followerKey);
        logger.info("key " + String.valueOf(userId));
        return jedisAdapter.zscore(followerKey, String.valueOf(userId)) != null;
    }
}
