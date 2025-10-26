package org.picarran.mycathome;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("org.picarran.mycathome.mapper")
public class MyCatHomeApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyCatHomeApplication.class, args);
    }
}
