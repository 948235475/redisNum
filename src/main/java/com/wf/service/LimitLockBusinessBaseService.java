package com.wf.service;

import com.wf.ifunc.CountFunction;
import com.wf.ifunc.LimitFunction;
import com.wf.ifunc.WriteFunction;
import com.wf.model.Result;

public interface LimitLockBusinessBaseService {

    class Config{

        private Object id;

        private String key;

        private String lockName;

        private LimitFunction<Object[]> limit;

        private CountFunction<Object[]> count;

        private WriteFunction<Object[]> writeBus;

        public Object getId() {
            return id;
        }

        public void setId(Object id) {
            this.id = id;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getLockName() {
            return lockName;
        }

        public void setLockName(String lockName) {
            this.lockName = lockName;
        }

        public LimitFunction<Object[]> getLimit() {
            return limit;
        }

        public void setLimit(LimitFunction<Object[]> limit) {
            this.limit = limit;
        }

        public CountFunction<Object[]> getCount() {
            return count;
        }

        public void setCount(CountFunction<Object[]> count) {
            this.count = count;
        }

        public WriteFunction<Object[]> getWriteBus() {
            return writeBus;
        }

        public void setWriteBus(WriteFunction<Object[]> writeBus) {
            this.writeBus = writeBus;
        }
    }

    /**
     * 初始化函数
     * @param id
     * @param limit
     * @param countFunction
     * @param writeFunction
     * @return
     */
    Config init(Object id,LimitFunction<Object []> limit,
                CountFunction<Object[]> countFunction,WriteFunction<Object[]> writeFunction);

    /**
     * 执行读
     */
    Result doRead(Object[] limit,Object[] count, Config config);

    /**
     * 执行写：新增和删除
     */

    Result doWrite(int num,Object[] limit,Object[] count, Object[] write,Config config);

    /**
     * 查询是否还有空间添加资源
     * @param num
     * @param limit
     * @param count
     * @param config
     * @return
     * 》=0 可添加的数量
     * -1   key不存在无法获取锁
     * -2   没有足够的资源
     * -3   异常
     * -4   未知错误
     */
    int freeze(int num,Object[] limit,Object[] count,Config config);

    /**
     * 回滚
     * @param num
     * @param limit
     * @param count
     * @param config
     * @return
     */
    int rollback(int num,Object[] limit,Object[] count,Config config);
}
