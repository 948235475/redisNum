package com.wf.service.impl;

import com.wf.enums.ResultEnum;
import com.wf.ifunc.CountFunction;
import com.wf.ifunc.LimitFunction;
import com.wf.ifunc.WriteFunction;
import com.wf.model.BigScreen;
import com.wf.model.Result;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ScreenLimitLockBusinessBaseServiceImpl extends LimitLockBusinessBaseServiceImpl{

    /**
     * redis key 前缀
     */
    private static final String REDIS_KEY_PREFIX = "big:screen:screens:";

    /**
     * redis 锁前缀
     */
    private static final String LOCK_PREFIX = "{big:screen:screens}:";

    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public Config init(Object id, LimitFunction<Object[]> limitFunction, CountFunction<Object[]> countFunction, WriteFunction<Object[]> writeFunction) {
        CountFunction<Object[]> count = args ->{
            try {
                //执行查询mongo存的总数
                Query query = new Query(
                        Criteria.where("username").is(args[0]));
                return (int)mongoTemplate.count(query, BigScreen.class);
            }catch (Exception e){
                return 0;
            }
        };
        //限制的数量
        LimitFunction<Object[]> limit = args -> 10;
        Config config = new Config();
        config.setId(id);
        config.setWriteBus(writeFunction);
        config.setCount(count);
        config.setLimit(limit);
        System.out.println(id);
        System.out.println(config.getId());
        config.setKey(REDIS_KEY_PREFIX.concat(config.getId().toString()));
        config.setLockName(LOCK_PREFIX.concat(config.getId().toString()));
        return config;
    }

    @Override
    public Result doWrite(int num ,Object[] limitArgs,Object[] countArgs,Object[] writeArgs,Config config){
        Result result = super.doWrite(num, limitArgs, countArgs, writeArgs, config);
        switch (result.getCode()){
            case 7:
                result.setEnum(ResultEnum.HAS_NO_USER_READ_RIGHT);
                break;
            case 5:
                result.setEnum(ResultEnum.SCREEN_LIMIT);
                break;
            case 6:
                result.setEnum(ResultEnum.HAS_NO_USER_WRITE_RIGHT);
                break;
            default:
                break;
        }
        return result;
    }
}
