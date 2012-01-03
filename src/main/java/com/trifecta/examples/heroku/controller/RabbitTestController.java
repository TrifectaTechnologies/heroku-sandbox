package com.trifecta.examples.heroku.controller;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

/**
 * User: emd
 * Date: 12/14/11
 * Time: 1:43 PM
 */
@Controller
public class RabbitTestController {

    private final String QUEUE_NAME = "test-queue";

    @Autowired
    private AmqpTemplate amqpTemplate;

    @RequestMapping(value="mq/",method= RequestMethod.GET)
    @ResponseBody
    public final String getRabbit() throws IOException {
        StringBuffer output = new StringBuffer("RabbitMQ test...<br/>");

        String message = "Hello World!";

        amqpTemplate.convertAndSend(QUEUE_NAME,message);

        output.append(" [x] Sent '" + message + "'");

        return output.toString();
    }

}
