package com.wf.controller;

import com.alibaba.fastjson.JSON;
import com.wf.model.BigScreen;
import com.wf.model.Result;
import com.wf.service.impl.ScreenServiceImpl;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/screen")
public class SaveScreenController {

    @Resource
    private ScreenServiceImpl screenService;

    @RequestMapping(value = "/save", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    public Result save(@RequestBody String json){
        System.out.println(json);
        BigScreen bigScreen = JSON.parseObject(json,BigScreen.class);
        System.out.println(bigScreen.toString());
        Result result = screenService.save(bigScreen.getUsername(),bigScreen.getScreenName());
        if (result.getCode()==0){
            result.setMessage("成功");
        }else if (result.getCode() == 500){
            result.setMessage("超过上限");
        }else {
            System.out.println(result.toString());
            result.setMessage("系统异常");
        }
        return result;
    }
}
