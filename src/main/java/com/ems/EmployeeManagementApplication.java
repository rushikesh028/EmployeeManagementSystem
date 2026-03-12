package com.ems;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/*

           Employee Management System (EMS)
           Java Core + JDBC + MySQL + Spring Boot


   Tech Stack:
   - Java 25
   - Spring Boot 3.2
   - Spring JDBC
   - MySQL 8
   - HikariCP Connection Pool
   - Lombok

  Architecture:
   Controller → Service → DAO → MySQL
 */
@SpringBootApplication
@Slf4j
public class EmployeeManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmployeeManagementApplication.class, args);
        log.info("========================================================");
        log.info("  EMS Application started – http://localhost:8080");
        log.info("  API Base: http://localhost:8080/api");
        log.info("========================================================");
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                    .allowedOrigins("*")
                    .allowedMethods("GET","POST","PUT","PATCH","DELETE");
            }
        };
    }
}