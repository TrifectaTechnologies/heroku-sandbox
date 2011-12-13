package com.trifecta.examples.heroku;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * Created by IntelliJ IDEA.
 * User: emd
 * Date: 12/13/11
 * Time: 10:58 AM
 * To change this template use File | Settings | File Templates.
 */
public class Main {
    static Log log = LogFactory.getLog(Main.class);
    
    // This application is mapped in the pom file in the assembler plugin
    public static void main(String[] args) throws Exception{
        log.info("Starting application with Spring MVC and embedded Jetty...");

        //ConfigurableWebApplicationContext

        // Configure the Spring Configuration
        DispatcherServlet dispatcherServlet = new DispatcherServlet();
        dispatcherServlet.setContextClass(AnnotationConfigWebApplicationContext.class);
        dispatcherServlet.setContextConfigLocation("com.trifecta.examples.heroku.SpringContext");

        // Wrap Spring for deployment to embedded Jetty
        ServletHolder springHolder = new ServletHolder(dispatcherServlet);
        springHolder.setInitOrder(1);
        
        // Create the embedded Jetty server
        Server server = new Server(Integer.valueOf(System.getenv("PORT")));

        // Create the Jetty application context at the root of the domain
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        // Add the application context to the server
        server.setHandler(context);

        // Map spring MVC to the root of the application context
        context.addServlet(springHolder,"/*");

        // Start the embedded Jetty server
        server.start();

        log.info("Spring MVC and embedded Jetty application startup complete!");

        // Transfer control to the Jetty thread
        server.join();

    }
    
}
