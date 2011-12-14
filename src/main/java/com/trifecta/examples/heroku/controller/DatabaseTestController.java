package com.trifecta.examples.heroku.controller;

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
    private DataSource dataSource;


    @RequestMapping(value="db/",method= RequestMethod.GET)
    @ResponseBody
    public final String getDb() throws SQLException {

        Statement stmt = dataSource.getConnection().createStatement();
        stmt.executeUpdate("DROP TABLE IF EXISTS ticks");
        stmt.executeUpdate("CREATE TABLE ticks (tick timestamp)");
        stmt.executeUpdate("INSERT INTO ticks VALUES (now())");
        ResultSet rs = stmt.executeQuery("SELECT tick FROM ticks");
        
        StringBuffer output = new StringBuffer("Database Test...<br/>");
        
        while (rs.next()) {
            output.append("Read from DB: " + rs.getTimestamp("tick") + "<br/>");
        }

        return output.toString();
    }

}
