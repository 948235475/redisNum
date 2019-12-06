package com.example.demo;

import com.wf.service.impl.LockServiceImpl;
import com.wf.service.impl.RedisServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Resource
    private RedisServiceImpl redisService;

    @Resource
    private LockServiceImpl lockService;
    @Test
    public void contextLoads() {
        List<String> keys = new ArrayList<>();
        keys.add("wf");
        List<String> args = new ArrayList<>();
        args.add("2");
        args.add("1");
        int inr = redisService.rollback(keys,args).intValue();
    }

}
