package com.trifecta.examples.heroku.mapper;

import org.apache.ibatis.annotations.Select;

import java.sql.Timestamp;

/**
 * User: emd
 * Date: 1/4/12
 * Time: 8:58 AM
 */
public interface TickMapper {

    Timestamp getLastTick();
    void insertTick();

}
