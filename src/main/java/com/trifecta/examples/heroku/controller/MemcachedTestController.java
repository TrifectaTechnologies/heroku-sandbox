package com.trifecta.examples.heroku.controller;

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

    @RequestMapping(value="cache/",method= RequestMethod.GET)
    @ResponseBody
    public final String getMemcached() {
        return "Memcached test...";
    }

}
