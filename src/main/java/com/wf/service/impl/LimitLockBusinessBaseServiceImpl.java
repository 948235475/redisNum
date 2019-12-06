package com.wf.service.impl;

import com.wf.enums.ResultEnum;
import com.wf.model.LimitLockResult;
import com.wf.model.Result;
import com.wf.service.LimitLockBusinessBaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public abstract class LimitLockBusinessBaseServiceImpl implements LimitLockBusinessBaseService {

    @Resource
    private RedisServiceImpl redisService;

    @Resource
    private ValueOperations<String,Object> valueOperations;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Resource
    private LockServiceImpl lockService;

    /**
     * 不存在key
     */
    private static final int NOT_EXISTS_KEY = -1;

    /**
     * 资源不足
     */
    private static final int NOT_ENOUGH_RESOURCE = -2;

    /**
     * 资源已出
     */
    private static final int OUT_OF_RESOURCE = -2;

    /**
     * 发生异常
     */
    private static final int EXCEPTION = -3;

    /**
     * 未知错误
     */
    private static final int UNKNOW_ERROR = -4;

    /**
     * 限制异常
     */
    private static final int EXCEPTION_ON_LIMIT = -5;

    /**
     * 计数异常
     */
    private static final int EXCEPTION_ON_COUNT = -6;

    @Override
    public Result doRead(Object[] limit, Object[] count, Config config) {
        Object o = valueOperations.get(config.getKey());
        if (null == o){
            if (lockService.lockIfHasReadRight(config.getLockName())){
                int now = config.getLimit().apply(limit) - config.getCount().apply(count);
                valueOperations.set(config.getKey(),now,5, TimeUnit.MINUTES);
                lockService.unlockReadLock(config.getLockName());
                Result result = new Result(ResultEnum.SUCCESS);
                result.setData(now);
                return result;
            }else {
                return new Result(ResultEnum.HAS_NO_READ_REGHT);
            }
        }else {
            Result result = new Result(ResultEnum.SUCCESS);
            result.setData(o);
            return result;
        }
    }

    @Override
    public Result doWrite(int num, Object[] limit, Object[] count, Object[] write, Config config) {
        int tmp = freeze(num, limit, count, config);
        switch (tmp){
            case NOT_EXISTS_KEY:
                return new Result(ResultEnum.HAS_NO_READ_REGHT);
            case NOT_ENOUGH_RESOURCE:
                return new Result(ResultEnum.RESOURCES_LIMIT);
            case EXCEPTION:
                return new Result(ResultEnum.CATCHED_EXCEPTION);
            case UNKNOW_ERROR:
                return new Result(ResultEnum.UNKNOW_ERROR);
            case EXCEPTION_ON_LIMIT:
                return new Result(ResultEnum.EXCEPTION_ON_LIMIT);
            case EXCEPTION_ON_COUNT:
                return new Result(ResultEnum.EXCEPTION_ON_COUNT);
            default:
                if (!lockService.lockIfHasWriteRight(config.getLockName())){
                    return new Result(ResultEnum.HAS_NO_WRITE_REGHT);
                }
                Result result = config.getWriteBus().apply(write);
                lockService.unlockWriteLock(config.getLockName());
                LimitLockResult lockResult = (LimitLockResult) result.getData();
                if (null == lockResult){
                    int rollbackResult = rollback(num, limit, count, config);
                    if (rollbackResult < 0){
                        //回滚失败删除key
                        redisTemplate.expire(config.getKey(),100,TimeUnit.MILLISECONDS);
                    }
                }else {
                    if (lockResult.getFail() > 0){
                        int rollBackResult;
                        if (num > 0){
                            rollBackResult = rollback(lockResult.getFail(),limit,count,config);
                        }else {
                            rollBackResult = rollback(-lockResult.getFail(),limit,count,config);
                        }
                        if (rollBackResult < 0){
                            //回滚失败删除key
                            redisTemplate.expire(config.getKey(),100,TimeUnit.MILLISECONDS);
                        }
                    }
                }
                return result;
        }
    }

    @Override
    public int freeze(int num, Object[] limitArgs, Object[] countArgs, Config config) {
        try {
            int limit = config.getLimit().apply(limitArgs);
            int count = config.getCount().apply(countArgs);
            if (limit < 0){
                lockService.unlockReadLock(config.getLockName());
                return EXCEPTION_ON_LIMIT;
            }
            if (count < 0){
                lockService.unlockReadLock(config.getLockName());
                return EXCEPTION_ON_COUNT;
            }
            List<String> keys = new ArrayList<>();
            keys.add(config.getKey());
            List<String> args = new ArrayList<>();
            args.add(num+"");
            int inr = redisService.freeze(keys,args).intValue();
            if (NOT_ENOUGH_RESOURCE == inr){
                return NOT_ENOUGH_RESOURCE;
            }else if (NOT_EXISTS_KEY == inr){
                if (lockService.lockIfHasReadRight(config.getLockName())){
                    valueOperations.set(config.getKey(),limit-count,3,TimeUnit.MINUTES);
                    lockService.unlockReadLock(config.getLockName());
                    return freeze(num, limitArgs, countArgs, config);
                }else {
                    return NOT_EXISTS_KEY;
                }
            }else if (inr >= 0 ){
                return inr;
            }else {
                return UNKNOW_ERROR;
            }
        }catch (Exception e){
            System.out.println(e);
            log.info(""+e);
            return EXCEPTION;
        }
    }

    @Override
    public int rollback(int num, Object[] limitArgs, Object[] countArgs, Config config) {
        try {
            int limit = config.getLimit().apply(limitArgs);
            int count = config.getCount().apply(countArgs);
            if (limit < 0){
                lockService.unlockReadLock(config.getLockName());
                return EXCEPTION_ON_LIMIT;
            }
            if (count < 0){
                lockService.unlockReadLock(config.getLockName());
                return EXCEPTION_ON_COUNT;
            }
            List<String> keys = new ArrayList<>();
            keys.add(config.getKey());
            List<String> args = new ArrayList<>();
            args.add(num+"");
            args.add(limit+"");
            int inr = redisService.rollback(keys,args).intValue();
            if (OUT_OF_RESOURCE == inr){
                return OUT_OF_RESOURCE;
            }else if (NOT_EXISTS_KEY == inr){
                if (lockService.lockIfHasReadRight(config.getLockName())){
                    valueOperations.set(config.getKey(),limit-count,5,TimeUnit.MINUTES);
                    lockService.unlockReadLock(config.getLockName());
                    return rollback(num, limitArgs, countArgs, config);
                }else {
                    return NOT_EXISTS_KEY;
                }
            }else if (inr >= 0 ){
                return inr;
            }else {
                return UNKNOW_ERROR;
            }
        }catch (Exception e){
            return EXCEPTION;
        }
    }
}
