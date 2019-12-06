package com.wf.service;

public interface LockService {

    /**
     * 如果有读权限就锁
     * @param key
     * @return
     */
    boolean lockIfHasReadRight(String key);

    /**
     * 如果有写权限就锁
     * @param key
     * @return
     */
    boolean lockIfHasWriteRight(String key);

    /**
     * 解锁读锁
     * @param key
     * @return
     */
    boolean unlockReadLock(String key);

    /**
     * 解锁写锁
     * @param key
     * @return
     */
    boolean unlockWriteLock(String key);

}
