package com.example.demo;

import com.wf.service.impl.LockServiceImpl;
import com.wf.service.impl.RedisServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
        List<String> list = new ArrayList<String>();

        list.add("111");
        list.add("222");
        list.add("333");
        list.add("444");
        list.add("333");
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()){
            String a = iterator.next();
            if (a.equals("222")){
                list.remove(a);
            }
        }
        ExecutorService fixedThreadPool = Executors.newScheduledThreadPool(4);
        System.out.println(Arrays.toString(list.toArray()));
    }

    @Test
    public void time(){
        String startTime = "2020-11-08 17:12:00";
        String endTime = "2020-11-08 17:23:00";
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date endDate = null;
        Date startDate = null;
        try {
            startDate = dateFormat.parse(startTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            endDate = dateFormat.parse(endTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Double datePoor = getDatePoor(startDate,endDate);
        System.out.println(datePoor);
    }


    public static Double getDatePoor(Date startDate, Date endDate) {

        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - startDate.getTime();
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        Long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        // long sec = diff % nd % nh % nm / ns;
        Long mymint = diff/1000/60;
        int intValue = mymint.intValue();
        BigDecimal b = new BigDecimal((double)intValue/60);
        Double result = b.setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue();
        return result;
    }


}
