package com.wf.service;

import java.util.List;

public interface RedisService {

    boolean addLock(String username,int value);

    boolean reduceLock(String username,int value);

    boolean lock(String key, String timeout,String value);

    boolean unLock(String key, String timeout,String value);

    Long distributlyLock(List<String> keys,List<String> args);

    Long distributlyUnLock(List<String> keys,List<String> args);



    //对数量限制相关的处理

    /**
     * 检查lua脚本
     * @param keys
     * @param args
     * @return
     */
    Long freeze(List<String> keys,List<String> args);

    /**
     * 检查lua脚本
     * @param keys
     * @param args
     * @return
     */
    Long upsert(List<String> keys,List<String> args);

    /**
     * 执行回滚lua脚本
     * @param keys
     * @param args
     * @return
     */
    Long rollback(List<String> keys,List<String> args);

    /**
     * 锁-读锁
     * @param readLockKey
     * @param writeLockKey
     * @return
     */
    Long lockReadLock(String readLockKey, String writeLockKey);

    /**
     * 锁-写锁
     * @param readLockKey
     * @param writeLockKey
     * @return
     */
    Long lockWriteLock(String readLockKey,String writeLockKey);

    /**
     * 解锁——读锁
     * @param key
     * @return
     */
    Long unlockReadLock(String key);

    /**
     * 解锁——写锁
     * @param key
     * @return
     */
    Long unlockWriteLock(String key);
}
