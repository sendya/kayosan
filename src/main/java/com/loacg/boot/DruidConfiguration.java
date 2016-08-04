package com.loacg.boot;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;

import com.alibaba.druid.support.http.WebStatFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Project: kayosan
 * Author: Sendya <18x@loacg.com>
 * Time: 8/4/2016 11:20 PM
 */
@Configuration
public class DruidConfiguration {

    @Bean
    public ServletRegistrationBean druidServlet() {
        return new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
    }

    @Bean
    public DataSource druidDataSource(@Value("${spring.datasource.driverClassName}") String driver,
                                      @Value("${spring.datasource.url}") String url,
                                      @Value("${spring.datasource.username}") String username,
                                      @Value("${spring.datasource.password}") String password,
                                      @Value("${spring.datasource.initialSize}") Integer initialSize,
                                      @Value("${spring.datasource.minIdle}") Integer minIdle,
                                      @Value("${spring.datasource.maxActive}") Integer maxActive,
                                      @Value("${spring.datasource.maxWait}") Long maxWait,
                                      @Value("${spring.datasource.timeBetweenEvictionRunsMillis}") Long timeBetweenEvictionRunsMillis,
                                      @Value("${spring.datasource.minEvictableIdleTimeMillis}") Long minEvictableIdleTimeMillis,
                                      @Value("${spring.datasource.validationQuery}") String validationQuery,
                                      @Value("${spring.datasource.testWhileIdle}") boolean testWhileIdle,
                                      @Value("${spring.datasource.testOnBorrow}") boolean testOnBorrow,
                                      @Value("${spring.datasource.testOnReturn}") boolean testOnReturn,
                                      @Value("${spring.datasource.poolPreparedStatements}") boolean poolPreparedStatements,
                                      @Value("${spring.datasource.maxPoolPreparedStatementPerConnectionSize}") Integer maxPoolPreparedStatementPerConnectionSize,
                                      @Value("${spring.datasource.connectionProperties}") Properties connectionProperties,
                                      @Value("${spring.datasource.filters}") String filters
    ) {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName(driver);
        druidDataSource.setUrl(url);
        druidDataSource.setUsername(username);
        druidDataSource.setPassword(password);
        druidDataSource.setInitialSize(initialSize);
        druidDataSource.setMinIdle(minIdle);
        druidDataSource.setMaxActive(maxActive);
        druidDataSource.setMaxWait(maxWait);
        druidDataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        druidDataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        druidDataSource.setValidationQuery(validationQuery);
        druidDataSource.setTestWhileIdle(testWhileIdle);
        druidDataSource.setTestOnBorrow(testOnBorrow);
        druidDataSource.setTestOnReturn(testOnReturn);
        druidDataSource.setPoolPreparedStatements(poolPreparedStatements);
        druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);
        druidDataSource.setConnectProperties(connectionProperties);

        try {
            druidDataSource.setFilters(filters);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return druidDataSource;
    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new WebStatFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        return filterRegistrationBean;
    }

}
