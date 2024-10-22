package org.among.springbatchstudy;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

// 스케쥴링 위해 @EnableScheduling, @EnableBatchProcessing 추가(다만, 스키마 생성이 안 되어 직접 생성)
//@EnableScheduling
//@EnableBatchProcessing
@SpringBootApplication
public class SpringBatchStudyApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBatchStudyApplication.class, args);
    }

}
