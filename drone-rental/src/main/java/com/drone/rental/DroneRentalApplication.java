package com.drone.rental;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 无人机租赁系统 - 启动类
 *
 * @author 毕业设计
 */
@SpringBootApplication
public class DroneRentalApplication {

    public static void main(String[] args) {
        SpringApplication.run(DroneRentalApplication.class, args);
        System.out.println("====================================");
        System.out.println("  无人机租赁系统启动成功！");
        System.out.println("  Swagger文档: http://localhost:8080/api/swagger-ui/index.html");
        System.out.println("====================================");
    }
}
