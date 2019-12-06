package com.wf.service.impl;

import com.wf.config.RedisConfig;
import com.wf.service.RedisService;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class RedisServiceImpl implements RedisService {

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    public Object evalSha(String lua,List<String> keys,List<String> args){
        Object result = redisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection){
                Object nativeConnection = connection.getNativeConnection();

                //集群
                if (nativeConnection instanceof JedisCluster){
                    return ((JedisCluster)nativeConnection).eval(lua, keys, args);
                } else if (nativeConnection instanceof  Jedis) {
                    return ((Jedis)nativeConnection).eval(lua, keys, args);
                }
                return null;
            }
        });
        return result;
    }

    @Override
    public boolean addLock(String username, int value) {
        return false;
    }

    @Override
    public boolean reduceLock(String username, int value) {
        return false;
    }

    @Override
    public boolean lock(String key, String timeout, String value) {
        return false;
    }

    @Override
    public boolean unLock(String key, String timeout, String value) {
        return false;
    }

    @Override
    public Long distributlyLock(List<String> keys, List<String> args) {
        return null;
    }

    @Override
    public Long distributlyUnLock(List<String> keys, List<String> args) {
        return null;
    }


    //数量限制的处理

    @Override
    public Long freeze(List<String> keys, List<String> args) {
        return (Long)evalSha(RedisConfig.FREEZE_LUA,keys,args);
    }


    @Override
    public Long rollback(List<String> keys, List<String> args) {
        return (Long)evalSha(RedisConfig.ROLLBACK_LUA,keys,args);
    }

    @Override
    public Long lockReadLock(String readLockKey, String writeLockKey) {
        List<String> keys = new ArrayList<String>(){{
            add(writeLockKey);
            add(readLockKey);
        }};
        List<String> args = new ArrayList<>();
        return (Long)evalSha(RedisConfig.LOCK_LUA,keys,args);
    }

    @Override
    public Long lockWriteLock(String readLockKey, String writeLockKey) {
        List<String> keys = new ArrayList<String>(){{
            add(readLockKey);
            add(writeLockKey);
        }};
        List<String> args = new ArrayList<>();
        return (Long)evalSha(RedisConfig.LOCK_LUA,keys,args);
    }

    @Override
    public Long unlockReadLock(String readLockKey) {
        List<String> keys = new ArrayList<String>(){{
            add(readLockKey);
        }};
        List<String> args = new ArrayList<>();
        return (Long)evalSha(RedisConfig.UNLOCK_LUA,keys,args);
    }

    @Override
    public Long unlockWriteLock(String writeLockKey) {
        List<String> keys = new ArrayList<String>(){{
            add(writeLockKey);
        }};
        List<String> args = new ArrayList<>();
        return (Long)evalSha(RedisConfig.UNLOCK_LUA,keys,args);
    }
}
