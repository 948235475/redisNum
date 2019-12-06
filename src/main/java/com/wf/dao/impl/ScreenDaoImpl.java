package com.wf.dao.impl;

import com.wf.dao.ScreenDao;
import com.wf.model.BigScreen;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class ScreenDaoImpl implements ScreenDao {

    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public BigScreen save(BigScreen bigScreen) {
        return mongoTemplate.insert(bigScreen);
    }
}
