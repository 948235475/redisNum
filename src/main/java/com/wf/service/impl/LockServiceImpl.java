package com.wf.service.impl;

import com.wf.service.LockService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class LockServiceImpl implements LockService {

    @Resource
    private ValueOperations<String,Object> valueOperations;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Resource
    private RedisServiceImpl redisService;

    private static final String READ_SUFFIX = ":read";

    private static final String WRITE_SUFFIX = ":write";

    /**
     * 获取读锁名称
     * @param key
     * @return
     */
    private String getReadLockName(String key){
        return key.concat(READ_SUFFIX);
    }

    /**
     * 获取写锁名称
     * @param key
     * @return
     */
    private String getWriteLockName(String key){
        return key.concat(WRITE_SUFFIX);
    }

    @Override
    public boolean lockIfHasReadRight(String key) {
        long result = redisService.lockReadLock(getReadLockName(key),getWriteLockName(key));
        if (result != 0){
            return false;
        }else {
            return true;
        }
    }

    @Override
    public boolean lockIfHasWriteRight(String key) {
        long result = redisService.lockReadLock(getReadLockName(key),getWriteLockName(key));
        if (result != 0){
            return false;
        }else {
            return true;
        }
    }

    @Override
    public boolean unlockReadLock(String key) {
        return 0 == redisService.unlockReadLock(getReadLockName(key));
    }

    @Override
    public boolean unlockWriteLock(String key) {
        return 0 == redisService.unlockWriteLock(getWriteLockName(key));
    }
}
