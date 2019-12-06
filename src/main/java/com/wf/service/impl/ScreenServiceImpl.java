package com.wf.service.impl;

import com.wf.dao.impl.ScreenDaoImpl;
import com.wf.enums.ResultEnum;
import com.wf.ifunc.WriteFunction;
import com.wf.model.BigScreen;
import com.wf.model.LimitLockResult;
import com.wf.model.Result;
import com.wf.service.LimitLockBusinessBaseService;
import com.wf.service.ScreenService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ScreenServiceImpl implements ScreenService {

    @Resource
    private ScreenDaoImpl screenDao;

    @Resource
    private ScreenLimitLockBusinessBaseServiceImpl screenLimitLockBusinessBaseService;

    @Override
    public Result save(String username, String screenName) {
        BigScreen bigScreen = new BigScreen();
        bigScreen.setUsername(username);
        bigScreen.setScreenName(screenName);

        Result result;
        WriteFunction<Object[]> subWrite = x -> {
            Result result1 = null;
            try {
                result1 = new Result();
                BigScreen bigScreen1 = screenDao.save(bigScreen);
                result1.setData(new LimitLockResult(1,0));
            } catch (Exception e) {
                e.printStackTrace();
                result1.setCode(ResultEnum.IS_NOT_INITIAL.getCode());
                result1.setMessage("未知异常");
                result1.setData(new LimitLockResult(0,1));
            }
            return result1;
        };
        LimitLockBusinessBaseService.Config config = screenLimitLockBusinessBaseService.
                init(username,null,null,subWrite);
        result = screenLimitLockBusinessBaseService.doWrite(
                1,null,new  Object[]{username},null,config
        );
        return result;
    }
}
