package com.bdps.config;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 * 	等同於 application.properties =>
 *   #spring.datasource.url=jdbc:oracle:thin:@localhost:1521:orcl
 *   #spring.datasource.username=BDPS
 *   #spring.datasource.password=ChuKuang
 *   #spring.datasource.driver-class-name=oracle.jdbc.OracleDriver
 * 
 * */
@Configuration
public class OracleConfig {

    @Bean
    public DataSource dataSource() throws SQLException {

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("oracle.jdbc.OracleDriver");
//        dataSource.setUrl("jdbc:oracle:thin:@localhost:1521:orcl");
        dataSource.setUrl("jdbc:oracle:thin:@52.255.137.177:1521:cdb1");
        dataSource.setUsername("BDPS");
        dataSource.setPassword("ChuKuang");        
        
        return dataSource;
    }
}
