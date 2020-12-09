package com.gmall.manage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan(basePackages = "com.gmall.manage.mapper")
@EnableTransactionManagement
@ComponentScan(basePackages="com.gmall")
public class ManageServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(ManageServiceApplication.class, args);
	}
}
