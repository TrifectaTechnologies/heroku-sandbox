package com.trifecta.examples.heroku.controller;

import com.trifecta.examples.heroku.model.Couch;
import org.lightcouch.CouchDbClient;
import org.lightcouch.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * User: emd
 * Date: 12/14/11
 * Time: 1:55 PM
 */
@Controller
public class CouchTestController {

    @Autowired
    private CouchDbClient dbClient;

    @RequestMapping(value="couch/",method= RequestMethod.GET)
    @ResponseBody
    public final String getCouch() {

        Couch couch = new Couch("red");
        Response response = dbClient.save(couch); // response holds CouchDB response

        return "CouchDB test... id=" + response.getId();
    }

}
