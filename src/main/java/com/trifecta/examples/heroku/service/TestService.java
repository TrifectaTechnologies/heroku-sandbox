package com.trifecta.examples.heroku.service;

import com.trifecta.examples.heroku.mapper.TickMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

/**
 * User: emd
 * Date: 1/4/12
 * Time: 8:14 AM
 */

@Service
@Transactional(readOnly = true)
public class TestService {

    @Autowired
    private TickMapper tickMapper;

    public Timestamp getTick() {
        return tickMapper.getLastTick();
    }

    @Transactional(readOnly = false)
    public void tick() {
        tickMapper.insertTick();
    }

}
