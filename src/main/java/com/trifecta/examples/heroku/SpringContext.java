package com.trifecta.examples.heroku;

import net.spy.memcached.ConnectionFactory;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.ConnectionFactoryBuilder.Protocol;
import net.spy.memcached.auth.AuthDescriptor;
import net.spy.memcached.auth.PlainCallbackHandler;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lightcouch.CouchDbClient;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;

/**
 * User: emd
 * Date: 12/13/11
 * Time: 11:13 AM
 */

@Configuration
@EnableWebMvc
@EnableTransactionManagement
@ComponentScan(basePackages = "com.trifecta.examples.heroku")
public class SpringContext {
    
    Log log = LogFactory.getLog(SpringContext.class);

    // Begin Memcached Client Configuration
    @Bean
    public PlainCallbackHandler getMemcachedPlainCallbackHandler() {
        return new PlainCallbackHandler(System.getenv("MEMCACHE_USERNAME"), System.getenv("MEMCACHE_PASSWORD"));
    }

    @Bean
    public AuthDescriptor getMemcachedAuthDescriptor() {
        return new AuthDescriptor(new String[]{"PLAIN"},getMemcachedPlainCallbackHandler());
    }

    @Bean
    public ConnectionFactory getConnectionMemcachedFactory() {
        ConnectionFactoryBuilder factoryBuilder = new ConnectionFactoryBuilder();
        factoryBuilder.setProtocol(Protocol.BINARY);
        
        factoryBuilder.setAuthDescriptor(getMemcachedAuthDescriptor());
        
        return factoryBuilder.build();
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


    // Begin PostgreSQL DataSource Configuration

    @Bean
    public URI getDbUrl() throws URISyntaxException {
        return new URI(System.getenv("DATABASE_URL"));
    }

    @Bean
    public BasicDataSource getDataSource() throws URISyntaxException {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:postgresql://"+getDbUrl().getHost()+getDbUrl().getPath());
        dataSource.setUsername(getDbUrl().getUserInfo().split(":")[0]);
        dataSource.setPassword(getDbUrl().getUserInfo().split(":")[1]);

        return dataSource;
    }

    @Bean
    public PlatformTransactionManager txManager() throws URISyntaxException {
        return new DataSourceTransactionManager(getDataSource());
    }

    // End PostgreSQL DataSource Configuration

    // Begin MyBatis Configuration
    
    @Bean
    public SqlSessionFactoryBean getSqlSessionFactory() throws URISyntaxException, IOException {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(getDataSource());

        PathMatchingResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
        Resource[] resolvers = resourceResolver.getResources("classpath*:com/trifecta/examples/heroku/mapper/**/*.xml");
        sqlSessionFactoryBean.setMapperLocations( resolvers );

        return sqlSessionFactoryBean;
    }

    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setBasePackage("com.trifecta.examples.heroku.mapper");

        return mapperScannerConfigurer;
    }
    
    // End MyBatis Configuration

    // Begin RabbitMQ Configuration

    @Bean
    public URI getAmqpUrl() throws URISyntaxException {
        return new URI(System.getenv("RABBITMQ_URL"));
    }
    
    @Bean
    public org.springframework.amqp.rabbit.connection.ConnectionFactory getRabbitConnectionFactory() throws NoSuchAlgorithmException, KeyManagementException, URISyntaxException {
        org.springframework.amqp.rabbit.connection.ConnectionFactory connectionFactory = new CachingConnectionFactory(getAmqpUrl().getHost());
        return connectionFactory;
    }

    @Bean
    public AmqpAdmin amqpAdmin() throws NoSuchAlgorithmException, KeyManagementException, URISyntaxException {
        return new RabbitAdmin(getRabbitConnectionFactory());
    }

    @Bean
    public AmqpTemplate amqpTemplate() throws NoSuchAlgorithmException, KeyManagementException, URISyntaxException {
        return new RabbitTemplate(getRabbitConnectionFactory());
    }

    @Bean
    public Queue myQueue() {
        return new Queue("myqueue");
    }

    // End RabbitMQ Configuration

    // Begin CouchDB Configuration

    @Bean
    public URI getCouchUrl() throws URISyntaxException {
        return new URI(System.getenv("CLOUDANT_URL"));
    }

    @Bean(destroyMethod = "shutdown")
    public CouchDbClient couchDbClient() throws URISyntaxException {
        
        String dbName = "lightrate";
        boolean createDbIfNotExist = true;
        String protocol = getCouchUrl().getScheme();
        String host = getCouchUrl().getHost();
        int port = getCouchUrl().getScheme().equals("https") ? 443 : ( getCouchUrl().getScheme().equals("http") ? 80 : 5984 );
        if( getCouchUrl().getPort() != -1 ) {
            port = getCouchUrl().getPort();
        }
        log.info( "Port: '" + port + "'" );
        String username = "";
        String password = "";
        if( getCouchUrl().getUserInfo() != null ) {
            username = getCouchUrl().getUserInfo().split(":")[0];
            password = getCouchUrl().getUserInfo().split(":")[1];
        }

        return new CouchDbClient(dbName,createDbIfNotExist,protocol,host,port,username,password);
    }

    // End CouchDB Configuration
}
