package com.trifecta.examples.heroku.worker;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;

/**
 * User: emd
 * Date: 12/14/11
 * Time: 7:35 PM
 */
public class RabbitWorker {

    static Log log = LogFactory.getLog(RabbitWorker.class);

    private static final String QUEUE_NAME = "test-queue";

    public static void main(String[] args) throws IOException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        log.info(" [*] Waiting for messages. To exit press CTRL+C");

        QueueingConsumer consumer = new QueueingConsumer(channel);
        channel.basicConsume(QUEUE_NAME, true, consumer);

        try {
            while (true) {
                QueueingConsumer.Delivery delivery = null;
                delivery = consumer.nextDelivery();
                String message = new String(delivery.getBody());
                log.info(" [x] Received '" + message + "'");
            }
        } catch (InterruptedException e) {
            log.error("Interrupted!",e);
        } finally {
            channel.close();
            connection.close();
        }

    }

}
