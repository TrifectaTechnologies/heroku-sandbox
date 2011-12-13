package com.trifecta.examples.heroku;

import net.spy.memcached.ConnectionFactory;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.ConnectionFactoryBuilder.Protocol;
import net.spy.memcached.auth.AuthDescriptor;
import net.spy.memcached.auth.PlainCallbackHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collections;

/**
 * User: emd
 * Date: 12/13/11
 * Time: 11:13 AM
 */

@Configuration
public class SpringContext {
    
    Log log = LogFactory.getLog(SpringContext.class);
    
    public void SpringContext() {
        log.info("Building Spring Configuration Context...");
    }

    // Begin Memcached Client Configuration

    @Bean
    public PlainCallbackHandler getMemcachedPlainCallbackHandler() {
        log.info("Credentials: "+System.getenv("MEMCACHE_USERNAME")+"/"+System.getenv("MEMCACHE_PASSWORD"));
        return new PlainCallbackHandler(System.getenv("MEMCACHE_USERNAME"), System.getenv("MEMCACHE_PASSWORD"));
    }

    @Bean
    public AuthDescriptor getMemcachedAuthDescriptor() {
        return new AuthDescriptor(new String[]{"PLAIN"},getMemcachedPlainCallbackHandler());
    }

    @Bean
    public ConnectionFactory getConnectionMemcachedFactory() {
        ConnectionFactoryBuilder factoryBuilder = new ConnectionFactoryBuilder();
        return factoryBuilder.setProtocol(Protocol.BINARY).setAuthDescriptor(getMemcachedAuthDescriptor()).build();
    }

    @Bean
    public InetSocketAddress getServerMemcachedAddress() {
        return new InetSocketAddress(System.getenv("MEMCACHE_SERVERS"), 11211);
    }

    @Bean
    public MemcachedClient getMemcachedClient() throws IOException{
        MemcachedClient memcachedClient =
                new MemcachedClient(
                        getConnectionMemcachedFactory(),
                        Collections.singletonList(getServerMemcachedAddress()));
        return memcachedClient;
    }

    // End Memcached Client Configuration

}
