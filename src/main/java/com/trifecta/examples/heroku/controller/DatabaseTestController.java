package com.trifecta.examples.heroku.controller;

import com.trifecta.examples.heroku.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * User: emd
 * Date: 12/14/11
 * Time: 1:15 PM
 */
@Controller
public class DatabaseTestController {

    @Autowired
    private TestService testService;

    @RequestMapping(value="db/",method= RequestMethod.GET)
    @ResponseBody
    public final String getDb() throws SQLException {
        
        StringBuffer output = new StringBuffer("Database Test...<br/>");

        output.append("Last tick: " + testService.getTick() + "<br/>");

        testService.tick();
        
        output.append("New tick: " + testService.getTick());

        return output.toString();
    }

}
