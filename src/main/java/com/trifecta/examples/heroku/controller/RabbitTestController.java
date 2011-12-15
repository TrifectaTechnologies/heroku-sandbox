package com.trifecta.examples.heroku.controller;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
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

    @RequestMapping(value="mq/",method= RequestMethod.GET)
    @ResponseBody
    public final String getRabbit() throws IOException {
        StringBuffer output = new StringBuffer("RabbitMQ test...<br/>");

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        String message = "Hello World!";
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
        output.append(" [x] Sent '" + message + "'");

        channel.close();
        connection.close();



        return output.toString();
    }

}
