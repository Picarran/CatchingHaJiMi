package org.picarran.catchinghajimi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("org.picarran.catchinghajimi.mapper")
public class CatchingHaJiMi {
    public static void main(String[] args) {
        SpringApplication.run(CatchingHaJiMi.class, args);
    }
}
