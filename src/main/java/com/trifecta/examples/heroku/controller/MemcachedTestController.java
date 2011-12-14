package com.trifecta.examples.heroku.controller;

import net.spy.memcached.MemcachedClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * User: emd
 * Date: 12/14/11
 * Time: 1:43 PM
 */
@Controller
public class MemcachedTestController {

    //@Autowired
    MemcachedClient memcachedClient;

    @RequestMapping(value="cache/",method= RequestMethod.GET)
    @ResponseBody
    public final String getMemcached() {

        StringBuffer output = new StringBuffer("Memcached test...<br/>");

        memcachedClient.add("testSpring", 0, "testDataSpring");
        output.append(memcachedClient.get("testSpring"));

        return output.toString();
    }

}
