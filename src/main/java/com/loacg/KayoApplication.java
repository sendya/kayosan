package com.loacg;

import com.alibaba.druid.pool.DruidDataSource;
import com.loacg.kayo.Kayo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.telegram.telegrambots.TelegramApiException;

import javax.sql.DataSource;

@Configuration
@SpringBootApplication
@EnableAutoConfiguration
@ServletComponentScan
public class KayoApplication {

	public static void main(String[] args) {
		SpringApplication.run(KayoApplication.class, args);

/*		try {
			Kayo.run();
		} catch (TelegramApiException e) {
			System.exit(0);
		}*/
	}

	@Autowired
	private Environment env;

	@Bean
	public DataSource dataSource() {
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setUrl(env.getProperty("spring.datasource.url"));
		dataSource.setUsername(env.getProperty("spring.datasource.username"));//用户名
		dataSource.setPassword(env.getProperty("spring.datasource.password"));//密码
		dataSource.setInitialSize(5);
		dataSource.setMaxActive(20);
		dataSource.setMinIdle(2);
		dataSource.setMaxWait(60000);
		dataSource.setValidationQuery("SELECT 1");
		dataSource.setTestOnBorrow(false);
		dataSource.setTestWhileIdle(true);
		dataSource.setPoolPreparedStatements(false);
		return dataSource;
	}
}
