package org.among.springbatchstudy.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ConditionalBatchConfig {
    private static Logger logger = LoggerFactory.getLogger(ConditionalBatchConfig.class);

    /**
     * 시나리오.
     *  Step1 실패) Step1 > Step4
     *  Step1 성공) Step1 > Step2 > Step3
     */
    private int count = 0;

    @Bean
    public Job flowJob(JobRepository jobRepository, Step step1, Step step2, Step step3, Step step4) {
        logger.info("----- Job1 Execute -----");
        return new JobBuilder("flow-job", jobRepository)
                .start(step1)
                    .on("FAILED")
                    .to(step4)
                .from(step1)
                    .next(step2)
                    .next(step3)
                    .end()
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository, Tasklet task1, PlatformTransactionManager platformTransactionManager) {
        logger.info("----- Step1 Execute -----");
        return new StepBuilder("step1", jobRepository)
                .tasklet(task1, platformTransactionManager)
                .build();
    }

    @Bean
    public Step step2(JobRepository jobRepository, Tasklet task2, PlatformTransactionManager platformTransactionManager) {
        logger.info("----- Step2 Execute -----");
        return new StepBuilder("step2", jobRepository)
                .tasklet(task2, platformTransactionManager)
                .build();
    }

    @Bean
    public Step step3(JobRepository jobRepository, Tasklet task3, PlatformTransactionManager platformTransactionManager) {
        logger.info("----- Step3 Execute -----");
        return new StepBuilder("step3", jobRepository)
                .tasklet(task3, platformTransactionManager)
                .build();
    }

    @Bean
    public Step step4(JobRepository jobRepository, Tasklet task4, PlatformTransactionManager platformTransactionManager) {
        logger.info("----- Step4 Execute -----");
        return new StepBuilder("step4", jobRepository)
                .tasklet(task4, platformTransactionManager)
                .build();
    }

    @Bean
    public Tasklet task1() {
        return (contribution, chunkContext) -> {
            logger.info("----- Task1 Execute -----");
            logger.info("Task1 : {}", count);
            if (count < 2) {
                if (count == 1) {
//                    contribution.setExitStatus(ExitStatus.FAILED);
                    throw new RuntimeException("----- Step1 Runtime Exception -----");
                }
                count++;
                return RepeatStatus.CONTINUABLE;
            }
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Tasklet task2() {
        return (contribution, chunkContext) -> {
            logger.info("----- Task2 Execute -----");
            logger.info("Task2 : {}", count);
            if (count != 0) {
                count--;
                return RepeatStatus.CONTINUABLE;
            }
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Tasklet task3() {
        return (contribution, chunkContext) -> {
            logger.info("----- Task3 Execute -----");
            logger.info("Task3 : {}", count);
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Tasklet task4() {
        return (contribution, chunkContext) -> {
            logger.info("----- Task4 Execute -----");
            logger.info("Task4 : {}", count);
            return RepeatStatus.FINISHED;
        };
    }
}
